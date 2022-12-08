package com.scandecode_example.receiver;

import static com.scandecode_example.SpdConstant.DEF_start;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * @author :Reginer in  2019/9/25 14:24.
 * 联系方式:QQ:282921012
 * 功能描述:开机广播
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!TextUtils.equals(ACTION, intent.getAction())) {
            return;
        }

        SystemProperties.set(DEF_start, "true");

    }

    /**
     * 判断某个服务是否正在运行的方法
     * <p>
     * <p>
     * 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     *
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isWorked(Context context, String name) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert myManager != null;
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (name.equals(runningService.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
