package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEdt,passEdt;
    private Button registerBtn;
    private TextView alreadyAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdt = findViewById(R.id.whatsapp_register_email_phn_Edt);
        passEdt = findViewById(R.id.whatsapp_register_pass_Edt);
        registerBtn = findViewById(R.id.register_email_BtnId);
        alreadyAccountLink = findViewById(R.id.register_already_Id);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        rootRef = FirebaseDatabase.getInstance().getReference();

        alreadyAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Creating Account");
                loadingBar.setMessage("Wait, while we are creating account for you...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                String email = emailEdt.getText().toString();
                String pass = passEdt.getText().toString();

                if(TextUtils.isEmpty(email)){
                    emailEdt.setError("Email is mandatory");
                    emailEdt.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(pass)){
                    passEdt.setError("Password is mandatory");
                    passEdt.requestFocus();
                    return;
                }
                else {

                    mAuth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        String currentUserId = mAuth.getCurrentUser().getUid();

                                        rootRef.child("Users").child(currentUserId).setValue("");

                                        Toast.makeText(getApplicationContext(),"Account created successfully.",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,SettingsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else {
                                        Toast.makeText(getApplicationContext(),"Error: "+task.getException().toString(),Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });

                }

            }
        });


    }
}