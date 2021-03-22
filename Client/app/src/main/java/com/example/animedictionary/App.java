package com.example.animedictionary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.animedictionary.services.anime.AnimeService;
import com.example.animedictionary.views.activities.animepage.AnimePageActivity;
import com.example.animedictionary.views.activities.main.MainActivity;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private AnimeService animeService;

    @Override
    public void onCreate() {
        super.onCreate();
        initTools();
        initViewsInjector();
    }

    private void initTools() {
        animeService = new Retrofit.Builder()
                .baseUrl("http://192.168.0.110:25525")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(AnimeService.class)
        ;
    }

    private void initViewsInjector() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).init(animeService);
                } else if (activity instanceof AnimePageActivity) {
                    ((AnimePageActivity) activity).init(animeService);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                // do nothing
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                // do nothing
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                // do nothing
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                // do nothing
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                // do nothing
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // do nothing
            }
        });
    }
}