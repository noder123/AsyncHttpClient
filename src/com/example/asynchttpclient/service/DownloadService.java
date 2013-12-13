package com.example.asynchttpclient.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

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
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadService(){
        super("");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        String tempLocation=Environment.getExternalStorageDirectory().getPath();
        cacheDir=new File(tempLocation);
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String remoteUrl=intent.getExtras().getString("url");
        String location;
        int count;
        String fileName="file.jpeg";
        File tmp = new File(cacheDir.getPath()
                + File.separator +fileName);
        /*if(tmp.exists()){
            location = tmp.getAbsolutePath();
            notifyFinished(location, remoteUrl);
            stopSelf();
            return;
        }*/
        try{
            URL url = new URL(remoteUrl);
            HttpURLConnection httpCon =
                    (HttpURLConnection)url.openConnection();
            if(httpCon.getResponseCode() != 200)
                throw new Exception("Failed to connect");
            InputStream is = httpCon.getInputStream();
            FileOutputStream fos = new FileOutputStream(tmp);
            byte data[] = new byte[1024];

            long total = 0;

            while ((count = is.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called

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

    public static  final String TRANS_DONE="done";
    private void notifyFinished(String location, String remoteUrl){
        Intent intent=new Intent(TRANS_DONE);
        intent.putExtra("location", location);
        intent.putExtra("url", remoteUrl);
        DownloadService.this.sendBroadcast(intent);
    }
}
