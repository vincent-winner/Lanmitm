package com.oinux.lanmitm.ui;

import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.oinux.lanmitm.ActionBarActivity;
import com.oinux.lanmitm.R;

public class HttpClone extends ActionBarActivity {

	private TextView cloneLogText;
	private EditText cloneUrlText;
	private Button cloneBtn;
	private Stack<String> urlStack;
	private ExecutorService urlParseExecutors;
	private Handler logHandler;
	private CheckBox cssBtn, jsBtn, pngBtn, jpgBtn, gifBtn;
	private int infoColor, warnColor, errorColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.http_clone);
		setBarTitle(Html.fromHtml("<b>" + getString(R.string.http_clone) + "</b>"));

		cloneLogText = (TextView) findViewById(R.id.http_clone_log);
		cloneUrlText = (EditText) findViewById(R.id.clone_url);

		infoColor = getResources().getColor(R.color.log_info);
		warnColor = getResources().getColor(R.color.log_warn);
		errorColor = getResources().getColor(R.color.log_error);

		cssBtn = (CheckBox) findViewById(R.id.css_check);
		jsBtn = (CheckBox) findViewById(R.id.js_check);
		pngBtn = (CheckBox) findViewById(R.id.png_check);
		jpgBtn = (CheckBox) findViewById(R.id.jpg_check);
		gifBtn = (CheckBox) findViewById(R.id.gif_check);

		cloneBtn = (Button) findViewById(R.id.clone_btn);
		cloneBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = cloneUrlText.getEditableText().toString();
				if (!TextUtils.isEmpty(url))
					cloneSite(url);
			}
		});

		logHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				BackgroundColorSpan colorSpan = null;
				switch (msg.what) {
				case Log.INFO:
					colorSpan = new BackgroundColorSpan(infoColor);
					cloneLogText.append("I: ");
					break;
				case Log.WARN:
					colorSpan = new BackgroundColorSpan(warnColor);
					cloneLogText.append("W: ");
					break;
				case Log.ERROR:
					colorSpan = new BackgroundColorSpan(errorColor);
					cloneLogText.append("E: ");
					break;
				default:
					break;
				}
				SpannableString span = new SpannableString((String) msg.obj);
				span.setSpan(colorSpan, 0, span.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				cloneLogText.append(span);
			}
		};
	}

	private void cloneSite(String url) {
		urlStack = new Stack<String>();
		urlStack.push(url);
		urlParseExecutors = Executors.newFixedThreadPool(10);
		urlParseExecutors.execute(new UrlParseThread());
	}

	class UrlParseThread extends Thread {

		@Override
		public void run() {
			String url = urlStack.pop();
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					logHandler.obtainMessage(Log.INFO,
							getString(R.string.obtain_success))
							.sendToTarget();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				logHandler.obtainMessage(Log.ERROR, e.getMessage()).sendToTarget();
			}
			httpClient.getConnectionManager().shutdown();
		}
	}
}
