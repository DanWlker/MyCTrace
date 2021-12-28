package com.example.myctrace.ui.checkin;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myctrace.R;
import com.example.myctrace.ui.EmulateQRScanningPage;
import com.example.myctrace.ui.checkinhistory.CheckInHistoryFragment;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.ui.register.Register;
import com.example.myctrace.ui.scanqr.ScanQr;
import com.example.myctrace.ui.toknow.NewsAdapter;
import com.example.myctrace.ui.toknow.NewsModel;
import com.example.myctrace.uireusablecomponents.checkInLocation.CheckInLocationAdapter;
import com.example.myctrace.uireusablecomponents.checkInLocation.CheckInLocationModal;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CheckIn extends Fragment {

    private CheckInViewModel mViewModel;

    // to handle image callback
    ActivityResultLauncher<Intent> launchScanQRActivity;

    //Firebase stuff
    DatabaseReference mbase;
    private RecyclerView recyclerView;
    CheckInLocationAdapter adapter;

    public static CheckIn newInstance() {
        return new CheckIn();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        launchScanQRActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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

        //Firebase stuff
        mbase = FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


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

        checkRisk(view);

        checkVacStatus(view);

        loadRecentCheckIns(view);

    }

    private void checkVacStatus(View v) {
        TextView txtViewVaccination = v.findViewById(R.id.txtViewVaccination);
        TextView txtViewVaccinationDate = v.findViewById(R.id.txtViewVaccinationDate);
        LinearProgressIndicator progressBarVaccination = v.findViewById(R.id.progressBarVaccination);

        mbase.child("vacInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                String dosage = "No Vaccination";
                String dateTime = "0";

                if(task.getResult().child("secondDose").exists()) {
                    dosage = "Fully Vaccinated";
                    dateTime = String.valueOf(task.getResult().child("secondDose").getValue());
                } else if(task.getResult().child("firstDose").exists()) {
                    dosage = "First Dose";
                    dateTime = String.valueOf(task.getResult().child("firstDose").getValue());
                }

                Long dateTimeLong = Long.valueOf(dateTime);
                Date date = new Date(dateTimeLong*1000);
                DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                String formatted = format.format(date);

                int trackColor;
                int indicatorColor;
                int progress;

                switch (dosage) {
                    case "Fully Vaccinated":
                        indicatorColor = ContextCompat.getColor(v.getContext(), R.color.green_primary);
                        trackColor = ContextCompat.getColor(v.getContext(), R.color.green_secondary);
                        progress = 100;
                        break;
                    case "First Dose":
                        indicatorColor = ContextCompat.getColor(v.getContext(), R.color.orange_primary);
                        trackColor = ContextCompat.getColor(v.getContext(), R.color.orange_secondary);
                        progress = 50;
                        break;
                    case "No Vaccination":
                        indicatorColor = ContextCompat.getColor(v.getContext(), R.color.orange_primary);
                        trackColor = ContextCompat.getColor(v.getContext(), R.color.orange_secondary);
                        progress = 0;
                        break;
                    default:
                        indicatorColor = ContextCompat.getColor(v.getContext(), R.color.blue_primary);
                        trackColor = ContextCompat.getColor(v.getContext(), R.color.blue_secondary);
                        progress = 0;
                }

                txtViewVaccination.setText(dosage);
                txtViewVaccination.setTextColor(indicatorColor);

                if(dosage.equals("No Vaccination"))
                    txtViewVaccinationDate.setText("No Vaccination Date");
                else
                    txtViewVaccinationDate.setText("Vaccination Date: " + formatted);
                
                txtViewVaccinationDate.setTextColor(indicatorColor);
                progressBarVaccination.setProgressCompat(progress, true);
                progressBarVaccination.setIndicatorColor(indicatorColor);
                progressBarVaccination.setTrackColor(trackColor);

            }
        });
    }

    private void checkRisk(View v) {
        TextView txtViewRisk = v.findViewById(R.id.txtViewRisk);
        TextView txtViewRiskDate = v.findViewById(R.id.txtViewRiskDate);

        mbase.child("riskInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                String riskStatus;
                if(task.getResult().child("riskStatus").exists()) {
                    riskStatus = String.valueOf(task.getResult().child("riskStatus").getValue());
                } else {
                    riskStatus = "No Data";
                }

                String dateTime;
                if(task.getResult().child("riskStatus").exists()) {
                   dateTime = String.valueOf(task.getResult().child("dateTime").getValue());
                } else {
                    dateTime = "0";
                }


                Long dateTimeLong = Long.valueOf(dateTime);
                Date date = new Date(dateTimeLong*1000);
                DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                String formatted = format.format(date);


                int colorToSet;

                switch (riskStatus) {
                    case "High":
                        colorToSet = ContextCompat.getColor(v.getContext(), R.color.orange_primary);
                        break;
                    case "Low":
                        colorToSet = ContextCompat.getColor(v.getContext(), R.color.green_primary);
                        break;
                    default:
                        colorToSet = ContextCompat.getColor(v.getContext(), R.color.blue_primary);
                }

                txtViewRisk.setText(riskStatus);
                if(dateTime.equals("0")) {
                    txtViewRiskDate.setText("No Asssement Date");
                } else {
                    txtViewRiskDate.setText("Health Risk Assesment: " + formatted);
                }
                txtViewRisk.setTextColor(colorToSet);
                txtViewRiskDate.setTextColor(colorToSet);

            }
        });

    }

    private void loadRecentCheckIns(View v) {
        //Firebase stuff
        recyclerView = v.findViewById(R.id.checkInLocationRecycler);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(v.getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);


        FirebaseRecyclerOptions<CheckInLocationModal> options =
                new FirebaseRecyclerOptions.Builder<CheckInLocationModal>()
                        .setIndexedQuery(mbase.child("checkIns").orderByKey().limitToLast(4), mbase.child("checkIns").getRef(), CheckInLocationModal.class)
                    //.setQuery(mbase.child("checkIns"), CheckInLocationModal.class)
                    .build();

        adapter = new CheckInLocationAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onSelfScanQR(String data) {
        Log.d("scanqrdebug", "I now know the value is: " + data);

        Long currentEpochSecond = Instant.now().getEpochSecond();
        Date currentDateTime = new Date(currentEpochSecond*1000);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String formatted = format.format(currentDateTime);

        //upload onto firebase
        mbase.child("checkIns")
                .child(formatted)
                .child("location")
                .setValue(data);

        mbase.child("checkIns")
                .child(formatted)
                .child("dateTime")
                .setValue(currentEpochSecond);
    }


}