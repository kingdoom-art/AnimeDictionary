package com.example.animedictionary.connect;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAPI {
    //собственно сам запрос, который хотим отрпавить
    @GET("/")
    Call<TestConnection> testConnect();

    @GET("/get_anime")
    Call<AnimePage> getInfoAnime(@Query("page") String page, @Query("anime_number") int num);
}
