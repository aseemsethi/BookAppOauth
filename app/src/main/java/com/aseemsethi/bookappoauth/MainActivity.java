package com.aseemsethi.bookappoauth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.aseemsethi.bookappoauth.ui.main.SectionsPagerAdapter;
import com.aseemsethi.bookappoauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        //startService(new Intent(this,MqttService.class));
        Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, MqttService.class);
        serviceIntent.setAction(MqttService.MQTTSUBSCRIBE_ACTION);
        serviceIntent.putExtra("topic", "aseemsethi");
        startService(serviceIntent);
    }
}