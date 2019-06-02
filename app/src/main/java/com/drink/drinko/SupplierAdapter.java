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

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.Holder> {
    ArrayList<User> userArrayList;
    Context context;
    Click click;
    public SupplierAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        click=(Click) context;
        View view= LayoutInflater.from(context).inflate(R.layout.item_supplier,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final User user=userArrayList.get(i);
        holder.tvName.setText(user.getName());
        holder.tvNumber.setText(user.getUserType());
        holder.tvMail.setText(timing(user.getStartTime(),user.getEndTime()));
        holder.tvAddress.setText(user.getLocation());
        holder.imageView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        Picasso.get().load(user.getProfile()).error(R.drawable.ic_invert_colors_black_24dp).into(holder.imageView);
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(user);
            }
        });


        if(user.getNumberOfRating()!=null){
            int numberOfRating= Integer.parseInt(user.getNumberOfRating());
            Float rt= Float.parseFloat(user.getRating());
            holder.rtBar.setRating(rt/numberOfRating);
            holder.tvRatingNumber.setText(String.format("%.1f",(rt/numberOfRating))+"/"+String.valueOf(numberOfRating));
        }

        if(user.getLocation().equals("recently connected")){
            holder.btnDelete.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)holder.btnCall.getLayoutParams();
            relativeParams.topMargin=0;
            holder.btnCall.setLayoutParams(relativeParams);

        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.delete(user.getContact());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,PreviewActivity.class).putExtra("uid",user.getUid()));
            }
        });
//        Toast.makeText(context,user.getProfile(),Toast.LENGTH_SHORT).show();

    }

    private String timing(String startTime, String endTime) {
   //     startTime="1247";endTime="1148";
        if(startTime!=null||endTime!=null){
            try{
                int hs= Integer.parseInt(startTime.substring(0,2));
                String mins=(startTime.substring(2,4));
                int he=Integer.parseInt(endTime.substring(0,2));
                String mine=(endTime.substring(2,4));
                String p1="am",p2="am";
                if(hs/12>0){
                    p1="pm";
                }if(he/12>0){
                    p2="pm";
                }
                if(hs>12){
                    hs=hs-12;
                }if(he>12){
                    he=he-12;
                }
                return hs+":"+ mins+" "+p1+" to "+he+":"+mine +" "+p2;
            }catch (Exception e){

            }
        }
    return "9:00 am to 11:30 pm";
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class  Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvName,tvMail,tvNumber,tvAddress,tvRatingNumber;
        RatingBar rtBar;
        ImageButton btnCall,btnDelete;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvRatingNumber=itemView.findViewById(R.id.tvRatingNumber);
            rtBar=itemView.findViewById(R.id.rating);
            tvAddress=itemView.findViewById(R.id.tvAddress);
            imageView=itemView.findViewById(R.id.imageView);
            tvName=itemView.findViewById(R.id.tvName);
            tvMail=itemView.findViewById(R.id.tvMail);
            tvNumber=itemView.findViewById(R.id.tvNumber);
            btnCall=itemView.findViewById(R.id.btnCall);
            btnDelete=itemView.findViewById(R.id.btnDelete);
        }
    }
}
