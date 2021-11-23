package com.example.myctrace.ui.toknow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myctrace.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;

        public ViewHolder(View itemView){
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    private List<NewsModel> mNews;

    public NewsAdapter(List<NewsModel> news){
        mNews = news;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View newsView = inflater.inflate(R.layout.news_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(newsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        NewsModel news = mNews.get(position);

        TextView textViewTitle = holder.tv_title;
        textViewTitle.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }


}
