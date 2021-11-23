package com.example.myctrace.ui.toknow;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myctrace.R;

import java.util.ArrayList;

public class ToknowFragment extends Fragment {

    ArrayList<NewsModel> news;

    private ToknowViewModel mViewModel;

    public static ToknowFragment newInstance() {
        return new ToknowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toknow_fragment, container, false);

        RecyclerView rv_news = view.findViewById(R.id.rv_news);
        news = NewsModel.createNewsList(10);
        //Log.i(news.get(0).getTitle(), "String");
        NewsAdapter adapter = new NewsAdapter(news);
        rv_news.setAdapter(adapter);
        rv_news.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ToknowViewModel.class);
        // TODO: Use the ViewModel
    }

}