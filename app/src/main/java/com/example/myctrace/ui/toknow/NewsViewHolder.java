package com.example.myctrace.ui.toknow;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myctrace.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private final TextView tv_title;

    private NewsViewHolder(View itemView){
        super(itemView);
        tv_title = itemView.findViewById(R.id.tv_title);
    }

    public void bind(String title) {
        tv_title.setText(title);
    }

    static NewsViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);
        return new NewsViewHolder(view);
    }
}
