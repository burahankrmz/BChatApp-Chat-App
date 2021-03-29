package com.ourorobos.firebasephoneauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.fonts.FontFamily;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityMainBinding;
import com.ourorobos.firebasephoneauthentication.model.user.Users;
import com.ourorobos.firebasephoneauthentication.view.RealMainActivity;
import com.ourorobos.firebasephoneauthentication.view.auth.UserInfoActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //biewbinding
    private ActivityMainBinding binding;

    //if code send failed, will used to resend code OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId; //will hold OTP/Verification code

    private static final String TAG = "MAIN_TAG";

    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog pd;

    private FirebaseAuth userfirebaseAuth;
    private FirebaseUser userfirebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLl.setVisibility(View.VISIBLE);//show phone layout
        binding.codeLl.setVisibility(View.GONE);//hind code layout, when OTP sent then hide layout and show code layout

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        /*
        userfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(userfirebaseUser!=null) {
            startActivity(new Intent(this,UserInfoActivity.class));
        }

         */

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
                singInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull  FirebaseException e) {
                pd.dismiss();
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,@NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId,forceResendingToken);

                Log.d(TAG,"onCodeSent"+verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this,"Verification code sent...",Toast.LENGTH_SHORT).show();

                binding.codeSentDescription.setText("Please type the verification code we sent \nto " +binding.phoneEt.getText().toString().trim());

            }
        };

        binding.phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this,"Please enter phone number...,",Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVerification(phone);
                }

            }
        });

        binding.resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this,"Please enter phone number...,",Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerificationCode(phone,forceResendingToken);
                }
            }
        });

        binding.codeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = binding.codeEt.getText().toString().trim();
                if(TextUtils.isEmpty(code)) {
                    Toast.makeText(MainActivity.this,"Please enter verification code...",Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyPhoneNumberWithCode(mVerificationId,code);
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifyinh Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        singInWithPhoneAuthCredential(credential);
    }

    private void singInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        //String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        //Toast.makeText(MainActivity.this,"Logged ın as"+phone,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                        /*
                        if(user != null) {
                            String userID = user.getUid();
                            Users users = new Users(
                                    userID,
                                    "",
                                    user.getPhoneNumber(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "");
                            firestore.collection("Users").document("UserInfo").collection(userID)
                                    .add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                                }
                            });

                        }else {
                            Toast.makeText(getApplicationContext(),"Hata alındı",Toast.LENGTH_SHORT).show();
                        }

                        //startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                         */
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed signing in
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

}