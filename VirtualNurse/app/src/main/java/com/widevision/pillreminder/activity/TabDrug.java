package com.widevision.pillreminder.activity;

import android.content.Intent;
import android.os.Bundle;


public class TabDrug extends TabGroupActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startChildActivity("drug", new Intent(this,
				DrugsActivity.class));
	}
}
