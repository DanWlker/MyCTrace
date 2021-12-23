package com.example.myctrace.ui.toknow;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NewsModel {

    private String title, url, imgurl;

    public NewsModel() {}

    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getImgUrl(){
        return imgurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImgUrl(String imgUrl) {
        this.imgurl = imgUrl;
    }

    /*public static ArrayList<NewsModel> createNewsList(int n){
        ArrayList<NewsModel> news = new ArrayList<NewsModel>();

        for (int i = 1; i < n; ++i){
            news.add(new NewsModel("This is news number : " + i));
        }

        return news;
    } */
}
