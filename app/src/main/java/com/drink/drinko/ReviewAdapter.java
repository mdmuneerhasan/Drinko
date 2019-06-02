package com.drink.drinko;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder> {
    ArrayList<Review> userArrayList;
    Context context;
    public ReviewAdapter(ArrayList<Review> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.item_review,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Review user=userArrayList.get(i);
        holder.tvName.setText(user.getName());
        holder.tvComment.setText(user.getComment());
        holder.rtStars.setRating(Float.parseFloat(user.getRating()));
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class  Holder extends RecyclerView.ViewHolder{
        TextView tvName,tvComment;
        RatingBar rtStars;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvComment=itemView.findViewById(R.id.tvComment);
            rtStars=itemView.findViewById(R.id.rating);

        }
    }
}
