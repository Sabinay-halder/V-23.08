package com.widevision.pillreminder.activity;

import android.content.Intent;
import android.os.Bundle;


public class TabHistory extends TabGroupActivity {

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startChildActivity("history", new Intent(this,
				HistoryActivity.class));
	}
}
