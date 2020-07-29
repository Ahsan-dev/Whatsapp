package com.example.whatsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Module.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

   private RecyclerView requestsRecycler;
   private DatabaseReference chatReqRef,userRootRef,contactsRootRef;
   private String currrentUserId;
   private FirebaseAuth mAuth;

    public RequestsFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View reqstfragrView = inflater.inflate(R.layout.fragment_requests, container, false);

        requestsRecycler = reqstfragrView.findViewById(R.id.requests_recyclerId);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        userRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRootRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        currrentUserId = mAuth.getCurrentUser().getUid();


        return reqstfragrView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> reqOptions = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatReqRef.child(currrentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(reqOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {

                holder.itemView.findViewById(R.id.requests_acceptBtnId).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requests_cancelBtnId).setVisibility(View.VISIBLE);

                final String reqUsersId = getRef(position).getKey();
                DatabaseReference typeRef = getRef(position).child("request_type").getRef();

                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();

                            if(type.equals("received")){
                                userRootRef.child(reqUsersId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image")){
                                            String image = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.contImgView);

                                        }
                                             final String name = dataSnapshot.child("userName").getValue().toString();
                                            String status = dataSnapshot.child("userStatus").getValue().toString();
                                            holder.contNmTxt.setText(name);
                                            holder.contSttsTxt.setText(status);
                                            holder.reqAcceptBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle(name+ "Chat Request..");
                                                    //builder.setMessage("Do you want to accept "+name+" ?");

                                                    CharSequence action[] = new CharSequence[]{
                                                          "Accept",
                                                          "Cancel"
                                                    };

                                                    builder.setItems(action, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            if(which==0){

                                                                contactsRootRef.child(currrentUserId).child(reqUsersId)
                                                                        .child("Contacts").setValue("saved")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful()){
                                                                                    contactsRootRef.child(reqUsersId).child(currrentUserId)
                                                                                            .child("Contacts").setValue("saved")
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if(task.isSuccessful()){
                                                                                                        chatReqRef.child(currrentUserId).child(reqUsersId)
                                                                                                                .removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                        if(task.isSuccessful()){

                                                                                                                            chatReqRef.child(reqUsersId).child(currrentUserId)
                                                                                                                                    .removeValue()
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                            if(task.isSuccessful()){

                                                                                                                                                Toast.makeText(getContext(),"Contact accepted and saved",Toast.LENGTH_SHORT).show();

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

                                                            }else if(which==1){
                                                               dialog.cancel();
                                                            }

                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });

                                            holder.reqCancelBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Do you cancel request from "+name+" ?");
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            chatReqRef.child(currrentUserId).child(reqUsersId)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){
                                                                        chatReqRef.child(reqUsersId).child(currrentUserId)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful()){
                                                                                    Toast.makeText(getContext(),"Contact request removed",Toast.LENGTH_SHORT).show();
                                                                                }

                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });

                                                        }
                                                    });

                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            dialog.cancel();

                                                        }
                                                    });
                                                    builder.setCancelable(false);
                                                    builder.show();

                                                }
                                            });



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if(type.equals("sent")){
                                Toast.makeText(getContext(),"Sent",Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_items,viewGroup,false);
                return new RequestsViewHolder(view);
            }
        };
        requestsRecycler.setAdapter(adapter);
        adapter.startListening();







    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder{

         TextView contNmTxt, contSttsTxt;
        CircleImageView contImgView;
        Button reqAcceptBtn, reqCancelBtn;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            contNmTxt = itemView.findViewById(R.id.user_name_id);
            contSttsTxt = itemView.findViewById(R.id.user_status_id);
            contImgView = itemView.findViewById(R.id.user_profile_imageId);
            reqAcceptBtn = itemView.findViewById(R.id.requests_acceptBtnId);
            reqCancelBtn = itemView.findViewById(R.id.requests_cancelBtnId);

        }
    }





}