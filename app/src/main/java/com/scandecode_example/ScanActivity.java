package com.scandecode_example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.scandecode_example.adapter.UnitAdapter;
import com.scandecode_example.model.DataBean;
import com.scandecode_example.model.WeightEvent;
import com.scandecode_example.utils.FileUtils;
import com.scandecode_example.utils.SpUtils;
import com.scandecode_example.utils.ToastUtils;
import com.scandecode_example.utils.excel.ExcelUtils;
import com.scandecode_example.view.EndWindow;
import com.speedata.utils.MyDateAndTime;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.format.Colour;

/**
 * @author xuyan  Example page to implement scan-related functions
 * 20230418  aimid
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
    private TextView tvAimid;
    private TextView tvAimidToType;
    private int scancount = 0;
    private ScanInterface scanDecode;

    private RecyclerView recyclerView;

    private DataBean dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();
    }

    @SuppressLint({"ClickableViewAccessibility", "NewApi", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void initView() {
        EventBus.getDefault().register(this);
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");
        mSingle = findViewById(R.id.btn_onetime);
        mTimes = findViewById(R.id.btn_times);
        tvcound = findViewById(R.id.tv_cound);
        tvAimid = findViewById(R.id.tv_aimid);
        tvAimidToType = findViewById(R.id.tv_aimidtotype);
        mTimesScan = false;
        mTimes.setOnClickListener(v -> {
            if (mTimesScan) {
                handler.removeCallbacks(startTask);
                handler.removeCallbacks(startScan);
                sendBroadcast(new Intent("com.geomobile.se4500barcodestop"));
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
            tvAimid.setText("");
            tvAimidToType.setText("");
        });
        mSingle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP: {
                    if (mTimesScan) {
                        mTimes.performClick();
                    } else {
                        handler.removeCallbacks(startTask);
                        handler.removeCallbacks(startScan);
                        sendBroadcast(new Intent("com.geomobile.se4500barcodestop"));
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
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void getBarcode(String data) {
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

            @SuppressLint("SetTextI18n")
            @Override
            public void getBarcodeAimid(String aimid) {
                tvAimid.setText("aimid:" + aimid);
                tvAimidToType.setText("条码类型:" + FileUtils.aimidToType(aimid));
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
    private final Runnable startTask = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent("com.geomobile.se4500barcodestop"));
            handler.postDelayed(startScan, 200);
            handler.postDelayed(startTask, (int) SpUtils.get(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, 2000));
            mTimesScan = true;
        }
    };

    /**
     * 开始扫描
     * start scan
     */
    private final Runnable startScan = () -> {
        sendBroadcast(new Intent("com.geomobile.se4500barcode"));

    };

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mTimesScan = false;
        sendBroadcast(new Intent("com.geomobile.se4500barcodestop"));
        handler.removeCallbacks(startTask);
        handler.removeCallbacks(startScan);
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

}
