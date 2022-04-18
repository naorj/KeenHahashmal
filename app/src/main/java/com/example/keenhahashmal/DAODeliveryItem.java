package com.example.keenhahashmal;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAODeliveryItem {

    private final DatabaseReference databaseReference;

    public DAODeliveryItem(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference=db.getReference(DeliveryItem.class.getSimpleName());
    }

    public Task<Void> add(DeliveryItem deliveryItem){ return databaseReference.push().setValue(deliveryItem); }
}