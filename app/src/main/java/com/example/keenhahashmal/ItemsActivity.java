package com.example.keenhahashmal;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Item> mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView=findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItem=new ArrayList<>();
        mAdapter=new ImageAdapter(ItemsActivity.this,mItem);
        mRecyclerView.setAdapter(mAdapter);

        mStorage=FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Item");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItem.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Item item = postSnapshot.getValue(Item.class);
                    item.setKey(postSnapshot.getKey());
                    mItem.add(item);
                }
                Collections.reverse(mItem);     //added
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }*/

  /*  private void search(String s) {

        Query query=mDatabaseRef.orderByChild("expenseType").startAt(s).endAt(s+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    List<Item> searchList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Item item = postSnapshot.getValue(Item.class);
                        item.setKey(postSnapshot.getKey());
                        searchList.add(item);
                    }
                    //Collections.reverse(searchList);     //added
                    ImageAdapter mAdapter2=new ImageAdapter(ItemsActivity.this,searchList);
                    //mAdapter2.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mAdapter2);
                }
                else{
                    Toast.makeText(ItemsActivity.this, "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}