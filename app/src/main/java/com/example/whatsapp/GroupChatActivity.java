package com.example.whatsapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrollView scrollMsg;
    private TextView groupMsgTxt;
    private EditText groupMsgEdt;
    private ImageButton groupMsgSendBtn;
    private DatabaseReference grpRootRef, userRootRef;
    private String currentDate, currentTime;
    private FirebaseAuth mauth;
    private String currentUserName, currentUserId;

    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getStringExtra("currentGrpName");

        toolbar = findViewById(R.id.groupFrag_appBar_layoutId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group: "+currentGroupName);

        mauth = FirebaseAuth.getInstance();



        scrollMsg = findViewById(R.id.group_chat_scrollViewId);
        groupMsgTxt = findViewById(R.id.group_chat_message_textId);
        groupMsgEdt = findViewById(R.id.group_chat_msg_EdtId);
        groupMsgSendBtn = findViewById(R.id.group_chat_sendBtnId);

        grpRootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        userRootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        currentUserId = mauth.getCurrentUser().getUid();

        userRootRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("userName").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        groupMsgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollMsg.fullScroll(ScrollView.FOCUS_DOWN);
                String message = groupMsgEdt.getText().toString().trim();
                String messageKey = grpRootRef.child(currentGroupName).push().getKey();


                groupMsgEdt.setText("");

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDateF = new SimpleDateFormat("dd,MM,yyyy");
                currentDate = currentDateF.format(calendar.getTime());

                SimpleDateFormat currentTimeF = new SimpleDateFormat("hh:mm a");
                currentTime = currentTimeF.format(calendar.getTime());

                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getApplicationContext(),"Enter message..",Toast.LENGTH_LONG).show();
                }else {
                    HashMap<String,Object> messageMap = new HashMap<>();
                    messageMap.put("UserName",currentUserName);
                    messageMap.put("message",message);
                    messageMap.put("Date",currentDate);
                    messageMap.put("Time",currentTime);

                    grpRootRef.child(currentGroupName).child(messageKey).updateChildren(messageMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();

                                }
                            });
                }



            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        grpRootRef.child(currentGroupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    displayMessage(dataSnapshot);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    displayMessage(dataSnapshot);

                }



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void displayMessage(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String date = (String) ((DataSnapshot)iterator.next()).getValue();
            String time = (String) ((DataSnapshot)iterator.next()).getValue();
            String userName = (String) ((DataSnapshot)iterator.next()).getValue();
            String message = (String) ((DataSnapshot)iterator.next()).getValue();

            groupMsgTxt.append("Name: "+userName+"\n"+message+"\n"+date+"      "+time+"\n\n");
            scrollMsg.fullScroll(ScrollView.FOCUS_DOWN);


        }


    }
}