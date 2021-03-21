package com.example.animedictionary.connect;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAPI {
    //собственно сам запрос, который хотим отрпавить
    @GET("/")
    Single<TestConnection> testConnect();

    @GET("/get_anime")
    Single<AnimePage> getInfoAnime(@Query("page") String page, @Query("anime_number") int num);
}
