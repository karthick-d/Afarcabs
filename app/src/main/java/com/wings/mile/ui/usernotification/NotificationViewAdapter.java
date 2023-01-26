package com.wings.mile.ui.usernotification;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wings.mile.R;
import com.wings.mile.model.Getpost;

import java.util.List;

;


public class NotificationViewAdapter extends RecyclerView.Adapter<NotificationViewAdapter.MyViewHolder> {
    final List<Getpost> getposts;
    Getpost notificationData;
    private Context context;
    private ItemClickListener mClickListener;
    final int showHideValue;

     int panchayatId;
     int blockId;
     int districtId;
     NotificationViewAdapter(Context context, List<Getpost> getdata,int visiblity,int panchayatId,int blockId,int districtId) {

         this.context = context;
         this.getposts = getdata;
         showHideValue=visiblity;
         this.panchayatId = panchayatId;
         this.blockId = blockId;
         this.districtId = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer_placeholder_layout, viewGroup, false);
        MyViewHolder directoryViewHolder = new MyViewHolder(rootView);
        context = viewGroup.getContext();
        return directoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int adapterPosition) {
        try {
            notificationData = getposts.get(adapterPosition);
            if (notificationData.getMessage().contains("Videos") || notificationData.getMessage().contains("video")) {
                myViewHolder.text.setText(nameSpiltCombine(notificationData.getName()) + " " + context.getString(R.string.double_dash));
            } else if (notificationData.getMessage().contains("Documents") || notificationData.getMessage().contains("document")) {
                myViewHolder.text.setText(nameSpiltCombine(notificationData.getName()) + " " + context.getString(R.string.rideid));
            } else if (notificationData.getMessage().contains("Messages") || notificationData.getMessage().contains("message")) {
                myViewHolder.text.setText(nameSpiltCombine(notificationData.getName()) + " " + context.getString(R.string.menu_about));
            }
            //myViewHolder.date.setText(Utility.Companion.formatDate(notificationData.getDate(),1));
            myViewHolder.delete.setVisibility(showHideValue);

            if (notificationData.getIsread()){
                myViewHolder.baseLayout.setBackgroundColor(context.getColor(R.color.white));
            } else{
                myViewHolder.baseLayout.setBackgroundColor(context.getColor(R.color.light_grey));
            }

        }catch (Exception e){
            Log.e("Error",e.toString());
        }
    }

    private String nameSpiltCombine(String name){
        if (name.isEmpty()) {
            return "";
        } else {
            String[] nameSplit = name.split(" ");
            String LastName;
            if (nameSplit.length>1 && nameSplit[1].length() > 6) {
                LastName = nameSplit[1].substring(0, 1);
                return LastName+" "+ nameSplit[0];
            } else if(nameSplit.length>1){
                LastName = nameSplit[1];
                return nameSplit[0] + " " + LastName;
            }else{
                return name;
            }
        }
    }


    @Override
    public int getItemCount() {
        return getposts.size();
    }



    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position,String id);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView text;
        private TextView date;

        private final ImageView delete;
private final RelativeLayout baseLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textmessage);
            date = itemView.findViewById(R.id.date);
            date = itemView.findViewById(R.id.date);

            delete = itemView.findViewById(R.id.delete);
            baseLayout = itemView.findViewById(R.id.rl_base);
            baseLayout.setOnClickListener(this);

            delete.setOnClickListener(this);
            if (isBlockLevelOfficer()||isDistrictOfficer()||isPanchayatOfficer()) {
                delete.setVisibility(View.GONE);
            }else{
                delete.setVisibility(View.VISIBLE);
            }
        }

        public Boolean isBlockLevelOfficer(){
            return  panchayatId ==0 && blockId !=0 && districtId !=0 ;
        }
        public Boolean isDistrictOfficer(){
            return  panchayatId ==0 && blockId ==0 && districtId !=0 ;
        }
        public Boolean isPanchayatOfficer(){
            return  panchayatId !=0 && blockId !=0 && districtId !=0 ;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {

                mClickListener.onItemClick(view, getAdapterPosition(),notificationData.getId());
            }
        }


    }
    public Getpost getItem(int id) {
        return getposts.get(id);
    }
}
