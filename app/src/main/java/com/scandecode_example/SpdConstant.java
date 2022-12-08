package com.scandecode_example;


/**
 * @author xuyan  constant
 */
public class SpdConstant {
    /**
     * 使能扫描（通用系统参数）
     * Enable scanning (general system parameters)
     */
    static final String SCAN_KEY_REPORT = "persist.sys.keyreport";


    /**
     * 记录当前扫描间隔
     * Record the current scan interval
     */
    public static final String INTERVAL_LEVEL = "INTERVAL_LEVEL";


    /**
     * 默认路径 前两个是初始化上电，第三个是扫描上下电
     */
    public static final String DEF_direction = "/sys/class/gpio/gpio198/direction";
    public static final String DEF_value = "/sys/class/gpio/gpio198/value";

    public static final String DEF_direction_init = "/sys/class/gpio/gpio123/direction";
    public static final String DEF_value_init = "/sys/class/gpio/gpio123/value";

    public static final String DEF_direction_scan = "/sys/class/gpio/gpio244/direction";
    public static final String DEF_value_scan = "/sys/class/gpio/gpio244/value";

    public static final String DEF_start = "start.barcode.first";

}
