package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabsAccessorAdapter mTabsAccessorAdapter;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private FirebaseUser mFUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();



        mFUser = mAuth.getCurrentUser();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");
        mTablayout = findViewById(R.id.main_tabalayoutId);

        mViewPager = findViewById(R.id.main_view_pagerId);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTablayout.setupWithViewPager(mViewPager);
        RootRef = FirebaseDatabase.getInstance().getReference();




    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mFUser == null){
            sendUsertoLoginActivity();
        }
    }

    private void sendUsertoLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_find_friends_id){

            startActivity(new Intent(MainActivity.this,FindFriendsActivity.class));

        }
        if(item.getItemId() == R.id.menu_settings_id){

            Intent setIntent  = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(setIntent);
            //finish();

        }
        if(item.getItemId() == R.id.menu_logout_id){

            mAuth.signOut();
            sendUsertoLoginActivity();

        }
        if(item.getItemId() == R.id.menu_create_group_id){

            createNewGroup();

        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewGroup() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Create New Group:");
        builder.setCancelable(false);
        final EditText newGrpEdt = new EditText(MainActivity.this);
        newGrpEdt.setHint("e.g Friend zone");
         newGrpEdt.setPadding(35,10,25,10);
        newGrpEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        builder.setView(newGrpEdt);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }

        });

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newGrp = newGrpEdt.getText().toString();

                if(TextUtils.isEmpty(newGrp)){
                    Toast.makeText(getApplicationContext(),"Enter the group name.",Toast.LENGTH_LONG).show();


                }else {

                    createGroupToDatabase(newGrp);

                }


            }
        });

        builder.show();


    }

    private void createGroupToDatabase(final String newGrp) {



        RootRef.child("Groups").child(newGrp).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),newGrp+" group is created successfully.",Toast.LENGTH_LONG).show();
                        }

                    }
                });



    }
}