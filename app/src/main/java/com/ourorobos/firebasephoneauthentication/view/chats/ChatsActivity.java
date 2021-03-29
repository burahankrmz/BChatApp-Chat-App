package com.ourorobos.firebasephoneauthentication.view.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.adapter.ChatsAdapter;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityChatsBinding;
import com.ourorobos.firebasephoneauthentication.model.Chatlist;
import com.ourorobos.firebasephoneauthentication.model.chat.Chats;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private ActivityChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chats);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        String userProfile = intent.getStringExtra("userProfile");


        if(receiverID != null) {
            binding.chatsusernametv.setText(userName);
            Glide.with(this).load(userProfile).into(binding.chatsprofileimage);
        }

        binding.chatbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.chatmsged.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(binding.chatmsged.getText().toString())) {
                    binding.chatfabtn.setImageDrawable(getDrawable(R.drawable.chatmicbtn));
                }else {
                    binding.chatfabtn.setImageDrawable(getDrawable(R.drawable.chatsendbtn));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initBtnClick();

        readChats();
    }

    private void initBtnClick() {
        binding.chatfabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(binding.chatmsged.getText().toString())) {
                    sendTextMessage(binding.chatmsged.getText().toString());

                    binding.chatmsged.setText("");
                }
            }
        });
        binding.chatbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendTextMessage(String text) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("kk:mm ");
        String currentTime = df.format(currentDateTime.getTime());
        Chats chats = new Chats(
                "Son Görülme:"+today+", "+currentTime,
                text,
                "TEXT",
                firebaseUser.getUid(),
                receiverID
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send","Başarılı");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("Send","Başarısız");
            }
        });

        //ADD to ChatList
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    private void readChats() {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chats chats = snapshot.getValue(Chats.class);
                        Chats date = snapshot.getValue(Chats.class);
                        if(chats != null && chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                            || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                        ){
                            list.add(chats);
                            if(chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)) {
                                list.add(date);
                                binding.chatsuserstatus.setText(date.getDatetime());
                            }
                        }
                    }
                    if(adapter!=null) {
                        adapter.notifyDataSetChanged();
                        binding.recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.recyclerView.smoothScrollBy(R.id.chattv,R.id.chattv);
                            }
                        });
                    }else {
                        adapter = new ChatsAdapter(list, ChatsActivity.this);
                        binding.recyclerView.setAdapter(adapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}