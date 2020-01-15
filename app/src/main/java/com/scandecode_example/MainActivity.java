package com.scandecode_example;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

/**
 * @author xuyan  Original sample page, temporarily deprecated
 * Implement the main page for calling scan related functions
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mReception;
    private TextView tvcound;
    private Button btnSingleScan, btnClear, btnTouch;
    private ToggleButton toggleButtonRepeat, toggleButtonSound, toggleButtonVibrate;
    private boolean isFlag = false;
    private int scancount = 0;
    private ScanInterface scanDecode;

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanDecode = new ScanDecode(this);
        //初始化扫描服务
        //Initialize the scan service
        scanDecode.initService("true");
        btnSingleScan = findViewById(R.id.buttonscan);
        btnClear = findViewById(R.id.buttonclear);
        toggleButtonRepeat = findViewById(R.id.button_repeat);
        mReception = findViewById(R.id.EditTextReception);

        tvcound = findViewById(R.id.tv_cound);
        btnClear.setOnClickListener(this);
        btnTouch = findViewById(R.id.buttonscan);
        toggleButtonSound = findViewById(R.id.butSound);
        toggleButtonVibrate = findViewById(R.id.butVibrate);

        btnTouch.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                //停止扫描
                //Stop scanning
                case MotionEvent.ACTION_UP: {
                    if (toggleButtonRepeat.isChecked()) {
                        toggleButtonRepeat.performClick();
                    } else {
                        handler.removeCallbacks(startTask);
                        scanDecode.stopScan();
                    }
                    break;
                }
                //启动扫描
                //Start scan
                case MotionEvent.ACTION_DOWN: {
                    scanDecode.starScan();
                    break;
                }

                default:
                    break;
            }
            return false;
        });

        toggleButtonRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                handler.removeCallbacks(startTask);
                handler.postDelayed(startTask, 0);
            } else {
                handler.removeCallbacks(startTask);
                scanDecode.stopScan();
            }
        });

        //Beep
        if ("true".equals(SystemProperties.get("persist.sys.playscanmusic"))) {
            toggleButtonSound.setChecked(true);
        }

        toggleButtonSound.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                SystemProperties.set("persist.sys.playscanmusic", "true");
            } else {
                SystemProperties.set("persist.sys.playscanmusic", "false");
            }
        });

        //Vibrator
        if ("true".equals(SystemProperties.get("persist.sys.scanvibrate"))) {
            toggleButtonVibrate.setChecked(true);
        }

        toggleButtonVibrate.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                SystemProperties.set("persist.sys.scanvibrate", "true");
            } else {
                SystemProperties.set("persist.sys.scanvibrate", "false");
            }

        });

        //result
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {
                scancount += 1;
                tvcound.setText(getString(R.string.scan_time) + scancount + "");
                mReception.append(data + "\n");
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {
                //返回原始解码数据,无
                //Returns raw decoded data,not have
//                scancount+=1;
//                tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                mReception.append(DataConversionUtils.byteArrayToString(bytes) +"\n");
            }
        });
    }

    Handler handler = new Handler();

    //连续扫描
    //Continuous scan
    private Runnable startTask = new Runnable() {
        @Override
        public void run() {
            scanDecode.starScan();
            handler.postDelayed(startTask, 1000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //清屏
            //Clear screen
            case R.id.buttonclear:
                mReception.setText("");
                scancount = 0;
                tvcound.setText(getString(R.string.scan_time) + scancount + "");
                break;
            //启动扫描
            //Start scan
            case R.id.buttonscan:
                scanDecode.starScan();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //停止扫描
        //Stop scanning
        scanDecode.stopScan();
        handler.removeCallbacks(startTask);
        //回复初始状态
        //Return to initial state
        scanDecode.onDestroy();
        super.onDestroy();
    }

}
