package com.example.animedictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.animedictionary.connect.AnimePage;
import com.example.animedictionary.connect.IAPI;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageAnime extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    int anime_number = 0;
    String page = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_anime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.animeInfo);
        imageView = (ImageView) findViewById(R.id.animeImage);
        getAnime();
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
    }

    //это чудо работает ассинхранно
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        //сюда подставляется ссылка на картинку
        //это хуйня является реализацией класса родителя, вызова этой функции не будет
        //явного по крайне мерее
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        //действия после загрузки изображений
        protected void onPostExecute(Bitmap result) {
            //когда изображение получено его нужно разместить в виджете
            imageView.setImageBitmap(result);
        }
    }

    private void getAnime(){
        MainActivity.retrofit.create(IAPI.class).getInfoAnime(page, anime_number).enqueue(new Callback<AnimePage>() {
            @Override
            public void onResponse(Call<AnimePage> call, Response<AnimePage> response) {
                anime_number = response.body().animeNumber;
                page = response.body().page;
                (new DownloadImageTask()).execute(response.body().linkImage);
                textView.setText(response.body().infoAnime);
            }

            @Override
            public void onFailure(Call<AnimePage> call, Throwable t) {

            }
        });
    }

    public void nextPage(View view){
        anime_number++;
        getAnime();
    }
}