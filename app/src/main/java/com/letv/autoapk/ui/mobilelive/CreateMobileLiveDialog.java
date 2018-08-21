package com.letv.autoapk.ui.mobilelive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.dialog.DialogResultListener;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.ImageUtils;
import com.letv.autoapk.utils.SystemUtls;

public class CreateMobileLiveDialog extends BaseDialog implements OnClickListener {

    @Override
    public int layoutId() {
        return R.layout.live_create;
    }

    private EditText edittitle;
    private Uri imgUri;
    private String path;
    private String tempPath;
    private ImageView livecover;
    private View start;
    private String coverUrl = "";
    private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			canSubmit();
		}
	};
	private void canSubmit(){
		if (TextUtils.isEmpty(path)){
			start.setEnabled(false);
			return;	
		}
            
        String title = edittitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
        	start.setEnabled(false);
            return;
        }
        start.setEnabled(true);
	}
	private Handler cliphandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
		     if(msg.what==1){
		    	 resizePic(imgUri.getPath());
		     }
		     if(msg.what==2){
		    	 resizePic(ImageUtils.getPath(context.getApplicationContext(), imgUri));
		     }
		}
		
	};
    @Override
    protected void setupUI(View view, Bundle bundle) throws Exception {
        view(R.id.close).setOnClickListener(this);
        livecover = view(R.id.livecover);
        livecover.setOnClickListener(this);
        edittitle = view(R.id.edittitle);
        edittitle.addTextChangedListener(textWatcher);
        start = view(R.id.start);
        start.setEnabled(false);
        start.setOnClickListener(this);
        setCancelable(false);
        this.getDialog().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent intent = new Intent("com.letv.record.backpress");
                    context.sendBroadcast(intent, null);
                    return true;
                } else
                    return false;
            }
        });

    }

    @Override
    public int getStyle() {
        return android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start) {
            if (TextUtils.isEmpty(path))
                return;
            String title = edittitle.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(edittitle.getApplicationWindowToken(), 0);
            if (getActivity() instanceof DialogResultListener) {
                uploadMobileLiveCover(path, title);
            }
        }
        if (id == R.id.close) {
            dismiss();
            if (getActivity() instanceof DialogResultListener) {
                ((DialogResultListener) getActivity()).onDialogResult(0, Activity.RESULT_CANCELED, null);
            }

        }
        if (id == R.id.livecover) {
            showSelectPicDialog();
        }
        if (id == R.id.dialog_cancel) {
            dialog.cancel();
            dialog = null;
        }
        if (id == R.id.gallary) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                startActivityForResult(intent, SELECT_PIC_KITKAT);
            } else {
                startActivityForResult(intent, SELECT_PIC);
            }

            dialog.dismiss();
            dialog = null;
        }
        if (id == R.id.camera) {
            int permission = ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.CAMERA);
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { android.Manifest.permission.CAMERA }, CAMERA_REQUEST);
                return;
            }
            openCamera();
            dialog.dismiss();
            dialog = null;
        }
    }

    private static final int SELECT_PIC_KITKAT = 4;
    private static final int SELECT_PIC = 5;
    private static final int PHOTOHRAPH = 3;
    private static final int NONE = 0;
    private static final int CAMERA_REQUEST = 1;
    private static final int PHOTORESULT = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;

        if (requestCode == PHOTORESULT) {
            if (resultCode == Activity.RESULT_OK&&data!=null) {

                path = data.getStringExtra("path");
                livecover.setImageURI(null);
                livecover.setImageURI(Uri.parse(path));
                canSubmit();
            }
            return;
        }
        if (CAMERA_REQUEST == requestCode) {
            int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.CAMERA);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
            return;
        }
        // 拍照
        if (requestCode == PHOTOHRAPH) {
//            resizePic(imgUri.getPath());
            cliphandler.sendEmptyMessage(1);
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == SELECT_PIC || requestCode == SELECT_PIC_KITKAT) {
            imgUri = data.getData();
            cliphandler.sendEmptyMessage(2);
//            resizePic(getPath(imgUri));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void resizePic(String path) {
        tempPath = path;
        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENTNAME, ClipCoverFragment.class.getName());
        intent.putExtra("path", path);
        startActivityForResult(intent, PHOTORESULT);
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Wysie_Soh: Create path for temp file
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            imgUri = Uri.fromFile(new File(context.getExternalCacheDir(), "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        } else {
            imgUri = Uri.fromFile(new File(context.getCacheDir(), "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, PHOTOHRAPH);
    }

    private Dialog dialog;
    private BackpressReceiver backpressReceiver;

    private void showSelectPicDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.mine_login_photodialog, null);
        dialog = new Dialog(getActivity(), R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // dialog中的三个按钮，从图库选择，拍照选择，取消
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        dialog.findViewById(R.id.gallary).setOnClickListener(this);
        dialog.findViewById(R.id.camera).setOnClickListener(this);
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void uploadMobileLiveCover(final String imgPath, final String title) {
        new UiAsyncTask<Boolean>(this) {

            private String alertMessage;

            @Override
            protected Boolean doBackground() {
                RequestParams params = new RequestParams(StringDataRequest.MAIN_URL + "/upLoadMobileBroadcastCover");
                params.addBodyParameter("coverPic", new File(imgPath), null);
                params.addBodyParameter(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
                params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
                params.addBodyParameter("authtoken", LoginInfoUtil.getToken(context));
                params.addBodyParameter("liveTitle", title);
                String responseStr;
                try {
                    responseStr = x.http().postSync(params, String.class);
                    JSONObject responseJSON;
                    responseJSON = new JSONObject(responseStr);
                    int statusCode = responseJSON.optInt("state");
                    alertMessage = responseJSON.optString("alertMessage");
                    String message = responseJSON.optString("message");
                    if (statusCode == 0) {
                        String content = responseJSON.optString("content");
                        JSONObject jsonObj = new JSONObject(content);
                        String data = jsonObj.optString("data");
                        JSONObject dataObj = new JSONObject(data);
                        coverUrl = dataObj.optString("liveImg");// 封面图片地址
                    } else {
                        return false;
                    }
                } catch (JSONException e) {
                	Logger.log(e);
                    return false;
                } catch (Throwable e1) {
                	Logger.log(e1);
                    return false;
                }
                return true;
            }

            protected void post(Boolean result) {
                if (result) {
                    dismissAllowingStateLoss();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("coverUrl", coverUrl);
                    ((DialogResultListener) getActivity()).onDialogResult(0, Activity.RESULT_OK, bundle);
                } else {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).showToastSafe(alertMessage, 1);
                    }
                }
            };
        }.showDialog().execute();
    }

    @Override
    public void onResume() {
        backpressReceiver = new BackpressReceiver();
        context.registerReceiver(backpressReceiver, new IntentFilter("com.letv.record.backpress"));
        super.onResume();
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(backpressReceiver);
        super.onPause();
    }

    class BackpressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(context instanceof BaseActivity){
                ((BaseActivity) context).finish();
            }
            dismiss();
        }

    }

}
