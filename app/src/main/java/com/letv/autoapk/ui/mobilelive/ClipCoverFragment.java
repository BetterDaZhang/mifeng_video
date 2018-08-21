package com.letv.autoapk.ui.mobilelive;

import java.io.File;
import java.io.FileOutputStream;

import org.xutils.common.util.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.lecloud.xutils.util.OtherUtils;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;


public class ClipCoverFragment extends BaseFragment implements OnClickListener{

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}
	private ImageView mImageView;
	private int mClipWidth;
	private int mClipHeight;
	
	private int mBitmapWidth;
	private int mBitmapHeight;
	
	private int mTopOffset = 0;
	private int mLeftOffset = 0;
	private final static int MAX_WIDTH = 1080;
	private View editView;
	private String path;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id==R.id.close||id == R.id.cancel){
			getActivity().setResult(Activity.RESULT_CANCELED);
			getActivity().finish();
		}
		if(id == R.id.ok){
			mImageView.buildDrawingCache();
			Bitmap mBitmap = mImageView.getDrawingCache();
			if(mBitmap==null){
				mImageView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				mImageView.layout(0, 0, mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight());	
				mImageView.buildDrawingCache();
				mBitmap = mImageView.getDrawingCache();
			}
			new SaveBitmapAsyncTask(this,mBitmap).showDialog().execute();
		}
		
	}
	
	
	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.live_clipcover, null);
		editView = view.findViewById(R.id.edit_square);
		mImageView = (ImageView) view.findViewById(R.id.clip_image);
		mImageView.setOnTouchListener(new ImageTouchListener());
		mImageView.setDrawingCacheEnabled(true);
		mImageView.setDrawingCacheBackgroundColor(Color.BLACK);
		mImageView.setDrawingCacheQuality(100);
		mClipWidth = getResources().getDisplayMetrics().widthPixels;
		mClipHeight = mClipWidth*3/4;
		path = getArguments().getString("path");
		view.findViewById(R.id.close).setOnClickListener(this);
		view.findViewById(R.id.cancel).setOnClickListener(this);
		view.findViewById(R.id.ok).setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new LoadImageTask(this).showDialog().execute();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {

			mImageView.destroyDrawingCache();
			mImageView.getImageMatrix().reset();
			mImageView.setImageBitmap(null);
			mImageView.setEnabled(true);
			
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	/**
	 * 防止oom
	 * 
	 * @param filePath
	 * @param sampleSize
	 * @return
	 */
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
				oom.printStackTrace();
				continue;
			} catch (Exception e) {
				break;
			}
			if (bitmap == null) {
				break;
			}
		}
		return bitmap;
	}
	private class LoadImageTask extends UiAsyncTask<Bitmap>{
		private int mywidth;
		private int myheight;
		public LoadImageTask(Fragment fragment) {
			super(fragment);
			mywidth = getResources().getDisplayMetrics().widthPixels;
			myheight = getResources().getDisplayMetrics().heightPixels;
		}

		@Override
		protected void post(Bitmap result) {
			if (result == null) {
				return;
			}
			mBitmapWidth = result.getWidth();
			mBitmapHeight = result.getHeight();
			mImageView.setImageBitmap(result);
			mTopOffset = editView.getTop();
//			if (mBitmapWidth < mywidth) {
//				int x = (mywidth - mBitmapWidth) / 2;
//				Matrix matrix = mImageView.getImageMatrix();
//				matrix.postTranslate(x, 0);
//				mImageView.setImageMatrix(matrix);
//			}
//			if (mBitmapHeight < mywidth) {
//				int y = (mywidth - mBitmapHeight) / 2;
//				Matrix matrix = mImageView.getImageMatrix();
//				matrix.postTranslate(0, y);
//				mImageView.setImageMatrix(matrix);
//			}
			if (mBitmapWidth >= mBitmapHeight) {
				Matrix matrix = mImageView.getImageMatrix();
				matrix.postTranslate(0, mTopOffset);
				mImageView.setImageMatrix(matrix);
			}
			mImageView.invalidate();
		}

		@Override
		protected Bitmap doBackground() throws Throwable {
			try {

				String filePath = path;
				ExifInterface exif = new ExifInterface(filePath);
				int degree = 0;
				if (exif != null) {

					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
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
				Bitmap optionbitmap = BitmapFactory.decodeFile(filePath, opt);
                if(degree==90||degree==270){
                	int temp = opt.outHeight;
                	opt.outHeight = opt.outWidth;
                	opt.outWidth = temp;
                }
				int sampleFctor = 1;
				if (opt.outWidth > mywidth || opt.outHeight > myheight) {
					int sampleW = opt.outWidth / mywidth;
					int sampleH = opt.outHeight / myheight;
					sampleFctor = sampleW < sampleH ? sampleW : sampleH;
				}
				opt = null;
				if(optionbitmap!=null){
					optionbitmap.recycle();
					opt = null;
				}
				Bitmap bitmap = decodeBitmap(filePath, sampleFctor);
				if (bitmap == null) {
					return null;
				}
				int bmpWidth = bitmap.getWidth();
				int bmpHeight = bitmap.getHeight();
				
				Matrix matrix = new Matrix();
				float scale = 1.0f;
				int clipwidth = mClipWidth;
				int clipheight = mClipHeight;
				if(degree==90||degree==270){
					clipwidth = mClipHeight;
					clipheight = mClipWidth;
					
                }
				if (bmpWidth < bmpHeight) {
					scale = ((float) clipwidth) / bmpWidth;
				} else {
					scale = ((float) clipheight) / bmpHeight;
				}
				matrix.setRotate(degree);
				matrix.postScale(scale, scale);
				Bitmap processedBitmap = null;
				try {
					processedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bmpWidth, bmpHeight, matrix, true);
					if (processedBitmap != bitmap) {
						bitmap.recycle();
						bitmap = null;
					}
				} catch (OutOfMemoryError e) {
					Logger.log(e);
				}
				return processedBitmap;
			} catch (Exception e) {
				Logger.log(e);
			} catch (OutOfMemoryError e) {
				Logger.log(e);
				System.gc();
			}
			return null;
		}
		
	}
	private class ImageTouchListener implements OnTouchListener {


		private Matrix matrix = new Matrix();
		private Matrix savedMatrix = new Matrix();
		private PointF startPoint = new PointF();
		private PointF midPoint = new PointF();

		private static final int STATE_NONE = 0;
		private static final int STATE_DRAG = 1;
		private static final int STATE_ZOOM = 2;

		private int currentMode;
		private float startDistance;
		private float[] mMatrixValue = new float[9];
		private float[] mTranslateLimit = new float[2];

		public ImageTouchListener() {
			currentMode = STATE_NONE;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				matrix.set(mImageView.getImageMatrix());
				savedMatrix.set(matrix);
				matrix.getValues(mMatrixValue);

				float scaleFactor = mMatrixValue[0];
				mTranslateLimit[0] = mClipWidth - mBitmapWidth * scaleFactor;
				mTranslateLimit[1] = mClipHeight + mTopOffset - mBitmapHeight
						* scaleFactor;
				startPoint.set(event.getX(), event.getY());
				currentMode = STATE_DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				startDistance = spacing(event);
				if (startDistance > 10f) {
					savedMatrix.set(matrix);
					getMidPoint(midPoint, event);
					currentMode = STATE_ZOOM;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (currentMode == STATE_DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - startPoint.x,
							event.getY() - startPoint.y);
					matrix.getValues(mMatrixValue);
					float fx = mMatrixValue[2];
					float fy = mMatrixValue[5];
					boolean overroll = false;
					if (mTranslateLimit[0] > 0) {
						if (fx < 0) {
							fx = 0;
							overroll = true;
						} else if (fx > mTranslateLimit[0]) {
							fx = mTranslateLimit[0];
							overroll = true;
						}

					} else {
						if (fx > 0) {
							fx = 0;
							overroll = true;
						} else if (fx < mTranslateLimit[0]) {
							fx = mTranslateLimit[0];
							overroll = true;
						}
					}
					if (mTranslateLimit[1] > mTopOffset) {
						if (fy < mTopOffset) {
							fy = mTopOffset;
							overroll = true;
						} else if (fy > mTranslateLimit[1]) {
							fy = mTranslateLimit[1];
							overroll = true;
						}
					} else {
						if (fy > mTopOffset) {
							fy = mTopOffset;
							overroll = true;
						} else if (fy < mTranslateLimit[1]) {
							fy = mTranslateLimit[1];
							overroll = true;
						}
					}

					if (overroll) {
						matrix.setScale(mMatrixValue[0], mMatrixValue[0]);
						matrix.postTranslate(fx, fy);
					}

				} else if (currentMode == STATE_ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / startDistance;
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				Matrix testMatrix = mImageView.getImageMatrix();
				float[] values = new float[9];
				testMatrix.getValues(values);
				float scale = values[0];

				if (scale < 1.0) {
					matrix.setScale(1.0f, 1.0f);
					int x = 0;
					int y = mTopOffset;
					if (mBitmapWidth < mClipWidth) {
						x = (mClipWidth - mBitmapWidth) / 2;
					}
					if (mBitmapHeight < mClipHeight) {
						y = (mClipHeight - mBitmapHeight) / 2 + y;
					}

					matrix.postTranslate(x, y);
				} else {
					float fx = values[2];
					float fy = values[5];
					boolean overroll = false;

					mTranslateLimit[0] = mClipWidth - mBitmapWidth * scale;
					mTranslateLimit[1] = mClipHeight + mTopOffset
							- mBitmapHeight * scale;

					if (mTranslateLimit[0] > 0) {
						fx = mTranslateLimit[0] / 2;
						overroll = true;
					} else if (fx < mTranslateLimit[0]) {
						fx = mTranslateLimit[0];
						overroll = true;
					}
					if (mTranslateLimit[1] > mTopOffset) {
						fy = (mTranslateLimit[1] + mTopOffset) / 2;
						overroll = true;
					} else if (fy < mTranslateLimit[1]) {
						fy = mTranslateLimit[1];
						overroll = true;
					}

					if (overroll) {
						matrix.setScale(values[0], values[0]);
						matrix.postTranslate(fx, fy);
					}
				}
				currentMode = STATE_NONE;
				break;
			case MotionEvent.ACTION_UP:
				currentMode = STATE_NONE;
				break;
			default:
				break;
			}
			mImageView.setImageMatrix(matrix);
			return true;
		}

		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void getMidPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
		
	}
	private class SaveBitmapAsyncTask extends UiAsyncTask<String> {

		public SaveBitmapAsyncTask(Fragment fragment,Bitmap mBitmap) {
			super(fragment);
			this.mBitmap = mBitmap;
		}

		@Override
		public void post(String result) {
			if(result!=null){
				Intent ok = new Intent();
				ok.putExtra("path", result);
				getActivity().setResult(Activity.RESULT_OK,ok);
				
			}else{
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			if(mBitmap!=null&&!mBitmap.isRecycled()){
				mBitmap.recycle();
				mBitmap = null;
			}
			getActivity().finish();
		}
		private Bitmap mBitmap ;

		@Override
		protected String doBackground() throws Throwable {
			int x = 0;
			int y = mTopOffset;
			int width = mClipWidth;
			int height = mClipHeight;

			Bitmap cropBitmap = null;
			Bitmap scaleBitmap = null;
			try {
				cropBitmap = Bitmap.createBitmap(mBitmap, x, y, width, height);
				if(cropBitmap!=mBitmap&&mBitmap!=null&&!mBitmap.isRecycled()){
					mBitmap.recycle();
					mBitmap = null;
				}
				int mScaleImageHeight = mClipHeight;
				int mScaleImageWidth = mClipWidth;
				mScaleImageWidth=mScaleImageWidth>MAX_WIDTH?MAX_WIDTH:mScaleImageWidth;
				mScaleImageHeight = mScaleImageWidth*3/4;
//				Logger.e("clipcover", mClipHeight+" "+mClipWidth);
//				Logger.e("clipcover", mScaleImageHeight+" "+mScaleImageWidth);
				scaleBitmap = Bitmap.createScaledBitmap(cropBitmap,
						mScaleImageWidth, mScaleImageHeight, false);
				if (cropBitmap != scaleBitmap && !cropBitmap.isRecycled()) {
					cropBitmap.recycle();
					cropBitmap = null;
				}

			} catch (Exception e1) {
				Logger.log(e1);
			} catch (OutOfMemoryError e) {
				Logger.log(e);
				System.gc();
			}
			if (scaleBitmap == null)
				return null;
			File outdir = FileUtil.getCacheDir("temp");
			if(outdir==null)
				return null;
			File srcfile = new File(path);
			String filename = srcfile.getName()+".tmp";
			File outFile = new File(outdir,filename);
			if(outFile.exists())
				outFile.delete();
			String path = null;
			try {
				FileOutputStream outStream = new FileOutputStream(outFile);
				boolean save = scaleBitmap.compress(CompressFormat.JPEG, 90, outStream);
				outStream.flush();
				outStream.close();
				path = outFile.getAbsolutePath();
				if(save){
					return path;
				}
			} catch (Exception e) {
				Logger.log(e);
				return null;
			}finally{
				if (cropBitmap != null) {
					cropBitmap.recycle();
					cropBitmap = null;
				}
				if (scaleBitmap != null) {
					scaleBitmap.recycle();
					scaleBitmap = null;
				}
			}
			
			return null;
		}

	}
	
}
