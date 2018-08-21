package com.letv.autoapk.common.net;

import com.android.volley.VolleyError;

public class ResponseData<T> {
	public int httpCode;
	public VolleyError volleyError;
	public T data;
}