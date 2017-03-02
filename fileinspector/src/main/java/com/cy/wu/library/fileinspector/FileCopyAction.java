package com.cy.wu.library.fileinspector;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by wcy on 17/2/23.
 */

public class FileCopyAction implements Action {

    private Context mContext;
    private String mActionName;

    public FileCopyAction(Context context, String name) {
        this.mContext = context;
        this.mActionName = name;
    }

    @Override
    public void doAction(final FileInfo fileInfo, final ActionCallBack actionCallBack) {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("Copy File")
                .setMessage("copy this file " + fileInfo.fileName + " to " +
                        mContext.getExternalFilesDir(null).getAbsolutePath())
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CopyFileAsyncTask asyncTask = new CopyFileAsyncTask(mContext, actionCallBack);
                        asyncTask.execute(fileInfo.filePath);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public String toString() {
        return mActionName;
    }

    class CopyFileAsyncTask extends AsyncTask<String, Integer, Boolean> {

        private Context mContext;

        private ProgressDialog mProgressDialog;

        private ActionCallBack mCallBack;

        public CopyFileAsyncTask(Context context, ActionCallBack callBack) {
            mContext = context;
            mCallBack = callBack;
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(params == null || params.length == 0 ){
                return false;
            }
            String filePath = params[0];
            File file = new File(filePath);
            File destFile = new File(mContext.getExternalFilesDir(null), file.getName());
            try {
                FileUtil.copyDirectory(file, destFile);
                return true;
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mProgressDialog.dismiss();
            Toast.makeText(mContext, aBoolean ? "Copy Success" : "Copy Failure",
                    Toast.LENGTH_LONG).show();
            if(mCallBack != null){
                mCallBack.callBack(aBoolean);
            }

        }
    }
}
