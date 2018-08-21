package com.letv.autoapk.boss;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class VipPackageRequest extends StringDataRequest {

	public VipPackageRequest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "/vipPackage";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		// TODO Auto-generated method stub

	}

	private String vipPic;
	String getVipPic(){
		return vipPic;
	}
	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		// TODO Auto-generated method stub
		List<PackageInfo> list = (List<PackageInfo>)outputData[0];
		PackageInfo info = null;
		if (statusCode == 0) {
			JSONObject json = new JSONObject(content);
			JSONArray jsonArray = (JSONArray) json.opt("data");
			vipPic = json.getString("vipPic");
			if(jsonArray==null)
				return statusCode;
			JSONObject obj = null;
			for (int i = 0; i < jsonArray.length(); ++i) {
				info = new PackageInfo();
				obj = (JSONObject) jsonArray.get(i);
				//setinfo
				info.name = obj.getString("name");
				info.pid  = obj.getString("id");
				info.price = obj.getString("price");
				info.duration = obj.getString("durationName");
				info.icon = obj.optString("pic");
				list.add(info);
			}
		}
		return statusCode;
	}

}
