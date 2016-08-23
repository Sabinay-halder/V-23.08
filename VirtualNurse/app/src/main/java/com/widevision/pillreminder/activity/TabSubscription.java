package com.widevision.pillreminder.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mercury-one on 14/1/16.
 */
public class TabSubscription extends TabGroupActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startChildActivity("subscription", new Intent(this,
                SubscriptionActivity.class));
    }
}
