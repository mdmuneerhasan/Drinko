package com.drink.drinko;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.Holder> {
    ArrayList<User> userArrayList;
    Context context;
    public ServiceAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.item_service,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final User user=userArrayList.get(i);
        holder.tvTag.setText(user.getName());
        holder.imageView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        Picasso.get().load(user.getProfile()).resize(250,200).centerCrop()
                .error(R.drawable.ic_invert_colors_black_24dp).into(holder.imageView);
//        Toast.makeText(context,user.getProfile(),Toast.LENGTH_SHORT).show();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ServiceActivity.class);
                intent.putExtra("userType",user.getUserType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
    class  Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvTag;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            tvTag=itemView.findViewById(R.id.tvTag);
        }
    }
}
