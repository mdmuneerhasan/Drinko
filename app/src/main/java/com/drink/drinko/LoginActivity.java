package com.drink.drinko;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG ="LoginActivity" ;
    String email;
    String password;
    EditText edtEmail;
    EditText edtPassword;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    Storage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        progressBar=findViewById(R.id.progress_circular);
        mAuth=FirebaseAuth.getInstance();
        storage=new Storage(this);
        if(storage.getId()!=null){
            finish();
        }
        findViewById(R.id.btnSign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             email=edtEmail.getText().toString();
             password=edtPassword.getText().toString();
                if(email.trim().length()<2){
                    edtEmail.setError("invalid email");
                    return;
                }
                if(password.trim().length()<2){
                    edtPassword.setError("invalid password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    final DatabaseReference databaseReference=new Connection().getDbUser();
                                    databaseReference.child("none").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            User user1=dataSnapshot.getValue(User.class);
                                            if(user1==null){
                                                Toast.makeText(LoginActivity.this,"No such user exist",
                                                        Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                return;
                                            }
                                            Toast.makeText(LoginActivity.this,"Welcome "+ user1.getName(),
                                                    Toast.LENGTH_SHORT).show();
                                            storage.setContact(user1.getContact());
                                            storage.setEmail(user.getEmail());
                                            storage.setId(user.getUid());
                                            storage.setName(user1.getName());
                                            storage.setProfile(user1.getProfile());
                                            storage.setUserType(user1.getUserType());
                                            Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                    //    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                             //       updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });
    }

    public void signup(View view) {
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}
