package com.example.animedictionary.views.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.animedictionary.views.activities.animepage.AnimePageActivity;
import com.example.animedictionary.R;
import com.example.animedictionary.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private MainActivityPresenter presenter;
    private ActivityMainBinding binding;

    public void init(@NonNull MainActivityPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

        presenter.testConnectServer();
    }

    private void setupView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.reLoad.setOnClickListener(view -> presenter.testConnectServer());
    }

    protected void showLoading() {
        binding.reLoad.setVisibility(View.INVISIBLE);
        binding.textView.setText(R.string.load);
    }

    protected void showLoadingError() {
        //ошибочка, например, если сервер не доступен
        binding.textView.setText(R.string.error);
        //покажем кнопку перезагрузки
        binding.reLoad.setVisibility(View.VISIBLE);
    }

    protected void openAnimePageActivity() {
        Intent intent = new Intent(MainActivity.this, AnimePageActivity.class);
        startActivity(intent);
    }
}