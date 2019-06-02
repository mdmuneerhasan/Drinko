package com.drink.drinko;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String userType;
    String email;
    String password;
    String name;
    String number;
    Connection connection;
    String url;
    EditText edtAuthenticate;
    Button btnAuthenticate;
    String startTime,endTime;
    String count;
    boolean boolAuthenticate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progress_circular);
        edtAuthenticate=findViewById(R.id.edtAuthentication);
        btnAuthenticate=findViewById(R.id.btnAuthenticate);

        edtAuthenticate.setVisibility(View.GONE);
        btnAuthenticate.setVisibility(View.GONE);
        boolAuthenticate=false;
        url="default";
        userType="Customer";
        RadioGroup radioGroup=findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioCustomer:
                        edtAuthenticate.setVisibility(View.GONE);
                        btnAuthenticate.setVisibility(View.GONE);
                        userType="Customer";
                        boolAuthenticate=false;
                        break;
                    case R.id.radioSupplier:
                        edtAuthenticate.setVisibility(View.GONE);
                        btnAuthenticate.setVisibility(View.GONE);
                        boolAuthenticate=false;
                        userType="Supplier";
                        break;
                    case R.id.radioCertifiedSupplier:
                        edtAuthenticate.setVisibility(View.VISIBLE);
                        btnAuthenticate.setVisibility(View.VISIBLE);
                        boolAuthenticate=true;
                        userType="Certified Supplier";
                        break;
                    case R.id.radioColdWater:
                        edtAuthenticate.setVisibility(View.VISIBLE);
                        boolAuthenticate=true;
                        btnAuthenticate.setVisibility(View.VISIBLE);
                        userType="Cold Water supplier";
                        break;
                }
            }
        });
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtAuthenticate.getText().toString().equals("9876")){
                    edtAuthenticate.setVisibility(View.GONE);
                    btnAuthenticate.setText("Authentication Successful");
                    btnAuthenticate.setBackgroundColor(Color.parseColor("#00FF00"));
                    boolAuthenticate=false;
                }else{
                    edtAuthenticate.setError("Wrong Code try Again");
                }
            }
        });
        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtEmail=findViewById(R.id.edtEmail);
                EditText edtPassword=findViewById(R.id.edtPassword);
                EditText edtName=findViewById(R.id.edtName);
                EditText edtNumber=findViewById(R.id.edtNumber);
                EditText edtUrl=findViewById(R.id.edtUrl);

                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                name = edtName.getText().toString();
                number = edtNumber.getText().toString();
                url = edtUrl.getText().toString();
                if(edtAuthenticate.getText().toString().equals("9876")){
                    edtAuthenticate.setVisibility(View.GONE);
                    btnAuthenticate.setText("Authentication Successful");
                    btnAuthenticate.setBackgroundColor(Color.parseColor("#00FF00"));
                    boolAuthenticate=false;
                }else{
                    edtAuthenticate.setError("Wrong Code try Again");
                }
                if(boolAuthenticate){
                    edtAuthenticate.setError("Enter Authentication Code");
                    return;
                }
                if(!email.contains("@")||email.trim().length()<4){
                    edtEmail.setError("invalid email");
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
                if(password.trim().length()<4){
                    edtPassword.setError("Password too short");
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
                connection = new Connection();

                connection.getDbOffer().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count= String.valueOf(dataSnapshot.getChildrenCount());
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(SignUpActivity.this, "Authentication Successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            User user1=new User(name,email,userType,url,number,"not available");
                                            user1.setUid(user.getUid());
                                            user1.setRating(getString(R.string.Rating));
                                            user1.setNumberOfRating(getString(R.string.NumberOfRating));
                                            user1.setEndTime(endTime);user1.setStartTime(startTime);
                                            connection.getDbUser().child(userType).child(user.getUid()).setValue(user1);
                                            connection.getDbUser().child("none").child(user.getUid()).setValue(user1);
                                            connection.getDbOffer().child("11"+count).setValue(user1);
                                            Storage storage=new Storage(getBaseContext());
                                            storage.setContact(number);
                                            storage.setEmail(email);
                                            storage.setId(user.getUid());
                                            storage.setName(name);
                                            storage.setProfile(url);
                                            storage.setUserType(userType);
                                            Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            // If sign in fails, display a message to the user.
                                            Log.w("error On Muneer", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed. "+task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                            //   updateUI(null);
                                        }


                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
    });
    }
}
