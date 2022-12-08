package com.scandecode_example;

import static com.scandecode_example.SpdConstant.DEF_direction;
import static com.scandecode_example.SpdConstant.DEF_direction_init;
import static com.scandecode_example.SpdConstant.DEF_direction_scan;
import static com.scandecode_example.SpdConstant.DEF_start;
import static com.scandecode_example.SpdConstant.DEF_value;
import static com.scandecode_example.SpdConstant.DEF_value_init;
import static com.scandecode_example.SpdConstant.DEF_value_scan;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.serialport.SerialPortSpd;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandecode_example.adapter.UnitAdapter;
import com.scandecode_example.model.DataBean;
import com.scandecode_example.model.WeightEvent;
import com.scandecode_example.utils.FileUtils;
import com.scandecode_example.utils.SpUtils;
import com.scandecode_example.utils.ToastUtils;
import com.scandecode_example.utils.excel.ExcelUtils;
import com.scandecode_example.view.EndWindow;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.utils.MyDateAndTime;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jxl.format.Colour;

/**
 * @author xuyan  Example page to implement scan-related functions
 * <p>
 * 初始化开关串口与GPIO，显示串口返回的扫描数据。
 * <p>
 * HG050C定制版扫描demo
 */
public class ScanActivity extends AppCompatActivity {

    private Button mSingle;
    private Button mTimes;
    private ImageView mSettings;
    private TextView mClear;
    private UnitAdapter mAdapter;
    private List<DataBean> mList;
    private boolean mTimesScan;
    private TextView tvcound;
    private int scancount = 0;

    private RecyclerView recyclerView;

    private DataBean dataBean;

    private static final String TAG = "SerialPort";

    private SerialPortSpd mSerialPort = null;
    private ReadThread mReadThread;
    //private Handler handler = null;
    private String readstr = "";
    private byte[] tmpbuf = new byte[1024];
    private int fd;


    private BroadcastReceiver mDisplayReceiver;
    private int readed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();

