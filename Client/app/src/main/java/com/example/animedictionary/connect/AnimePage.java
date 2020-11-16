package com.example.animedictionary.connect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimePage {
    @SerializedName("id_inf")
    @Expose
    public int id;
    @SerializedName("link_anime")
    @Expose
    public String linkAnime;
    @SerializedName("link_image")
    @Expose
    public String linkImage;
    @SerializedName("info_anime")
    @Expose
    public String infoAnime;
    @SerializedName("anime_number")
    @Expose
    public int animeNumber;
    @SerializedName("page")
    @Expose
    public String page;

    //метод исключительно для проверки, все ли ок верулось
    public String toString(){
        return id+"\n"+linkAnime+"\n"+linkImage+"\n"+infoAnime+"\n"+animeNumber+"\n"+page;
    }
}
