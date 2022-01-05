package com.example.myctrace.ui.todo;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myctrace.ui.riskassesment.RiskAssessmentActivity;
import com.example.myctrace.databinding.TodoFragmentBinding;
import com.google.android.material.card.MaterialCardView;

public class TodoFragment extends Fragment {

    private TodoFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = TodoFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //bind elements
        MaterialCardView cvRisk = binding.cvRisk;

        //risk assessment card onclick listener
        cvRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch intent
                Intent i = new Intent(getActivity(), RiskAssessmentActivity.class);
                startActivity(i);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}