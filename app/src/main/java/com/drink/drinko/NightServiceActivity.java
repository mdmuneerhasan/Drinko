package com.drink.drinko;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import okhttp3.internal.Util;

public class NightServiceActivity extends AppCompatActivity {
    TextView tvAddress,tvTiming,tvRate,tvAgreement;
    Spinner spinner;
    Storage storage;
    String defaultNumber;
    boolean service=false;
    int numberOfBottles=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_service);
        tvAddress=findViewById(R.id.tvAddress);
        spinner=findViewById(R.id.spinner);
        tvTiming=findViewById(R.id.tvTiming);
        tvRate=findViewById(R.id.tvRate);
        tvAgreement=findViewById(R.id.tvAgreement);
        storage=new Storage(this);
        Picasso.get().load("https://www.bisleri.com/sites/default/files/styles/product_main_image/public/2018-04/250ml_0.png?itok=I6eU7DNp").resize(450,250).centerCrop().into((ImageView) findViewById(R.id.imageView1));
        Picasso.get().load("https://5.imimg.com/data5/TW/JT/MY-56363871/aquafina-mineral-water-500x500.jpg").resize(450,250).centerCrop().into((ImageView) findViewById(R.id.imageView2));
        Picasso.get().load("https://4.imimg.com/data4/AX/LX/ANDROID-37115108/product-250x250.jpeg").resize(450,250).centerCrop().into((ImageView) findViewById(R.id.imageView3));
        String bottles[]={"select no. of bottle","1 bottle","2 bottle","3 bottle","4 bottle"};
        ArrayAdapter<String> ad=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,bottles);
        spinner.setAdapter(ad);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfBottles=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Connection().getDbControl().child("nightService").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // setting defaults
                int avail = dataSnapshot.child("availblity").getValue(Integer.class);
                double lat = dataSnapshot.child("lat").getValue(Double.class);
                double error = dataSnapshot.child("error").getValue(Double.class);
                String start= dataSnapshot.child("startTime").getValue(String.class);
                String end= dataSnapshot.child("endTime").getValue(String.class);

                defaultNumber = dataSnapshot.child("number").getValue(String.class);
                double longi = dataSnapshot.child("longi").getValue(Double.class);
                tvAddress.setText(storage.getLocation());
                tvRate.setText(dataSnapshot.child("rate").getValue(String.class));
                if (Math.abs(lat - Float.parseFloat(storage.getLatitude())) < error && Math.abs(longi - Float.parseFloat(storage.getLongitude())) < error) {
                    if (avail != 0) {
                        tvTiming.setText("Service Available ");
                        tvAgreement.setText(dataSnapshot.child("agreement").getValue(String.class));
                        service=true;
                    } else {
                        tvTiming.setText("service not available at this time please try in working hour \n Timing : "+start+" to "+end);
                        service=false;
                    }
                }else {
                    tvTiming.setText("service not available in your location");
                    service=false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendRequest(View view) {
        if(service){
            StringBuilder message=new StringBuilder("Please supply water ");
            if(storage.getName()!=null){
                message.append("to "+storage.getName()+" ");
            }
            if(storage.getLocation()!=null){
                message.append("at "+storage.getLocation()+"\n");
                message.append("http://www.google.com/maps/place/"+storage.getLatitude()+","+storage.getLongitude());
            }
            if(numberOfBottles<1){
                Toast.makeText(this,"please select number of bottles",Toast.LENGTH_LONG).show();
                return;
            }else {
                message.append(" Quantity : "+numberOfBottles+" bottles");
            }
            if(storage.getContact()!=null){
                message.append(" contact number : "+storage.getContact());

            }
            String formattedNumber = "91"+defaultNumber;
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+formattedNumber +"&text="+message.toString()));
                startActivity(intent);
            }
            catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"it may be you don't have whats app",Toast.LENGTH_LONG).show();

        }
        }else {
            Toast.makeText(this,String.valueOf("Something went wrong..."),Toast.LENGTH_SHORT).show();
            tvTiming.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    public void call(View view) {
        if(service){
            StringBuilder message=new StringBuilder("Please supply water ");
            if(storage.getName()!=null){
                message.append("to "+storage.getName()+" ");
            }
            if(storage.getLocation()!=null){
                message.append("at "+storage.getLocation()+"\n");
                message.append("http://www.google.com/maps/place/"+storage.getLatitude()+","+storage.getLongitude());
            }
            if(numberOfBottles<1){
                Toast.makeText(this,"please select number of bottles",Toast.LENGTH_LONG).show();
                return;
            }else {
                message.append(" Quantity : "+numberOfBottles+" bottles");
            }
            if(storage.getContact()!=null){
                message.append(" contact number : "+storage.getContact());

            }
            try{
                Double.parseDouble(defaultNumber);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + defaultNumber));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},234);
                    }
                    else
                    {
                        startActivity(intent);
                        FirebaseDatabase.getInstance().getReference("complement").push().child("location").setValue(message.toString());

                    }
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("complement").push().child("location").setValue(message.toString());
                    startActivity(intent);
                }
            }catch (Exception e){

                Toast.makeText(this,"invalid supplier number",Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this,String.valueOf("Something went wrong..."),Toast.LENGTH_SHORT).show();
            tvTiming.setTextColor(Color.parseColor("#ff0000"));
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==234){
            if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                call(new View(this));
            }else{
                Toast.makeText(this,"Please Grant Permission...",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
