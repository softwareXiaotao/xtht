package com.walnutin.xtht.bracelet.mvp.ui.activity.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.walnutin.xtht.bracelet.mvp.ui.activity.di.module.ResetbyEmailModule;

import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity.ResetbyEmailActivity;

@ActivityScope
@Component(modules = ResetbyEmailModule.class, dependencies = AppComponent.class)
public interface ResetbyEmailComponent {
    void inject(ResetbyEmailActivity activity);
}