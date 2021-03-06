package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.walnutin.xtht.bracelet.mvp.model.entity.User;
import com.walnutin.xtht.bracelet.mvp.model.entity.UserBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface RegistbyEailContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
         void  regist_success();
    }

    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {
        Observable<UserBean> get_registdata(RequestBody body);
    }
}