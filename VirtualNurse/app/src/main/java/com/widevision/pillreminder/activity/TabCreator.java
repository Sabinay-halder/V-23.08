package com.widevision.pillreminder.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.widevision.pillreminder.R;

/**
 * Created by mercury-one on 10/8/15.
 */
public class TabCreator extends TabActivity {

    /**
     * Called when the activity is first created.
     */
    public static TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set TabChangeListener called when tab changed

        tabHost = getTabHost();
        tabHost.getTabWidget().setStripEnabled(false);
        Intent intent;

      setTabs();




    }

   private void setTabs() {

           addTab("", R.drawable.drug, TabDrug.class);
          //  addTab("", R.drawable.reminder, TabReminder.class);
            addTab("", R.drawable.history, TabHistory.class);
            addTab("", R.drawable.setting, TabSetting.class);
            addTab("", R.drawable.subscribe, TabSubscription.class);

    }

    private void addTab(String name, int drawable, Class<?> c) {

        TabHost.TabSpec spec = tabHost.newTabSpec(name);
        spec.setIndicator(getTabIndicator(tabHost.getContext(), drawable)); // new function to inject our own tab layout
        spec.setContent(new Intent(this, c));
        tabHost.setTag(name);
        tabHost.addTab(spec);
    }



   private void setNewTab( TabHost tabHost, String tag, int icon, Intent intent) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), icon)); // new function to inject our own tab layout
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);

        return view;
    }


}
