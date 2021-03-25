package com.example.animedictionary.views.activities.animepage;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.animedictionary.databinding.ActivityPageAnimeBinding;
import com.example.animedictionary.models.anime.AnimePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AnimePageActivity extends AppCompatActivity {
    private AnimePageActivityPresenter presenter;
    private ActivityPageAnimeBinding binding;

    public void init(@NonNull AnimePageActivityPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

        //отобразим стартовую инфу
        presenter.getAnime(true);
    }

    private void setupView() {
        binding = ActivityPageAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle(getTitle());
        binding.buttonNext.setOnClickListener(view -> presenter.getAnime(true));
        binding.buttonPrev.setOnClickListener(view -> presenter.getAnime(false));
    }

    protected void showAnime(@NonNull AnimePage animePage, @NonNull Bitmap image) {
        //выводим инфу
        binding.scrolling.animeInfo.setText(animePage.infoAnime);
        binding.scrolling.animeImage.setImageBitmap(image);
    }
}