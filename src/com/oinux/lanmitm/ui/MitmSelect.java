package com.oinux.lanmitm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;

import com.oinux.lanmitm.ActionBarActivity;
import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.R;
import com.oinux.lanmitm.service.HijackService;
import com.oinux.lanmitm.service.InjectService;
import com.oinux.lanmitm.service.KillService;
import com.oinux.lanmitm.service.SnifferService;

public class MitmSelect extends ActionBarActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, com.oinux.lanmitm.R.layout.mitm_select);

		setBarTitle(Html.fromHtml("<b>" + getString(R.string.function_select)
				+ "</b> - <small>" + AppContext.getTarget().getIp() + "</small>"));

		findViewById(R.id.mitm_select_sniffer).setOnClickListener(this);
		findViewById(R.id.mitm_select_hijack).setOnClickListener(this);
		findViewById(R.id.mitm_select_inject).setOnClickListener(this);
		findViewById(R.id.mitm_select_kill).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.mitm_select_sniffer:
			if (AppContext.isTcpdumpRunning) {
				stopService(new Intent(MitmSelect.this, SnifferService.class));
			}
			intent = new Intent(MitmSelect.this, SniffActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out);
			break;
		case R.id.mitm_select_hijack:
			if (AppContext.isHijackRunning) {
				stopService(new Intent(MitmSelect.this, HijackService.class));
				if (AppContext.getHijackList() != null)
					AppContext.getHijackList().clear();
			}
			intent = new Intent(MitmSelect.this, HijackActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out);
			break;
		case R.id.mitm_select_inject:
			if (AppContext.isInjectRunning) {
				stopService(new Intent(MitmSelect.this, InjectService.class));
			}
			intent = new Intent(MitmSelect.this, InjectActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out);
			break;
		case R.id.mitm_select_kill:
			if (AppContext.isKillRunning) {
				stopService(new Intent(MitmSelect.this, KillService.class));
			}
			intent = new Intent(MitmSelect.this, KillActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out);
		default:
			break;
		}
	}


	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right, R.anim.slide_right_out);
	}
}
