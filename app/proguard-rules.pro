# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepattributes *JavascriptInterface*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
#-ignorewarnings
-dontwarn com.tencent.**
-dontwarn com.lecloud.**
-dontwarn com.letv.adlib.**
-dontwarn com.xiaomi.**
-dontwarn com.ut.mini.**
-dontwarn com.umeng.message.**
-dontwarn com.le.video.camcorder.**
-dontwarn com.le.utils.format.**
-dontwarn com.letv.ads.plugin.**
-dontwarn org.cmf.**
-dontwarn com.letv.lepaysdk.**
-dontwarn pl.droidsonroids.gif.**
-dontwarn com.letv.android.client.cp.sdk.videoview.**
-dontwarn com.letv.plugin.pluginloader.**
-keep class com.tencent.** { *; }
-keep class com.sina.** { *; }
-keep class android.support.** { *; }
-keep class android.volley.** { *; }
-keep class com.handmark.pulltorefresh.library.** { *; }
-keep class com.summerxia.dateselector.** { *; }
-keep class com.viewpagerindicator.** { *; }
-keep class org.xutils.** { *; }
-keep class com.google.gson.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class com.letv.autoapk.dao.** { *; }
-keep class master.flame.danmaku.** { *; }
-keep class tv.cjump.jni.** { *; }
-keep class org.java_websocket.** { *; }
-keep class pl.droidsonroids.gif.** { *; }
#######################################################pay
-keep class com.alipay.** { *; }
-keep class com.letv.lepaysdk.** { *; }
#######################################################recorder
-keep class com.letv.recorder.** { *; }
-keep class com.le.utils.gles.** { *; }
-keep class com.le.filter.gles.**{ *; }
-keep class com.le.utils.common.**{ *;}
-keep class com.le.utils.format.**{*;}
-keep class com.le.share.streaming.**{*;}
#######################################################umeng
-keep class u.aly.** { *; }
-keep class com.umeng.** { *; }
-keep class com.ta.utdid2.** { *; }
-keep class com.ut.device.** { *; }
-keep class org.android.** { *; }
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.letv.autoapk.R$*{
 public static final int *;
}
-keep class com.umeng.message.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.protobuffer.* {
        public <fields>;
        public <methods>;
}

-keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.local.* {
        public <fields>;
        public <methods>;
}
-keep class org.android.agoo.impl.*{
        public <fields>;
        public <methods>;
}



-keep class org.android.agoo.service.* {*;}
-keep class org.android.spdy.**{*;}

##################################################lecloudsdk
-keepclasseswithmembernames class * {
    native <methods>;
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
	public static int v(...);
	public static int i(...);
	public static int w(...);
	public static int d(...);
	public static int e(...);
}

-keep class android.support.v4.app.NotificationCompat**{
    public *;
}

-keep class com.lecloud.sdk.http.** { *;}


-dontwarn com.lecloud.sdk.api.ad.entity.AdElementInfo
-keep class com.lecloud.sdk.api.ad.entity.AdElementInfo { *;}

#-dontwarn com.lecloud.sdk.api.ad.impl.LeTvAd
#-keep class com.lecloud.sdk.api.ad.impl.LeTvAd { *;}

-dontwarn com.lecloud.sdk.player.IPlayer
-keep class com.lecloud.sdk.player.IPlayer { *;}

-dontwarn com.lecloud.sdk.api.md.entity.action.**
-keep class com.lecloud.sdk.api.md.entity.action.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.live.**
-keep class com.lecloud.sdk.api.md.entity.live.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.cloud.**
-keep class com.lecloud.sdk.api.md.entity.vod.cloud.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.saas.**
-keep class com.lecloud.sdk.api.md.entity.vod.saas.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.VideoHolder
-keep class com.lecloud.sdk.api.md.entity.vod.VideoHolder { *;}

-dontwarn com.lecloud.sdk.api.md.IActionMediaData
-keep class com.lecloud.sdk.api.md.IActionMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.ILiveMediaData
-keep class com.lecloud.sdk.api.md.ILiveMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.IMediaData
-keep class com.lecloud.sdk.api.md.IMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.IVodMediaData
-keep class com.lecloud.sdk.api.md.IVodMediaData { *;}

-dontwarn com.lecloud.sdk.utils.LeLog
-keep class com.lecloud.sdk.utils.LeLog{ *;}

-dontwarn com.lecloud.sdk.utils.LeLog.LeLogMode
-keep class com.lecloud.sdk.utils.LeLog.LeLogMode { *;}

-dontwarn com.lecloud.sdk.videoview.**
-keep class com.lecloud.sdk.videoview.** { *;}

-dontwarn com.lecloud.sdk.player.live.**
-keep class com.lecloud.sdk.player.live.** { *;}

