package com.ourorobos.firebasephoneauthentication.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ourorobos.firebasephoneauthentication.menu.CallsFragment;
import com.ourorobos.firebasephoneauthentication.menu.ChatsFragment;
import com.ourorobos.firebasephoneauthentication.menu.StatusFragment;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.databinding.ActivityRealmainBinding;
import com.ourorobos.firebasephoneauthentication.view.contact.ContactsActivity;
import com.ourorobos.firebasephoneauthentication.view.settings.SettingsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RealMainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    private ActivityRealmainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_realmain);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void setUpWithViewPager(ViewPager viewPager){
        RealMainActivity.SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(),"Sohbetler");
        //adapter.addFragment(new StatusFragment(),"Durum");
        adapter.addFragment(new CallsFragment(),"Aramalar");
        viewPager.setAdapter(adapter);

    }

    private static class SectionPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmenTitleList = new ArrayList<>();

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment,String title) {
            mFragmentList.add(fragment);
            mFragmenTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmenTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                Toast.makeText(RealMainActivity.this, "Action Search", Toast.LENGTH_LONG).show();
                break;
            case R.id.profile_settings:
                startActivity(new Intent(RealMainActivity.this, SettingsActivity.class));
                break;
            case R.id.new_group:
                Toast.makeText(RealMainActivity.this, "Action yeni grup", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeFIcon(final int index) {
        //binding.Fbtn.hide();

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                switch (index) {
                    case 0:
                        binding.Fbtn.setImageDrawable(getDrawable(R.drawable.btn_message));
                        binding.Fbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(RealMainActivity.this, ContactsActivity.class));
                            }
                        });
                        break;
                    case 1:
                        binding.Fbtn.setImageDrawable(getDrawable(R.drawable.btn_camera));
                        break;
                    case 2:
                        binding.Fbtn.setImageDrawable(getDrawable(R.drawable.btn_call));
                        break;
                }
                binding.Fbtn.show();
            }
        },400);
    }

}