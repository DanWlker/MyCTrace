package com.example.myctrace.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myctrace.R;
import com.example.myctrace.databinding.FragmentHomeBinding;
import com.example.myctrace.ui.todo.TodoFragment;
import com.example.myctrace.ui.toknow.ToknowFragment;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.tvUName;
        final MaterialCardView cvRisk = binding.cvRisk;
        final MaterialCardView cvVac = binding.cvVac;
        final MaterialCardView cvTodo = binding.cvTodo;
        final MaterialCardView cvToknow = binding.cvToknow;

//        cvRisk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), RiskAssessmentActivity.class);
//                startActivity(i);
//            }
//        });

        cvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toDoFrag = new TodoFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, toDoFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        cvToknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toKnowFrag = new ToknowFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, toKnowFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
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