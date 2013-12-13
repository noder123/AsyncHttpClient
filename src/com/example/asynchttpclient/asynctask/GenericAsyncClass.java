package com.example.asynchttpclient.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: I302083
 * Date: 12/3/13
 * Time: 2:51 PM
 * This class is a generic asynctask which will return an Object according to the nature of the result
 *
 */
public class GenericAsyncClass extends AsyncTask<String, Integer, Object> {
    Payload payload;
    WeakReference<ProgressBar> progressBarWeakReference;
    WeakReference<ProgressDialog> progressDialogWeakReference;

    public GenericAsyncClass(Payload payload){
        this.payload=payload;
    }

    /*
    *To show the progressbar
    */
    public GenericAsyncClass(Payload payload, ProgressBar progressBar){
        this.payload=payload;
        progressBarWeakReference=new WeakReference<ProgressBar>(progressBar);
    }

    /*
    *To show the progressdialog
    */
    public GenericAsyncClass(Payload payload, ProgressDialog progressDialog){
        this.payload=payload;
        progressDialogWeakReference=new WeakReference<ProgressDialog>(progressDialog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressDialogWeakReference!=null){
            progressDialogWeakReference.get().show();
            return;
        }
        else if(progressBarWeakReference!=null){
            progressBarWeakReference.get().setVisibility(View.VISIBLE);
            progressBarWeakReference.get().setProgress(0);
            return;
        }
        return;
    }

    @Override
    protected Object doInBackground(String... strings) {
        return null;
    }

    public static class Payload{
        public int taskType;          //the task type to be performed in doInBackground
        public Exception exception;
        public Object[] data;         //the data to be passed to the doInBackground
        public Object result;         //the result to be given to onPostExecute
    }
}
