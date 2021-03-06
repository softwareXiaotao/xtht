package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.walnutin.xtht.bracelet.mvp.model.api.service.UserService;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract.RunningIndoorContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class RunningIndoorModel extends BaseModel implements RunningIndoorContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public RunningIndoorModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
        super(repositoryManager);
        this.mGson = gson;
        this.mApplication = application;
    }

    @Override
    public Observable<String> sportData(RequestBody body) {
        return mRepositoryManager.obtainRetrofitService(UserService.class)
                .sportData(body);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

}