package com.oinux.lanmitm;

import java.lang.ref.WeakReference;

import android.os.Handler;

public class WeakHandler<T> extends Handler {
	private WeakReference<T> mRef;

	public WeakHandler(T ref) {
		mRef = new WeakReference<T>(ref);
	}

	protected WeakReference<T> getRef() {
		return mRef;
	}
}
