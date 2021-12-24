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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myctrace.R;
import com.example.myctrace.ui.editprofile.EditProfileActivity;
import com.example.myctrace.ui.riskassesment.RiskAssessmentActivity;
import com.example.myctrace.databinding.FragmentProfileBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProfileFragment extends Fragment {

    //private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    Bitmap qrBitmap;
    private String username = "Default Username";
    private String idNum = "123456121234";
    private String phoneNum;
    private String riskStatus;
    private String vacStatus;
    private String currLocation;
    private String profilePicURL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        //final TextView textView = binding.tvName;
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final MaterialCardView cvRisk = binding.cvRisk;
        final MaterialCardView cvVac = binding.cvVac;
        final ImageView btn_edit = binding.btnEditprofile;

        final TextView btn_showqr = binding.btnShowQR;

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
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);

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

                //bind elements of the window
                TextView tv_username = popupView.findViewById(R.id.tv_username);
                TextView tv_id = popupView.findViewById(R.id.tv_id);
                ImageView img_qr = popupView.findViewById(R.id.img_qr);

                //Set the fields accordingly
                tv_username.setText("Plug in the username here");
                tv_id.setText("This is an id");
                generateQR("990011223344");
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


        /*profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        }); */
        return root;
    }

    private void generateQR(String str)
    {
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