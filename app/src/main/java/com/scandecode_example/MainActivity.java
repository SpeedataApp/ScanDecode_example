package com.scandecode_example;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mReception;
    private TextView tvcound;
    private Button btnSingleScan, btnClear, btnTouch;
    private ToggleButton toggleButtonRepeat,toggleButtonSound,toggleButtonVibrate;
    private boolean isFlag = false;
    private int scancount = 0;
    private ScanInterface scanDecode;

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务
        btnSingleScan =  findViewById (R.id.buttonscan);
        btnClear =  findViewById (R.id.buttonclear);
        toggleButtonRepeat =  findViewById(R.id.button_repeat);
        mReception =  findViewById (R.id.EditTextReception);
//        btnStop = (Button) findViewById(R.id.buttonstop);
//        btnStop.setOnClickListener(this);
        tvcound =  findViewById (R.id.tv_cound);
        btnClear.setOnClickListener(this);
        btnTouch =  findViewById (R.id.buttonscan);
        toggleButtonSound = findViewById (R.id.butSound);
        toggleButtonVibrate = findViewById (R.id.butVibrate);

        btnTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:{
                        scanDecode.stopScan();//停止扫描
                        handler.removeCallbacks(startTask);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:{
                        scanDecode.starScan();//启动扫描
                        break;
                    }

                    default:
                        break;
                }
                return false;
            }
        });

        toggleButtonRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    scancount = 0;
                    handler.removeCallbacks(startTask);
                    handler.postDelayed(startTask, 0);
                } else {
                    handler.removeCallbacks(startTask);
                    scanDecode.stopScan();
                }
            }
        });

        if ("true".equals(SystemProperties.get("persist.sys.playscanmusic"))) {
            toggleButtonSound.setChecked(true);
        }

        toggleButtonSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    SystemProperties.set("persist.sys.playscanmusic","true");
                } else {
                    SystemProperties.set("persist.sys.playscanmusic","false");
                }
            }
        });

        if ("true".equals(SystemProperties.get("persist.sys.scanvibrate"))) {
            toggleButtonVibrate.setChecked(true);
        }

        toggleButtonVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    SystemProperties.set("persist.sys.scanvibrate","true");
                }
                else {
                    SystemProperties.set("persist.sys.scanvibrate","false");
                }

            }
        });

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {
                scancount+=1;
                tvcound.setText(getString(R.string.scan_time)+scancount+"");
                mReception.append(data+"\n");
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {
                //返回原始解码数据
//                scancount+=1;
//                tvcound.setText(getString(R.string.scan_time)+scancount+"");
//                mReception.append(DataConversionUtils.byteArrayToString(bytes) +"\n");
            }
        });
    }
    Handler handler = new Handler();

    //连续扫描
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
            case R.id.buttonclear:
                mReception.setText(""); //清屏
                scancount=0;
                tvcound.setText(getString(R.string.scan_time)+scancount+"");
                break;
            case R.id.buttonscan:
                scanDecode.starScan();//启动扫描
                break;
//            case R.id.buttonstop:
//                scanDecode.stopScan();//停止扫描
//                handler.removeCallbacks(startTask);
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanDecode.onDestroy();//回复初始状态
    }
}