-dontwarn com.lecloud.sdk.player.vod.**
-keep class com.lecloud.sdk.player.vod.** { *;}

-dontwarn com.lecloud.sdk.listener.**
-keep class com.lecloud.sdk.listener.** { *;}

-dontwarn com.lecloud.sdk.api.ad.entity.**
-keep class com.lecloud.sdk.api.ad.entity.** { *;}

-dontwarn com.lecloud.sdk.api.ad.IAd
-keep class com.lecloud.sdk.api.ad.IAd { *;}

-dontwarn com.lecloud.sdk.api.ad.IAdContext
-keep class com.lecloud.sdk.api.ad.IAdContext { *;}

-dontwarn com.lecloud.sdk.api.ad.ILeTvAd
-keep class com.lecloud.sdk.api.ad.ILeTvAd { *;}

-dontwarn com.lecloud.sdk.api.ad.ILeTvAdContext
-keep class com.lecloud.sdk.api.ad.ILeTvAdContext { *;}

-dontwarn com.lecloud.sdk.api.stats.IPlayAction
-keep class com.lecloud.sdk.api.stats.IPlayAction { *;}

-dontwarn com.lecloud.sdk.api.stats.IStats
-keep class com.lecloud.sdk.api.stats.IStats { *;}

-dontwarn com.lecloud.sdk.api.stats.IStatsContext
-keep class com.lecloud.sdk.api.stats.IStatsContext { *;}

-dontwarn com.lecloud.sdk.api.cde.**
-keep class com.lecloud.sdk.api.cde.** { *;}

-dontwarn com.lecloud.sdk.api.feedback.IFeedBackListener
-keep class com.lecloud.sdk.api.feedback.IFeedBackListener { *;}

-dontwarn com.lecloud.sdk.api.feedback.LeFeedBack
-keep class com.lecloud.sdk.api.feedback.LeFeedBack { *;}

-dontwarn com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener
-keep class com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener { *;}


-dontwarn com.lecloud.sdk.api.timeshift.ItimeShiftListener
-keep class com.lecloud.sdk.api.timeshift.ItimeShiftListener { *;}

-dontwarn com.lecloud.sdk.api.status.ActionStatus
-keep class com.lecloud.sdk.api.status.ActionStatus { *;}

-dontwarn com.lecloud.sdk.api.status.ActionStatusListener
-keep class com.lecloud.sdk.api.status.ActionStatusListener { *;}

-dontwarn com.lecloud.sdk.constant.**
-keep class com.lecloud.sdk.constant.** { *;}

-dontwarn com.lecloud.sdk.download.control.**
-keep class com.lecloud.sdk.download.control.** { *;}

-dontwarn com.lecloud.sdk.download.info.LeDownloadInfo
-keep class com.lecloud.sdk.download.info.LeDownloadInfo { *;}

-dontwarn com.lecloud.sdk.download.observer.LeDownloadObserver
-keep class com.lecloud.sdk.download.observer.LeDownloadObserver { *;}

-dontwarn com.lecloud.sdk.config.LeCloudPlayerConfig
-keep class com.lecloud.sdk.config.LeCloudPlayerConfig { *;}

-dontwarn com.lecloud.sdk.download.plugin.**
-keep class com.lecloud.sdk.download.plugin.** { *;}

-dontwarn com.lecloud.sdk.surfaceview.**
-keep class com.lecloud.sdk.surfaceview.** { *;}

-dontwarn com.lecloud.sdk.download.control.DownloadCenter
-keep class com.lecloud.sdk.download.control.DownloadCenter { *;}

-dontwarn com.lecloud.sdk.download.control.BaseDownloadCenter
-keep class com.lecloud.sdk.download.control.BaseDownloadCenter { *;}


-dontwarn com.lecloud.cp.sdk.api.md.entity.**
-keep class com.lecloud.cp.sdk.api.md.entity.** { *;}


-keep class cn.mmachina.** { *; }
-keep class com.letv.adlib.** { *; }
-keep public class com.letv.ads.** { *; }
-keep public class com.letv.plugin.pluginloader.**{ *;}
-keep class com.letvcloud.cmf.** { *; }
-keep class android.net.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class org.apache.commons.** { *; }
-keep class com.lecloud.xutils.** { *; }

-keep class * implements android.os.Parcelable { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class android.os.SystemProperties
-keepclassmembers class android.os.SystemProperties{
    public <fields>;
    public <methods>;
}
-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}

-keep class * implements java.io.Serializable { *; }

-keep class android.app.IServiceConnection { *;}

-keep class * implements android.os.IInterface { *;}

-keep class android.util.Singleton { *;}

#youmeng push
-keep class android.util.Singleton { *;}
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}