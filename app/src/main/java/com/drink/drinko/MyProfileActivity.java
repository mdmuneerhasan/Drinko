package com.drink.drinko;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String userType;
    EditText edtName;
    EditText edtNumber;
    EditText edtUrl;
    String email;
    String password;
    String name;
    String number;
    String url;
    String startTime,endTime;
    Storage storage;
    boolean boolAuthenticate;
    RadioButton rdSupplier,rdcoldSupplier,rdCertifiedSupplier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        storage=new Storage(this);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progress_circular);
        rdSupplier=findViewById(R.id.radioSupplier);
        rdcoldSupplier=findViewById(R.id.radioColdWater);
        rdCertifiedSupplier=findViewById(R.id.radioCertifiedSupplier);
        edtName = findViewById(R.id.edtName);
        edtNumber = findViewById(R.id.edtNumber);
        edtUrl = findViewById(R.id.edtUrl);
        email=storage.getEmail();
        boolAuthenticate=false;
        url="default";
        userType="Customer";
        RadioGroup radioGroup=findViewById(R.id.radio);


        // setting default
        if(storage.getUserType().equals("Supplier")){
            userType="Supplier";
            rdSupplier.setChecked(true);
            rdSupplier.setVisibility(View.VISIBLE);
        }else if(storage.getUserType().equals("Certified Supplier")){
            rdCertifiedSupplier.setChecked(true);
            userType="Certified Supplier";
            rdCertifiedSupplier.setVisibility(View.VISIBLE);
        }else if(storage.getUserType().equals("Cold Water supplier")){
            rdcoldSupplier.setChecked(true);
            userType="Cold Water Supplier";
            rdcoldSupplier.setVisibility(View.VISIBLE);
        }else{
            RadioButton rdCustomer=findViewById(R.id.radioCustomer);
            rdCustomer.setVisibility(View.VISIBLE);
            rdCustomer.setChecked(true);
        }
        edtName.setText(storage.getName());
        edtUrl.setText(storage.getProfile());
        edtNumber.setText(storage.getContact());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioCustomer:
                        userType="Customer";
                        boolAuthenticate=false;
                        break;
                    case R.id.radioSupplier:
                        boolAuthenticate=false;
                        userType="Supplier";
                        break;
                    case R.id.radioCertifiedSupplier:
                        boolAuthenticate=true;
                        userType="Certified Supplier";
                        break;
                    case R.id.radioColdWater:
                        boolAuthenticate=true;
                        userType="Cold Water supplier";
                        break;
                }
            }
        });
        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtName.getText().toString();
                number = edtNumber.getText().toString();
                url = edtUrl.getText().toString();
                if(!email.contains("@")||email.trim().length()<4){
                    return;
                }
                if(name.trim().length()<4){
                    edtName.setError("Name too short");
                    return;
                }
                if(number.trim().length()<10){
                    edtNumber.setError("Number not valid");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                TimePicker start=findViewById(R.id.timeStart);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startTime=String.valueOf(start.getHour()*100+start.getMinute());
                }
                TimePicker end=findViewById(R.id.timeEnd);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    endTime=String.valueOf(end.getHour()*100+end.getMinute());
                }
                if(storage.getId()!=null){
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(MyProfileActivity.this, "Data updated.",
                            Toast.LENGTH_SHORT).show();
                    Connection connection=new Connection();
                    User user1=new User(name,email,userType,url,number,"none");
                    user1.setEndTime(endTime);user1.setStartTime(startTime);
                    connection.getDbUser().child(userType).child(storage.getId()).setValue(user1);
                    connection.getDbUser().child("none").child(storage.getId()).setValue(user1);
                    storage.setContact(number);
                    storage.setEmail(email);
                    storage.setName(name);
                    storage.setProfile(url);
                    storage.setUserType(userType);
                    Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                    startActivity(intent);
                    finish();

                }
        }
    });
    }
}
