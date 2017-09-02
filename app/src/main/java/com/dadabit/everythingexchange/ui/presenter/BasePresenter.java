package com.dadabit.everythingexchange.ui.presenter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public abstract class BasePresenter<T extends MvpView> {
    HandlerThread thread;
    Looper looper;
    Handler handler;
    Handler uiHandler;

    private T mView;

    public BasePresenter() {}

    public HandlerThread getThread() {
        return thread;
    }

    public Looper getLooper() {
        return looper;
    }

    public Handler getHandler() {
        return handler;
    }

    public Handler getUiHandler() {
        if (uiHandler == null){
            uiHandler = new Handler(Looper.getMainLooper());
        }

        return uiHandler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void attachView(T t) {
        thread = new HandlerThread(getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        looper = thread.getLooper();
        mView = t;
    }

    public void detachView() {
        mView = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        if (thread != null) {
            thread.quit();
        }

        if (uiHandler != null){
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler = null;
        }
    }

    public T getView() {
        return mView;
    }

    public boolean isViewAttached() {
        return mView != null;
    }

}