package com.example.keenhahashmal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentShowItems extends Fragment {

    View v;

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Item> mItem;

    public FragmentShowItems() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_items,container,false);
        mRecyclerView=v.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mItem=new ArrayList<>();
        mAdapter=new ImageAdapter(getContext(),mItem);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

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
                Collections.reverse(mItem);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.searchmenu,menu); // searchmenu is the name of the file, menu is class argument
        MenuItem item=menu.findItem(R.id.search);
        MenuItem item2=menu.findItem(R.id.sp);
        //item2.setVisible(false);
        Spinner sp = (Spinner) item2.getActionView(); // find the spinner
        ArrayAdapter<String> myAdapter=new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.filterOfItem));
        sp.setAdapter(myAdapter);

        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //if (!s.isEmpty())
                //searchitems(s,sp.getSelectedItem().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty())
                    searchitems(s,sp.getSelectedItem().toString());
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter=new ImageAdapter(getContext(),mItem);
                mRecyclerView.setAdapter(mAdapter);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchitems(String s, String spinnerState){
        Query query=null;
        if (spinnerState.equals("תאריך")){
            query=mDatabaseRef.orderByChild("dateOfCreate").startAt(s).endAt(s+"\uf8ff");
        }
        else if (spinnerState.equals("מחיר")){
            if(s.indexOf('.', s.indexOf('.') + 1) != -1) {
                Toast.makeText(getContext(), "הינך מכניס תאריך במקום מחיר", Toast.LENGTH_SHORT).show();
                query=mDatabaseRef.orderByChild("dateOfCreate").startAt(s).endAt(s+"\uf8ff");
            }
            else{
                query=mDatabaseRef.orderByChild("price").startAt(Double.parseDouble(s));
            }
        }
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
                    mAdapter=new ImageAdapter(getContext(),searchList);
                    mRecyclerView.setAdapter(mAdapter);
                }
                else{
                    List<Item> searchList = new ArrayList<>();
                    mAdapter=new ImageAdapter(getContext(),searchList);
                    mRecyclerView.setAdapter(mAdapter);
                    Toast.makeText(getContext(), "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}