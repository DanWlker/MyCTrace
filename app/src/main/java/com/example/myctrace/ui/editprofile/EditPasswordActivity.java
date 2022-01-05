package com.example.myctrace.ui.editprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myctrace.MainActivity;
import com.example.myctrace.R;
import com.example.myctrace.ui.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class EditPasswordActivity extends AppCompatActivity {

    //firebase user reference
    FirebaseUser user;
    //initializing layout elements
    EditText etOldPAss, etNewPass, etConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        //bind layout elements
        etOldPAss = findViewById(R.id.et_oldpass);
        etNewPass = findViewById(R.id.et_newpass);
        etConfirmPass = findViewById(R.id.et_confirmpass);
        MaterialButton btn_submit;
        btn_submit = findViewById(R.id.btn_submit);

        //submit button onclick listener
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if any of the edit text are empty
                if (etOldPAss.getText().toString().trim().isEmpty() ||
                        etNewPass.getText().toString().trim().isEmpty() ||
                        etConfirmPass.getText().toString().trim().isEmpty())
                {
                    //if any of the fields are empty, toast message
                    Toast.makeText(EditPasswordActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if new pass matches confirm pass
                if (!etNewPass.getText().toString().trim()
                        .equals(etConfirmPass.getText().toString().trim())) {
                    //if doesn't match, toast message
                    Toast.makeText(EditPasswordActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //proceed to update password
                updatePassword(
                        etOldPAss.getText().toString().trim(),
                        etNewPass.getText().toString().trim()
                );
            }
        });
    }

    private void updatePassword(String oldpass, String newpass) {
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass);

        //reauthenticate user
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //if authenticate successful, update password
                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                //communication to database or update password unsucessful, toast message
                                Toast.makeText(EditPasswordActivity.this, "Couldn't communicate with database now, please try again later.", Toast.LENGTH_SHORT).show();
                            }else {
                                //Toast update password success message
                                Toast.makeText(EditPasswordActivity.this, "Password Changed Successfully. Please login again", Toast.LENGTH_SHORT).show();
                                //switch intent
                                signOutSwitchIntent();
                            }
                        }
                    });
                }else {
                    //authentication failed, old password is incorrect
                    Toast.makeText(EditPasswordActivity.this, "Authentication failed, please ensure all fields are correct.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signOutSwitchIntent() {
        //signout
        FirebaseAuth.getInstance().signOut();
        //switch intent
        Intent intent = new Intent(EditPasswordActivity.this, Login.class);
        startActivity(intent);
        EditPasswordActivity.this.finish();
    }
}
