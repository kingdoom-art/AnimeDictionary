package com.example.animedictionary.views.activities.main;

import androidx.annotation.NonNull;

import com.example.animedictionary.services.anime.AnimeService;
import com.example.animedictionary.tools.rx.Transformer;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivityPresenter {
    private final WeakReference<MainActivity> activity;
    private final AnimeService animeService;
    private Disposable testConnectRequest;

    public MainActivityPresenter(@NonNull MainActivity activity, @NonNull AnimeService animeService) {
        this.activity = new WeakReference<>(activity);
        this.animeService = animeService;
    }

    protected void testConnectServer() {
        if (!isInLoading()) {
            //чудо запустится ассинхранно, а пока оно грудится, лупанем индикатор загрузки
            activity.get().showLoading();
            testConnectRequest = animeService.testConnect()
                .compose(Transformer.actionBasicScheduler())
                .doFinally(() -> testConnectRequest = null)
                .subscribe(testConnection -> activity.get().openAnimePageActivity()
                    , error -> activity.get().showLoadingError())
            ;
        }
    }

    private boolean isInLoading() {
        return testConnectRequest != null && !testConnectRequest.isDisposed();
    }
}
