package com.example.keenhahashmal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductCompare extends AppCompatActivity {

    Context mContext=this;
    private RecyclerView mRecyclerView;
    private ImageAdapterProduct mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Product> mItem;
    FloatingActionButton fab;
    private int mDate, mMonth, mYear;
    Date date2;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_compare);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab=findViewById(R.id.fab);

        mRecyclerView=findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItem=new ArrayList<>();
        mAdapter=new ImageAdapterProduct(ProductCompare.this,mItem);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    fab.hide();
                } else{
                    fab.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Product");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(mContext)
                        .setContentHolder(new ViewHolder(R.layout.dialogcontentforfab))
                        .setExpanded(true,1150) // was 1200
                        .create();

                View myview=dialogPlus.getHolderView();
                EditText price=myview.findViewById(R.id.price);
                EditText name=myview.findViewById(R.id.name);
                EditText date=myview.findViewById(R.id.date);
                EditText description=myview.findViewById(R.id.description);
                AutoCompleteTextView auto=myview.findViewById(R.id.autoCompleteTextView);
                Button add=myview.findViewById(R.id.usubmit);

                //auto-complete supplier: (the find view added above with all the edit texts)

                ArrayAdapter<String> myad=new ArrayAdapter<>(ProductCompare.this,
                        android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.suppliers));
                auto.setAdapter(myad);
                auto.setThreshold(1);

                date.setOnClickListener((View v) -> {
                    final Calendar Cal=Calendar.getInstance();
                    mDate=Cal.get(Calendar.DAY_OF_MONTH);
                    mMonth=Cal.get(Calendar.MONTH);
                    mYear=Cal.get(Calendar.YEAR);
                    DatePickerDialog datePickerDialog=new DatePickerDialog(mContext,
                            (datePicker, year, month, dayOfMonth) -> {
                        Cal.set(year, month, dayOfMonth);
                        date.setText(simpleDateFormat.format(Cal.getTime()));
                        date2=Cal.getTime();
                    },mYear,mMonth,mDate);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                    datePickerDialog.show();
                });

                dialogPlus.show();
                DAOProduct daoProduct=new DAOProduct();
                add.setOnClickListener(v -> {

                    double pricee;
                    if (price.getText().toString().length() == 0) {
                        pricee = 0;
                    } else {
                        pricee = Double.parseDouble(price.getText().toString());
                    }
                    Product product = new Product(name.getText().toString(), pricee,
                            description.getText().toString(), date.getText().toString(), "נקנה מ: "+auto.getText().toString());
                    daoProduct.add(product).addOnSuccessListener((Void suc) ->
                    {
                        dialogPlus.dismiss();
                        try {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }).addOnFailureListener(er -> {
                        dialogPlus.dismiss();
                        Toast.makeText(ProductCompare.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    Toast.makeText(ProductCompare.this, "מוצר הוכנס", Toast.LENGTH_SHORT).show();
                });
            }
        });

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItem.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Product product = postSnapshot.getValue(Product.class);
                    product.setmKey(postSnapshot.getKey());
                    mItem.add(product);
                }
                Collections.reverse(mItem);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductCompare.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);
        MenuItem item=menu.findItem(R.id.search);
        MenuItem item2=menu.findItem(R.id.sp).setVisible(false);

        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter=new ImageAdapterProduct(mContext,mItem);
                mRecyclerView.setAdapter(mAdapter);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void search(String s){
        Query query=mDatabaseRef.orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    List<Product> searchList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Product product = postSnapshot.getValue(Product.class);
                        product.setmKey(postSnapshot.getKey());
                        searchList.add(product);
                    }
                    ImageAdapterProduct mAdapterTemp=new ImageAdapterProduct(ProductCompare.this,searchList);
                    mRecyclerView.setAdapter(mAdapterTemp);
                }
                else{
                    List<Product> searchList = new ArrayList<>();
                    mAdapter=new ImageAdapterProduct(mContext,searchList);
                    mRecyclerView.setAdapter(mAdapter);
                    Toast.makeText(ProductCompare.this, "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductCompare.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}