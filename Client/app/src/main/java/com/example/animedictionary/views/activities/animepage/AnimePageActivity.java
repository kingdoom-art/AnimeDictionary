package com.example.animedictionary.views.activities.animepage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.animedictionary.databinding.ActivityPageAnimeBinding;
import com.example.animedictionary.models.anime.AnimePage;
import com.example.animedictionary.services.anime.AnimeService;
import com.example.animedictionary.tools.rx.Transformer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;

import java.io.InputStream;

import io.reactivex.rxjava3.disposables.Disposable;

public class AnimePageActivity extends AppCompatActivity {
    private AnimeService animeService;
    private ActivityPageAnimeBinding binding;
    private Disposable loadRequest;
    private int animeNumber = -1;
    private String page = "1";

    public void init(@NonNull AnimeService animeService) {
        this.animeService = animeService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

        //отобразим стартовую инфу
        getAnime(true);
    }

    private void setupView() {
        binding = ActivityPageAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle(getTitle());
        binding.button8.setOnClickListener(view -> getAnime(true));
        binding.button7.setOnClickListener(view -> getAnime(false));
    }

    private boolean isInLoading() {
        return loadRequest != null && !loadRequest.isDisposed();
    }

    private void getAnime(boolean forward) {
        if (!isInLoading()) {
            //получим инфу из бд о следующем аниме на текуей странице, если такого нет
            //в бд сработает переход на новую страницу
            loadRequest = animeService.getInfoAnime(page, forward ? animeNumber + 1 : animeNumber - 1)
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
                    animeNumber = animePage.first.animeNumber;
                    //получаем текущую страницу
                    page = animePage.first.page;

                    //сразу грузим картинку
                    if (animePage.second != null) {
                        showAnime(animePage.first, animePage.second);
                    }
                })
            ;
        }
    }

    private void showAnime(@NonNull AnimePage animePage, @NonNull Bitmap image) {
        //выводим инфу
        binding.scrolling.animeInfo.setText(animePage.infoAnime);
        binding.scrolling.animeImage.setImageBitmap(image);
    }
}