package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView userProPic,userCovrPic;
    private Button sendReqBtn,removeReqBtn;
    private TextView userNmTxt, userSttsTxt;
    private String receiveruser_id = "", senderUserId,CurrentState;
    private DatabaseReference userRootRef, ContactsRootRef;
    private DatabaseReference ChatReqRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        receiveruser_id = getIntent().getStringExtra("user_id");
        userRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        ContactsRootRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        CurrentState = "new";

        userProPic = findViewById(R.id.profile_user_imageId);
        userNmTxt = findViewById(R.id.profile_user_nameId);
        userSttsTxt = findViewById(R.id.profile_user_statusId);
        sendReqBtn = findViewById(R.id.profile_send_msg_requestBtn);
        removeReqBtn = findViewById(R.id.profile_cancel_chat_requestBtn);

        manageChatMethod();

    }


    protected void manageChatMethod() {

        userRootRef.child(receiveruser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("image")){

                    String uName = dataSnapshot.child("userName").getValue().toString();
                    String uStatus = dataSnapshot.child("userStatus").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(userProPic);
                    userNmTxt.setText(uName);
                    userSttsTxt.setText(uStatus);

                    SendMessageRequestMethod();


                }else {

                    String uName = dataSnapshot.child("userName").getValue().toString();
                    String uStatus = dataSnapshot.child("userStatus").getValue().toString();
                    userNmTxt.setText(uName);
                    userSttsTxt.setText(uStatus);
                    SendMessageRequestMethod();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessageRequestMethod() {


        ChatReqRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receiveruser_id)){
                    String requ_type = dataSnapshot.child(receiveruser_id).child("request_type").getValue().toString();
                    if(requ_type.equals("sent")){
                        CurrentState = "request_sent";
                        sendReqBtn.setText("Cancel chat request");
                    }
                    else if(requ_type.equals("received")){
                        CurrentState = "request_received";
                        sendReqBtn.setText("Accept Chat Request");
                        removeReqBtn.setVisibility(View.VISIBLE);
                        removeReqBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatReq();
                                removeReqBtn.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
                else {

                    ContactsRootRef.child(senderUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiveruser_id)){
                                        CurrentState = "friends";
                                        sendReqBtn.setText("Remove this Contact");
                                        //startActivity(new Intent(UserProfileActivity.this,UserProfileActivity.class));
                                    }
                                    else {

                                        CurrentState = "new";
                                        sendReqBtn.setText("Send Message");

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if(!senderUserId.equals(receiveruser_id)){

            sendReqBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendReqBtn.setEnabled(false);
                    if(CurrentState.equals("new")){
                        sendChatReq();
                    }
                    if(CurrentState.equals("request_sent")){
                        cancelChatReq();
                    }
                    if(CurrentState.equals("request_received")){
                        AcceptChatReq();
                    }
                    if(CurrentState.equals("friends")){
                        removeSpecificContacts();
                    }
                }
            });

        }
        else {
            sendReqBtn.setVisibility(View.INVISIBLE);
        }

    }



    private void sendChatReq() {

        ChatReqRef.child(senderUserId).child(receiveruser_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            ChatReqRef.child(receiveruser_id).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                sendReqBtn.setEnabled(true);
//                                                CurrentState = "reuest_sent";
//                                                sendReqBtn.setText("Cancel chat request");
                                            }

                                        }
                                    });

                        }



                    }
                });

    }

    private void cancelChatReq() {

            ChatReqRef.child(senderUserId).child(receiveruser_id).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                ChatReqRef.child(receiveruser_id).child(senderUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sendReqBtn.setEnabled(true);
                                                    CurrentState = "new";
                                                    sendReqBtn.setText("Send Message");
                                                }
                                            }
                                        });
                            }

                        }
                    });

    }

    private void AcceptChatReq() {

        ContactsRootRef.child(senderUserId).child(receiveruser_id).child("Contacts")
                .setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            ContactsRootRef.child(receiveruser_id).child(senderUserId).child("Contacts")
                                    .setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                ChatReqRef.child(senderUserId).child(receiveruser_id).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()) {
                                                                    ChatReqRef.child(receiveruser_id).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                                    if(task.isSuccessful()){

                                                                                        sendReqBtn.setVisibility(View.VISIBLE);
                                                                                        sendReqBtn.setEnabled(true);
                                                                                        CurrentState = "friends";
                                                                                        sendReqBtn.setText("Remove this Contact");
                                                                                        removeReqBtn.setVisibility(View.INVISIBLE);
                                                                                        removeReqBtn.setEnabled(false);

                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void removeSpecificContacts() {

        ContactsRootRef.child(senderUserId).child(receiveruser_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            ContactsRootRef.child(receiveruser_id).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendReqBtn.setEnabled(true);
                                                CurrentState = "new";
                                                sendReqBtn.setText("Send Message");
                                            }
                                        }
                                    });
                        }

                    }
                });




    }




}