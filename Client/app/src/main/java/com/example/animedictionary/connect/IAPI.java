package com.example.animedictionary.connect;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IAPI {
    //собственно сам запрос, который хотим отрпавить
    @GET("/")
    Call<TestConnection> testConnect();
}
