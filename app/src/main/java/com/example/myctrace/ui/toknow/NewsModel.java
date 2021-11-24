package com.example.myctrace.ui.toknow;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NewsModel {

    public String mtitle;

    public NewsModel(String title){
        mtitle = title;
    }

    public String getTitle(){
        return mtitle;
    }

    private static int lastNewsId = 0;

    public static ArrayList<NewsModel> createNewsList(int n){
        ArrayList<NewsModel> news = new ArrayList<NewsModel>();

        for (int i = 1; i < n; ++i){
            news.add(new NewsModel("This is news number : " + i));
        }

        return news;
    }
}