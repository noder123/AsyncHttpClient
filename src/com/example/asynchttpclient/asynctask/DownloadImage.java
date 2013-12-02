package com.example.asynchttpclient.asynctask;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownloadImage extends AsyncTask<String, Integer, Bitmap> {
	private static final String TAG=DownloadImage.class.getSimpleName();
	private WeakReference<ImageView> imageView;
	private WeakReference<ProgressBar> progressBar;
	private WeakReference<Context> context;
	
	public DownloadImage(ImageView imageView, ProgressBar progressBar, Context context){
		this.imageView=new WeakReference<ImageView>(imageView);
		this.progressBar=new WeakReference<ProgressBar>(progressBar);
		this.context=new WeakReference<Context>(context);
	}
	
	@Override
	protected void onPreExecute() {
		if(progressBar!=null){
			progressBar.get().setVisibility(View.VISIBLE);
            progressBar.get().setMax(100);
        }
		super.onPreExecute();
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		InputStream inputStream=null;
		Bitmap bitmap=null;
        ByteArrayOutputStream outputStream=null;
		try{
			URL url=new URL(params[0]);
			URLConnection connection=url.openConnection();
			connection.connect();
			//get the length of the file
			int lengthOfFile=connection.getContentLength();
			byte[] buffer=new byte[1024];
            int total=0;
            int count=0;
			inputStream=new BufferedInputStream(url.openStream(), 8192);
            outputStream=new ByteArrayOutputStream(lengthOfFile);
            while((count=inputStream.read(buffer))!=-1){
                total=total+count;
                publishProgress(new Integer((total*100)/lengthOfFile));
                outputStream.write(buffer, 0, count);
            }
			bitmap=BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, total);
			inputStream.close();
            outputStream.flush();
            outputStream.close();

		}catch(Exception e){
            Log.d(TAG,e.toString());
            return null;
		}
		return bitmap;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
        progressBar.get().setProgress(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
        progressBar.get().setProgress(0);
        progressBar.get().setVisibility(View.GONE);
        if(imageView!=null){
            if(result!=null){

                imageView.get().setImageBitmap(result);
            }
        }
		super.onPostExecute(result);
	}


}
