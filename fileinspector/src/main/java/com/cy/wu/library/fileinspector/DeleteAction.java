package com.cy.wu.library.fileinspector;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;

/**
 * Created by wcy on 17/2/23.
 */

public class DeleteAction implements Action {

    private Context mContext;
    private String mActionName;

    public DeleteAction(Context context, String name) {
        this.mContext = context;
        this.mActionName = name;
    }

    @Override
    public void doAction(final FileInfo fileInfo, final ActionCallBack actionCallBack) {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("Delete File")
                .setMessage("delete this file " + fileInfo.fileName + "?")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DeleteAsyncTask asyncTask = new DeleteAsyncTask(mContext, actionCallBack);
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

    class DeleteAsyncTask  extends AsyncTask<String, Integer, Boolean>{

        private Context mContext;

        private ProgressDialog mProgressDialog;

        private ActionCallBack mCallBack;

        public DeleteAsyncTask(Context context, ActionCallBack callBack) {
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
            return FileUtil.deleteDir(file);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mProgressDialog.dismiss();
            Toast.makeText(mContext, aBoolean ? "Delete Success" : "Delete Failure",
                    Toast.LENGTH_LONG).show();
            if(mCallBack != null){
                mCallBack.callBack(aBoolean);
            }

        }
    }
}
