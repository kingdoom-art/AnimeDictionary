package com.example.animedictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.animedictionary.connect.AnimePage;
import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.connect.TestConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //собственна сама ссылочка к нашуму серверу
    public static final String url = "http://192.168.0.110:25525";
    public TextView textView;
    public ProgressBar progressBar;
    public Button reLoad;
    //адаптер интерфейсов джава к http вызовам
    public static Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        reLoad = (Button) findViewById(R.id.reLoad);

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        testConnectServer();

    }

    private void testConnectServer(){
        //чудо запустится ассинхранно, а пока оно грудится, лупанем индикатор загрузки
        retrofit.create(IAPI.class).testConnect().enqueue(new Callback<TestConnection>() {
            @Override
            public void onResponse(Call<TestConnection> call, Response<TestConnection> response) {
                Intent intent = new Intent(MainActivity.this, PageAnime.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<TestConnection> call, Throwable t) {
                //ошибочка, например, если сервер не доступен
                textView.setText(R.string.error);
                //покажем кнопку перезагрузки
                reLoad.setVisibility(View.VISIBLE);
            }
        });
    }

    public void reLoad(View view){
        reLoad.setVisibility(View.INVISIBLE);
        textView.setText(R.string.load);
        testConnectServer();
    }
}