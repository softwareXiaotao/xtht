package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.presenter;

import android.app.Application;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.LogUtils;
import com.jess.arms.utils.UiUtils;
import com.jess.arms.widget.imageloader.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;

import javax.inject.Inject;

import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.app.MyApplication;
import com.walnutin.xtht.bracelet.mvp.model.entity.UserBean;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract.RegistbyEailContract;

import java.util.HashMap;
import java.util.List;


@ActivityScope
public class RegistbyEailPresenter extends BasePresenter<RegistbyEailContract.Model, RegistbyEailContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public RegistbyEailPresenter(RegistbyEailContract.Model model, RegistbyEailContract.View rootView
            , RxErrorHandler handler, Application application
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }


    public void regist(String email, String password) {

        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("platform", "Android");
        hashMap.put("userType", 1);
        String jsonStr = JSONObject.toJSONString(hashMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), jsonStr);
        mModel.get_registdata(body)
                .subscribeOn(Schedulers.io()).doOnSubscribe(disposable -> {
            mRootView.showLoading();//显示上拉刷新的进度条
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean users) {
                        mRootView.hideLoading();
                        DataHelper.saveDeviceData(MyApplication.getAppContext(), "UserBean", users);
                        DataHelper.setStringSF(MyApplication.getAppContext(), "username", email);
                        DataHelper.setStringSF(MyApplication.getAppContext(), "userpassword", password);
                        DataHelper.setStringSF(MyApplication.getAppContext(), "load_tag", "email");
                        DataHelper.setStringSF(MyApplication.getAppContext(), "token", users.getToken());
                        DataHelper.setStringSF(MyApplication.getAppContext(), "isload", "default");
                        mRootView.regist_success();
                        //mRootView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRootView.hideLoading();
                        super.onError(e);
                        LogUtils.debugInfo("load_p" + e.toString());
                    }


                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

}