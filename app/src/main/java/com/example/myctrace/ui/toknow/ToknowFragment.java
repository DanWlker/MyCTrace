package com.example.myctrace.ui.toknow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myctrace.R;
import com.example.myctrace.databinding.ActivityMainBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ToknowFragment extends Fragment {

    ArrayList<NewsModel> news;
    DatabaseReference mbase;

    public static ToknowFragment newInstance() {
        return new ToknowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toknow_fragment, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("To Know");

        mbase = FirebaseDatabase.getInstance().getReference().child("news");
        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                .setQuery(mbase, NewsModel.class)
                .build();

        RecyclerView rv_news = view.findViewById(R.id.rv_news);
        NewsAdapter adapter = new NewsAdapter(options);
        rv_news.setAdapter(adapter);
        rv_news.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.startListening();

        return view;
    }
}