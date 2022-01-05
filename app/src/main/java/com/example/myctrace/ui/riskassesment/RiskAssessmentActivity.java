package com.example.myctrace.ui.riskassesment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myctrace.MainActivity;
import com.example.myctrace.R;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.ui.register.Register;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiskAssessmentActivity extends AppCompatActivity {

    //Database reference
    DatabaseReference mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_assessment);

        //bind elements
        RadioGroup rg_q1, rg_q2, rg_q3;
        RadioButton rb_q1, rb_q2, rb_q3;
        rg_q1 = findViewById(R.id.rg_q1);
        rg_q2 = findViewById(R.id.rg_q2);
        rg_q3 = findViewById(R.id.rg_q3);
        rb_q1 = findViewById(R.id.rb_q1);
        rb_q2 = findViewById(R.id.rb_q2);
        rb_q3 = findViewById(R.id.rb_q3);
        MaterialButton btn_submit;
        btn_submit = findViewById(R.id.btn_submit);

        //set firebase reference
        mbase = FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //submit button onclick listener
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if all radio groups have at least one checked option
                if (rg_q1.getCheckedRadioButtonId() == -1 ||
                        rg_q2.getCheckedRadioButtonId() == -1 ||
                        rg_q3.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RiskAssessmentActivity.this,
                            "Please Answer All Questions",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //check if there are any "Yes" checked
                    if (rb_q1.isChecked() || rb_q2.isChecked() || rb_q3.isChecked()) {
                        //if any of the "yes" are checked, update risk status of user to high
                        mbase.child("riskInfo").child("riskStatus").setValue("High");
                        mbase.child("riskInfo").child("dateTime").setValue(System.currentTimeMillis()/1000);
                        //Toast message
                        Toast.makeText(RiskAssessmentActivity.this, "Submitted Successfully!", Toast.LENGTH_SHORT).show();
                        //Switch Intent to MainActivity
                        switchIntent();
                    } else {
                        //else, update risk status of user to low
                        mbase.child("riskInfo").child("riskStatus").setValue("Low");
                        mbase.child("riskInfo").child("dateTime").setValue(System.currentTimeMillis()/1000);
                        //Toast message
                        Toast.makeText(RiskAssessmentActivity.this, "Submitted Successfully!", Toast.LENGTH_SHORT).show();
                        //Switch intent to Main activity
                        switchIntent();
                    }
                }
            }
        });
    }

    private void switchIntent() {
        //switch intent to main activity
        Intent intent = new Intent(RiskAssessmentActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}