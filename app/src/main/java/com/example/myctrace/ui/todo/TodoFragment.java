package com.example.myctrace.ui.todo;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myctrace.R;
import com.example.myctrace.databinding.FragmentHomeBinding;
import com.example.myctrace.databinding.TodoFragmentBinding;
import com.example.myctrace.ui.assessment.AssessmentFragment;
import com.example.myctrace.ui.home.HomeViewModel;
import com.google.android.material.card.MaterialCardView;

public class TodoFragment extends Fragment {

    private TodoViewModel todoViewModel;
    private TodoFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        binding = TodoFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final MaterialCardView cvRisk = binding.cvRisk;

        cvRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment riskAssFrag = new AssessmentFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, riskAssFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
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