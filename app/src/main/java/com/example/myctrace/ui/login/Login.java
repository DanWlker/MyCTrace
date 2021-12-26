package com.example.myctrace.ui.login;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myctrace.MainActivity;
import com.example.myctrace.databinding.ActivityLoginBinding;
import com.example.myctrace.R;
import com.example.myctrace.ui.register.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity {

    //Firebase related stuff
    private FirebaseAuth mAuth;

    //Views to instantiate
    TextView registerText;
    Button loginButton;
    EditText editTextIdentificationCard;
    EditText editTextPassword;

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
    private View mControlsView;
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

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Firebase related stuff
        mAuth = FirebaseAuth.getInstance();

        //Initialize views
        registerText = findViewById(R.id.register_text);
        loginButton = findViewById(R.id.login_button);
        editTextIdentificationCard = findViewById(R.id.editTextIdentificationCard);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                //TODO:change to start activity without letting the user able to press back button to access stack history
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(
                        editTextIdentificationCard.getText().toString().trim().isEmpty() ||
                                editTextPassword.getText().toString().trim().isEmpty()
                ) {
                    Toast.makeText(Login.this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                    return;
                }

                loginUsingFirebase();
            }
        });

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    private void loginUsingFirebase() {
        mAuth.signInWithEmailAndPassword(
                editTextIdentificationCard.getText().toString().trim() + "@myctrace.com",
                editTextPassword.getText().toString().trim()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("Firebase", "Login successful");
                    //redirect to home
                    redirectToHome();
                } else {
                    Log.d("Firebase", "Login failed");
                    Toast.makeText(Login.this, "Failed to login! Please retry again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void redirectToHome() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        //TODO:change to start activity without letting the user able to press back button to access stack history
        startActivity(intent);
        finish();
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