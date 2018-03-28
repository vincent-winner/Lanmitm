package com.oinux.lanmitm.service;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.proxy.HttpProxy;
import com.oinux.lanmitm.util.ShellUtils;

public class ProxyService extends BaseService {

	public static final String[] PORT_REDIRECT_CMD = {
			"iptables -t nat -F",
			"iptables -F",
			"iptables -t nat -I POSTROUTING -s 0/0 -j MASQUERADE",
			"iptables -P FORWARD ACCEPT",
			"iptables -t nat -A PREROUTING -j DNAT -p tcp --dport 80 --to "
					+ AppContext.getIp() + ":" + HttpProxy.HTTP_PROXY_PORT };

	public static final String[] UN_PORT_REDIRECT_CMD = {
			"iptables -t nat -F",
			"iptables -F",
			"iptables -t nat -I POSTROUTING -s 0/0 -j MASQUERADE",
			"iptables -t nat -D PREROUTING -j DNAT -p tcp --dport 80 --to "
					+ AppContext.getIp() + ":" + HttpProxy.HTTP_PROXY_PORT };

	protected HttpProxy mHttpProxy;

	protected void startHttpProxy() {
		if (!AppContext.isHijackRunning && !AppContext.isInjectRunning) {
			HttpProxy.stop = false;

			new Thread() {
				@Override
				public void run() {
					ShellUtils.execCommand(PORT_REDIRECT_CMD, true, true);
				}
			}.start();

			mHttpProxy = HttpProxy.getInstance();
			mHttpProxy.start();
		} else {
			mHttpProxy = HttpProxy.getInstance();
		}
	}

	protected void stopHttpProxy() {
		if (!AppContext.isHijackRunning && !AppContext.isInjectRunning) {
			new Thread() {
				@Override
				public void run() {
					ShellUtils.execCommand(UN_PORT_REDIRECT_CMD, true, true);
				}
			}.start();

			HttpProxy.stop = true;
			if (mHttpProxy != null) {
				mHttpProxy.interrupt();
				mHttpProxy = null;
			}
		}
	}
}
