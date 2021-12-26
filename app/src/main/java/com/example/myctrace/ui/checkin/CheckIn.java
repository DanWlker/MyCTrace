package com.example.myctrace.ui.checkin;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myctrace.R;
import com.example.myctrace.ui.EmulateQRScanningPage;
import com.example.myctrace.ui.checkinhistory.CheckInHistoryFragment;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.ui.register.Register;
import com.example.myctrace.ui.scanqr.ScanQr;

import org.w3c.dom.Text;

public class CheckIn extends Fragment {

    private CheckInViewModel mViewModel;

    // to handle image callback
    ActivityResultLauncher<Intent> launchScanQRActivity;

    public static CheckIn newInstance() {
        return new CheckIn();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        launchScanQRActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            //do your stuff below
                            onSelfScanQR(data.getExtras().getString("value"));
                        }
                    }
                }
        );

        return inflater.inflate(R.layout.check_in_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CheckInViewModel.class);

        // TODO: Use the ViewModel
        View view = getView();

        TextView textViewMore = view.findViewById(R.id.view_more);
        textViewMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment frag = new CheckInHistoryFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Button buttonGroupCheckIn = view.findViewById(R.id.btn_groupcheckin);
        buttonGroupCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new ScanQr();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Button buttonSelfCheckIn = view.findViewById(R.id.btn_self_check_in);



        buttonSelfCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanQR = new Intent(getActivity(), EmulateQRScanningPage.class);
                launchScanQRActivity.launch(scanQR);
            }
        });

    }

    private void onSelfScanQR(String data) {
        Log.d("scanqrdebug", "I now know the value is: " + data);
        //upload
    }


}