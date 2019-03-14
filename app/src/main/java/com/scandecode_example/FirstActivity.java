package com.scandecode_example;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.scandecode_example.utils.ToastUtils;

import static com.scandecode_example.SpdConstant.SCAN_KEY_REPORT;

/**
 * @author xuyan  首页面跳转，检测是否能扫描
 */
public class FirstActivity extends AppCompatActivity {

    private Button mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initView();
    }

    private void initView() {

        mSetting = findViewById(R.id.btn_set);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToastSafe(R.string.restart);
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                finish();
            }
        });


        if (("true".equals(SystemProperties.get(SCAN_KEY_REPORT, "false")))
               // && ("true".equals(SystemProperties.get(SCAN_KEY_DISABLE, "false")))
        ) {
            startActivity(new Intent(FirstActivity.this, ScanActivity.class));
            finish();
        }
    }


}
