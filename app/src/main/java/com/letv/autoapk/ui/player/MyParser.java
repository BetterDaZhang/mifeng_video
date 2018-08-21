package com.letv.autoapk.ui.player;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.common.utils.Logger;

import android.R.integer;
import android.graphics.Color;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.android.JSONSource;

public class MyParser extends BaseDanmakuParser {

	@Override
	protected IDanmakus parse() {
		if (mDataSource != null && mDataSource instanceof JSONSource) {
            JSONSource jsonSource = (JSONSource) mDataSource;
            return doParse(jsonSource.data());
        }
        return new Danmakus();
	}
	/**
     * @param danmakuListData 弹幕数据
     *                        传入的数组内包含普通弹幕，会员弹幕，锁定弹幕。
     * @return 转换后的Danmakus
     */
    private Danmakus doParse(JSONArray danmakuListData) {
        Danmakus danmakus = new Danmakus();
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus;
        }
        for (int i = 0; i < danmakuListData.length(); i++) {
            try {
                JSONObject danmakuArray = danmakuListData.getJSONObject(i);
                if (danmakuArray != null) {
                    danmakus = _parse(danmakuArray, danmakus,colors[i%4]);
                }
            } catch (JSONException e) {
            	Logger.log(e);
            }
        }
        return danmakus;
    }
    public static int getRandomColor(){
    	return colors[new Random().nextInt(4)];
    }
    private static int colors[] = {Color.parseColor("#ff7abe"),Color.parseColor("#f00079"),Color.parseColor("#ff9400"),Color.parseColor("#06dfff")};
    private Danmakus _parse(JSONObject jsonObject, Danmakus danmakus,int color) {
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonObject == null || jsonObject.length() == 0) {
            return danmakus;
        }
            try {
                JSONObject obj = jsonObject;
                BaseDanmaku item = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, mDisp);
                long time = jsonObject.getLong("htime");
                if (item != null) {
                    item.time = time;
                    item.textSize = 28f * (mDispDensity - 0.6f);
                    item.textColor = color;
                    item.textShadowColor = item.textColor <= Color.BLACK ? Color.WHITE : Color.BLACK;
                    DanmakuFactory.fillText(item, obj.getString("content"));
                    item.index = 1;
                    item.setTimer(mTimer);
                    danmakus.addItem(item);
                }
            } catch (JSONException e) {
            	Logger.log(e);
            } catch (NumberFormatException e) {
            	Logger.log(e);
            }
        return danmakus;
    }
}
