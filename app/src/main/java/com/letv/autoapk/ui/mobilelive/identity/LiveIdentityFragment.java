package com.letv.autoapk.ui.mobilelive.identity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.IDNumberUtil;

/**
 * 身份验证页面
 */
public class LiveIdentityFragment extends BaseTitleFragment implements OnClickListener {

    @Override
    public void onResume() {
        imgUri = Uri.parse(MyApplication.getInstance().getString("imgUri") == null ? "" : MyApplication.getInstance().getString("imgUri"));
        super.onResume();
    }

    @Override
    public void onPause() {
        MyApplication.getInstance().putString("imgUri", imgUri != null ? imgUri.toString() : "");
        super.onPause();
    }

    @Override
    protected boolean loadingData() {
        return true;
    }

    @Override
    protected View createContentView() {
        return setupDataView();
    }

    @Override
    protected void onHandleMessage(Message msg) {
    }

    private TextView titleRight;

    @Override
    protected void initCustomerView() {
        setStatusBarColor(getResources().getColor(R.color.code04));
        setTitle(getString(R.string.liveidentity), getResources().getColor(R.color.code6));
        setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(15));
        setLeftClickListener(new TitleLeftClickListener() {

            @Override
            public void onLeftClickListener() {
                getActivity().finish();
            }
        });
        titleRight = new TextView(mActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleRight.setGravity(Gravity.CENTER);
        titleRight.setLayoutParams(params);
        titleRight.setText(R.string.livedone);
        titleRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        titleRight.setPadding(0, 0, mActivity.dip2px(15), 0);
        titleRight.setTextColor(mActivity.getResources().getColorStateList(R.color.live_ok_bg));
        titleRight.setEnabled(false);
        mTitleRightLay.addView(titleRight);

        setRightClickListener(new TitleRightClickListener() {

            @Override
            public void onRightClickListener() {
                if (!titleRight.isEnabled())
                    return;
                String name = username.getText().toString().trim();
                String cardidString = cardid.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cardidString)) {
                    return;
                }
                if (IDNumberUtil.IDCardValidate(cardidString) == false) {
                    mActivity.showToastSafe(getString(R.string.letv_record_error_idnumber), 0);
                    return;
                }
                if (TextUtils.isEmpty(imgPath)) {
                    mActivity.showToastSafe(getString(R.string.letv_record_identity_no_image), 0);
                    return;
                }
                uploadAnchorInfo(name, cardidString);
            }

        });
    }

    private void uploadAnchorInfo(final String name, final String cardidstring) {

        new UiAsyncTask<Boolean>(this) {

            @Override
            protected Boolean doBackground() {
                RequestParams params = new RequestParams(StringDataRequest.MAIN_URL + "/upLoadMobileBroadcastInfo");
                params.addBodyParameter("IDCardPic", new File(imgPath), null);
                params.addBodyParameter(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
                params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
                params.addBodyParameter("name", name);
                params.addBodyParameter("IDCardNo", cardidstring);
                String responseStr;
                try {
                    responseStr = x.http().postSync(params, String.class);
                    JSONObject responseJSON;
                    responseJSON = new JSONObject(responseStr);
                    int statusCode = responseJSON.optInt("state");
                    String alertMessage = responseJSON.optString("alertMessage");
                    mActivity.showToastSafe(alertMessage, 0);
                    String message = responseJSON.optString("message");
                    if (statusCode == 0) {
                        String content = responseJSON.optString("content");
                        JSONObject jsonObj = new JSONObject(content);
                        String data = jsonObj.optString("data");
                        JSONObject dataObj = new JSONObject(data);
                        String IDCardPicUrl = dataObj.optString("IDCardPicUrl");// 身份证地址
                        String applyTime = dataObj.optString("applyTime");// 申请时间
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
                    showWaitIdentityDialog();
                } else {
                    mActivity.showToastSafe(getString(R.string.letv_record_upload_anchorinfo_failed), 0);
                }
            };
        }.showDialog().execute();
    }

    /**
     * 主播身份审核等待
     */
    public void showWaitIdentityDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setContentView(R.layout.letv_record_wait_identity);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        cancel.setText(getResString(R.string.ensure));
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }

    private EditText username;
    private EditText cardid;
    private ImageView cardpic;
    private ImageView okbtn;
    /** 从图库选择,KITKAT以上 */
    private static final int SELECT_PIC_KITKAT = 4;
    private static final int SELECT_PIC = 5;
    private static final int PHOTOHRAPH = 3;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int NONE = 0;
    private static final int CAMERA_REQUEST = 1;
    private static final int PHOTORESULT = 2;

    private static final int MAX_WIDTH = 720;
    private static final int MAX_HEIGHT = 720;
    private Uri imgUri;// 图片uri
    private String imgPath;
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {

            canSubmit();
        }
    };

    private void canSubmit() {
        String name = username.getText().toString().trim();
        String cardidString = cardid.getText().toString().trim();
        if (TextUtils.isEmpty(cardidString) || IDNumberUtil.IDCardValidate(cardidString) == false) {
            titleRight.setEnabled(false);
            okbtn.setVisibility(View.GONE);
            return;
        }
        okbtn.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(name)) {
            titleRight.setEnabled(false);
            return;
        }

        if (TextUtils.isEmpty(imgPath)) {
            titleRight.setEnabled(false);
            return;
        }
        titleRight.setEnabled(true);
        titleRight.invalidate();
    }

    @Override
    protected View setupDataView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.live_identity, null);
        username = (EditText) view.findViewById(R.id.liveuser);
        username.addTextChangedListener(textWatcher);
        cardid = (EditText) view.findViewById(R.id.livecardid);
        cardid.addTextChangedListener(textWatcher);
        okbtn = (ImageView) view.findViewById(R.id.editok);
        cardpic = (ImageView) view.findViewById(R.id.cardpic);
        cardpic.setOnClickListener(this);
        view.findViewById(R.id.deletepic).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cardpic) {
            showSelectPicDialog();
        }
        if (id == R.id.deletepic) {
            imgPath = null;
            titleRight.setEnabled(false);
            cardpic.setImageURI(null);
            cardpic.setImageResource(R.drawable.live_bg_photo);
        }
        if (id == R.id.dialog_cancel) {
            dialog.cancel();
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
        }
        if (id == R.id.camera) {
            int permission = ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(), android.Manifest.permission.CAMERA);
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity, new String[] { android.Manifest.permission.CAMERA }, CAMERA_REQUEST);
                return;
            }

            openCamera();
            dialog.dismiss();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Wysie_Soh: Create path for temp file
        File tempFile;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tempFile = new File(mActivity.getExternalCacheDir(), "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        } else {
            tempFile = new File(mActivity.getCacheDir(), "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        }
        // if(!tempFile.exists()){
        // try {
        // tempFile.createNewFile();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        // Runtime runtime = Runtime.getRuntime();
        // try {
        // runtime.exec("chmod 777 " + tempFile);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        imgUri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTOHRAPH);
    }

    private Bitmap decodeBitmap(String filePath, int sampleSize) {
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        Bitmap bitmap = null;
        while (bitmap == null) {
            try {
                Options opts = new Options();
                opts.inSampleSize = sampleSize;
                bitmap = BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError oom) {
                ++sampleSize;
                continue;
            } catch (Exception e) {
            	Logger.log(e);
                break;
            }
            if (bitmap == null) {
                break;
            }
        }
        return bitmap;
    }

    private void resizePic(String path) {
        if (path == null)
            return;
        imgPath = path;
        final String filePath = path;
        new UiAsyncTask<Void>(this) {

            @Override
            protected Void doBackground() throws Throwable {

                try {
                    ExifInterface exif = new ExifInterface(filePath);
                    int degree = 0;
                    if (exif != null) {

                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            break;
                        }
                    }
                    Options opt = new Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, opt);

                    int sampleFctor = 1;
                    if (opt.outWidth > MAX_WIDTH || opt.outHeight > MAX_HEIGHT) {
                        int sampleW = opt.outWidth / MAX_WIDTH;
                        int sampleH = opt.outHeight / MAX_HEIGHT;
                        sampleFctor = sampleW < sampleH ? sampleW : sampleH;
                    }
                    Bitmap bitmap = decodeBitmap(filePath, sampleFctor);
                    if (bitmap == null) {
                        return null;
                    }

                    int bmpWidth = bitmap.getWidth();
                    int bmpHeight = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    float scale = 1.0f;
                    if (bmpWidth < bmpHeight) {
                        scale = ((float) MAX_WIDTH) / bmpWidth;
                    } else {
                        scale = ((float) MAX_WIDTH) / bmpHeight;
                    }
                    if (scale > 1.0f) {
                        scale = 1.0f;
                    }
                    matrix.setRotate(degree);
                    matrix.postScale(scale, scale);
                    Bitmap processedBitmap = null;
                    try {
                        processedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
                        if (processedBitmap != bitmap) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        File outdir = FileUtil.getCacheDir("temp");
                        if (outdir == null)
                           return null;
                        File srcfile = new File(filePath);
                        String filename = srcfile.getName() + ".tmp";
                        File outFile = new File(outdir, filename);
                        FileOutputStream outStream = new FileOutputStream(outFile);
                        boolean save = processedBitmap.compress(CompressFormat.JPEG, 85, outStream);
                        outStream.flush();
                        outStream.close();
                        if (save) {
                            imgPath = outFile.getAbsolutePath();

                        }
                    } catch (OutOfMemoryError e) {
                        Logger.log(e);
                    } finally {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        if (processedBitmap != null && !processedBitmap.isRecycled()) {
                            processedBitmap.recycle();
                            processedBitmap = null;
                        }
                    }
                } catch (Exception e) {
                    Logger.log(e);
                } catch (OutOfMemoryError e) {
                    // TODO: handle exception
                    Logger.log(e);
                }

                return null;
            }

            @Override
            protected void post(Void result) {
                cardpic.setImageURI(Uri.parse(imgPath));
                canSubmit();
            }

        }.showDialog().execute();
    }

    /**
     * 获取图片地址
     * 
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        final boolean isKitKat = VERSION.SDK_INT >= VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(mActivity.getApplicationContext(), uri)) {
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
            return null;
        } else {
            return getDataColumn(uri, null, null);
        }
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = mActivity.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        if (CAMERA_REQUEST == requestCode) {
            int permission = ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(), android.Manifest.permission.CAMERA);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
            return;
        }
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            if (imgUri == null) {
                imgUri = Uri.parse(MyApplication.getInstance().getString("imgUri") == null ? "" : MyApplication.getInstance().getString("imgUri"));
            }
            resizePic(imgUri.getPath());
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == SELECT_PIC || requestCode == SELECT_PIC_KITKAT) {
            imgUri = data.getData();
            resizePic(getPath(imgUri));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Dialog dialog;

    private void showSelectPicDialog() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.mine_login_photodialog, null);
        dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
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
        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
