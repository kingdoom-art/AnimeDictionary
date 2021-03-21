package com.example.animedictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.databinding.ActivityPageAnimeBinding;
import com.example.animedictionary.tools.rx.Transformer;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;
import android.view.View;

import java.io.InputStream;

import io.reactivex.rxjava3.disposables.Disposable;

public class PageAnime extends AppCompatActivity {
    int anime_number = -1;
    String page = "1";
    private Disposable loadRequest;
    private ActivityPageAnimeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPageAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button8.setOnClickListener(this::nextPage);
        binding.button7.setOnClickListener(this::previousPage);
        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle(getTitle());
        //отобразим стартовую инфу
        getAnime(true);
    }

    private boolean isInLoading() {
        return loadRequest != null && !loadRequest.isDisposed();
    }

    private void getAnime(boolean forward) {
        if (!isInLoading()) {
            //получим инфу из бд о следующем аниме на текуей странице, если такого нет
            //в бд сработает переход на новую страницу
            loadRequest = MainActivity.retrofit.create(IAPI.class)
                .getInfoAnime(page, forward ? anime_number + 1 : anime_number - 1)
                .compose(Transformer.actionBasicScheduler())
                .doFinally(() -> loadRequest = null)
                .map(animePage -> {
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
                }).subscribe(animePage -> {
                    //получаем номер текущего аниме на странице
                    anime_number = animePage.first.animeNumber;
                    //получаем текущую страницу
                    page = animePage.first.page;

                    //сразу грузим картинку
                    if (animePage.second != null) {
                        //выводим инфу
                        binding.scrolling.animeInfo.setText(animePage.first.infoAnime);
                        binding.scrolling.animeImage.setImageBitmap(animePage.second);
                    }
                })
            ;
        }
    }

    public void nextPage(View view) {
        getAnime(true);
    }

    public void previousPage(View view) {
        getAnime(false);
    }
}