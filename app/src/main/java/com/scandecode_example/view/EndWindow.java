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

import com.scandecode_example.AppDecode;
import com.scandecode_example.R;
import com.scandecode_example.SpdConstant;
import com.scandecode_example.model.WeightEvent;
import com.scandecode_example.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * @author xuyan
 */
public class EndWindow extends PopupWindow {


    @SuppressLint({"NewApi", "SetTextI18n"})
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
        TextView mInterval = popupView.findViewById(R.id.interval_set);
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


        //扫描间隔
        int level1 = (int) SpUtils.get(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, 2000);
        mInterval.setText(AppDecode.getInstance().getString(R.string.change) + level1);
        mInterval.setOnClickListener(v -> {
            //直接1-5之间轮，默认为2

            int level = (int) SpUtils.get(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, 2000) + 1000;

            if (level >= 6000) {
                level = 1000;
            }

            SpUtils.put(AppDecode.getInstance(), SpdConstant.INTERVAL_LEVEL, level);
            mInterval.setText(AppDecode.getInstance().getString(R.string.change) + level);

        });

        //导出
        mExit.setOnClickListener(v -> {
            dismiss();
            EventBus.getDefault().postSticky(new WeightEvent("explore", ""));

        });

        popupView.setOnClickListener(v -> dismiss());

    }


}
