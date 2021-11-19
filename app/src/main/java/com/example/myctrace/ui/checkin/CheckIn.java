package com.example.myctrace.ui.checkin;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myctrace.R;
import com.example.myctrace.databinding.FragmentDashboardBinding;
import com.example.myctrace.ui.checkinhistory.CheckInHistoryFragment;

import org.w3c.dom.Text;

public class CheckIn extends Fragment {

    private CheckInViewModel mViewModel;

    public static CheckIn newInstance() {
        return new CheckIn();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.check_in_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        // TODO: Use the ViewModel
    }


}