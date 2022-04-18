package com.example.keenhahashmal;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOProduct {

    private final DatabaseReference databaseReference;
    public DAOProduct(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference=db.getReference(Product.class.getSimpleName());
    }

    public Task<Void> add(Product product){ return databaseReference.push().setValue(product); }
}