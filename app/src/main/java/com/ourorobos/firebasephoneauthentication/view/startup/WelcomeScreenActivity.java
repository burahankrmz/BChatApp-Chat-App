package com.ourorobos.firebasephoneauthentication.view.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.MainActivity;
import com.ourorobos.firebasephoneauthentication.view.RealMainActivity;

public class WelcomeScreenActivity extends AppCompatActivity {
    private TextView accepttextview,welcometextview;
    private ImageView welcomeicon;
    private Button acceptbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        accepttextview = findViewById(R.id.accepttextview);
        welcometextview = findViewById(R.id.welcometextview);
        welcomeicon = findViewById(R.id.welcomeicon);
        acceptbutton = findViewById(R.id.acceptbutton);

        acceptbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));
                        finish();
            }
        });
    }
}