package com.drink.drinko;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String userType;
    String email;
    String password;
    String name;
    String number;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progress_circular);
        url="default";
        userType="Customer";
        RadioGroup radioGroup=findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioCustomer:
                        userType="Customer";
                        break;
                    case R.id.radioSupplier:
                        userType="Supplier";
                        break;
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
                                    Connection connection=new Connection();
                                    User user1=new User(name,email,userType,url,number,"not available");
                                    connection.getDbUser().child(userType).child(user.getUid()).setValue(user1);
                                    connection.getDbUser().child("none").child(user.getUid()).setValue(user1);
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
    });
    }
}
