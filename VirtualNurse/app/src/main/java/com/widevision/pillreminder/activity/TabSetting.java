package com.widevision.pillreminder.activity;

import android.content.Intent;
import android.os.Bundle;


public class TabSetting extends TabGroupActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startChildActivity("setting", new Intent(this,
				SettingActivity.class));
	}
}
