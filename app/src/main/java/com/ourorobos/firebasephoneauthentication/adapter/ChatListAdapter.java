package com.ourorobos.firebasephoneauthentication.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.model.Chatlist;
import com.ourorobos.firebasephoneauthentication.model.chat.Chats;
import com.ourorobos.firebasephoneauthentication.model.user.Users;
import com.ourorobos.firebasephoneauthentication.view.chats.ChatsActivity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private final List<Chatlist> list;
    private final Context context;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String currentUserID;


    public ChatListAdapter(List<Chatlist> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list,parent,false);
        return new Holder(view);
    }
    

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        Chatlist chatlist = list.get(position);

        holder.usernametextview.setText(chatlist.getUserName());
        holder.chattextview.setText(chatlist.getDescription());
        holder.datetextview.setText(chatlist.getDate());

        Glide.with(context).load(chatlist.getUrlProfile()).into(holder.profileicon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",chatlist.getUserID())
                        .putExtra("userName",chatlist.getUserName())
                        .putExtra("userProfile",chatlist.getUrlProfile())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView usernametextview,datetextview,chattextview;
        private final CircularImageView profileicon;
        public Holder(@NonNull  View itemView) {
            super(itemView);

            profileicon = itemView.findViewById(R.id.profileicon);
            usernametextview = itemView.findViewById(R.id.usernametextview);
            datetextview = itemView.findViewById(R.id.datetextview);
            chattextview = itemView.findViewById(R.id.chattextview);

        }
    }
    private void updateUserStatus(String state) {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM DD, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currenTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentDate.format(calendar.getTime());

        HashMap<String,Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("date", saveCurrentDate);

        currentUserID = firebaseAuth.getCurrentUser().getUid();

        reference.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }
}
