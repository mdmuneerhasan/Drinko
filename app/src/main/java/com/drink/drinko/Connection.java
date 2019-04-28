package com.drink.drinko;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
