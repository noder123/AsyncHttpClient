package com.example.asynchttpclient;

import java.io.*;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.*;
import com.example.asynchttpclient.asynctask.DownloadImage;
import com.example.asynchttpclient.service.DownloadService;
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

public class MainActivity extends Activity implements OnClickListener {
	Button button;
	ProgressBar progressBar;
    ImageView imageView;
    private BroadcastReceiver imageReceiver;
    private ProgressDialog progressDialog;
    private BroadcastReceiver updateReceiver;
    private Notification notification;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button=(Button)findViewById(R.id.button);
		button.setOnClickListener(this);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		imageView=(ImageView)findViewById(R.id.imageView);
		progressBar.setVisibility(View.GONE);
        imageReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String location=intent.getStringExtra("location");
                if(location==null || location.length()==0){
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
                File imageFile=new File(location);
                if(!imageFile.exists()){
                    //progressDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Unable to download file", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.GONE);
                Bitmap b= BitmapFactory.decodeFile(location);
                imageView.setImageBitmap(b);
                //progressDialog.dismiss();
            }
        };

        updateReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int update=intent.getIntExtra("update", 0);
                progressBar.setProgress(update);
                //notification.contentView.setProgressBar(R.id.download_progressBar, 100, update, false);
            }
        };

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
                //progressDialog=ProgressDialog.show(this, "Please Wait..", "");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                progressBar.setProgress(0);
                IntentFilter intentFilter=new IntentFilter();
                intentFilter.addAction(DownloadService.TRANS_DONE);
                registerReceiver(imageReceiver, intentFilter);

                IntentFilter intentFilterUpdate=new IntentFilter();
                intentFilterUpdate.addAction(DownloadService.TRANS_UPDATE);
                registerReceiver(updateReceiver, intentFilterUpdate);

                Intent intent=new Intent(this, DownloadService.class);
                intent.putExtra("url", "http://www.technobuffalo.com/wp-content/uploads/2012/12/Google-Apps.jpeg");
                startService(intent);
				//new DownloadImage(imageView, progressBar, getApplicationContext()).execute("http://www.technobuffalo.com/wp-content/uploads/2012/12/Google-Apps.jpeg");
			}
		}
		
	}
}
