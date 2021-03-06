package com.walnutin.xtht.bracelet.mvp.ui.activity.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.walnutin.xtht.bracelet.mvp.ui.activity.di.module.DateSelectModule;

import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity.DateSelectActivity;

@ActivityScope
@Component(modules = DateSelectModule.class, dependencies = AppComponent.class)
public interface DateSelectComponent {
    void inject(DateSelectActivity activity);
}