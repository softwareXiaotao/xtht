package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract;

import android.content.Context;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.walnutin.xtht.bracelet.mvp.model.entity.BasicItemSupport;
import com.walnutin.xtht.bracelet.mvp.model.entity.BasicSettingsMenue;
import com.walnutin.xtht.bracelet.mvp.ui.adapter.BasicSettingsAdapter;

import java.util.List;


public interface BasicSettingsContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void setAdapter(List<BasicSettingsMenue> settingsMenues);
        Context getContext();
    }

    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {
        List<BasicSettingsMenue> getBasicSettingsMenues(BasicItemSupport basicItemSupport);
    }
}