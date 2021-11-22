package com.example.myctrace.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myctrace.R;
import com.example.myctrace.databinding.FragmentProfileBinding;
import com.example.myctrace.ui.assessment.AssessmentFragment;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final MaterialCardView cvRisk = binding.cvRisk;
        final MaterialCardView cvVac = binding.cvVac;
        final ImageView btn_edit = binding.btnEditprofile;
        final TextView textView = binding.tvName;

        cvRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment riskAssFrag = new AssessmentFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, riskAssFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(riskAssFrag.getClass().getName());
                ft.commit();
            }
        });

        /*btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new EditProfileFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });*/

        /*profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        }); */
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}