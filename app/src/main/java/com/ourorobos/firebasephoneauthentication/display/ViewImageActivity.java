package com.ourorobos.firebasephoneauthentication.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.common.Common;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityRealProfileBinding;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);

        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);

        binding.backbtnimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}