        initBarcode();

    }


    @SuppressLint({"ClickableViewAccessibility", "NewApi", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void initView() {
        EventBus.getDefault().register(this);

        mSingle = findViewById(R.id.btn_onetime);
        mTimes = findViewById(R.id.btn_times);
        tvcound = findViewById(R.id.tv_cound);
        mTimesScan = false;
        //重复扫描
        mTimes.setOnClickListener(v -> {
            if (mTimesScan) {
                handler.removeCallbacks(startTask);
                handler.removeCallbacks(startScan);

                mTimesScan = false;
                mTimes.setText(getString(R.string.start_times));
            } else {
                if ((System.currentTimeMillis() - mkeyTime) > 1000) {
                    mkeyTime = System.currentTimeMillis();

                    handler.removeCallbacks(startTask);
                    handler.removeCallbacks(startScan);
                    handler.postDelayed(startTask, 0);
                    mTimesScan = true;
                    mTimes.setText(getString(R.string.stop_times));
                } else {
                    ToastUtils.showShortToastSafe(R.string.please_do_not_click);
                }
            }
        });
        recyclerView = findViewById(R.id.rv_content);
        mList = new ArrayList<>();
        mAdapter = new UnitAdapter(mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(ScanActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mSettings = findViewById(R.id.title_settings);
        mSettings.setOnClickListener(v -> new EndWindow(ScanActivity.this).showAtLocation(mSettings, Gravity.START, 0, 0));
        mClear = findViewById(R.id.title_clear);
        mClear.setOnClickListener(v -> {
            mList.clear();
            mAdapter.notifyDataSetChanged();
            scancount = 0;
            tvcound.setText("");
        });

        //单次扫描，按下开始抬起结束
        mSingle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
                    if (mTimesScan) {
                        mTimes.performClick();
                    } else {
                        handler.removeCallbacks(startTask);
                        handler.removeCallbacks(startScan);

                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
                    if ((System.currentTimeMillis() - mkeyTime) > 500) {
                        mkeyTime = System.currentTimeMillis();
                        handler.postDelayed(startScan, 0);
                    } else {
                        ToastUtils.showShortToastSafe(R.string.please_do_not_click);
                    }
                    break;
                }
                default:
                    break;
            }
            return false;
        });


        boolean cn = "CN".equals(getApplicationContext().getResources().getConfiguration().locale.getCountry());
        if (cn) {
            recyclerView.setBackground(AppDecode.getInstance().getDrawable(R.drawable.bg_qqqq));
        } else {
            recyclerView.setBackground(AppDecode.getInstance().getDrawable(R.drawable.bg_pppp));
        }
    }

    Handler handler = new Handler();

    /**
     * 连续扫描，有间隔地不停触发扫描
     * Continuous scan
     */
    private final Runnable startTask = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(startScan, 200);
            handler.postDelayed(startTask, (int) SpUtils.get(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, 2000));
            mTimesScan = true;
        }
    };

    /**
     * 开始扫描，先判断是不是开机后第一次。
     * start scan
     */
    private final Runnable startScan = () -> {

        writeOne(DEF_value_scan);
    };


    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WeightEvent event) {
        switch (event.getMessage()) {
            case "explore":
                outPutFile();
                break;
            case "excel":
                outPutExcel();
                break;
            default:
                break;
        }
    }

    private void outPutExcel() {
        if (mList.size() == 0) {
            ToastUtils.showShortToastSafe(R.string.no_file);
            return;
        }


        //导出数据 查询数据生成文件，生成后删除数据
        String name = MyDateAndTime.getMakerDate();
        //导出excel
        try {

            ExcelUtils.getInstance()
                    .setSHEET_NAME(name)//设置表格名称
                    .setFONT_COLOR(Colour.BLUE)//设置标题字体颜色
                    .setFONT_TIMES(8)//设置标题字体大小
                    .setFONT_BOLD(true)//设置标题字体是否斜体
                    .setBACKGROND_COLOR(Colour.GRAY_25)//设置标题背景颜色
                    .setContent_list_Strings(mList)//设置excel内容
                    .setWirteExcelPath(Environment.getExternalStorageDirectory() + File.separator + name + ".xls")
                    .createExcel(ScanActivity.this);

        } catch (Exception e) {
            e.printStackTrace();

        }

        scanFile(this, Environment.getExternalStorageDirectory() + File.separator + name + ".xls", 1);
    }

    private void outPutFile() {
        if (mList.size() == 0) {
            ToastUtils.showShortToastSafe(R.string.no_file);
            return;
        }
        FileUtils fileUtils = new FileUtils();
        int h = fileUtils.outputOnefile(mList, createFilename());
        scanFile(this, createFilename(), h);
    }

    /**
     * 创建导出文件的名字          Create export file name
     *
     * @return 完整文件路径+名     Full file path + name
     */
    @SuppressLint("SdCardPath")
    public String createFilename() {
        String checktime = MyDateAndTime.getMakerDate();
        String date = checktime.substring(0, 8);
        String time = checktime.substring(8, 12);
        String name = "barcode" + date + "_" + time;
        return "/sdcard/" + name + ".txt";
    }

    /**
     * 更新文件显示的广播，在生成文件后调用一次。
     * Update the broadcast shown by the file, called once after the file is generated.
     */
    public static void scanFile(Context context, String filePath, int h) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
        if (h == 1) {
            ToastUtils.showShortToastSafe(R.string.explore_success);
        }
    }

    /**
     * 返回键监听
     * Return key listening
     */
    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    boolean cn = "CN".equals(getApplicationContext().getResources().getConfiguration().locale.getCountry());
                    if (cn) {
                        ToastUtils.showShortToastSafe("再次点击返回退出");
                    } else {
                        ToastUtils.showShortToastSafe("Press the exit again");
                    }
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //====================================扫描服务集成=======================================

    //简单判断isutf8
    private boolean isUTF8(byte[] sx) {
        Log.d(TAG, "begian to isUTF8");
        for (int i = 0; i < sx.length; ) {
            if (sx[i] < 0) {
                if ((sx[i] >>> 5) == 0x7FFFFFE) {
                    if (((i + 1) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE)) {
                        i = i + 2;
                    } else {
                        return false;
                    }
                } else if ((sx[i] >>> 4) == 0xFFFFFFE) {
                    if (((i + 2) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE) && ((sx[i + 2] >>> 6) == 0x3FFFFFE)) {
                        i = i + 3;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                i++;
            }
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private void initBarcode() {

        if (SystemProperties.getBoolean(DEF_start, true)) {
            //3个Gpio初始化
            initFirst(DEF_direction);
            initFirst(DEF_direction_init);
            initFirst(DEF_direction_scan);
            SystemProperties.set(DEF_start, "false");
        }

        //2个上电
        writeOne(DEF_value);
        writeOne(DEF_value_init);

        //handler初始化，在这里处理显示扫描结果
        handler = new Handler() {

            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {

                    String data = (String) msg.obj;

                    if (recyclerView.getBackground() != null) {
                        recyclerView.setBackground(null);
                    }
                    scancount += 1;
                    tvcound.setText(getString(R.string.scan_time) + scancount + "");
                    dataBean = new DataBean();
                    dataBean.setBarcode(data);
                    mList.add(dataBean);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

                }

            }
        };


        //初始化串口，开始读串口2
        mSerialPort = new SerialPortSpd();

        try {

            mSerialPort.OpenSerial(SerialPortSpd.SERIAL_TTYS2, 9600);

            if (mSerialPort != null) {
                Log.d(TAG, "open SerialPort success");
                fd = mSerialPort.getFd();
                mReadThread = new ReadThread();
                mReadThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //注册广播休眠停止扫描。
        mDisplayReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Objects.requireNonNull(action).equals(Intent.ACTION_SCREEN_OFF)) {
                    writeZero(DEF_value);
                } else if (Objects.requireNonNull(action).equals(Intent.ACTION_SCREEN_ON)) {
                    writeZero(DEF_value);
                }
            }
        };
        final IntentFilter se4500dispfilter = new IntentFilter();

        se4500dispfilter.addAction(Intent.ACTION_SCREEN_OFF);
        se4500dispfilter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(mDisplayReceiver, se4500dispfilter);

    }


    //停止扫描下电写0
    private void writeZero(String pathwhat) {

        try {
            File mScanDeviceName = new File(pathwhat);
            BufferedWriter mScanCtrlFileWrite = new BufferedWriter(new FileWriter(mScanDeviceName, false));
            mScanCtrlFileWrite.write("0");
            mScanCtrlFileWrite.flush();
            mScanCtrlFileWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //开始扫描上电写1
    private void writeOne(String pathwhat) {

        try {
            //File ScanDeviceName = new File("proc/driver/scan");
            File mScanDeviceName = new File(pathwhat);
            BufferedWriter mScanCtrlFileWrite = new BufferedWriter(new FileWriter(mScanDeviceName, false));
            mScanCtrlFileWrite.write("1");
            mScanCtrlFileWrite.flush();
            mScanCtrlFileWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //首次扫描前写成out
    private void initFirst(String pathwhat) {

        try {
            File mScanDeviceName = new File(pathwhat);
            BufferedWriter mScanCtrlFileWrite = new BufferedWriter(new FileWriter(mScanDeviceName, false));
            mScanCtrlFileWrite.write("out");
            mScanCtrlFileWrite.flush();
            mScanCtrlFileWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //退出程序解除注册等
    @Override
    public void onDestroy() {
        mReadThread.interrupt();

        writeZero(DEF_value_scan);

        writeZero(DEF_value);
        writeZero(DEF_value_init);

        unregisterReceiver(mDisplayReceiver);
        mSerialPort.CloseSerial(fd);

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mTimesScan = false;

        handler.removeCallbacks(startTask);
        handler.removeCallbacks(startScan);

        super.onDestroy();
    }


    //从串口读结果并发给handler
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    try {
                        tmpbuf = mSerialPort.ReadSerial(fd, 1024, 200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (tmpbuf != null && (DataConversionUtils.byteArrayToInt(new byte[]{tmpbuf[0]}) < 240)) {
                        readed = tmpbuf.length;
                        byte[] readbuf = new byte[readed];
                        System.arraycopy(tmpbuf, 0, readbuf, 0, readed);
                        if (isUTF8(readbuf)) {
                            readstr = new String(readbuf, StandardCharsets.UTF_8);
                            Log.d(TAG, "is a utf8 string");
                        } else {
                            readstr = new String(readbuf, "gbk");
                            Log.d(TAG, "is a gbk string");
                        }

                        if (readstr != null) {
                            //为扫描结果添加已经存储的前后缀

                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = readstr;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (SecurityException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
