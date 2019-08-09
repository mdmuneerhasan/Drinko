package com.drink.drinko;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Connection {
    FirebaseDatabase instance;


    public Connection() {
        instance=FirebaseDatabase.getInstance();
    }

    public DatabaseReference getDbUser() {
        return instance.getReference("user");
    }

    public DatabaseReference getDbRequest() {
        return instance.getReference("request");
    }

    public DatabaseReference getDbOffer() {
        return instance.getReference("offer");
    }
    public DatabaseReference getDbControl() {
        return instance.getReference("control");
    }

}
