package com.drink.drinko;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {
    ImageView imageView;
    TextView tvName,tvNumber,tvStarRating;
    RatingBar rtStar;
    Button btnSubmit;
    EditText edtComment;
    RecyclerView recyclerView;
    String comment;
    float rate=4.5f;
    String name;
    Intent intent;
    Storage storage;
    String id;
    User user;
    ReviewAdapter reviewAdapter;
    ArrayList<Review> list;
    float star=5;
    int numberOfRating=15;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        imageView=findViewById(R.id.imageView);
        tvName=findViewById(R.id.tvName);
        tvNumber=findViewById(R.id.tvNumber);
        rtStar=findViewById(R.id.rating);
        tvStarRating=findViewById(R.id.tvStarRating);
        edtComment=findViewById(R.id.edtComment);
        btnSubmit=findViewById(R.id.btnSubmit);
        recyclerView=findViewById(R.id.recycler);
        storage=new Storage(this);
        id=getIntent().getStringExtra("uid");
        list=new ArrayList<>();
        reviewAdapter=new ReviewAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewAdapter);

        rtStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate=rating;
            }
        });
        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtComment.getText().toString().trim().length()>0){
                    rtStar.setVisibility(View.VISIBLE);
                    tvStarRating.setVisibility(View.VISIBLE);
                }
            }
        });
        new Connection().getDbUser().child("none").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1=dataSnapshot.getValue(User.class);
                if(user1!=null){
                    user=user1;
                    Picasso.get().load(user1.getProfile()).error(R.drawable.ic_invert_colors_black_24dp).into(imageView);
                     tvName.setText(user1.getName());
                    tvNumber.setText(user1.getContact());
                    RatingBar ratingBar=findViewById(R.id.rating2);
                    TextView tvRating=findViewById(R.id.tvRatingNumber);

                    if(user.getNumberOfRating()!=null){
                        int numberOfRating= Integer.parseInt(user1.getNumberOfRating());
                        Float rt= Float.parseFloat(user1.getRating());
                        ratingBar.setRating(rt/numberOfRating);
                        tvRating.setText(String.format("%.1f",(rt/numberOfRating))+"/"+String.valueOf(numberOfRating));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review;
                comment=edtComment.getText().toString();
                name=storage.getName();
                if(comment.trim().length()>0){
                    if(name!=null){
                        review = new Review(String.valueOf(rate),comment,name);
                        new Connection().getDbUser().child("none").child(id).child("comments").child("name").setValue(review);
                    }else{
                        review=new Review(String.valueOf(rate),comment,"a drinko user");
                        new Connection().getDbUser().child("none").child(id).child("comments").push().setValue(review);
                    }
                    btnSubmit.setText("Review Submitted");
                    edtComment.setVisibility(View.GONE);
                    rtStar.setVisibility(View.GONE);
                    btnSubmit.setClickable(false);
                    DatabaseReference ref=new Connection().getDbUser();
                    star+=rate;
                    numberOfRating++;
                    ref.child("none").child(id).child("numberOfRating").setValue(String.valueOf(numberOfRating));
                    ref.child("none").child(id).child("rating").setValue(String.valueOf(star));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Connection().getDbUser().child("none").child(id).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                star= Float.parseFloat(getString(R.string.Rating));
                numberOfRating= Integer.parseInt(getString(R.string.NumberOfRating));
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Review review=dataSnapshot1.getValue(Review.class);
                    list.add(review);
                    star+=Float.parseFloat(review.getRating());
                    numberOfRating++;
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }    public void call(View view) {
            try{
                Double.parseDouble(user.getContact());
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getContact()));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},234);
                    }
                    else
                    {
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

