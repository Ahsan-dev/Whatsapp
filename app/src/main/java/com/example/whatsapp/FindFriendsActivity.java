package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Module.Contacts;
import com.example.whatsapp.ViewHolder.FindFriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar findFriendsToolbar;
    private RecyclerView findFrndsRecycler;
    private DatabaseReference userRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        findFriendsToolbar = findViewById(R.id.findFriendsToolbarId);
        findFrndsRecycler = findViewById(R.id.find_friends_recyclerId);

        userRootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(findFriendsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        findFrndsRecycler.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> findFrndOptions = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRootRef,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(findFrndOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {

                holder.nameTxt.setText(model.getUserName());
                holder.statusTxt.setText(model.getUserStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimg);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user_id = getRef(position).getKey();

                        Intent proIntent = new Intent(FindFriendsActivity.this,UserProfileActivity.class);
                        proIntent.putExtra("user_id",user_id);
                        startActivity(proIntent);
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

        findFrndsRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}