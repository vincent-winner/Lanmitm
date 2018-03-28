package com.oinux.lanmitm.service;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.ui.SniffActivity;
import com.oinux.lanmitm.util.ShellUtils;

import android.content.Intent;

public class KillService extends BaseService {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (!ShellUtils.checkRootPermission())
			return super.onStartCommand(intent, flags, startId);

		startKill();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		this.my_notice_id = KILL_NOTICE;
		this.my_ticker_text = "正在禁止目标上网，点击管理";
		this.cls = KillService.class;
		super.onCreate();
	}

	private void startKill() {
		Intent intent = new Intent(this, ArpService.class);
		intent.putExtra("arp_cheat_way", ArpService.ONE_WAY_HOST);
		intent.putExtra("ip_forward", false);
		startService(intent);

		AppContext.isKillRunning = true;
	}

	private void stopKill() {
		stopService(new Intent(this, ArpService.class));
		AppContext.isKillRunning = false;
	}

	@Override
	public void onDestroy() {
		stopKill();
		super.onDestroy();
	}
}
