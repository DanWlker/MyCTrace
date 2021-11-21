package com.example.myctrace.ui.toknow;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myctrace.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_title;

    public ViewHolder(View itemView) {
        super(itemView);
        tv_title = itemView.findViewById(R.id.tv_title);
    }
}
