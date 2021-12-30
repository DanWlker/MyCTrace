package com.example.myctrace.ui.profile;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myctrace.MainActivity;
import com.example.myctrace.R;
import com.example.myctrace.ui.editprofile.EditPasswordActivity;
import com.example.myctrace.ui.editprofile.EditProfileActivity;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.ui.riskassesment.RiskAssessmentActivity;
import com.example.myctrace.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProfileFragment extends Fragment {

    //private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    //Firebase ref
    DatabaseReference mbase, mbase_state;

    MaterialCardView cvRisk, cvVac, cvState;
    ImageView btn_edit;
    TextView btn_showqr;
    TextView tvUname, tvPhone, tvIc, userIcon;
    Button logoutButton, changePassButton;

    TextView riskTitle, riskUpdated, riskDate;
    ImageView riskIcon;

    TextView vacTitle, vacUpdated, vacDate;
    ImageView vacIcon;

    TextView stateTitle, stateUpdated, stateRisk;
    ImageView stateIcon;

    Bitmap qrBitmap;
    String uname, phone, icNum;
    String vacDosage, vacDose1Time, vacDose2Time, vacFacility, vacManufacturer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //setting firebase reference
        mbase = FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mbase_state = FirebaseDatabase.getInstance().getReference("states");

        //binding layout components
        bindElements();

        //set user details
        setUserDetails();
        checkRisk();
        checkVac();
        //checkStateStatus();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        cvRisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RiskAssessmentActivity.class);
                startActivity(i);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        cvVac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.fragment_component_vaccine_pop_up, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);

                //bind elements of the popup view
                TextView tv_popupuname = popupView.findViewById(R.id.tv_popupuname);
                TextView tv_ic = popupView.findViewById(R.id.tv_ic);
                TextView tv_d1_date = popupView.findViewById(R.id.tv_d1_date);
                TextView tv_d1_facility = popupView.findViewById(R.id.tv_d1_facility);
                TextView tv_d1_manufacturer = popupView.findViewById(R.id.tv_d1_manufacturer);
                TextView tv_d2_date = popupView.findViewById(R.id.tv_d2_date);
                TextView tv_d2_facility = popupView.findViewById(R.id.tv_d2_facility);
                TextView tv_d2_manufacturer = popupView.findViewById(R.id.tv_d2_manufacturer);

                //Set the fields accordingly
                tv_popupuname.setText(uname);
                tv_ic.setText(icNum);
                if (vacDosage.equals("Fully Vaccinated")) {
                    tv_d1_date.setText("Date Administered: " + vacDose1Time);
                    tv_d1_facility.setText("Facility Administered: " +vacFacility);
                    tv_d1_manufacturer.setText("Vaccine Manufacturer: " + vacManufacturer);
                    tv_d2_date.setText("Date Administered: " + vacDose2Time);
                    tv_d2_facility.setText("Facility Administered: " + vacFacility);
                    tv_d2_manufacturer.setText("Vaccine Manufacturer: " + vacManufacturer);
                } else if (vacDosage.equals("First Dose")) {
                    tv_d1_date.setText("Date Administered: " + vacDose1Time);
                    tv_d1_facility.setText("Facility Administered: " + vacFacility);
                    tv_d1_manufacturer.setText("Vaccine Manufacturer: " + vacManufacturer);
                    tv_d2_date.setText("Date Administered: No Data");
                    tv_d2_facility.setText("Facility Administered: No Data");
                    tv_d2_manufacturer.setText("Vaccine Manufacturer: No Data");
                } else {
                    tv_d1_date.setText("Date Administered: No Data");
                    tv_d1_facility.setText("Facility Administered: No Data");
                    tv_d1_manufacturer.setText("Vaccine Manufacturer: No Data");
                    tv_d2_date.setText("Date Administered: No Data");
                    tv_d2_facility.setText("Facility Administered: No Data");
                    tv_d2_manufacturer.setText("Vaccine Manufacturer: No Data");
                }

                View container;

                if (popupWindow.getBackground() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        container = (View) popupWindow.getContentView().getParent();
                    } else {
                        container = popupWindow.getContentView();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container = (View) popupWindow.getContentView().getParent().getParent();
                    } else {
                        container = (View) popupWindow.getContentView().getParent();
                    }
                }

                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                wm.updateViewLayout(container, p);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        btn_showqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.fragment_component_user_qr_pop_up, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);

                //bind elements of the popup view
                TextView tv_username = popupView.findViewById(R.id.tv_username);
                TextView tv_id = popupView.findViewById(R.id.tv_id);
                ImageView img_qr = popupView.findViewById(R.id.img_qr);

                //Set the fields accordingly
                tv_username.setText(uname);
                tv_id.setText(icNum);
                generateQR(icNum);
                img_qr.setImageBitmap(qrBitmap);

                View container;

                if (popupWindow.getBackground() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        container = (View) popupWindow.getContentView().getParent();
                    } else {
                        container = popupWindow.getContentView();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container = (View) popupWindow.getContentView().getParent().getParent();
                    } else {
                        container = (View) popupWindow.getContentView().getParent();
                    }
                }

                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                wm.updateViewLayout(container, p);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditPasswordActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }

    private void setUserDetails() {
        mbase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) { return; }

                if (task.getResult().child("uname").exists()) {
                    uname = String.valueOf((task.getResult().child("uname").getValue()));
                } else {
                    uname = "User";
                }

                if (task.getResult().child("phone").exists()) {
                    phone = String.valueOf((task.getResult().child("phone").getValue()));
                } else {
                    phone = "0";
                }

                if (task.getResult().child("icNumber").exists()) {
                    icNum = String.valueOf((task.getResult().child("icNumber").getValue()));
                } else {
                    icNum = "0";
                }

                if (task.getResult().child("currState").exists()) {
                    String currState = String.valueOf((task.getResult().child("currState").getValue()));
                    checkStateStatus(currState);
                } else {
                    String currState = "Others";
                    checkStateStatus(currState);
                }

                tvUname.setText(uname);
                userIcon.setText(Character.toString(uname.charAt(0)));

                if (phone.equals("0"))
                    tvPhone.setText("No Phone Number");
                else
                    tvPhone.setText(phone);

                if (icNum.equals("0"))
                    tvIc.setText("No IC Number");
                else
                    tvIc.setText(icNum);
            }
        });
    }

    private void checkRisk() {
        mbase.child("riskInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) { return; }

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

                riskTitle.setText(riskStatus + " Risk");
                setRiskState(riskStatus);

                if(dateTime.equals("0")) {
                    riskDate.setText("No Asssement Date");
                } else {
                    riskDate.setText(formatted);
                }

            }
        });
    }

    private void checkVac() {
        mbase.child("vacInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                vacDosage = "No Vaccination";
                String d1time = "0";
                String d2time = "0";
                String dateTime = "0";

                if (task.getResult().child("facility").exists())
                    vacFacility = String.valueOf(task.getResult().child("facility").getValue());
                else
                    vacFacility = "No Data";

                if (task.getResult().child("manufacturer").exists())
                    vacManufacturer = String.valueOf(task.getResult().child("manufacturer").getValue());
                else
                    vacManufacturer = "No Data";

                if (task.getResult().child("firstDose").exists()) {
                    vacDosage = "First Dose";
                    d1time = String.valueOf(task.getResult().child("firstDose").getValue());
                    dateTime = d1time;
                }

                if (task.getResult().child("secondDose").exists()) {
                    vacDosage = "Fully Vaccinated";
                    d2time = String.valueOf(task.getResult().child("secondDose").getValue());
                    dateTime = d2time;
                }

                vacDose1Time = formatDate(d1time);
                vacDose2Time = formatDate(d2time);

                vacTitle.setText(vacDosage);
                setVacStatus(vacDosage);

                if(vacDosage.equals("No Vaccination"))
                    vacDate.setText("No Vaccination Date");
                else
                    vacDate.setText(formatDate(dateTime));
            }
        });
    }

    private String formatDate(String dateTime) {
        Long dateTimeLong = Long.valueOf(dateTime);
        Date date = new Date(dateTimeLong*1000);
        DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String formatted = format.format(date);
        return formatted;
    }

    private void setRiskState(String status) {
        if (status.equals("High"))
        {
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_orange));
            riskTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            riskUpdated.setTextColor(getResources().getColor(R.color.orange_primary));
            riskDate.setTextColor(getResources().getColor(R.color.orange_primary));
        } else {
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.blue_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue));
            riskTitle.setTextColor(getResources().getColor(R.color.blue_primary));
            riskUpdated.setTextColor(getResources().getColor(R.color.blue_primary));
            riskDate.setTextColor(getResources().getColor(R.color.blue_primary));
        }
    }

    private void setVacStatus(String dosage) {
        if (dosage.equals("Fully Vaccinated"))
        {
            cvVac.setCardBackgroundColor(getResources().getColor(R.color.green_secondary));
            vacIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_green));
            vacTitle.setTextColor(getResources().getColor(R.color.green_primary));
            vacUpdated.setTextColor(getResources().getColor(R.color.green_primary));
            vacDate.setTextColor(getResources().getColor(R.color.green_primary));
        } else {
            cvVac.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            vacIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_orange));
            vacTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            vacUpdated.setTextColor(getResources().getColor(R.color.orange_primary));
            vacDate.setTextColor(getResources().getColor(R.color.orange_primary));
        }
    }

    private void setStateStatus(String risk) {
        if (risk.equals("High"))
        {
            cvState.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            stateIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_map_pin_orange));
            stateTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            stateUpdated.setTextColor(getResources().getColor(R.color.orange_primary));
            stateRisk.setTextColor(getResources().getColor(R.color.orange_primary));
        } else {
            cvState.setCardBackgroundColor(getResources().getColor(R.color.blue_secondary));
            stateIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_map_pin_blue));
            stateTitle.setTextColor(getResources().getColor(R.color.blue_primary));
            stateUpdated.setTextColor(getResources().getColor(R.color.blue_primary));
            stateRisk.setTextColor(getResources().getColor(R.color.blue_primary));
        }
    }

    private void checkStateStatus(String state) {
        mbase_state.child(state).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                String riskstat = "No Data";
                if (task.getResult().exists())
                    riskstat = String.valueOf(task.getResult().getValue());

                stateTitle.setText(state);
                stateRisk.setText(riskstat);
                setStateStatus(riskstat);
            }
        });
    }

    private void bindElements() {
        cvRisk = binding.cvRisk;
        cvVac = binding.cvVac;
        cvState = binding.cvState;
        btn_edit = binding.btnEditprofile;
        btn_showqr = binding.btnShowQR;
        logoutButton = binding.logoutButton;
        changePassButton = binding.btnChangePassword;

        tvUname = binding.tvUname;
        tvPhone = binding.tvPhone;
        tvIc = binding.tvIc;
        userIcon = binding.iconProfile;

        riskTitle = binding.riskTitle;
        riskUpdated = binding.riskUpdated;
        riskDate = binding.riskDate;
        riskIcon = binding.riskIcon;

        vacTitle = binding.vacTitle;
        vacUpdated = binding.vacUpdated;
        vacDate = binding.vacDate;
        vacIcon = binding.vacIcon;

        stateTitle = binding.stateTitle;
        stateUpdated = binding.stateUpdated;
        stateRisk = binding.stateRisk;
        stateIcon = binding.stateIcon;
    }

    private void generateQR(String str) {
        //generate QR code based on given string
        QRGEncoder encoder = new QRGEncoder(str, null, QRGContents.Type.TEXT, 100);
        try {
            qrBitmap = encoder.encodeAsBitmap();
        } catch (WriterException e) {
            Log.e("Encoder", e.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}