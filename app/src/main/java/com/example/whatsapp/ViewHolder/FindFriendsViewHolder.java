package com.example.whatsapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView profileimg;
    public TextView nameTxt,statusTxt;


    public FindFriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        profileimg = itemView.findViewById(R.id.user_profile_imageId);
        nameTxt = itemView.findViewById(R.id.user_name_id);
        statusTxt = itemView.findViewById(R.id.user_status_id);

    }
}
