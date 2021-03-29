package com.ourorobos.firebasephoneauthentication.view.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.adapter.ContactsAdapter;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityContactsBinding;
import com.ourorobos.firebasephoneauthentication.model.user.Users;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    private ActivityContactsBinding binding;
    private List<Users> list = new ArrayList<>();
    private ContactsAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_contacts);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if(firebaseUser !=null) {
            getContactListe();
        }

        binding.contactbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getContactListe() {
        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshots: queryDocumentSnapshots) {
                    String userID = snapshots.getString("userID");
                    String userName = snapshots.getString("userName");
                    String imageUri = snapshots.getString("imageProfile");
                    String userBio = snapshots.getString("bio");

                    Users user = new Users();
                    user.setUserID(userID);
                    user.setBio(userBio);
                    user.setImageProfile(imageUri);
                    user.setUserName(userName);

                    if(userID != null && !userID.equals(firebaseUser.getUid())) {
                        list.add(user);
                    }
                    //Log.d(TAG,"onSuccess: data"+snapshots.toString());
                }
                adapter = new ContactsAdapter(list,ContactsActivity.this);
                binding.recyclerView.setAdapter(adapter);
            }
        });
    }
}