package com.example.animedictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.tools.rx.Transformer;

import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //собственна сама ссылочка к нашуму серверу
    public static final String url = "http://192.168.0.110:25525";
    public TextView textView;
    public ProgressBar progressBar;
    public Button reLoad;
    //адаптер интерфейсов джава к http вызовам
    public static Retrofit retrofit;
    private Disposable testConnectRequest;

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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        testConnectServer();
    }

    private boolean isInLoading() {
        return testConnectRequest != null && !testConnectRequest.isDisposed();
    }

    private void testConnectServer(){
        if (!isInLoading()) {
            //чудо запустится ассинхранно, а пока оно грудится, лупанем индикатор загрузки
            testConnectRequest = retrofit.create(IAPI.class).testConnect()
                .compose(Transformer.actionBasicScheduler())
                .doFinally(() -> testConnectRequest = null)
                .subscribe(testConnection -> {
                    //если коннект прошел удачно переводим на страницу с аниме
                    Intent intent = new Intent(MainActivity.this, PageAnime.class);
                    startActivity(intent);
                }, error -> {
                    //ошибочка, например, если сервер не доступен
                    textView.setText(R.string.error);
                    //покажем кнопку перезагрузки
                    reLoad.setVisibility(View.VISIBLE);
                })
            ;
        }
    }
/*
    public void reLoad() {
        reLoad.setVisibility(View.INVISIBLE);
        textView.setText(R.string.load);
        testConnectServer();
    }
*/
}