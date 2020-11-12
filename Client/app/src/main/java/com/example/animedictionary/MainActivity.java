package com.example.animedictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.animedictionary.connect.IAPI;
import com.example.animedictionary.connect.TestConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String url = "http://192.168.0.110:25525";
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(IAPI.class).testConnect().enqueue(new Callback<TestConnection>() {
            @Override
            public void onResponse(Call<TestConnection> call, Response<TestConnection> response) {
                //если все, ок. мы тут
                textView.setText(response.body().answer);
            }

            @Override
            public void onFailure(Call<TestConnection> call, Throwable t) {
                //ошибочка
                textView.setText(t.getMessage());
            }
        });

    }
}