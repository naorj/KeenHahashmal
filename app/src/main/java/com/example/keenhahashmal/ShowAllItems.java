package com.example.keenhahashmal;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ShowAllItems extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout=findViewById(R.id.tablayout_id);
        viewPager=findViewById(R.id.viewpager_id);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new FragmentShowItems(),"תצוגת פריטים");
        adapter.AddFragment(new FragmentShowDeliveryItems(),"תצוגת תעודות משלוח");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_airport_shuttle);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setElevation(0);
    }
}