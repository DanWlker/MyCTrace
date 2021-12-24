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
