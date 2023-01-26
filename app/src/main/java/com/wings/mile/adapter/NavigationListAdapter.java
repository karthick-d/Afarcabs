package com.wings.mile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.wings.mile.R;
import com.wings.mile.model.Datamodel;



public class NavigationListAdapter extends RecyclerView.Adapter<NavigationListAdapter.NavigationViewHolder> {
    private final LayoutInflater mInflater;
    final Context context;
    Datamodel[] datamodel = null;
    private ItemClickListener mClickListener;


    public NavigationListAdapter(Context ctx, Datamodel[] drawerItem) {
        this.mInflater = LayoutInflater.from(ctx);
        context = ctx;
        datamodel = drawerItem;

    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_nav_drawer, parent, false);
        return new NavigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        Datamodel model = datamodel[position];
        holder.icon.setImageResource(model.getIcon());
        holder.name.setText(model.getName());
//        if (position == 1) {
//            holder.expand.setVisibility(View.VISIBLE);
//
//            holder.expand.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    if(holder.rowLayout.getVisibility()==View.VISIBLE){
//                        holder.expand.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
//                        holder.rowLayout.setVisibility(View.GONE);
//                    }else{
//                        holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//                        holder.rowLayout.setVisibility(View.VISIBLE);
//
//                    }
//
//
//                }
//            });

        //}

        if(model.getChecked()) {
            holder.icon.setColorFilter(context.getColor(R.color.proceed_btncolor));
            holder.name.setTextColor(context.getColor(R.color.proceed_btncolor));
        }else{
            holder.icon.setColorFilter(context.getColor(R.color.menu_text));
            holder.name.setTextColor(context.getColor(R.color.menu_text));
        }
    }


    @Override
    public int getItemCount() {
        return datamodel.length;
    }


    public Datamodel getItem(int value) {
        return datamodel[value];
    }

    public class NavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView icon,expand;
        final TextView name,licenseexpiry,insuranceexpiry,useraccounts;
        final ConstraintLayout rowConstraintLayout;
        final LinearLayoutCompat rowLayout;

        public NavigationViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iconImage);
            name = itemView.findViewById(R.id.textName);
            expand = itemView.findViewById(R.id.expand_view);
            rowConstraintLayout=itemView.findViewById(R.id.row);
            rowLayout=itemView.findViewById(R.id.details_lay);
            licenseexpiry=itemView.findViewById(R.id.licenseexpiry);
            insuranceexpiry=itemView.findViewById(R.id.insurance_expiry);
            useraccounts=itemView.findViewById(R.id.useraccounts);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }




}
