package com.letv.autoapk.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.utils.Logger;

public class VipPackageRequest2 extends StringDataRequest {

	public VipPackageRequest2(Context context) {
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

	private SparseArray<String> vipPic;

	SparseArray<String> getVipPic() {
		return vipPic;
	}

	private void parseList(JSONArray jsonArray, List<List<PackageInfo>> plist) {
		if(jsonArray== null)
			return;
		List<PackageInfo> list = new ArrayList<PackageInfo>();
		PackageInfo info = null;
		JSONObject obj = null;
		try {
			
			for (int i = 0; i < jsonArray.length(); ++i) {
				info = new PackageInfo();
				obj = (JSONObject) jsonArray.get(i);
				// setinfo
				info.name = obj.getString("name");
				info.pid = obj.getString("id");
				info.price = obj.getString("price");
				info.duration = obj.getString("durationName");
				info.icon = obj.optString("pic");
				info.lever = obj.optInt("level");
				list.add(info);
			}
			plist.add(list);
		} catch (JSONException e) {
			Logger.log(e);
		}
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		// TODO Auto-generated method stub
		List<List<PackageInfo>> list = (List<List<PackageInfo>>) outputData[0];

		if (statusCode == 0) {
			JSONObject json = new JSONObject(content);
			JSONArray array = (JSONArray) json.opt("data");

			if (array == null)
				return statusCode;
			vipPic = new SparseArray<String>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				parseList(object.getJSONArray("data"), list);
				vipPic.put(i, object.getString("vipPic"));
			}

		}
		return statusCode;
	}

}
