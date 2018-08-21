package com.letv.autoapk.common.net;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.widget.ImageView.ScaleType;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.toolbox.HttpHeaderParser;
import com.letv.autoapk.common.utils.Logger;

public class GifRequest extends Request<GifDrawable> {
	/** Socket timeout in milliseconds for image requests */
	private static final int IMAGE_TIMEOUT_MS = 1000;

	/** Default number of retries for image requests */
	private static final int IMAGE_MAX_RETRIES = 2;

	/** Default backoff multiplier for image requests */
	private static final float IMAGE_BACKOFF_MULT = 2f;

	private final Response.Listener<GifDrawable> mListener;
	private final int mMaxWidth;
	private final int mMaxHeight;
	private ScaleType mScaleType;

	/**
	 * Decoding lock so that we don't decode more than one image at a time (to
	 * avoid OOM's)
	 */
	private static final Object sDecodeLock = new Object();

	/**
	 * Creates a new image request, decoding to a maximum specified width and
	 * height. If both width and height are zero, the image will be decoded to
	 * its natural size. If one of the two is nonzero, that dimension will be
	 * clamped and the other one will be set to preserve the image's aspect
	 * ratio. If both width and height are nonzero, the image will be decoded to
	 * be fit in the rectangle of dimensions width x height while keeping its
	 * aspect ratio.
	 * 
	 * @param url
	 *            URL of the image
	 * @param listener
	 *            Listener to receive the decoded bitmap
	 * @param maxWidth
	 *            Maximum width to decode this bitmap to, or zero for none
	 * @param maxHeight
	 *            Maximum height to decode this bitmap to, or zero for none
	 * @param scaleType
	 *            The ImageViews ScaleType used to calculate the needed image
	 *            size.
	 * @param decodeConfig
	 *            Format to decode the bitmap to
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public GifRequest(String url, Response.Listener<GifDrawable> listener,
			int maxWidth, int maxHeight, ScaleType scaleType,
			Response.ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS,
				IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
		mListener = listener;
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;
		mScaleType = scaleType;
	}

	@Override
	public Priority getPriority() {
		return Priority.LOW;
	}

	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 * 
	 * @param maxPrimary
	 *            Maximum size of the primary dimension (i.e. width for max
	 *            width), or zero to maintain aspect ratio with secondary
	 *            dimension
	 * @param maxSecondary
	 *            Maximum size of the secondary dimension, or zero to maintain
	 *            aspect ratio with primary dimension
	 * @param actualPrimary
	 *            Actual size of the primary dimension
	 * @param actualSecondary
	 *            Actual size of the secondary dimension
	 * @param scaleType
	 *            The ScaleType used to calculate the needed image size.
	 */
	/*private static int getResizedDimension(int maxPrimary, int maxSecondary,
			int actualPrimary, int actualSecondary, ScaleType scaleType) {

		// If no dominant value at all, just return the actual.
		if ((maxPrimary == 0) && (maxSecondary == 0)) {
			return actualPrimary;
		}

		// If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
		if (scaleType == ScaleType.FIT_XY) {
			if (maxPrimary == 0) {
				return actualPrimary;
			}
			return maxPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;

		// If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect
		// ratio.
		if (scaleType == ScaleType.CENTER_CROP) {
			if ((resized * ratio) < maxSecondary) {
				resized = (int) (maxSecondary / ratio);
			}
			return resized;
		}

		if ((resized * ratio) > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}*/

	@Override
	protected Response<GifDrawable> parseNetworkResponse(
			NetworkResponse response) {
		// Serialize all decode on a global lock to reduce concurrent heap
		// usage.
		synchronized (sDecodeLock) {
			try {
				return doParse(response);
			} catch (OutOfMemoryError e) {
				VolleyLog.e("Caught OOM for %d byte image, url=%s",
						response.data.length, getUrl());
				return Response.error(new ParseError(e));
			}
		}
	}

	/**
	 * The real guts of parseNetworkResponse. Broken out for readability.
	 */
	private Response<GifDrawable> doParse(NetworkResponse response) {
		byte[] data = response.data;
		// BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		GifDrawable drawable = null;
		try {
			drawable = new GifDrawable(data);

		} catch (IOException e) {
			Logger.log(e);
		}
		if (drawable == null) {
			return Response.error(new ParseError(response));
		} else {
			return Response.success(drawable,
					HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	@Override
	protected void deliverResponse(GifDrawable response) {
		mListener.onResponse(response);
	}

	/**
	 * Returns the largest power-of-two divisor for use in downscaling a bitmap
	 * that will not result in the scaling past the desired dimensions.
	 * 
	 * @param actualWidth
	 *            Actual width of the bitmap
	 * @param actualHeight
	 *            Actual height of the bitmap
	 * @param desiredWidth
	 *            Desired width of the bitmap
	 * @param desiredHeight
	 *            Desired height of the bitmap
	 */
	// Visible for testing.
	/*static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}*/
}
