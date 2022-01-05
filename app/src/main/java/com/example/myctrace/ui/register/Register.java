package com.example.myctrace.ui.register;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myctrace.databinding.ActivityRegisterBinding;
import com.example.myctrace.R;
import com.example.myctrace.ui.icverification.ICVerification;
import com.example.myctrace.ui.login.Login;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Register extends AppCompatActivity {


    //Views that need to be initialized
    TextView loginText;
    Button registerButton;
    EditText editTextIdentificationCard;
    EditText editTextPassword;
    EditText editTextRetypePassword;
    EditText editTextPhoneNumber;
    EditText editTextUserName;
    AutoCompleteTextView editTextFilledExposedDropdown;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {

            } else {

            }
        }
    };
;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //find views
        loginText = findViewById(R.id.login_text);
        registerButton = findViewById(R.id.register_button);
        editTextIdentificationCard = findViewById(R.id.editTextIdentificationCard);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRetypePassword = findViewById(R.id.editTextRetypePassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextUserName = findViewById(R.id.editTextUserName);

        String[] type = new String[] {"Johor", "Kedah", "Kelantan", "Kuala Lumpur", "Labuan", "Melaka", "Negeri Sembilan", "Pahang", "Penang", "Perak", "Perlis", "Putrajaya", "Sabah", "Sarawak", "Selangor", "Terengganu", "Others"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.state_dropdown_menu_popup,
                        type);

        editTextFilledExposedDropdown =
                findViewById(R.id.edTxtState);
        editTextFilledExposedDropdown.setAdapter(adapter);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister();
            }
        });

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void onClickRegister() {
        HashMap<String, String> input = new HashMap<String, String>();
        input.put("identificationNumber", editTextIdentificationCard.getText().toString().trim());
        input.put("password", editTextPassword.getText().toString().trim());
        input.put("retypePassword", editTextRetypePassword.getText().toString().trim());
        input.put("phoneNumber", editTextPhoneNumber.getText().toString().trim());
        input.put("uname",editTextUserName.getText().toString().trim());
        input.put("currState", editTextFilledExposedDropdown.getText().toString().trim());

        if(!checkFields(input)) {
            return;
        }

        //launch next screen and pass the checked data to the ic verification screen
        redirectToICVerification(input);
    }

    private void redirectToICVerification(HashMap<String, String> input) {
        Intent intent = new Intent(Register.this, ICVerification.class);
        intent.putExtra("input", input);
        startActivity(intent);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        //TODO:change to start activity without letting the user able to press back button to access stack history
        startActivity(intent);
        finish();
    }

    private boolean checkFields(Map<String, String> input) {
        for(String value: input.values()) {
            if(value.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(input.get("password").length() < 6) {
            Toast.makeText(this, "Password Length must be larger than 6.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!input.get("password").equals(input.get("retypePassword"))) {
            Toast.makeText(this, "Two password fields do not match.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}