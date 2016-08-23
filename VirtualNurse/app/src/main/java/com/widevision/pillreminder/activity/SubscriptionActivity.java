package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.Extension;
import com.widevision.pillreminder.util.IabHelper;
import com.widevision.pillreminder.util.IabResult;
import com.widevision.pillreminder.util.Inventory;
import com.widevision.pillreminder.util.PreferenceConnector;
import com.widevision.pillreminder.util.Purchase;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 8/1/16.
 */
public class SubscriptionActivity extends Activity {
    public static String items[] = {"com.widevision.pillreminder.monthly", "com.widevision.pillreminder.sixmonth", "com.widevision.pillreminder.annual"};

    @Bind(R.id.check_one_month)RadioButton check_one;
    @Bind(R.id.check_free)RadioButton check_free;
    @Bind(R.id.check_year)RadioButton check_year;
    @Bind(R.id.check_six_month)RadioButton check_six;
    @Bind(R.id.subscribeNowBtn)ImageView subscribeNowBtn;
    @Bind(R.id.layout_free)LinearLayout mLayoutFree;
    private ProgressDialog progressDialog;
    private IInAppBillingService mService;
    private Bundle skuDetails;
    Boolean onemonth,sixmonth,yearly;
    String freeuser="",subscribedUser,check_two_review="";
    private int a=100;
    private Extension ext;
    // The helper object
    private IabHelper mHelper;
    // request code for the purchase flow
    static final int RC_REQUEST = 10001;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subscription_layout);
        ButterKnife.bind(this);
        ext = Extension.getInstance();
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        startService(serviceIntent);
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        progressDialog = new ProgressDialog(getParent());
        check_two_review =PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.Free_Two_Month, "");
        freeuser=PreferenceConnector.readString(getApplicationContext(),PreferenceConnector.Free_Subscription, "");
        subscribedUser=PreferenceConnector.readString(getApplicationContext(),PreferenceConnector.subscription, "");
        if(freeuser.equals("yes")){
            mLayoutFree.setVisibility(View.GONE);
        }
        else if(subscribedUser.equals("yes")){
            mLayoutFree.setVisibility(View.GONE);
        }
        else if((!check_two_review.equals(""))||(check_two_review.equals(""))){
            mLayoutFree.setVisibility(View.GONE);
        }
        String base64EncodedPublicKey = getResources().getString(R.string.Base64EncodedString);
        check_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    check_six.setChecked(false);
                    check_year.setChecked(false);
                    check_free.setChecked(false);

                    a = 0;
                }
            }
        });
        check_six.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    a = 1;
                    check_one.setChecked(false);
                    check_year.setChecked(false);
                    check_free.setChecked(false);
                }
            }
        });


        check_year.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    a = 2;
                    check_six.setChecked(false);
                    check_one.setChecked(false);
                    check_free.setChecked(false);
                }
            }
        });


        check_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    a = 10;
                    check_six.setChecked(false);
                    check_year.setChecked(false);
                    check_one.setChecked(false);
                }
            }
        });
        if (ext.executeStrategy(SubscriptionActivity.this, "", "internet")) {

            mHelper = new IabHelper(this, base64EncodedPublicKey);
             mHelper.enableDebugLogging(true);
            setWaitScreen(true);
            Log.d("", "Starting setup.");
            try {
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {


                        if (!result.isSuccess()) {
                            complain("Problem setting up in-app billing: " + result);
                            return;
                        }

                        if (mHelper == null) {
                            setWaitScreen(false);
                            return;
                        }

                        Log.d("", "Setup successful. Querying inventory.");
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                        getItemsForSubscription();

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            subscribeNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((a == 0) || (a == 1) || (a == 2)) {

                        try {
                            mHelper.flagEndAsync();
                        } catch (Exception e) {

                        }
                        onBuySongButtonClicked(items[a]);
                    } else if (a == 10) {
                        TabCreator.tabHost.setCurrentTab(1);
                        PreferenceConnector.writeString(getParent(), PreferenceConnector.Free_Subscription, "yes");

                    }
                    else if (a == 100) {
                        Constants.alert(getParent(), "Please select the subscription period. ");
                    }

                }
            });
        }
        else
        {
            Constants.alert(getParent(), "Please connect to Internet. ");
        }

    }

    private void getItemsForSubscription() {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(items[0]);
        skuList.add(items[1]);
        skuList.add(items[2]);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            skuDetails = mService.getSkuDetails(3,
                    getPackageName(), "subs", querySkus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // User clicked the "Buy song" button
    public void onBuySongButtonClicked(String id) {

        String payload = "";
        setWaitScreen(true);
        mHelper.launchSubscriptionPurchaseFlow(getParent(), id, RC_REQUEST, mPurchaseFinishedListener, payload);

    }
    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        if (progressDialog != null) {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            if (set) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        mHelper.flagEndAsync();
                    } catch (Exception e) {

                    }
                }
            });
        }
    }


    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    // Callback for when a purchase is finished

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                Log.d("", "Purchase null");
                check_free.setChecked(false);
                check_one.setChecked(false);
                check_six.setChecked(false);
                check_year.setChecked(false);
                setWaitScreen(false);
                return;
            }

            if (result == null || result.isFailure()) {
                Log.d("", "Purchase Failure");
                check_free.setChecked(false);
                check_one.setChecked(false);
                check_six.setChecked(false);
                check_year.setChecked(false);
                setWaitScreen(false);
                return;
            }
            Log.e("For test: ", purchase.getOriginalJson());
            Constants.alert(getParent(),""+purchase.getSku());
            Toast.makeText(getParent(),""+purchase.getSku(),Toast.LENGTH_LONG).show();


            Log.d("", "Purchase successful."+purchase.getSku());

            PreferenceConnector.writeString(getParent(), PreferenceConnector.Free_Subscription, "no");
            PreferenceConnector.writeString(getParent(),PreferenceConnector.subscription, "yes");
            PreferenceConnector.writeString(getParent(),PreferenceConnector.todayDate, Constants.getCurrentDate());

            setWaitScreen(false);
            TabCreator.tabHost.setCurrentTab(1);

        }
    };


    @Override
    protected void onResume() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if(freeuser.equals("yes")){
            mLayoutFree.setVisibility(View.GONE);
        }
        else if(subscribedUser.equals("yes")){
            mLayoutFree.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...

            super.onActivityResult(requestCode, resultCode, data);

            TabCreator.tabHost.setCurrentTab(1);
        }

    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
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

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("", "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d("", "Query inventory was successful.");

            // Do we have the premium upgrade?
            Purchase forOneMonth = inventory.getPurchase(items[0]);
            onemonth = (onemonth != null && verifyDeveloperPayload(forOneMonth));
            Log.d("", "User is " + (onemonth ? "PREMIUM" : "NOT PREMIUM"));

            // Do we have the infinite gas plan?
            Purchase forSixMonth = inventory.getPurchase(items[1]);
            sixmonth = (forSixMonth != null &&
                    verifyDeveloperPayload(forSixMonth));
            Log.d("", "User " + (sixmonth ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            // if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase forYear = inventory.getPurchase(items[2]);
            yearly = (forYear != null &&
                    verifyDeveloperPayload(forYear));
            Log.d("", "User " + (yearly ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if(inventory.getPurchase(items[0])!=null){
                check_one.setChecked(true);
            }
            if(inventory.getPurchase(items[0])!=null){
                check_one.setChecked(true);
            }
            if(inventory.getPurchase(items[0])!=null){
                check_one.setChecked(true);
            }
        }
    };


    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
}
