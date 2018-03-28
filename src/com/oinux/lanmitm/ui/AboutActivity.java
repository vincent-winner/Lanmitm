package com.oinux.lanmitm.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.oinux.lanmitm.R;

public class AboutActivity extends Activity implements OnClickListener {

	private TextView homeLink;
	private TextView upgradeLink;
	private TextView githubLink;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (dm.widthPixels);
		getWindow().setAttributes(lp);
		
		homeLink = (TextView) findViewById(R.id.my_home_link);
		homeLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); 
		homeLink.getPaint().setAntiAlias(true);
		homeLink.setOnClickListener(this); 
		upgradeLink = (TextView) findViewById(R.id.upgrade_history_link);
		upgradeLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); 
		upgradeLink.getPaint().setAntiAlias(true);
		upgradeLink.setOnClickListener(this); 
		githubLink = (TextView) findViewById(R.id.github_link);
		githubLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); 
		githubLink.getPaint().setAntiAlias(true);
		githubLink.setOnClickListener(this); 
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, BrowserActivity.class);
		intent.putExtra("view_type", BrowserActivity.BROWSER_COMMON);
		switch (v.getId()) {
		case R.id.my_home_link:
			intent.putExtra("url", homeLink.getText());
			startActivity(intent);
			break;
		case R.id.upgrade_history_link:
			intent.putExtra("url", upgradeLink.getText());
			startActivity(intent);
			break;
		case R.id.github_link:
			intent.putExtra("url", githubLink.getText());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
