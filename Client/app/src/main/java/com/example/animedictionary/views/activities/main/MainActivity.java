package com.example.animedictionary.views.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.animedictionary.views.activities.animepage.AnimePageActivity;
import com.example.animedictionary.R;
import com.example.animedictionary.services.anime.AnimeService;
import com.example.animedictionary.databinding.ActivityMainBinding;
import com.example.animedictionary.tools.rx.Transformer;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private AnimeService animeService;
    private ActivityMainBinding binding;
    private Disposable testConnectRequest;

    public void init(@NonNull AnimeService animeService) {
        this.animeService = animeService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

        testConnectServer();
    }

    private void setupView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.reLoad.setOnClickListener(view -> testConnectServer());
    }

    private boolean isInLoading() {
        return testConnectRequest != null && !testConnectRequest.isDisposed();
    }

    private void testConnectServer() {
        if (!isInLoading()) {
            //чудо запустится ассинхранно, а пока оно грудится, лупанем индикатор загрузки
            showLoading();
            testConnectRequest = animeService.testConnect()
                .compose(Transformer.actionBasicScheduler())
                .doFinally(() -> testConnectRequest = null)
                .subscribe(testConnection -> {
                    //если коннект прошел удачно переводим на страницу с аниме
                    Intent intent = new Intent(MainActivity.this, AnimePageActivity.class);
                    startActivity(intent);
                }, error -> showLoadingError())
            ;
        }
    }

    private void showLoading() {
        binding.reLoad.setVisibility(View.INVISIBLE);
        binding.textView.setText(R.string.load);
    }

    private void showLoadingError() {
        //ошибочка, например, если сервер не доступен
        binding.textView.setText(R.string.error);
        //покажем кнопку перезагрузки
        binding.reLoad.setVisibility(View.VISIBLE);
    }
}