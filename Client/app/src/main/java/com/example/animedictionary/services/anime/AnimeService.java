package com.example.animedictionary.services.anime;

import com.example.animedictionary.models.anime.AnimePage;
import com.example.animedictionary.models.anime.TestConnection;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnimeService {
    //собственно сам запрос, который хотим отрпавить
    @GET("/")
    Single<TestConnection> testConnect();

    @GET("/get_anime")
    Single<AnimePage> getInfoAnime(@Query("page") String page, @Query("anime_number") int num);
}
