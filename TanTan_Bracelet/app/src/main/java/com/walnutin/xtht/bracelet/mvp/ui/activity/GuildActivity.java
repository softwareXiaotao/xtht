package com.walnutin.xtht.bracelet.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.base.BaseApplication;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.DataHelper;
import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.app.MyApplication;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.presenter.LoadingPresenter;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity.LoadActivity;

/**
 * Created by suns on 2017-06-14.
 */

public class GuildActivity extends BaseActivity {

    String isload = "";
    LoadingPresenter loadingPresenter;
    @Override
    public void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int initView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_guild;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        isload = DataHelper.getStringSF(MyApplication.getAppContext(), "isload");
        loadingPresenter=new LoadingPresenter();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isload.equals("default")) {
                    startActivity(new Intent(GuildActivity.this, LoadActivity.class));
                    finish();
                } else {
                    if (!DataHelper.getStringSF(MyApplication.getAppContext(), "username").equals("default")&&!DataHelper.getStringSF(MyApplication.getAppContext(), "userpassword").equals("default")){



                    }


                    startActivity(new Intent(GuildActivity.this, MainActivity.class));
                    finish();
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
