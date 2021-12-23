package com.example.myctrace.ui.toknow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myctrace.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/*public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public MaterialCardView cv_url;
        public ImageView img_cover;

        public ViewHolder(View itemView){
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            cv_url = itemView.findViewById(R.id.cv_url);
            img_cover = itemView.findViewById(R.id.img_cover);
        }
    }

    private Context mContext;
    private List<NewsModel> mNews;


    public NewsAdapter(List<NewsModel> news){
        mNews = news;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View newsView = inflater.inflate(R.layout.news_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(newsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        NewsModel news = mNews.get(position);

        TextView textViewTitle = holder.tv_title;
        textViewTitle.setText(news.getTitle());

        ImageView imageCover = holder.img_cover;
        Picasso.get().load(news.getImgUrl()).into(imageCover);

        MaterialCardView cardViewURL = holder.cv_url;
        cardViewURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl()));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }
} */

public class NewsAdapter extends FirebaseRecyclerAdapter<
        NewsModel, NewsAdapter.NewsViewholder> {

    Context mContext;

    public NewsAdapter(@NonNull FirebaseRecyclerOptions<NewsModel> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsViewholder holder, int position, @NonNull NewsModel model)
    {
        holder.tvTitle.setText(model.getTitle());

        holder.cvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                mContext.startActivity(browserIntent);
            }
        });

        Picasso.get().load(model.getImgUrl()).into(holder.imgCover);
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public NewsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        return new NewsAdapter.NewsViewholder(view);
    }

    class NewsViewholder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        MaterialCardView cvUrl;
        ImageView imgCover;

        public NewsViewholder(@NonNull View itemView)
        {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            cvUrl = itemView.findViewById(R.id.cv_url);
            imgCover = itemView.findViewById(R.id.img_cover);
        }
    }
}
