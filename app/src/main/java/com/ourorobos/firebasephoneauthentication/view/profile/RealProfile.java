package com.ourorobos.firebasephoneauthentication.view.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ourorobos.firebasephoneauthentication.common.Common;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityRealProfileBinding;
import com.ourorobos.firebasephoneauthentication.display.ViewImageActivity;
import com.ourorobos.firebasephoneauthentication.view.settings.SettingsActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class RealProfile extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    private ActivityRealProfileBinding binding;
    private BottomSheetDialog bottomSheetDialog,bottomSheetNameDialog;
    private ProgressDialog progressDialog;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_real_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);

        if(firebaseUser !=null) {
            getInfo();
        }
        initActionClick();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RealProfile.this,SettingsActivity.class));
        finish();
    }

    private void initActionClick() {
        binding.chooseprofileib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBottomSheetPickPhoto();
            }
        });
        binding.profilenamebtnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBottomSheetEditName();
            }
        });
        binding.profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileimage.invalidate();
                Drawable dr = binding.profileimage.getDrawable();
                Common.IMAGE_BITMAP =((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(RealProfile.this,binding.profileimage,"image");
                Intent intent = new Intent(RealProfile.this, ViewImageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showBottomSheetPickPhoto() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.lnclickgallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.lnclickphoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Camera",Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bottomSheetDialog.show();
    }

    private void showBottomSheetEditName() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);
        final EditText bottom_usered = view.findViewById(R.id.bottom_usered);
        ((View) view.findViewById(R.id.bottom_acceptbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(bottom_usered.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"Kullanıcı adı boş olamaz",Toast.LENGTH_SHORT).show();
                }else {
                    updateName(bottom_usered.getText().toString());
                    bottomSheetNameDialog.dismiss();
                }
            }
        });

        ((View) view.findViewById(R.id.bottom_cancelbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetNameDialog.dismiss();
            }
        });

        bottomSheetNameDialog = new BottomSheetDialog(this);
        bottomSheetNameDialog.setContentView(view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetNameDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bottomSheetNameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetNameDialog = null;
            }
        });
        bottomSheetNameDialog.show();
    }

    private void updateName(String newUserName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newUserName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Kullanıcı adı başarıyla değiştirildi.",Toast.LENGTH_SHORT).show();
                        getInfo();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Fotoğraf Seç"),IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            ImageUri = data.getData();

            uploadToFirebase();

            /*
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),ImageUri);
                binding.profileimage.setImageBitmap(bitmap);

            }catch (Exception e) {
                e.printStackTrace();
            }

             */
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebase() {
        if(ImageUri != null) {
            progressDialog.setMessage("Profil Fotonuz Ayarlanıyor..");
            progressDialog.show();
            StorageReference profilesref = FirebaseStorage.getInstance().getReference().child("ImageProfile/"+System.currentTimeMillis()+"."+getFileExtension(ImageUri));
            profilesref.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                    final String xdpwnload_uri = String.valueOf(downloadUri);

                    Toast.makeText(getApplicationContext(),"Upload Successfully",Toast.LENGTH_SHORT).show();

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile",xdpwnload_uri);
                    progressDialog.dismiss();
                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Upload Successfully",Toast.LENGTH_SHORT).show();
                            getInfo();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Upload Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = (documentSnapshot.getString("userName"));
                String userPhone = (documentSnapshot.getString("userPhone"));
                String userBio = (documentSnapshot.getString("bio"));
                String imageProfile = documentSnapshot.getString("imageProfile");
                binding.profileusernametv.setText(userName);
                binding.profileinfotv.setText(userBio);
                binding.profilephonetv.setText(userPhone);
                Glide.with(RealProfile.this).load(imageProfile).into(binding.profileimage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("Get Data","OnFailure"+e.getMessage());
            }
        });
    }
}