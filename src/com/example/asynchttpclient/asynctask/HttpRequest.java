package com.example.asynchttpclient.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: I302083
 * Date: 11/29/13
 * Time: 9:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequest extends AsyncTask<String, Integer, String> {
    private WeakReference<ProgressDialog> progressDialogWeakReference;
    private WeakReference<Context> contextWeakReference;

    public HttpRequest(ProgressDialog progressDialog, Context context){
        progressDialogWeakReference=new WeakReference<ProgressDialog>(progressDialog);
        contextWeakReference=new WeakReference<Context>(context);
    }
    @Override
    protected void onPreExecute() {
        if(progressDialogWeakReference!=null){
            progressDialogWeakReference.get().show();
        }
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        String response=null;
        HttpClient httpClient=new DefaultHttpClient();
        HttpGet httpGet =new HttpGet(arg0[0]);
        HttpResponse httpResponse=null;
        try{
            httpResponse=httpClient.execute(httpGet);
            HttpEntity httpEntity=httpResponse.getEntity();
            if(httpEntity!=null){
                InputStream is=httpEntity.getContent();
                response=convertStreamToString(is);
            }

        }catch(ClientProtocolException e){
            Log.d("ClientProtocolException", e.toString());
        }
        catch(IOException e){
            Log.d("IOException", e.toString());
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d("progress", progress[0].toString());
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(progressDialogWeakReference!=null){
            progressDialogWeakReference.get().dismiss();
        }
    }


    private String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
