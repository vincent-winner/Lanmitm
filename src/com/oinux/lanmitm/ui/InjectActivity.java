package com.oinux.lanmitm.ui;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.oinux.lanmitm.ActionBarActivity;
import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.R;
import com.oinux.lanmitm.proxy.HttpProxy;
import com.oinux.lanmitm.service.InjectService;

/**
 *
 * @author oinux
 *
 */
public class InjectActivity extends ActionBarActivity implements OnClickListener {

	private static final String TAG = "InjectActivity";

	private CheckBox injectCheckBox;
	private EditText urlPatternText;
	private EditText injectCodeText;
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, com.oinux.lanmitm.R.layout.inject_activity);

		setBarTitle(Html.fromHtml("<b>" + getString(R.string.code_inject)
				+ "</b> - <small>" + AppContext.getTarget().getIp() + "</small>"));

		headerView = findViewById(R.id.header_view);

		injectCheckBox = (CheckBox) findViewById(R.id.inject_check_box);
		if (AppContext.isInjectRunning) {
			injectCheckBox.setChecked(true);
		} else {
			injectCheckBox.setChecked(false);
		}
		injectCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(InjectActivity.this, InjectService.class);
				if (isChecked) {
					headerView.setVisibility(View.VISIBLE);
					startService(intent);
				} else {
					headerView.setVisibility(View.GONE);
					stopService(intent);
				}
			}
		});

		findViewById(R.id.inject_save_btn).setOnClickListener(this);

		urlPatternText = (EditText) findViewById(R.id.inject_pattern);
		injectCodeText = (EditText) findViewById(R.id.inject_code);

		urlPatternText.setText(HttpProxy.getInstance().getInjectPattern().pattern());
		injectCodeText.setText(HttpProxy.getInstance().getInject());
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right, R.anim.slide_right_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inject_save_btn:
			HttpProxy.getInstance().setInject(
					injectCodeText.getEditableText().toString());
			String pattern = urlPatternText.getEditableText().toString();
			if (!pattern.isEmpty()) {
				try {
					HttpProxy.getInstance().setInjectPattern(
							Pattern.compile(pattern));
				} catch (PatternSyntaxException e) {
					Toast.makeText(this,
							getString(R.string.regular_expression_invalid),
							Toast.LENGTH_SHORT).show();
				}
			}
			Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
