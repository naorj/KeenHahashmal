package com.example.keenhahashmal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();


        ImageView btnViewAll=findViewById(R.id.btnViewAll);
        ImageView btnCreatePDF=findViewById(R.id.btnCreatePDF);
        ImageView fabadditem = findViewById(R.id.addFab);
        //ImageView disconnect=findViewById(R.id.disconnect);
        ImageView product=findViewById(R.id.product);

        fabadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnObjectActivity();
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsActivity();
            }
        });
        btnCreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatePDFActivity();
            }
        });
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productCompareActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.disconnect:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }
    }

    private void addAnObjectActivity(){
        //Intent intent = new Intent(this, addAnObject.class);  //this was before
        Intent intent=new Intent(this,AddAnItemOrDeliveryNote.class);
        startActivity(intent);
    }

    private void itemsActivity(){
        //Intent intent = new Intent(this, ItemsActivity.class); //this was before
        Intent intent=new Intent(this,ShowAllItems.class);
        startActivity(intent);
    }
    private void CreatePDFActivity(){
        Intent intent = new Intent(this, CreatePDF.class);
        startActivity(intent);
    }
    private void productCompareActivity(){
        Intent intent=new Intent(this,ProductCompare.class);
        startActivity(intent);
    }
}