package com.drink.drinko;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

public class MainActivity extends AppCompatActivity implements Click {
    // new repo 1.1
    Storage storage;
    TextView tvAddress;
    int radius=1;
    int count=0;
    ArrayList<User> arrayListUser;
    ArrayList<User> arrayListService;
    RecyclerView recyclerView,recyclerView2;
    SupplierAdapter supplierAdapter;
    ServiceAdapter serviceAdapter;
    Connection connection;
    ProgressBar progressBar;
    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAddress=findViewById(R.id.tvAddress);
        progressBar=findViewById(R.id.progress);

        database=new Database(this);
        storage=new Storage(this);
        arrayListUser =new ArrayList<>();
        arrayListService =new ArrayList<>();
        recyclerView=findViewById(R.id.recycler);
        recyclerView2=findViewById(R.id.recycler2);
        connection=new Connection();
        supplierAdapter=new SupplierAdapter(arrayListUser);
        serviceAdapter=new ServiceAdapter(arrayListService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(supplierAdapter);



        arrayListService.add(new User("Cold Water Service","","Cold Water supplier","https://mondrian.mashable.com/uploads%252Fcard%252Fimage%252F782032%252F44e33672-4339-426b-a2e9-22e78a700e00.jpg%252F950x534__filters%253Aquality%252890%2529.jpg?signature=C9zK312NjY7n2SNEzTRCErsJ0tQ=&source=https%3A%2F%2Fblueprint-api-production.s3.amazonaws.com","",""));
        arrayListService.add(new User("Purity Certified Supplier","","Certified Supplier","https://img.freepik.com/free-vector/guarantee-best-quality-stamp_1017-7145.jpg?size=338&ext=jpg","",""));
        recyclerView2.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView2.setAdapter(serviceAdapter);
        tvAddress.setText("My Address \n "+storage.getLocation());
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                finish();



//                Intent intent=new Intent(getBaseContext(),MapsActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        fetch();

    }

    private void fetch() {

        arrayListUser.addAll(database.getUser());
        if(storage.getLatitude()!=null||storage.getLongitude()!=null){
            findSupplier("Supplier");
            findSupplier("Certified Supplier");
            findSupplier("Cold Water supplier");
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
                            if(user==null){
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

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {
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
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},234);
                    Toast.makeText(this,"please try again",Toast.LENGTH_SHORT).show();
                }
                else
                {   database.insert(user);
                    startActivity(intent);
                    arrayListUser.clear();
                    fetch();

                }
            }
            else
            {
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this,"invalid supplier number",Toast.LENGTH_SHORT).show();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},234);
                Toast.makeText(this,"please try again",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void delete(String contact) {
        database.deleteUser(contact);
        arrayListUser.clear();
        fetch();

    }
}
