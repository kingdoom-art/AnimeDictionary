package com.example.animedictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.databinding.ActivityMainBinding;
import com.example.animedictionary.tools.rx.Transformer;

import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //собственна сама ссылочка к нашуму серверу
    public static final String url = "http://192.168.0.110:25525";
    private ActivityMainBinding binding;
    //адаптер интерфейсов джава к http вызовам
    public static Retrofit retrofit;
    private Disposable testConnectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.reLoad.setOnClickListener(this::reLoad);

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
                    binding.textView.setText(R.string.error);
                    //покажем кнопку перезагрузки
                    binding.reLoad.setVisibility(View.VISIBLE);
                })
            ;
        }
    }

    public void reLoad(View view) {
        binding.reLoad.setVisibility(View.INVISIBLE);
        binding.textView.setText(R.string.load);
        testConnectServer();
    }
}