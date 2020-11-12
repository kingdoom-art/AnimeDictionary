package com.example.animedictionary.connect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestConnection {
    //имя переменной из json
    @SerializedName("answer")
    @Expose
    public String answer;
}
