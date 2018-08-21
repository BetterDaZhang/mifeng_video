package com.letv.autoapk.ui.player;

import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.task.UiAsyncTask;

public class DanmakuDialog extends BaseDialog implements OnClickListener {

	@Override
	public int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.play_danmakudialog;
	}

	private OnCancelListener cancel;

	public void setOnCancelListener(OnCancelListener cancel) {
		this.cancel = cancel;
	}

	private EditText content;
//	private TextView count;
	private Map<String, String> mInputParam;
	private ViewGroup layout;
	private InputMethodManager inputMethodManager;
    private boolean isShowkeyboard;
	public void setParam(Map<String, String> mInputParam) {
		this.mInputParam = mInputParam;
	}

	private String sendtext = null;
	String getSendText() {
		return sendtext;
	}

	
	
	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
        layout = view(R.id.container);
		layout.getViewTreeObserver().addOnGlobalLayoutListener( new OnGlobalLayoutListener() {  
            @Override  
            public void onGlobalLayout() {  
                Rect rect = new Rect();  
                layout.getWindowVisibleDisplayFrame(rect);  
                int rootInvisibleHeight = layout.getRootView().getHeight() - rect.bottom;  
                if (rootInvisibleHeight <= 128) { 
                    if(isShowkeyboard)
                    	dismiss();
                } else {
                	isShowkeyboard = true;
                }
            }  
        }); 
		isShowkeyboard = false;
		content = (EditText) view(R.id.edittext);
		content.setImeOptions(EditorInfo.IME_ACTION_SEND
				| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		content.setFreezesText(true);
//		count = (TextView) view(R.id.textcount);
		inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		view(R.id.cancel).setOnClickListener(this);
		view(R.id.sendtext).setOnClickListener(this);
//		content.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				int len = s.length();
//				count.setText(String.valueOf(50 - len));
//			}
//		});
		content.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND
						&& mInputParam != null) {
					final String text = content.getText().toString().trim();
					if (text.length() > 0) {
						new UiAsyncTask<Integer>(DanmakuDialog.this) {

							@Override
							protected void post(Integer result) {
								content.setText("");
								if (result == 0) {
									sendtext = text;
								}
								if (inputMethodManager.isActive(content)) {
									content.clearFocus();
									boolean hide = inputMethodManager
											.hideSoftInputFromWindow(
													content.getWindowToken(), 0);
								}
								dismiss();
							}

							@Override
							protected Integer doBackground() throws Throwable {
								DanmakuSendRequest request = new DanmakuSendRequest(
										context);
								mInputParam.put("content", text);
								return request.setInputParam(mInputParam)
										.request(Request.Method.GET);
							}

						}.showDialog().execute();
					}
					return true;
				}
				return false;
			}
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				content.requestFocus();
				inputMethodManager.showSoftInput(content, 0);
			}
		}, 300);
	}





	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		dismiss();
	}

	@Override
	protected void onResult(final int resultCode) {
		
		if (cancel != null)
			cancel.onCancel(getDialog());
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
	}


	@Override
	public int getStyle() {
		return android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.cancel) {
			if (inputMethodManager.isActive(content)) {
				content.clearFocus();
				boolean hide = inputMethodManager.hideSoftInputFromWindow(
						content.getWindowToken(), 0);
			}
			dismiss();
		}
		if(id==R.id.sendtext){
			final String text = content.getText().toString().trim();
				if (text.length() > 0) {
					new UiAsyncTask<Integer>(DanmakuDialog.this) {

						@Override
						protected void post(Integer result) {
							content.setText("");
							if (result == 0) {
								sendtext = text;
							}
							if (inputMethodManager.isActive(content)) {
								content.clearFocus();
								boolean hide = inputMethodManager
										.hideSoftInputFromWindow(
												content.getWindowToken(), 0);
							}
							dismiss();
						}

						@Override
						protected Integer doBackground() throws Throwable {
							DanmakuSendRequest request = new DanmakuSendRequest(
									context);
							mInputParam.put("content", text);
							return request.setInputParam(mInputParam)
									.request(Request.Method.GET);
						}

					}.showDialog().execute();

			}
		}
	}
}
