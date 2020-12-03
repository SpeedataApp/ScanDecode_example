# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\lenovo-pc\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class android.os.SystemProperties { *; }
-keep class android.annotation.SuppressLint { *; }
-keep class android.support.v7.app.AppCompatActivity { *; }
-keep class org.greenrobot.eventbus.Subscribe { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View



-keepattributes *Annotation*

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Dont warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.

-dontwarn android.support.**



-dontoptimize
-optimizationpasses 5  #代码压缩等级
-dontusemixedcaseclassnames #是否大小写混合
-dontskipnonpubliclibraryclasses
-dontpreverify      #混淆时是否做预校验
-ignorewarnings #这1句是屏蔽警告，脚本中把这行注释去掉
-verbose         #混淆时是否记录日志
-dontshrink


#------------------  android平台自带的排除项       ----------------

-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.scandecode_example.AppDecode{
                                                                                                       *;
                                                                                                       }
-keep public class com.scandecode_example.SpdConstant{
                                                                                                       *;
                                                                                                       }
-keep public class com.scandecode_example.FirstActivity{
                                                *;
                                                }
-keep public class com.scandecode_example.MainActivity{*;}
-keep public class com.scandecode_example.ScanActivity{*;}
-keep public class com.scandecode_example.utils.FileUtils{*;}
-keep public class com.scandecode_example.utils.SpUtils{*;}
-keep public class com.scandecode_example.utils.ToastUtils{*;}
-keep public class com.scandecode_example.model.WeightEvent{*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepattributes *Annotation*

-keepclassmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class *{
	native <methods>;
}



#------------------  共性的排除项目         ----------------
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除



-keep class **JNI* {*;}
#-keep class com.alibaba.fastjson.** { *; }
-dontwarn xyz.reginer.http.**


#okhttp

-dontwarn okhttp3.**


#okio

-dontwarn okio.**


##---------------Begin: proguard configuration for Gson ----------


-keepattributes Signature
-keepattributes *Annotation*


##---------------End: proguard configuration for Gson ----------


# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
