package com.example.myctrace.ui.editprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myctrace.MainActivity;
import com.example.myctrace.R;
import com.example.myctrace.ui.riskassesment.RiskAssessmentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    EditText et_uname, et_ic, et_phone;
    TextView icon_profile;

    DatabaseReference mbase;

    String uName, icNum, phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Firebase reference
        mbase = FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //bind elements
        et_uname = findViewById(R.id.et_uname);
        et_ic = findViewById(R.id.et_ic);
        et_phone = findViewById(R.id.et_phone);
        icon_profile = findViewById(R.id.icon_profile);
        MaterialButton btn_submit = findViewById(R.id.btn_submit);

        setFields();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
                Toast.makeText(EditProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                switchIntent();
            }
        });

    }

    private void setFields() {
        mbase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                if (task.getResult().child("icNumber").exists())
                    icNum = String.valueOf(task.getResult().child("icNumber").getValue());
                else
                    icNum = "No Data";

                if (task.getResult().child("uname").exists())
                    uName = String.valueOf(task.getResult().child("uname").getValue());
                else
                    uName = "No Data";

                if (task.getResult().child("phone").exists())
                    phoneNum = String.valueOf(task.getResult().child("phone").getValue());
                else
                    phoneNum = "No Data";

                et_ic.setText(icNum);
                et_uname.setHint(uName);
                et_phone.setHint(phoneNum);
                icon_profile.setText(Character.toString(uName.charAt(0)));
            }
        });
    }

    private void updateDetails() {
        if (!et_uname.getText().toString().isEmpty())
            uName = et_uname.getText().toString().trim();

        if (!et_phone.getText().toString().isEmpty())
            phoneNum = et_phone.getText().toString().trim();

        mbase.child("uname").setValue(uName);
        mbase.child("phone").setValue(phoneNum);
    }

    private void switchIntent() {
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}