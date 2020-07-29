package com.example.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView proImg;
    private EditText useraNameEdt, staTusEdt;
    private Button updateButton;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private static final int GALLERY_REQ_CODE = 1 ;
    private StorageReference profileImageRef;
    private String dounloadUrl;
    private Uri imageUri;
    private StorageTask uploadTask;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        proImg = findViewById(R.id.settings_user_imageId);
        useraNameEdt = findViewById(R.id.settings_userName_Id);
        staTusEdt = findViewById(R.id.settings_userStatus_Id);
        updateButton = findViewById(R.id.settings_updateButton_Id);
        toolbar = findViewById(R.id.settings_toolbar_Id);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = useraNameEdt.getText().toString();
                String staTus = staTusEdt.getText().toString();
                String uid = mAuth.getCurrentUser().getUid();

                final StorageReference filePath = profileImageRef.child(mAuth.getCurrentUser().getUid()+".jpg");

                uploadTask = filePath.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }else {

                            return filePath.getDownloadUrl();

                        }


                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                            Uri downloadUri = (Uri) task.getResult();
                            dounloadUrl = downloadUri.toString();

                            UsersRef.child(mAuth.getCurrentUser().getUid()).child("image").setValue(dounloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Image Uploaded successfully",Toast.LENGTH_LONG);
                                            }

                                        }
                                    });


                        }
                    }
                });

                HashMap<String,Object> settingMap = new HashMap<>();
                settingMap.put("userName",userName);
                settingMap.put("userStatus",staTus);
                settingMap.put("uid",uid);

                UsersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(settingMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getApplicationContext(),"Settings updated successfully...",Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }
                        });
            }
        });

        proImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQ_CODE);
            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();

        UsersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("userName") && dataSnapshot.hasChild("image")){

                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String status = dataSnapshot.child("userStatus").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    useraNameEdt.setVisibility(View.INVISIBLE);
                    useraNameEdt.setText(userName);
                    staTusEdt.setText(status);
                    Picasso.get().load(image).into(proImg);

                }
               else if(dataSnapshot.exists() && dataSnapshot.hasChild("userName")){
                    useraNameEdt.setVisibility(View.INVISIBLE);
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String status = dataSnapshot.child("userStatus").getValue().toString();

                    useraNameEdt.setText(userName);
                    staTusEdt.setText(status);

                }
               else {
                    useraNameEdt.setVisibility(View.VISIBLE);
                   Toast.makeText(getApplicationContext(),"Please set and update settings",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
             imageUri = result.getUri();
            Picasso.get().load(imageUri).into(proImg);

        }
    }
}