package com.scandecode_example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Slog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.scandecode_example.adapter.UnitAdapter;
import com.scandecode_example.model.WeightEvent;
import com.scandecode_example.utils.FileUtils;
import com.scandecode_example.utils.SpUtils;
import com.scandecode_example.utils.ToastUtils;
import com.scandecode_example.view.EndWindow;
import com.speedata.utils.MyDateAndTime;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuyan  Example page to implement scan-related functions
 */
public class ScanActivity extends AppCompatActivity {

    private Button mSingle;
    private Button mTimes;
    private ImageView mSettings;
    private TextView mClear;
    private UnitAdapter mAdapter;
    private List<String> mList;
    private boolean mTimesScan;
    private TextView tvcound;
    private int scancount = 0;
    private ScanInterface scanDecode;

    private RecyclerView recyclerView;

    private BufferedWriter ScanCtrlFileWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
    }

    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    private void initView() {
        EventBus.getDefault().register(this);
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");
        mSingle = findViewById(R.id.btn_onetime);
        mTimes = findViewById(R.id.btn_times);
        tvcound = findViewById(R.id.tv_cound);
        mTimesScan = false;
        mTimes.setOnClickListener(v -> {
            if (mTimesScan) {
                handler.removeCallbacks(startTask);

                if ("N43".equals(SystemProperties.get("persist.sys.scanheadtype"))) {
                    try {
                        File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
                        ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
                        ScanCtrlFileWrite.write("trigoff");
                        ScanCtrlFileWrite.flush();
                        ScanCtrlFileWrite.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    scanDecode.stopScan();
                }

                mTimesScan = false;
                mTimes.setText(getString(R.string.start_times));
            } else {
                handler.removeCallbacks(startTask);
                handler.postDelayed(startTask, 0);
                mTimesScan = true;
                mTimes.setText(getString(R.string.stop_times));

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
        mSingle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
                    if (mTimesScan) {
                        mTimes.performClick();
                    } else {
                        handler.removeCallbacks(startTask);

                        if ("N43".equals(SystemProperties.get("persist.sys.scanheadtype"))) {
                            try {
                                File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
                                ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
                                ScanCtrlFileWrite.write("trigoff");
                                ScanCtrlFileWrite.flush();
                                ScanCtrlFileWrite.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            scanDecode.stopScan();
                        }

                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
                    if ("N43".equals(SystemProperties.get("persist.sys.scanheadtype"))) {
                        startScan();
                    } else {
                        scanDecode.starScan();
                    }
                    break;
                }
                default:
                    break;
            }
            return false;
        });
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void getBarcode(String data) {
                if (recyclerView.getBackground() != null) {
                    recyclerView.setBackground(null);
                }
                scancount += 1;
                tvcound.setText(getString(R.string.scan_time) + scancount + "");
                mList.add(data);
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {
            }
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
     * 连续扫描
     * Continuous scan
     */
    private Runnable startTask = new Runnable() {
        @Override
        public void run() {
            if ("N43".equals(SystemProperties.get("persist.sys.scanheadtype"))) {

                try {
                    File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
                    ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
                    ScanCtrlFileWrite.write("trigoff");
                    ScanCtrlFileWrite.flush();
                    ScanCtrlFileWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SystemClock.sleep(20L);
                startScan();
            } else {
                scanDecode.starScan();
            }
            handler.postDelayed(startTask, (int) SpUtils.get(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, 2000));
            mTimesScan = true;
        }
    };

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mTimesScan = false;
        if ("N43".equals(SystemProperties.get("persist.sys.scanheadtype"))) {
            try {
                File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
                ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
                ScanCtrlFileWrite.write("trigoff");
                ScanCtrlFileWrite.flush();
                ScanCtrlFileWrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            scanDecode.stopScan();
        }
        handler.removeCallbacks(startTask);
        handler.removeCallbacks(mStopN43Scan);
        scanDecode.onDestroy();
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WeightEvent event) {
        switch (event.getMessage()) {
            case "explore":
                outPutFile();
                break;
            default:
                break;
        }
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


    private void sendBroadcasts(String s) {
        Intent intent = new Intent();
        intent.setAction(s);
        this.sendBroadcast(intent);
    }

    private void startScan() {

        handler.removeCallbacks(mStopN43Scan);
        android.os.SystemProperties.set("persist.sys.startscan", "true");
        try {
            File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
            ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
            ScanCtrlFileWrite.write("on");
            ScanCtrlFileWrite.flush();
            ScanCtrlFileWrite.write("trig");
            ScanCtrlFileWrite.flush();
            ScanCtrlFileWrite.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.postDelayed(mStopN43Scan, 10000);
    }

    //added by zyj N43
    private final Runnable mStopN43Scan = new Runnable() {
        @Override
        public void run() {
            Slog.i("N43", "mStopN43Scan");
            try {
                android.os.SystemProperties.set("persist.sys.startscan", "false");
                File ScanDeviceName = new File("/sys/class/misc/uartscan/trigger");
                ScanCtrlFileWrite = new BufferedWriter(new FileWriter(ScanDeviceName, false));
                ScanCtrlFileWrite.write("trigoff");
                ScanCtrlFileWrite.flush();
                ScanCtrlFileWrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    //end

}
