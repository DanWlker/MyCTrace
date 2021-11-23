package com.example.myctrace.ui.scanqr;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myctrace.MainActivity;
import com.example.myctrace.R;
import com.example.myctrace.ui.EmulateQRScanningPage;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.ui.register.Register;

public class ScanQr extends Fragment {

    private ScanQrViewModel mViewModel;

    public static ScanQr newInstance() {
        return new ScanQr();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scan_qr_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScanQrViewModel.class);
        View view = getView();

        TextView btnCheckIn = view.findViewById(R.id.scan_qr_check_in_button);
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EmulateQRScanningPage.class);
                //TODO:change to start activity without letting the user able to press back button to access stack history
                startActivity(intent);
            }
        });
        // TODO: Use the ViewModel
    }

}