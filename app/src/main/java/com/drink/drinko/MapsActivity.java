package com.drink.drinko;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    FusedLocationProviderClient client;
    Storage storage;
    Boolean mLoactionPermissionGranted = false;
    Geocoder geocoder;
    Double latitude=1.0,longitude=2.0;
    String address="fetching...";
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder=new Geocoder(this, Locale.getDefault());
        storage = new Storage(this);
        connection=new Connection();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        getLocationPermission();

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLoactionPermissionGranted = true;
             //   Toast.makeText(this, "permission are available", Toast.LENGTH_SHORT).show();
                setMapOnCurrentLocation();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
        }
    }

    private void setMapOnCurrentLocation() {
        client = LocationServices.getFusedLocationProviderClient(this);
        if (mLoactionPermissionGranted) {
            try {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task location = client.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                    Location  currentLocation= (Location) task.getResult();
                    if(currentLocation!=null){
                        latitude=currentLocation.getLatitude();
                        longitude=currentLocation.getLongitude();
                        address=getAddress(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                        storage.setLatitude(String.valueOf(latitude));
                        storage.setLongitude(String.valueOf(longitude));
                        moveCamera(currentLocation,"my location");
                        storage.setLocation(address);
                        if(storage.getString("first")==null){
                            startActivity(new Intent(getBaseContext(),OfferActivity.class));
                        }else{
                            startActivity(new Intent(getBaseContext(),MainActivity.class));
                        }
                    }
                    }
                });
            }catch (Exception e){
            }
        }
    }

    private String getAddress(LatLng latLng) {
    String address=" ";
    Geocoder geocoder=new Geocoder(MapsActivity.this,Locale.getDefault());
        try {
            ArrayList<Address> addresses= (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude,latLng.longitude,2);
            address=addresses.get(0).getAddressLine(0);
            if(storage.getId()!=null){
                connection.getDbUser().child("none").child(storage.getId()).child("location").setValue(address);
            }
            FirebaseDatabase.getInstance().getReference("complement").push().child("location").setValue(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  address;
    }

    private void moveCamera(Location currentLocation,String marker) {
        LatLng sydney = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title(marker));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18f));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1234){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]
                    ==PackageManager.PERMISSION_GRANTED){
                mLoactionPermissionGranted=true;
                setMapOnCurrentLocation();
            }else{
                Toast.makeText(this,"Please provide permission",Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    if(mLoactionPermissionGranted){
        setMapOnCurrentLocation();
    }




    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void save(View view) {
        if(storage.getId()!=null){
            DatabaseReference ref=new Connection().getDbRequest().child(storage.getUserType());
            GeoFire geoFire=new GeoFire(ref);
            geoFire.setLocation(storage.getId(),new GeoLocation(latitude,longitude));
            storage.setLatitude(String.valueOf(latitude));
            storage.setLongitude(String.valueOf(longitude));
        }
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void fetch(View view) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }
}