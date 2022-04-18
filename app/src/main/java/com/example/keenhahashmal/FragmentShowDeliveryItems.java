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

public class FragmentShowDeliveryItems extends Fragment {

    View v;

    private RecyclerView mRecyclerView;
    private ImageAdapterDelivery mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<DeliveryItem> mDeliveryItem;

    public FragmentShowDeliveryItems() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_fragment_show_delivery_items,container,false);
        mRecyclerView=v.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeliveryItem=new ArrayList<>();
        mAdapter=new ImageAdapterDelivery(getContext(),mDeliveryItem);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("DeliveryItem");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDeliveryItem.clear();
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    DeliveryItem mDitem = postSnapshot.getValue(DeliveryItem.class);
                    mDitem.setKey(postSnapshot.getKey());
                    mDeliveryItem.add(mDitem);
                }
                Collections.reverse(mDeliveryItem);
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
        inflater.inflate(R.menu.searchmenu,menu);
        MenuItem item=menu.findItem(R.id.search);
        MenuItem item2=menu.findItem(R.id.sp);
        Spinner sp = (Spinner) item2.getActionView(); // find the spinner
        ArrayAdapter<String> myAdapter=new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.filterOfDelivery));
        sp.setAdapter(myAdapter);

        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty())
                    search(s,sp.getSelectedItem().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty())
                    search(s,sp.getSelectedItem().toString());
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter=new ImageAdapterDelivery(getContext(),mDeliveryItem);
                mRecyclerView.setAdapter(mAdapter);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void search(String s,String spinnerState) {

        Query query=null;
        if(spinnerState.equals("תאריך")){
            query=mDatabaseRef.orderByChild("dateOfCreate").startAt(s).endAt(s+"\uf8ff");
        }
        else if (spinnerState.equals("שולם?")){
            query=mDatabaseRef.orderByChild("isPaid").startAt(s).endAt(s+"\uf8ff");
        }
        else if (spinnerState.equals("ספק")){
            query=mDatabaseRef.orderByChild("nameOfSupplier").startAt(s).endAt(s+"\uf8ff");
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    List<DeliveryItem> searchList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        DeliveryItem item = postSnapshot.getValue(DeliveryItem.class);
                        item.setKey(postSnapshot.getKey());
                        searchList.add(item);
                    }
                    mAdapter=new ImageAdapterDelivery(getContext(),searchList);
                    mRecyclerView.setAdapter(mAdapter);
                }
                else{
                    List<DeliveryItem> searchList = new ArrayList<>();
                    mAdapter=new ImageAdapterDelivery(getContext(),searchList);
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