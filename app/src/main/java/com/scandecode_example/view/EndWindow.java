package com.scandecode_example.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scandecode_example.R;
import com.scandecode_example.model.WeightEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author xuyan
 */
public class EndWindow extends PopupWindow {


    @SuppressLint("NewApi")
    public EndWindow(AppCompatActivity mContext) {

        Activity activity = mContext;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        setWidth(dm.widthPixels);
        setHeight(dm.heightPixels);

        View popupView = LayoutInflater.from(activity).inflate(R.layout.view_end_dialog_layout,
                new LinearLayout(activity), false);

        setContentView(popupView);

        TextView mCodeSet = popupView.findViewById(R.id.code_set);
        TextView mBalanceSet = popupView.findViewById(R.id.balance_set);
        TextView mExit = popupView.findViewById(R.id.exit_settings);

        if ("false".equals(SystemProperties.get("persist.sys.playscanmusic"))) {
            mCodeSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_off, null), null);
        } else {
            mCodeSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_no, null), null);
        }
        if ("false".equals(SystemProperties.get("persist.sys.scanvibrate"))) {
            mBalanceSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_off, null), null);
        } else {
            mBalanceSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_no, null), null);
        }

        //声音
        mCodeSet.setOnClickListener(v -> {
            if ("false".equals(SystemProperties.get("persist.sys.playscanmusic"))) {
                SystemProperties.set("persist.sys.playscanmusic", "true");
                mCodeSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_no, null), null);
            } else {
                SystemProperties.set("persist.sys.playscanmusic", "false");
                mCodeSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_off, null), null);
            }
        });

        //震动
        mBalanceSet.setOnClickListener(v -> {
            if ("false".equals(SystemProperties.get("persist.sys.scanvibrate"))) {
                SystemProperties.set("persist.sys.scanvibrate", "true");
                mBalanceSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_no, null), null);
            } else {
                SystemProperties.set("persist.sys.scanvibrate", "false");
                mBalanceSet.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.bt_off, null), null);
            }
        });

        //导出
        mExit.setOnClickListener(v -> {
            dismiss();
            EventBus.getDefault().postSticky(new WeightEvent("explore", ""));

        });

        popupView.setOnClickListener(v -> dismiss());

    }


}
