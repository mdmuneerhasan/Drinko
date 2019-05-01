package com.drink.drinko;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
        holder.tvNumber.setText(user.getContact());
        holder.tvMail.setText(user.getEmail());
        holder.tvAddress.setText(user.getLocation());
        holder.imageView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        Picasso.get().load(user.getProfile()).error(R.drawable.ic_invert_colors_black_24dp).into(holder.imageView);
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(user.getContact());
            }
        });
//        Toast.makeText(context,user.getProfile(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class  Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvName,tvMail,tvNumber,tvAddress;
        ImageButton btnCall;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvAddress=itemView.findViewById(R.id.tvAddress);
            imageView=itemView.findViewById(R.id.imageView);
            tvName=itemView.findViewById(R.id.tvName);
            tvMail=itemView.findViewById(R.id.tvMail);
            tvNumber=itemView.findViewById(R.id.tvNumber);
            btnCall=itemView.findViewById(R.id.btnCall);
        }
    }
}
