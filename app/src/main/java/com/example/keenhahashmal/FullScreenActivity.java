package com.example.keenhahashmal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenActivity extends AppCompatActivity {

    PhotoView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView=findViewById(R.id.fullScreenImageView);
        Intent callingActivityIntent=getIntent();
        if(callingActivityIntent!=null){
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri!=null && imageView!=null){
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView);
            }
        }
    }
}