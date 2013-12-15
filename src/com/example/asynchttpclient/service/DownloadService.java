package com.example.asynchttpclient.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.asynchttpclient.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by I302083 on 12/13/13.
 */
public class DownloadService extends IntentService {
    File cacheDir;
    Intent updateIntent;
    Notification notification;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    public static  final String TRANS_DONE="done";
    public static final String TRANS_UPDATE="update";

    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadService(){
        super("");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        updateIntent=new Intent(TRANS_UPDATE);
        String tempLocation=Environment.getExternalStorageDirectory().getPath();
        cacheDir=new File(tempLocation);
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.download_progress);
        //remoteViews.setProgressBar(R.id.download_progressBar, 100, 0, false);

        notificationBuilder=new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Downloading..")
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);

        notificationManager =(NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String remoteUrl=intent.getExtras().getString("url");
        String location;
        int count;
        String fileName="file.jpeg";
        File tmp = new File(cacheDir.getPath()
                + File.separator +fileName);
        try{
            URL url = new URL(remoteUrl);
            HttpURLConnection httpCon =
                    (HttpURLConnection)url.openConnection();
            if(httpCon.getResponseCode() != 200)
                throw new Exception("Failed to connect");
            InputStream is = httpCon.getInputStream();
            FileOutputStream fos = new FileOutputStream(tmp);
            int lengthOfFile=httpCon.getContentLength();
            byte data[] = new byte[1024];

            int total = 0;

            while ((count = is.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                //notificationBuilder.build().contentView.setProgressBar(R.id.download_progressBar, 100, (total*100)/lengthOfFile, false);
                notifyUpdate(new Integer((total*100)/lengthOfFile));
                notificationBuilder.setProgress(100, (total*100)/lengthOfFile, false);
                notificationManager.notify(0, notificationBuilder.build());
                // writing data to file
                fos.write(data, 0, count);
            }
            fos.flush(); fos.close();
            is.close();
            location = tmp.getAbsolutePath();
            notifyFinished(location, remoteUrl);
        }catch(Exception e){
            Log.e("Service", e.toString());
        }
    }

    private void notifyFinished(String location, String remoteUrl){
        Intent intent=new Intent(TRANS_DONE);
        intent.putExtra("location", location);
        intent.putExtra("url", remoteUrl);
        DownloadService.this.sendBroadcast(intent);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("Done");
        notificationBuilder.setOngoing(false);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void notifyUpdate(int update){
        updateIntent.putExtra("update", update);
        DownloadService.this.sendBroadcast(updateIntent);
    }
}
