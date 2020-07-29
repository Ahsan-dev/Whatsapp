package com.example.whatsapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Module.Contacts;
import com.example.whatsapp.ViewHolder.FindFriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ContactsFragment extends Fragment {

    private RecyclerView contactsRecycler;
    private DatabaseReference contactsRef,UserRootRef;
    private FirebaseAuth mAuth;
    private String currentUserId;



    public ContactsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsRecycler = contactsView.findViewById(R.id.contacts_recycler);
        contactsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UserRootRef = FirebaseDatabase.getInstance().getReference().child("Users");



        return contactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> conOptions = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(conOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, int position, @NonNull Contacts model) {

                String userId = getRef(position).getKey();
                UserRootRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("image")){
                            String image = dataSnapshot.child("image").getValue().toString();
                            String  name= dataSnapshot.child("userName").getValue().toString();
                            String status = dataSnapshot.child("userStatus").getValue().toString();

                            holder.nameTxt.setText(name);
                            holder.statusTxt.setText(status);
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.profileimg);
                        }else {

                            String  name= dataSnapshot.child("userName").getValue().toString();
                            String status = dataSnapshot.child("userStatus").getValue().toString();

                            holder.nameTxt.setText(name);
                            holder.statusTxt.setText(status);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_items,viewGroup,false);
                return new FindFriendsViewHolder(view);
            }
        };

        contactsRecycler.setAdapter(adapter);
        adapter.startListening();

    }
}