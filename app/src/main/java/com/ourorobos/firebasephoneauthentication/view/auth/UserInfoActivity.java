package com.ourorobos.firebasephoneauthentication.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityUserInfoBinding;
import com.ourorobos.firebasephoneauthentication.model.user.Users;
import com.ourorobos.firebasephoneauthentication.view.RealMainActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_info);

        progressDialog = new ProgressDialog(this);
        initButtonClick();
    }

    private void initButtonClick() {
        binding.devambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.userinfoed.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"Lütfen adını giriniz",Toast.LENGTH_SHORT).show();
                }else {
                    doUpdate();
                }
            }
        });

        binding.userinfoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pickImage();
                Toast.makeText(getApplicationContext(),"İleride güncellenecek...",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void doUpdate() {
        progressDialog.setMessage("Lütfen bekleyin...");
        progressDialog.show();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser !=null) {
            String userID = firebaseUser.getUid();
            Users users = new Users(
                    userID,
                    binding.userinfoed.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(users)//.update("userName",binding.userinfoed.getText().toString())
             .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Başarıyla Güncellendi.",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RealMainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Güncellenemedi."+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Firebase hata verdi",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }
}