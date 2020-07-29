package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupFragment extends Fragment {

    private View groupFragmentView;
    private ArrayAdapter<String> groupListAdapter;
    private ListView groupListView;
    private ArrayList<String> groupList = new ArrayList<>();
    private DatabaseReference GroupRootRef;



    public GroupFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_group, container, false);
        initializeView();
        return groupFragmentView;
    }

    private void initializeView() {

        groupListView = groupFragmentView.findViewById(R.id.group_listView_id);
        groupListAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,groupList);
        groupListView.setAdapter(groupListAdapter);

        GroupRootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        retrieveAndDisplayGroups();

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGrpName = parent.getItemAtPosition(position).toString();

                Intent getIntent = new Intent(getContext(),GroupChatActivity.class);
                getIntent.putExtra("currentGrpName",currentGrpName);
                startActivity(getIntent);
            }
        });
    }

    private void retrieveAndDisplayGroups() {

        GroupRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();


                    while (iterator.hasNext()){

                        set.add(((DataSnapshot)iterator.next()).getKey());
                }

                groupList.clear();
                groupList.addAll(set);
                groupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}