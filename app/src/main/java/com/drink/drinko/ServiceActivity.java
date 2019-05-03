package com.drink.drinko;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServiceActivity extends AppCompatActivity implements Click {
    Storage storage;
    TextView tvAddress;
    int radius=1;
    int count=0;
    ArrayList<User> arrayListUser;
    RecyclerView recyclerView;
    SupplierAdapter supplierAdapter;
    Connection connection;
    ProgressBar progressBar;
    String userType=" ";
    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        tvAddress=findViewById(R.id.tvAddress);
        progressBar=findViewById(R.id.progress);
        storage=new Storage(this);
        arrayListUser =new ArrayList<>();
        recyclerView=findViewById(R.id.recycler);
        connection=new Connection();
        database=new Database(this);
        supplierAdapter=new SupplierAdapter(arrayListUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(supplierAdapter);
        tvAddress.setText("My Address \n "+storage.getLocation());
        try{
            userType=getIntent().getStringExtra("userType");
        }catch (Exception e){

        }

        if(storage.getLatitude()!=null||storage.getLongitude()!=null){
            findSupplier(userType);
        }
    }
    private void findSupplier(String type) {
        DatabaseReference ref=new Connection().getDbRequest().child(type);
        GeoFire geoFire=new GeoFire(ref);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(Double.valueOf(storage.getLatitude()),Double.valueOf(storage.getLongitude())),radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                connection.getDbUser().child("none").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            User user=dataSnapshot.getValue(User.class);
                            if(user.getName()==null){
                                if(key!=null){
                                    new Connection().getDbRequest().child("Supplier").child(key).removeValue();
                                    new Connection().getDbRequest().child("Certified Supplier").child(key).removeValue();
                                    new Connection().getDbRequest().child("Cold Water supplier").child(key).removeValue();
                                }
                            }else{
                                arrayListUser.add(user);
                                count++;
                                progressBar.setVisibility(View.GONE);
                                supplierAdapter.notifyDataSetChanged();
                            }

                        }
                        else{
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(getApplicationContext(),"keyExited",Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

  //          Toast.makeText(getApplicationContext(),"keyExited",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
    //            Toast.makeText(getApplicationContext(),"onKeyMoved",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onGeoQueryReady() {
      //          Toast.makeText(getApplicationContext(),"onGeoQueryReady",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
        //        Toast.makeText(getApplicationContext(),"onGeoQueryError",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling,menu);
        if(storage.getId()!=null){
            menu.findItem(R.id.logIn).setTitle("My Profile");
            menu.findItem(R.id.logout).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       Intent intent;
        switch (item.getItemId()){
            case R.id.updateLocation:


//                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//                finish();



                intent=new Intent(this,MapsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logIn:
                if(storage.getId()!=null){
                    intent=new Intent(this,MyProfileActivity.class);
                    startActivity(intent);

//                    storage.clear();
//                    item.setTitle("Log in");
//                    Toast.makeText(this,"you are logged out",Toast.LENGTH_SHORT).show();
//                    FirebaseAuth.getInstance().signOut();
                    return true;
                }
                intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
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

    @Override
    public void onClick(User user) {
        String number=user.getContact();
        try{
             Double.parseDouble(number);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ServiceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ServiceActivity.this, new String[]{Manifest.permission.CALL_PHONE},234);
                    Toast.makeText(this,"please try again",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    database.insert(user);
                        startActivity(intent);
                }
            }
            else
            {
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this,"invalid supplier number",Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ServiceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ServiceActivity.this, new String[]{Manifest.permission.CALL_PHONE},234);
                Toast.makeText(this,"please try again",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void delete(String contact) {

    }
}
