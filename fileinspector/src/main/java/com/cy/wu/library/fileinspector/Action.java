package com.cy.wu.library.fileinspector;

/**
 * Created by wcy on 17/2/23.
 */

public interface Action {
    void doAction(final FileInfo fileInfo, ActionCallBack callBack);

    interface ActionCallBack{
        void callBack(boolean needReload);
    }
}
