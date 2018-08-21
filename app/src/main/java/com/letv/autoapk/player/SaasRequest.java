package com.letv.autoapk.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.utils.Md5Utils;
import com.letv.android.client.cp.sdk.api.md.request.CPVodRequest;
import com.letv.autoapk.common.utils.Logger;

public class SaasRequest extends CPVodRequest {
	public SaasRequest() {
		super();
	}

	public final static String SAAS_USERID = "saasuserid";
	public final static String SAAS_UTOKEN = "saasutoken";

	
//	public Map<String, String> buildUrlParameter() {
//		Map<String, String> variables = super.buildUrlParameter();
//		variables.put("userId", mediaDataParams.getString(SAAS_USERID));
//		String token = mediaDataParams.getString(SAAS_UTOKEN, "");
//		variables.put("utoken", token);
//		variables
//				.put("pu", mediaDataParams.getString(PlayerParams.KEY_PLAY_PU));
//		return variables;
//	}
	private String serverTimestemp2;
	@Override
	public void setServerTimestemp(String serverTimestemp) {
        this.serverTimestemp2 = serverTimestemp;
    }
	public Map<String, String> buildUrlParameter() {
        HashMap<String,String> variables = new HashMap<String,String>();
        variables.put("p", this.mediaDataParams.getString("businessline"));
        variables.put("cf", LeCloudPlayerConfig.getInstance().getCf());
        variables.put("uu", this.mediaDataParams.getString("uuid"));
        variables.put("vu", this.mediaDataParams.getString("vuid"));
        variables.put("format", "json");
        variables.put("pageurl", this.mContext.getPackageName());
        variables.put("pver", "SaasVod_v0.0.1-" + LeCloudPlayerConfig.getInstance().getAdType());
        variables.put("ran", this.serverTimestemp2);
        variables.put("ver", "SaasVod_v0.0.1");
        variables.put("pu", "6");
        variables.put("pet", "0");
        //saas
        variables.put("userId", mediaDataParams.getString(SAAS_USERID));
		String token = mediaDataParams.getString(SAAS_UTOKEN, "");
		variables.put("utoken", token);
        variables.put("sign", this.getMd5Sign(variables));
        return variables;
    }
	private String getMd5Sign(Map<String, String> params) {
        ArrayList<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer source = new StringBuffer();
        String cf = "";
        String vu = "";
        String plainText = "";

        try {
            Iterator<String> var8 = keys.iterator();

            while(var8.hasNext()) {
                String e = (String)var8.next();
                source.append((String)params.get(e));
                if(e.equals("cf")) {
                    cf = (String)params.get(e);
                }

                if(e.equals("vu")) {
                    vu = (String)params.get(e);
                }
            }

            plainText = Md5Utils.md5(cf + "04c5e1e616f668bc559af2afa98b9a25" + vu);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return Md5Utils.md5(source.toString() + "04c5e1e616f668bc559af2afa98b9a25" + plainText);
    }
	@Override
	public Object parseData(Object request) {
		try {
			return SaasVideo.fromJson(new JSONObject(String.valueOf(request)));
		} catch (JSONException e) {
			Logger.log(e);
		}
		return null;
	}

}
