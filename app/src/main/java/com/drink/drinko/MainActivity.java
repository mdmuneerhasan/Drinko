package com.drink.drinko;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    Storage storage;
    TextView tvAddress;
    int radius=11;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAddress=findViewById(R.id.tvAddress);
        storage=new Storage(this);



        tvAddress.setText("My Address \n "+storage.getLocation());
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if(storage.getLatitude()!=null||storage.getLongitude()!=null){
            findSupplier();
        }

    }
    StringBuilder stringBuilder=new StringBuilder();
    private void findSupplier() {
        DatabaseReference ref=new Connection().getDbRequest().child("Supplier");
        GeoFire geoFire=new GeoFire(ref);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(Double.valueOf(storage.getLatitude()),Double.valueOf(storage.getLongitude())),radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
             stringBuilder.append(key+"\n");
             TextView textView=findViewById(R.id.tvTest);
                textView.setText(stringBuilder);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(count<10){
                    if(radius>10){
                        return;
                    }
                    radius++;
                    findSupplier();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling,menu);
        if(storage.getId()!=null){
            menu.findItem(R.id.logIn).setTitle("Log out");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.updateLocation:
                Intent intent=new Intent(this,MapsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logIn:
                if(storage.getId()!=null){
                    storage.clear();
                    item.setTitle("Log in");
                    Toast.makeText(this,"you are logged out",Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    return true;
                }
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

}
