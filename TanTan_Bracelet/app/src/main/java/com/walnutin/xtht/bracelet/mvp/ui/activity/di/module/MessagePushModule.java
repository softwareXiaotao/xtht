package com.walnutin.xtht.bracelet.mvp.ui.activity.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract.MessagePushContract;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.model.MessagePushModel;


@Module
public class MessagePushModule {
    private MessagePushContract.View view;

    /**
     * 构建MessagePushModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MessagePushModule(MessagePushContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MessagePushContract.View provideMessagePushView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MessagePushContract.Model provideMessagePushModel(MessagePushModel model) {
        return model;
    }
}