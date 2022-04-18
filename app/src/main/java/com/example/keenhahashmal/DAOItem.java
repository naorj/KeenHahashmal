package com.example.keenhahashmal;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOItem {

    private final DatabaseReference databaseReference;

    public DAOItem(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference=db.getReference(Item.class.getSimpleName());
    }
    public Task<Void> add(Item item){
        return databaseReference.push().setValue(item);
    }
}