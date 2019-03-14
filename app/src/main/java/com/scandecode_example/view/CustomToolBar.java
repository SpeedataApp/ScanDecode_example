package com.scandecode_example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scandecode_example.R;


/**
 * @author xuyan  title栏
 */
public class CustomToolBar extends RelativeLayout implements View.OnClickListener {

    private ImageView imageSetting;
    private TextView tvClear;
    private BtnClickListener listener;

    public CustomToolBar(Context context) {
        super(context);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化组件
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.titlebar, this);
        imageSetting = findViewById(R.id.title_settings);
        imageSetting.setOnClickListener(this);
        tvClear = findViewById(R.id.title_clear);
        tvClear.setOnClickListener(this);
    }

    public void setTitleBarListener(BtnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 按钮点击接口
     */
    public interface BtnClickListener {
        void clearClick();
        void settingClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_clear:
                listener.clearClick();
                break;
            case R.id.title_settings:
                listener.settingClick();
                break;
            default:
                break;
        }
    }

}