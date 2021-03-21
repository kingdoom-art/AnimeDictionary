package com.example.animedictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.tools.rx.Transformer;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import io.reactivex.rxjava3.disposables.Disposable;

public class PageAnime extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    int anime_number = -1;
    String page = "1";
    private Disposable loadRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_anime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //инициализируем элементы, в которых будем отображать инфу об аниме
        textView = (TextView) findViewById(R.id.animeInfo);
        imageView = (ImageView) findViewById(R.id.animeImage);
        //отобразим стартовую инфу
        getAnime(/*true*/);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
    }

    private boolean isInLoading() {
        return loadRequest != null && !loadRequest.isDisposed();
    }

    private void getAnime(/*boolean forward*/) {
        if (!isInLoading()) {
            //получим инфу из бд о следующем аниме на текуей странице, если такого нет
            //в бд сработает переход на новую страницу
            loadRequest = MainActivity.retrofit.create(IAPI.class)
                .getInfoAnime(page, /*forward ? */anime_number + 1/* : anime_number - 1*/)
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
                        textView.setText(animePage.first.infoAnime);
                    }
                })
            ;
        }
    }
}