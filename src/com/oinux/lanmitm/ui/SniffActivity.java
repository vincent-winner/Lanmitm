package com.oinux.lanmitm.ui;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.oinux.lanmitm.ActionBarActivity;
import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.R;
import com.oinux.lanmitm.service.SnifferService;

/**
 *
 * @author oinux
 *
 */
public class SniffActivity extends ActionBarActivity {

	private Handler handler;
	private Runnable updateRunnable;

	private TextView fileSizeText = null;
	private CheckBox tcpdumpCheckBox;
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.sniff_activity);

		setBarTitle(Html.fromHtml("<b>" + getString(R.string.sniffer) + "</b> - <small>"
				+ AppContext.getTarget().getIp() + "</small>"));

		headerView = findViewById(R.id.header_view);
		fileSizeText = (TextView) findViewById(R.id.header_text);

		tcpdumpCheckBox = (CheckBox) findViewById(R.id.tcpdump_check_box);

		handler = new Handler();
		updateRunnable = new Runnable() {

			@Override
			public void run() {
				File file = new File(AppContext.getStoragePath() + "/"
						+ SnifferService.sniffer_file_name);
				if (file.exists()) {
					fileSizeText.setText(getString(
							R.string.already_sniffer_data,
							file.length() / (1000 * 1000f)));
					fileSizeText.invalidate();
				}
				handler.postDelayed(this, 1000);
			}
		};

		if (AppContext.isTcpdumpRunning) {
			tcpdumpCheckBox.setChecked(true);
			headerView.setVisibility(View.VISIBLE);
			handler.post(updateRunnable);
		} else {
			tcpdumpCheckBox.setChecked(false);
		}

		tcpdumpCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(SniffActivity.this, SnifferService.class);
				if (isChecked) {
					headerView.setVisibility(View.VISIBLE);
					startService(intent);
					handler.post(updateRunnable);
				} else {
					if (handler != null && updateRunnable != null) {
						handler.removeCallbacks(updateRunnable);
					}
					Toast.makeText(SniffActivity.this,
							getString(R.string.saved_in,
									AppContext.getStoragePath()
											+ "/"
											+ SnifferService.sniffer_file_name),
							Toast.LENGTH_LONG).show();
					stopService(intent);
					headerView.setVisibility(View.GONE);
					fileSizeText.setText("");
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right, R.anim.slide_right_out);
	}
}
