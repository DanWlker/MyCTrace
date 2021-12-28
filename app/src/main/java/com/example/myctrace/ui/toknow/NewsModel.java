package com.example.myctrace.ui.toknow;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NewsModel {

    private String title, url, imgurl;

    //empty constructor
    public NewsModel() {}

    //getter methods
    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getImgUrl(){
        return imgurl;
    }

    //setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImgUrl(String imgUrl) {
        this.imgurl = imgUrl;
    }
}
