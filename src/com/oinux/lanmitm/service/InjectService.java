package com.oinux.lanmitm.service;

import android.content.Intent;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.proxy.HttpProxy;
import com.oinux.lanmitm.ui.InjectActivity;
import com.oinux.lanmitm.util.ShellUtils;

public class InjectService extends ProxyService {

	public static final String DATASET_CHANGED = "HIJACK_DATASET_CHANGED";
	public static final String DATASET_COOKIES_CHANGED = "HIJACK_COOKIES_CHANGED";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (!ShellUtils.checkRootPermission())
			return super.onStartCommand(intent, flags, startId);

		startInject();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		this.my_notice_id = INJECT_NOTICE;
		this.my_ticker_text = "代码注入后台运行中，点击管理";
		this.cls = InjectActivity.class;
		super.onCreate();
	}

	private void startInject() {
		startHttpProxy();
		
		mHttpProxy.setProxyMode(HttpProxy.MODE_PROXY_DEEP);

		startArpService();
		
		AppContext.isInjectRunning = true;
	}

	private void stopInject() {
		stopArpService();

		if (mHttpProxy != null) {
			mHttpProxy.setProxyMode(HttpProxy.MODE_PROXY_SIMPLE);
		}

		stopHttpProxy();
		AppContext.isInjectRunning = false;
	}

	@Override
	public void onDestroy() {
		stopInject();
		super.onDestroy();
	}
}
