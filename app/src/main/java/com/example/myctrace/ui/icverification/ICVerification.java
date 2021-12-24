package com.example.myctrace.ui.icverification;

import android.annotation.SuppressLint;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myctrace.MainActivity;
import com.example.myctrace.databinding.ActivityIcverificationBinding;
import com.example.myctrace.R;
import com.example.myctrace.ui.login.Login;
import com.example.myctrace.utilities.TempUserLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ICVerification extends AppCompatActivity {

    //Firebase stuff
    FirebaseAuth mAuth;

    // to handle image callback
    ActivityResultLauncher<Intent> launchGalleryActivity;

    //usre info passed from register page
    HashMap<String, String> input;


    //Views that need to be initialized
    ImageView imageViewIdentificationCard;
    Button icVeriConfirmButton;

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

    private ActivityIcverificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIcverificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Firebase related stuff
        mAuth = FirebaseAuth.getInstance();

        //passed from register page
        Intent intent = getIntent();
        input = (HashMap<String, String>)intent.getSerializableExtra("input");


        //find views
        imageViewIdentificationCard = findViewById(R.id.imageViewIdentificationCard);
        icVeriConfirmButton = findViewById(R.id.icveri_confirm_button);

        launchGalleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();

                            //do your stuff below
                            Uri image = data.getData();
                            imageViewIdentificationCard.setImageURI(image);
                        }
                    }
                }
        );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        icVeriConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasNullOrEmptyDrawable(imageViewIdentificationCard)) {
                    Toast.makeText(
                            ICVerification.this,
                            "Please upload a picture of your identitiy card above first.",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                createUserInFirebase();
            }
        });

        imageViewIdentificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                launchGalleryActivity.launch(gallery);
            }
        });

        delayedHide(100);
    }

    private void createUserInFirebase() {

        mAuth.createUserWithEmailAndPassword(
                input.get("identificationNumber") + "@myctrace.com",
                input.get("password")
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // Sign in is successful
                    Log.d("Firebase", "createUserWithEmail: Success");

                    // Create user in realtime database and save data in it
                    Log.d("Firebase", "Saving user info in database");

                    saveUserInfoInFirebase();
                } else {
                    //Sign in is unsuccessful
                    Log.w("Firebase", "createUserWithEmail: Fail", task.getException());
                    Toast.makeText(ICVerification.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserInfoInFirebase() {
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("phone", input.get("phoneNumber"));
        userData.put("icNumber", input.get("identificationNumber"));

        FirebaseDatabase.getInstance().getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ICVerification.this, "User has been registered successfully", Toast.LENGTH_LONG);

                            redirectToLogin();
                        } else {
                            Toast.makeText(ICVerification.this, "Registration failed.", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    private void redirectToLogin() {
        //Redirect the user to login page
        Intent intent = new Intent(ICVerification.this, Login.class);
        //to clear the register page, may need revision
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public boolean hasNullOrEmptyDrawable(ImageView iv)
    {
        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }
}