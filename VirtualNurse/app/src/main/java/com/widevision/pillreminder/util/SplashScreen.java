package com.widevision.pillreminder.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.activity.PasswordActivity;
import com.widevision.pillreminder.activity.TabCreator;

/**
 * Created by mercury-one on 9/10/15.
 */
public class SplashScreen extends Activity {

    private final static int MSG_CONTINUE = 1234;
    private final static long DELAY = 2000;
    public static int visit;
    private IabHelper mHelper;
    String passcode,dateformateselect="",snoozeEnable="";
    private Extension ext;
    private IInAppBillingService mService;
    public static String items[] = {"com.widevision.pillreminder.monthly", "com.widevision.pillreminder.sixmonth", "com.widevision.pillreminder.annual"};
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        startService(serviceIntent);
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        String base64EncodedPublicKey = getResources().getString(R.string.Base64EncodedString);
        ext = Extension.getInstance();
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        passcode = PreferenceConnector.readString(getApplicationContext(), "passcode", "");
        dateformateselect = PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        snoozeEnable=  PreferenceConnector.readString(getApplicationContext(),"snoozeEnable","");
        if(snoozeEnable.equals("")){
            PreferenceConnector.writeString(getApplicationContext(), "snooze", "5");
        }

        if(dateformateselect.equals("")){
         PreferenceConnector.writeString(getApplicationContext(), "dateselectionformate", "MM-DD-YYYY");
        }

if (!PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.todayDate, "").equals(Constants.getCurrentDate())) {

                 try {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                         public void onIabSetupFinished(IabResult result) {
                             if (!result.isSuccess()) {
                                return;
                             }
                            if (mHelper == null) {
                              return;
                             }
                           Log.d("", "Setup successful. Querying inventory.");
                           mHelper.queryInventoryAsync(mGotInventoryListener);


                         }
                     });
                 } catch (Exception e) {
                    e.printStackTrace();
                 }
             } else {

                 if (passcode.equals("yes")) {
                     startActivity(new Intent(this, PasswordActivity.class));
                     finish();
                 } else {

                     startActivity(new Intent(this, TabCreator.class));
                     finish();
                 }

           }
    }




    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("", "Query inventory finished.");

          if (mHelper == null) return;
           if (result.isFailure()) {

                Toast.makeText(SplashScreen.this, "Failed to query inventory:", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("", "Query inventory was successful.");
              PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.todayDate, Constants.getCurrentDate());
            if((inventory.getPurchase(items[0])==null)&&(inventory.getPurchase(items[1])==null)&&((inventory.getPurchase(items[2])==null))){
               PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.Free_Subscription, "yes");
                startActivity(new Intent(SplashScreen.this, TabCreator.class));
                finish();
            }
            else{
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.subscription, "yes");
                startActivity(new Intent(SplashScreen.this, TabCreator.class));
                finish();
            }
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
            try {
            Log.d("", "Destroying helper.");
            if (mHelper != null) {
                mHelper.dispose();
                mHelper = null;
            }

            if (mService != null) {
                unbindService(mServiceConn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _continue() {

    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CONTINUE:
                    _continue();
                    break;
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);

        }

    }

}
