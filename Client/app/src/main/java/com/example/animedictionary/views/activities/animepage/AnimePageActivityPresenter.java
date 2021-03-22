package com.example.animedictionary.views.activities.animepage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.animedictionary.services.anime.AnimeService;
import com.example.animedictionary.tools.rx.Transformer;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.disposables.Disposable;

public class AnimePageActivityPresenter {
    private final WeakReference<AnimePageActivity> activity;
    private final AnimeService animeService;
    private Disposable loadRequest;
    private int animeNumber = -1;
    private String page = "1";

    public AnimePageActivityPresenter(@NonNull AnimePageActivity activity, @NonNull AnimeService animeService) {
        this.activity = new WeakReference<>(activity);
        this.animeService = animeService;
    }

    protected void getAnime(boolean forward) {
        if (!isInLoading()) {
            //получим инфу из бд о следующем аниме на текуей странице, если такого нет
            //в бд сработает переход на новую страницу
            loadRequest = animeService.getInfoAnime(page, forward ? animeNumber + 1 : animeNumber - 1)
                .map(animePage -> { // выполняется в потоке загрузки
                    Bitmap mIcon11 = null;
                    if (!animePage.linkImage.equals("error")) {
                        try {
                            InputStream in = new java.net.URL(animePage.linkImage).openStream();
                            mIcon11 = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Pair<>(animePage, mIcon11);
                })
                .compose(Transformer.actionBasicScheduler())
                .doFinally(() -> loadRequest = null)
                .subscribe(animePage -> {
                    //получаем номер текущего аниме на странице
                    animeNumber = animePage.first.animeNumber;
                    //получаем текущую страницу
                    page = animePage.first.page;

                    //сразу грузим картинку
                    if (animePage.second != null) {
                        activity.get().showAnime(animePage.first, animePage.second);
                    }
                })
            ;
        }
    }

    private boolean isInLoading() {
        return loadRequest != null && !loadRequest.isDisposed();
    }
}
