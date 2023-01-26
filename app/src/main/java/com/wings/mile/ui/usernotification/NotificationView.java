package com.wings.mile.ui.usernotification;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wings.mile.R;
import com.wings.mile.Utils.BaseActivity;
import com.wings.mile.model.Getpost;

import java.util.ArrayList;
import java.util.List;


public class NotificationView extends BaseActivity implements View.OnClickListener {


    List<Getpost> getdata;
    View about;
    ImageView back, deleteall;
    TextView toolbar_title;
    RecyclerView nvrecylcer;

   NotificationViewAdapter nvadapter;
    private TextView nodata;




    int visibility;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveData();
        setContentView(R.layout.notificationview_layout);
        about = findViewById(R.id.myoffice_toolbar);
        back = about.findViewById(R.id.button_back);
        toolbar_title = about.findViewById(R.id.toolbar_title);
        deleteall = about.findViewById(R.id.deleteall);
        nvrecylcer = findViewById(R.id.notification_listrecycler);
        nodata = findViewById(R.id.nodata);
        back.setOnClickListener(this);
        deleteall.setOnClickListener(this);
        //getNotificationTrigger();
        deleteall.setVisibility(visibility);

    }

//    private void getNotificationTrigger() {
//
//        if (Utility.isConnected(getApplicationContext())) {
//
//            nvrecylcer.setVisibility(View.VISIBLE);
//            APIInterface apiInterface_update = ApiUtils.getAPIService();
//            Call<ArrayList<Getpost>> call = apiInterface_update.GetNotificationTrigger();
//            call.enqueue(new Callback<ArrayList<Getpost>>() {
//                @Override
//                public void onResponse(Call<ArrayList<Getpost>> call, Response<ArrayList<Getpost>> response) {
//
//                    try {
//                        if (response.body().size() > 0) {
//                            updateDatabase(response.body());
//                            nodata.setVisibility(View.GONE);
//                        } else {
//                            nodata.setVisibility(View.VISIBLE);
//                            nodata.setText(R.string.notify);
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("Error",e.toString());
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<ArrayList<Getpost>> call, Throwable throwable) {
//                    // This method does nothing
//                }
//            });
//        } else {
//            nvrecylcer.setVisibility(View.VISIBLE);
//
//        }
//    }

//private void updateDatabase(ArrayList<Getpost> body){
//    for (int i=0;i<body.size()-1;i++) {
//        if(appDataBase.notificationDao().checkDataAvailability(body.get(i).getId()).size()>0){
//            // This condition does nothing
//        }else{
//            appDataBase.notificationDao().insertAll(body.get(i));
//        }
//
//    }
//
//}

    public void retrieveData(){
//        appDataBase.notificationDao().getAllRows().observe(this, new Observer<List<Getpost>>() {
//            @Override
//            public void onChanged(@Nullable List<Getpost> data) {
//                refresh();
//                updateAdpter((ArrayList<Getpost>) data);
//            }
//        });
    }


    private void updateAdpter(ArrayList<Getpost> body) {


        getdata = new ArrayList<Getpost>();
        getdata.addAll(body);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        nvrecylcer.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(nvrecylcer.getContext(),
                layoutManager.getOrientation());
        nvrecylcer.addItemDecoration(dividerItemDecoration);
//        nvadapter = new NotificationViewAdapter(getApplicationContext(), getdata,visibility,
//                loginUserResponse().getLgDirLst().get(0).getPanchayatId(),
//                loginUserResponse().getLgDirLst().get(0).getBlockId(),
//                loginUserResponse().getLgDirLst().get(0).getDistrictId()
//        );

        nvrecylcer.setAdapter(nvadapter);
        nvadapter.notifyDataSetChanged();
        nvadapter.setClickListener((view, position, id) -> {
            if (view.getId() == R.id.delete) {
               Getpost getpost= nvadapter.getItem(position);

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back) {
            onBackPressed();
        }

    }


    public void refresh() {
        if (nvadapter != null) {

            nvadapter.notifyDataSetChanged();
        }

    }


}
