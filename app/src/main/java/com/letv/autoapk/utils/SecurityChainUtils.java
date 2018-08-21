package com.letv.autoapk.utils;

import android.util.Log;

public class SecurityChainUtils {
    
    private static final String TAG = "SecurityChainUtils";

    /**
     * 直播防盗链
     * @param mpLiveDispatchInfo
     * @param reCountTime 是否需要重新计算时间
     * @return
     */
   /* public static String coverToSecurityUrl(String url,LivePlayerDispatchInfo mpLiveDispatchInfo,boolean reCountTime){
        
        String mountName = mpLiveDispatchInfo.mountName;
        String channel_name = mpLiveDispatchInfo.channel_name;
        long currentTime=0;
        if(reCountTime){
            currentTime= mpLiveDispatchInfo.currentTime;
        }else{
            currentTime = (System.currentTimeMillis() - mpLiveDispatchInfo.localTime) / 1000 + mpLiveDispatchInfo.currentTime;// 计算时间戳
        }
        
        // long currentTime = mLivePlayerDispatchInfo.currentTime;

        StringBuilder md5Params = new StringBuilder();
        md5Params.append("cztv");
        md5Params.append("/");
        md5Params.append(mountName);
        md5Params.append("/");
        md5Params.append(channel_name);
        md5Params.append(currentTime);

        Log.d(TAG, "[securityUrl] params:" + md5Params.toString());
        // String kParams = MD5Utls.stringToMD5(md5Params.toString());
        String kParams = MD5Utls.encodeByMD5(md5Params.toString());

        // String tParams=System.currentTimeMillis()/1000+"";
        String tParams = currentTime + "";

        StringBuilder finalParams = new StringBuilder();
        finalParams.append("?");
        finalParams.append("k=" + kParams);
        finalParams.append("&");
        finalParams.append("t=" + tParams);
        String tmp = url + finalParams.toString();
        Log.d(TAG, "[securityUrl] url:" + tmp);
        
        return tmp;
    }*/
 public static String coverToSecurityUrl(String url,String mountName,String channelName,long currentTime,long localTime,boolean reCountTime){
        
        String montname = mountName;
        String channel_name = channelName;
        long currenttime=0;
        if(reCountTime){
        	currenttime= currentTime;
        }else{
        	currenttime = (System.currentTimeMillis() - localTime) / 1000 + currentTime;// 计算时间戳
        }
        
        // long currentTime = mLivePlayerDispatchInfo.currentTime;

        StringBuilder md5Params = new StringBuilder();
        md5Params.append("cztv");
        md5Params.append("/");
        md5Params.append(montname);
        md5Params.append("/");
        md5Params.append(channel_name);
        md5Params.append(currenttime);

        // String kParams = MD5Utls.stringToMD5(md5Params.toString());
        String kParams = MD5Utls.encodeByMD5(md5Params.toString());

        // String tParams=System.currentTimeMillis()/1000+"";
        String tParams = currenttime + "";

        StringBuilder finalParams = new StringBuilder();
        finalParams.append("?");
        finalParams.append("k=" + kParams);
        finalParams.append("&");
        finalParams.append("t=" + tParams);
        String tmp = url + finalParams.toString();
        
        return tmp;
    }
}
