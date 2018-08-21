package com.letv.autoapk.ui.player;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.os.Handler;
import android.os.Message;

import com.letv.autoapk.common.utils.Logger;

public class DanmakuLiveClient extends WebSocketClient{

	
	private Handler handler;
	private Object lock = new Object();
//	static final String WBHOST = "ws://10.11.144.197:8088/dm/live";
	static final String WBHOST = "ws://dm.my.lecloud.com/dm/live";
	private ConnectionListener listener;
	
	void setConnectionListener(ConnectionListener listener){
		this.listener = listener;
	}
	public DanmakuLiveClient(URI serverURI,Handler handler) {
		
		super(serverURI);
		this.handler = handler;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Logger.i("DanmakuLiveClient", "onOpen===============");
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		Logger.i("DanmakuLiveClient", "onMessage===============");
		receiveMessage(message);
	}

	private void receiveMessage(String message){
		synchronized (lock) {
			Message sendmsg = handler.obtainMessage(PlayConst.GOT_DANMAKU);
			sendmsg.obj = message;
			sendmsg.sendToTarget();
		}
	}
	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		Logger.i("DanmakuLiveClient", code+" "+reason+" "+remote);
		if(code==1006&&listener!=null){
			listener.onTimeOut();
		}
	}

	@Override
	public void onError(Exception ex) {
		
		Logger.log(ex);
	}

	interface ConnectionListener{
		void onTimeOut();
	}
}
