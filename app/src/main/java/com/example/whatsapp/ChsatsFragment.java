package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp.Module.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChsatsFragment extends Fragment {

    private RecyclerView chatFragRecycler;
    private View chatFragView ;
    private DatabaseReference contactsRootRef, userRootRef, senderRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragView = inflater.inflate(R.layout.fragment_chsats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatFragRecycler = chatFragView.findViewById(R.id.chat_frag_recyclerId);
        chatFragRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRootRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        senderRef = contactsRootRef.child(currentUserId);




        return chatFragView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> chatOpt = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(senderRef,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatlistViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ChatlistViewHolder>(chatOpt) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatlistViewHolder holder, int position, @NonNull Contacts model) {

                final String contactsId = getRef(position).getKey();
                final String[] retImage = {"default image"};

                userRootRef.child(contactsId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            if (dataSnapshot.hasChild("image"))
                            {
                                retImage[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retImage[0]).placeholder(R.drawable.profile_image).into(holder.uProImg);
                            }

                            final String retName = dataSnapshot.child("userName").getValue().toString();
                            final String retStatus = dataSnapshot.child("userStatus").getValue().toString();

                            holder.uName.setText(retName);
                            holder.uStatus.setText("Date \n Time");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", contactsId);
                                    chatIntent.putExtra("visit_user_name", retName);
                                    chatIntent.putExtra("visit_image", retImage[0]);
                                   startActivity(chatIntent);
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ChatlistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_items,viewGroup,false);
                return new ChatlistViewHolder(view);
            }
        };

        chatFragRecycler.setAdapter(adapter);
        adapter.startListening();


    }

    public class ChatlistViewHolder extends RecyclerView.ViewHolder{
         TextView uName, uStatus;
         CircleImageView uProImg;

        public ChatlistViewHolder(@NonNull View itemView) {
            super(itemView);
            uName = itemView.findViewById(R.id.user_name_id);
            uStatus = itemView.findViewById(R.id.user_status_id);
            uProImg = itemView.findViewById(R.id.user_profile_imageId);

        }
    }
}