package com.scandecode_example;

/**
 * @author :Reginer in  2018/1/19 15:26.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class SpdConstant {
    static final int KT45Q = 0;
    static final int KT55 = 1;
    static final String KT45Q_S = "kt45q";
    static final String KT55_S = "kt55";
    static final String MACHINE_CODE = "persist.sys.machine_code";
    /**
     * 状态栏
     */
    static final String STATUS_BAR = "persist.sys.enableupmenu";
    /**
     * 安装apk
     */
    static final String INSTALL_MANAGER = "persist.sys.install_manager";
    /**
     * 触摸解锁
     */
    static final String SCREEN_LOCK = "persist.sys.spd_lock";
    /**
     * 任意键唤醒
     */
    static final String ANY_KEY_WAKE_UP = "persist.sys.enablekeywake";
    /**
     * 屏幕可触摸
     */
    static final String TOUCH_SCREEN = "persist.sys.enabletouch";
    /**
     * 浏览器
     */
    static final String BROWSER = "persist.sys.enablebrowser";
    /**
     * home键
     */
    static final String HOME_KEY = "persist.sys.enablehome";
    /**
     * 返回键
     */
    static final String BACK_KEY = "persist.sys.enableback";
    /**
     * 菜单键
     */
    static final String MENU_KEY = "persist.sys.enablerecent";
    /**
     * 物理按键音
     */
    static final String KEY_DOWN_T_ONE = "persist.sys.KeydownTone";
    /**
     * 扫描超时
     */
    static final String SCAN_TIME_OUT = "persist.sys.SCAN_TIME_OUT";
    /**
     * 是否正在扫描
     */
    static final String IS_SCAN = "persist.sys.startscan";
    /**
     * 开始扫描
     */
    static final String START_SCAN_ACTION = "com.geomobile.se4500barcode";
    /**
     * 停止扫描
     */
    static final String STOP_SCAN = "com.geomobile.se4500barcodestop";
    /**
     * 扫描模式
     */
    static final String SCAN_MODE = "persist.sys.scanmode";
    /**
     * 使能扫描(无效)
     */
    static final String SCAN_KEY_DISABLE = "persist.sys.scankeydisable";

    /**
     * 使能扫描（通用系统参数）
     */
    static final String SCAN_KEY_REPORT = "persist.sys.keyreport";

    /**
     * 自定义屏蔽物理按键
     */
     static final String ENABLE_CUSTOM = "persist.sys.EnableCustom";
    /**
     * 扫描前修改为false
     */
    static final String SCAN_STOP_IMME = "persist.sys.scanstopimme";

    /**
     * 全部物理按键
     */
    static final String DISABLE_KEYPAD = "persist.sys.disable_keypad";
}
