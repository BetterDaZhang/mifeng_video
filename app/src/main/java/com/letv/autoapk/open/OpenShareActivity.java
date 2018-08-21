package com.letv.autoapk.open;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class OpenShareActivity extends ContainerActivity implements
		IWeiboHandler.Response {
	protected Tencent mTencent;
	protected IWeiboShareAPI mWeiboShareAPI;
	protected IWXAPI api;
	public static final int THUMB_SIZE_W = 100;
	public static final int THUMB_SIZE_H = 70;
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (mWeiboShareAPI != null)
			mWeiboShareAPI.handleWeiboResponse(intent, this);
	}
	protected void init() {
		try {
			Intent intent = getIntent();
			if (intent != null && intent.hasExtra(FRAGMENTNAME)) {
				Bundle bundle = intent.getExtras();
				String name = bundle.getString(FRAGMENTNAME);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				Fragment current = Fragment.instantiate(this, name, bundle);
				ft.replace(R.id.container, current).commit();
			}else{
				if (mWeiboShareAPI == null){
					OpenSdk openSdk = MyApplication.getInstance().getOpenSdk();
					if (openSdk.hasBLOG()) {
						IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, openSdk.BLOGID);
						mWeiboShareAPI.handleWeiboResponse(intent, this);
						
					}
				}
					
				finish();
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		OpenSdk openSdk = MyApplication.getInstance().getOpenSdk();
		if (openSdk.hasBLOG()) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, openSdk.BLOGID);
			boolean isreg = mWeiboShareAPI.registerApp();
			
		}
		if (openSdk.hasMM())
			api = (IWXAPI) openSdk.getOpenObject(OpenSdk.TYPE_MM);
		if (openSdk.hasQQ())
			mTencent = (Tencent) openSdk.getOpenObject(OpenSdk.TYPE_QQ);
	}

	public boolean mmShare(String url,String title,String content,String image,Bitmap bitmap) {
		if (api.isWXAppInstalled()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = content;
			Bitmap thumb = Bitmap.createScaledBitmap(bitmap==null?BitmapFactory
					.decodeResource(getResources(), R.drawable.icon):bitmap,
					THUMB_SIZE_W, THUMB_SIZE_H, true);
//			Bitmap thumb = bitmap==null?BitmapFactory
//					.decodeResource(getResources(), R.drawable.icon):bitmap;
			msg.thumbData = OpenUtil.bmpToByteArray(thumb, true);
			if(thumb!=null&&!thumb.isRecycled()){
				thumb.recycle();
				thumb = null;
			}
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = OpenUtil.buildTransaction("img");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			boolean issent = api.sendReq(req);
			return issent;
		}else{
			showToastSafe(getString(R.string.noweichat), Toast.LENGTH_SHORT);
		}
		return false;
	}

	class MyImageListener implements ImageListener{
		String url;
		String title;
		String content;
		String image;
		int type;
		MyImageListener(int type,String url,String title,String content,String image){
			this.url = url;
			this.title = title;
			this.content = content;
			this.image = image;
			this.type = type;
		}
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if(type==OpenSdk.TYPE_BLOG){
				blogShare(url,title,content,image,null);
			}else if(type == OpenSdk.TYPE_MM){
				mmShare(url,title,content,image,null);
			}
			else if(type == OpenSdk.TYPE_MM_TIMELINE){
				mmtimelineShare(url,title,content,image,null);
			}
		}

		@Override
		public void onResponse(ImageContainer arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg0.getBitmap()==null||arg0.getBitmap().isRecycled())
				return;
			if(type==OpenSdk.TYPE_BLOG){
				blogShare(url,title,content,image,arg0.getBitmap());
			}else if(type == OpenSdk.TYPE_MM){
				mmShare(url,title,content,image,arg0.getBitmap());
			}
			else if(type == OpenSdk.TYPE_MM_TIMELINE){
				mmtimelineShare(url,title,content,image,arg0.getBitmap());
			}
		}
		
	}
	public boolean doshare(int type,String url,String title,String content,String image){
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(this);
		if (isNoNetwork) {
			return false;
		}
		if(type==OpenSdk.TYPE_BLOG){
			LruImageCache.getImageLoader(this).get(image, new MyImageListener(type,url,title,content,image));
		}else if(type == OpenSdk.TYPE_MM){
			LruImageCache.getImageLoader(this).get(image, new MyImageListener(type,url,title,content,image));
		}
		else if(type == OpenSdk.TYPE_MM_TIMELINE){
			LruImageCache.getImageLoader(this).get(image, new MyImageListener(type,url,title,content,image));
		}
		else if(type == OpenSdk.TYPE_QQ){
			String nonContent = content;
			if (TextUtils.isEmpty(content)) {
				nonContent = "  ";
			}
			return qqShare(url,title,nonContent,image,null);
		}
		else if(type == OpenSdk.TYPE_ZONE){
			return qzoneShare(url,title,content,image,null);
		}
		
		return false;
	}
	public boolean qzoneShare(String url,String title,String content,String image,Bitmap bitmap){
		Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
	    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
	    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);//选填
	    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
	    ArrayList<String> urls = new ArrayList<String>(1);
	    urls.add(image);
	    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);
	    if(mTencent!=null)
	    mTencent.shareToQzone(this, params,
				new BaseUiListener());
		return true;
	}
    public boolean qqShare(String url,String title,String content,String image,Bitmap bitmap){
		
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY,content);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,url);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,image);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
				QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
		if(mTencent!=null)
		mTencent.shareToQQ(this, params,
				new BaseUiListener());
		return true;
	}
	public boolean mmtimelineShare(String url,String title,String content,String image,Bitmap bitmap) {
		if (api.isWXAppInstalled()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = content;
			Bitmap thumb = Bitmap.createScaledBitmap(bitmap==null?BitmapFactory
					.decodeResource(getResources(), R.drawable.icon):bitmap,
					THUMB_SIZE_W, THUMB_SIZE_H, true);
//			Bitmap thumb = bitmap==null?BitmapFactory
//					.decodeResource(getResources(), R.drawable.icon):bitmap;
			msg.thumbData = OpenUtil.bmpToByteArray(thumb, true);
			if(thumb!=null&&!thumb.isRecycled()){
				thumb.recycle();
				thumb = null;
			}
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = OpenUtil.buildTransaction("img");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			boolean issent = api.sendReq(req);
			return issent;
		}else{
			showToastSafe(getString(R.string.noweichat), Toast.LENGTH_SHORT);
		}
		return false;
	}
	
	public boolean blogShare(String url,String title,String content,String image,Bitmap bitmap) {
		if(mWeiboShareAPI.isWeiboAppInstalled()&&mWeiboShareAPI.isWeiboAppSupportAPI()){
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();// 初始化微博的分享消息
			TextObject textObject = new TextObject();
	        textObject.text = title;
			weiboMessage.textObject = textObject;

			WebpageObject webpageObject = new WebpageObject();
			if(webpageObject.checkArgs()){
				webpageObject.actionUrl = url;
				webpageObject.setThumbImage(bitmap==null?BitmapFactory.decodeResource(getResources(), R.drawable.icon):bitmap);
				weiboMessage.mediaObject = webpageObject;
			}else{
				ImageObject imageObject = new ImageObject();
				imageObject.actionUrl = url;
				imageObject.setImageObject(bitmap==null?BitmapFactory.decodeResource(getResources(), R.drawable.icon):bitmap);
				textObject.text = textObject.text + url;
				weiboMessage.mediaObject = imageObject;
			}
			
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;
			boolean issent = mWeiboShareAPI.sendRequest(this,request); // 发送请求消息到微博，唤起微博分享界面
			return issent;
		}else{
			showToastSafe(getString(R.string.noblog), Toast.LENGTH_SHORT);
		}
		return false;
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, getString(R.string.share_ok), Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, getString(R.string.share_cancel), Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, getString(R.string.share_error, baseResp.errMsg), Toast.LENGTH_LONG)
					.show();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mTencent != null) {
			mTencent.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
	        if (null == response) {
	            return;
	        }
	        JSONObject jsonResponse = (JSONObject) response;
	        if (null != jsonResponse && jsonResponse.length() == 0) {
	            return;
	        }
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
		}
	}
}
