package com.oinux.lanmitm.service;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.ui.SniffActivity;
import com.oinux.lanmitm.util.ShellUtils;

import android.content.Intent;

public class SnifferService extends BaseService {

	private String tcpdump_cmd = null;
	private Thread tcpdump = null;
	public static String sniffer_file_name = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		sniffer_file_name = "tcpdump_" + System.currentTimeMillis() + ".pcap";

		tcpdump_cmd = getFilesDir() + "/tcpdump -w '"
				+ AppContext.getStoragePath() + "/" + sniffer_file_name + "' "
				+ " host " + AppContext.getTarget().getIp();

		if (!ShellUtils.checkRootPermission())
			return super.onStartCommand(intent, flags, startId);

		startSniffer();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		this.my_notice_id = SNIFFER_NOTICE;
		this.my_ticker_text = "数据嗅探后台运行中，点击管理";
		this.cls = SniffActivity.class;
		super.onCreate();
	}

	private void startSniffer() {
		
		startArpService();

		tcpdump = new Thread() {
			@Override
			public void run() {
				ShellUtils.execCommand(tcpdump_cmd, true, false);
			}
		};
		tcpdump.start();

		AppContext.isTcpdumpRunning = true;
	}

	private void stopSniffer() {
		if (tcpdump != null) {
			tcpdump.interrupt();
			tcpdump = null;
		}

		new Thread() {
			@Override
			public void run() {
				ShellUtils.execCommand("killall tcpdump", true, true);
			}
		}.start();

		stopArpService();

		AppContext.isTcpdumpRunning = false;
	}

	@Override
	public void onDestroy() {
		stopSniffer();
		super.onDestroy();
	}
}
