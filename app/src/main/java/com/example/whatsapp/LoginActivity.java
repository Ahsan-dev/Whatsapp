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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdt, passEdt;
    private Button emailLogBtn, phnLogBtn;
    private TextView createAccountTxtBtn, ForgetPassTxtBtn;
    private FirebaseAuth logAuth;
    private ProgressDialog loading;

    private FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdt = findViewById(R.id.whatsapp_login_email_phn_Edt);
        passEdt = findViewById(R.id.whatsapp_login_pass_Edt);
        emailLogBtn = findViewById(R.id.login_email_loginBtnId);
        phnLogBtn = findViewById(R.id.login_phn_loginBtnId);
        createAccountTxtBtn = findViewById(R.id.login_create_account_Id);
        ForgetPassTxtBtn = findViewById(R.id.login_forget_pass_linkId);
        logAuth = FirebaseAuth.getInstance();
        currentuser = logAuth.getCurrentUser();
        loading = new ProgressDialog(this);

        createAccountTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();

            }
        });

        emailLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading.setTitle("Logging in Account");
                loading.setMessage("Wait, while you are logged in into your account...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();

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

                    logAuth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        sendUsertoMainActivity();
                                        Toast.makeText(getApplicationContext(),"Logged in Successfully",Toast.LENGTH_SHORT).show();
                                        loading.dismiss();


                                    }else{

                                        Toast.makeText(getApplicationContext(),"Log in failed :"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                        loading.dismiss();

                                    }

                                }
                            });

                }
            }
        });

        phnLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phnLogIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phnLogIntent);
                finish();
            }
        });

    }




    private void sendUsertoMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }
}