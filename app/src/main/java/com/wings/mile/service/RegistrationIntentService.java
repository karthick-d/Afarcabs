package com.wings.mile.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;



import java.util.concurrent.TimeUnit;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    String FCM_token = null;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
//            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                @Override
//                public void onSuccess(InstanceIdResult instanceIdResult) {
//                    FCM_token = instanceIdResult.getToken();
//                    SharedPreference.updatePref("FCMtoken", FCM_token);
//                    Log.d(TAG, "FCM Registration Token: " + FCM_token);
//                }
           // });
            TimeUnit.SECONDS.sleep(1);

        } catch (InterruptedException ie) {
            Log.e("InterruptedException: ", ""+ie.getMessage());
            Thread.currentThread().interrupt();
        }  catch (Exception e) {
            Log.e("Error",e.toString());
            // If an exception happens while fetching the new token or updating registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

    }
}


