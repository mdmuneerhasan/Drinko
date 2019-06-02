package com.drink.drinko;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class OfferActivity extends AppCompatActivity {
    LinearLayout linearLayout1,linearLayout2;
    EditText edtId;
    String stringId;
    Connection connection;
    TextView tvCode,tvName,tvAddress;
    Storage storage;
    User user;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        linearLayout1=findViewById(R.id.linearLayout1);
        linearLayout2=findViewById(R.id.linearLayout2);
        edtId=findViewById(R.id.edtSupplierId);
        connection=new Connection();
        tvAddress=findViewById(R.id.edtAddress);
        tvCode=findViewById(R.id.tvCode);
        tvName=findViewById(R.id.edtName);
        storage=new Storage(this);
    }










    public void claim(final View view) {
        stringId = edtId.getText().toString();
        if (stringId.length()>2) {
            connection.getDbOffer().child(stringId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if(user!=null){
                        linearLayout2.setVisibility(View.VISIBLE);
                        linearLayout1.setVisibility(View.GONE);
                        tvName.setText(user.getName());
                        tvAddress.setText(user.getContact());
                        code= String.valueOf((int)(Math.random()*10000));
                        tvCode.setText("OTP : "+code);
                        claim2(view);
                    }else {
                        edtId.setError("No supplier exist with this id");
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }else{
            edtId.setError("Invalid id");
            Toast.makeText(this,"Invalid supplier id\nTry again...",Toast.LENGTH_SHORT).show();
        }
    }

    public void claim2(View view) {
        storage.setString("1name",user.getName());
        storage.setString("1number",user.getContact());
        storage.setString("1uid",user.getUid());
        storage.setString("1endTime",user.getEndTime());
        storage.setString("1startTime",user.getStartTime());
        storage.setString("1profile",user.getProfile());
        connection.getDbOffer().child("code").child(user.getName()).child(code).setValue(storage.getLocation());
        Toast.makeText(this,"you have claimed your offer",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connection.getDbOffer().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int versionNumber=1;
                String response=dataSnapshot.child("avail").getValue(String.class);
                if(response!=null){
                    PackageInfo pinfo = null;
                    try {
                        pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionNumber = pinfo.versionCode;

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(pinfo==null){
                        Toast.makeText(getBaseContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    int version= Integer.parseInt(response);
                    if(version>versionNumber){
                        storage.setString("1name","yes");
                        Toast.makeText(getBaseContext(),"Please update your version",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getBaseContext(),MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storage.setString("first","not null");
    }

    public void skip(View view) {
        startActivity(new Intent(this,MapsActivity.class));
    }
}
