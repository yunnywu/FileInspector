package com.cy.wu.library.fileinspector;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by wcy on 17/2/23.
 */

public class MD5Action implements Action {

    private Context mContext;
    private String mActionName;

    public MD5Action(Context context, String name) {
        this.mContext = context;
        this.mActionName = name;
    }

    @Override
    public void doAction(final FileInfo fileInfo, ActionCallBack actionCallBack) {
        if(!fileInfo.isFold) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("Check MD5")
                    .setMessage(fileInfo.fileName + "\n" + FileUtil.getFileMD5(fileInfo.filePath))
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    public String toString() {
        return mActionName;
    }
}
