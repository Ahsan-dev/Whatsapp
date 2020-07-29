package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private EditText phnNumberEdt, verifycodeEdt;
    private Button sendVerifyBtn, verifyBtn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String   mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();


        phnNumberEdt = findViewById(R.id.phone_login_phnEdt);
        verifycodeEdt = findViewById(R.id.phone_login_verificationCodeEdt);
        sendVerifyBtn = findViewById(R.id.phn_login_send_verification_BtnId);
        verifyBtn = findViewById(R.id.phn_login_virify_BtnId);
        loadingBar = new ProgressDialog(this);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                loadingBar.dismiss();
                signInWithPhoneAuthCredential(phoneAuthCredential);



            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
                phnNumberEdt.setVisibility(View.VISIBLE);
                sendVerifyBtn.setVisibility(View.VISIBLE);

                verifycodeEdt.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent( String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token;

                phnNumberEdt.setVisibility(View.GONE);
                sendVerifyBtn.setVisibility(View.GONE);

                verifycodeEdt.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);


            }
        };

        sendVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phnNum = phnNumberEdt.getText().toString();
                if(TextUtils.isEmpty(phnNum)){
                    phnNumberEdt.setError("Enter phone number");
                    phnNumberEdt.requestFocus();
                    return;
                }else {
                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Wait, while we are authenticating phone number..");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                          PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                  phnNum,
                                  60,
                                  TimeUnit.SECONDS,
                                  PhoneLoginActivity.this,
                                  mCallbacks
                          );
                }


            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phnNumberEdt.setVisibility(View.GONE);
                sendVerifyBtn.setVisibility(View.GONE);

                verifycodeEdt.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);

                String verifyCode = verifycodeEdt.getText().toString().trim();

                if(TextUtils.isEmpty(verifyCode)){
                    Toast.makeText(getApplicationContext(),"Enter verification code...",Toast.LENGTH_LONG).show();
                }else {
                    loadingBar.setTitle("Code verification");
                    loadingBar.setMessage("Wait, while we are verifying the verification code..");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(PhoneLoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Error: "+task.getException().toString(),Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

}