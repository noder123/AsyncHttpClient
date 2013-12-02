package com.example.asynchttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.asynchttpclient.asynctask.DownloadImage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {
	Button button;
	ProgressBar progressBar;
    ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button=(Button)findViewById(R.id.button);
		button.setOnClickListener(this);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		imageView=(ImageView)findViewById(R.id.imageView);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.button:{
				new DownloadImage(imageView, progressBar, getApplicationContext()).execute("http://www.technobuffalo.com/wp-content/uploads/2012/12/Google-Apps.jpeg");
			}
		}
		
	}
}
