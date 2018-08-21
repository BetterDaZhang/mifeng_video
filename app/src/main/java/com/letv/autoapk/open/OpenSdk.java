package com.letv.autoapk.open;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xutils.x;
import org.xutils.common.task.Priority;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

public class OpenSdk {

	// qq
	private Tencent mTencent;
	// blog
	private AuthInfo mAuthInfo;
	// wx
	private IWXAPI api;

	public static final int TYPE_QQ = 2;
	public static final int TYPE_ZONE = 5;
	public static final int TYPE_BLOG = 1;
	public static final int TYPE_MM = 3;
	public static final int TYPE_MM_TIMELINE = 4;

	private Context context;
	private boolean hasOpenId = false;

	public String QQID = null;
	public String BLOGID = null;
	public String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	public String WXID = null;
	public String WXKEY = null;
	public final static String WXGETTOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%1$s&secret=%2$s&code=%3$s&grant_type=authorization_code";
	public final static String WXGETUSER = "https://api.weixin.qq.com/sns/userinfo?access_token=%1$s&openid=%2$s";
	public final static String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%1$s&grant_type=refresh_token&refresh_token=%2$s";
	private boolean ismmreg = false;

	
	public OpenSdk(Context context) {
		this.context = context;
		parseIdasync();
	}
	private OpenItem parseItem(XmlPullParser xmlPullParser)
			throws XmlPullParserException, NumberFormatException, IOException {
		int eventType = xmlPullParser.getEventType();
		int depth = xmlPullParser.getDepth();
		OpenItem item = new OpenItem();
		while (!(eventType == XmlPullParser.END_TAG && xmlPullParser.getDepth() == depth)) {
			String nodeName = xmlPullParser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("id".equals(nodeName)) {
					item.id = xmlPullParser.nextText();
				}else if("redirecturl".equals(nodeName)){
					item.url = xmlPullParser.nextText();
				}
				else if("name".equals(nodeName)){
					item.name = xmlPullParser.nextText();
				}
				else if("key".equals(nodeName)){
					item.key = xmlPullParser.nextText();
				}
				break;
			default:
				break;
			}
			eventType = xmlPullParser.next();
		}
		return item;
	}
	
	private void parseIdasync() {
		x.task().run(new Runnable() {
			
			@Override
			public void run() {
				XmlPullParser xmlPullParser = null;
				try {
					xmlPullParser = Xml.newPullParser();
					xmlPullParser.setInput(context.getAssets().open("opensdk.xml"), "utf-8");
					int eventType = xmlPullParser.getEventType(); 
					while  (eventType != XmlPullParser.END_DOCUMENT) {  
		                String nodeName = xmlPullParser.getName(); 
//		                if(nodeName!=null){
//		                	Logger.logE(TAG, nodeName);
//		                }
		                switch (eventType) {
						case XmlPullParser.START_TAG:
							if("item".equals(nodeName)){
								OpenItem item = parseItem(xmlPullParser);
								if(item.name!=null&&item.id!=null){
									if("qq".equals(item.name)){
										QQID = item.id;
									}else if("mm".equals(item.name)){
										WXID = item.id;
										WXKEY = item.key;
									}else if("blog".equals(item.name)){
										BLOGID = item.id;
										if(!TextUtils.isEmpty(item.url)){
											REDIRECT_URL = item.url;
										}
										
									}
									hasOpenId = true;	
								}
								
							}
							break;
						default:
							break;
						}
		                eventType=xmlPullParser.next();
		            }  
					
				} catch (XmlPullParserException e) {
					Logger.log(e);
				} catch (IOException e) {
					Logger.log(e);
				}catch(Exception e){
					Logger.log(e);
				}
				
			}
		});
		
	}

	
	public boolean hasOpenId() {
		return hasOpenId;
	}

	public boolean hasQQ(){
		return QQID!=null;
	}
	public boolean hasBLOG(){
		return BLOGID!=null;
	}
	public boolean hasMM(){
		return WXID!=null;
	}
    public void logout(String logintype){
    	if("qq".equals(logintype)){
    		if(mTencent == null)
    			mTencent = (Tencent)getOpenObject(OpenSdk.TYPE_QQ);
    		mTencent.logout(context);
			mTencent = null;
		}
	}
	public Object getOpenObject(int type) {
		if (!hasOpenId)
			return null;
		if (type == TYPE_QQ || type == TYPE_ZONE) {
			synchronized (this) {
				if (mTencent == null) {
					try {
						mTencent = Tencent.createInstance(QQID, context);
					} catch (Exception e) {
						Logger.log(e);
					}
				}
			}
			return mTencent;
		}
		if (TYPE_BLOG == type) {
			if (mAuthInfo == null) {
				mAuthInfo = new AuthInfo(context, BLOGID, REDIRECT_URL, "email,direct_messages_read,direct_messages_write,"
			            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			            + "follow_app_official_microblog,");
			}
			return mAuthInfo;
		}
		if (TYPE_MM == type || TYPE_MM_TIMELINE == type) {
			if (api == null) {
				api = WXAPIFactory.createWXAPI(context, WXID, true);
				ismmreg = api.registerApp(WXID);

			}
			if (ismmreg == false) {
				ismmreg = api.registerApp(WXID);
			}
			return api;
		}
		return null;
	}
	
	class OpenItem{
		String id;
		String name;
		String key;
		String url;
	}
}
