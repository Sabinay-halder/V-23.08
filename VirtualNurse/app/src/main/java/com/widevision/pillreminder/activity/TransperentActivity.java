package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.widevision.pillreminder.util.PreferenceConnector;

/**
 * Created by mercury-one on 21/10/15.
 */
public class TransperentActivity extends Activity {
    Uri uri;
    Uri a;
    String sound = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        sound = PreferenceConnector.readString(TransperentActivity.this, PreferenceConnector.NOTIFICATION_SOUND, "");
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        if ( sound != null ) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse( sound ));
        } else {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,true);
        }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

                uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    a = uri;
                    PreferenceConnector.writeString(TransperentActivity.this, PreferenceConnector.NOTIFICATION_SOUND_TEXT, RingtoneManager.getRingtone(this, uri).getTitle(this));
                    PreferenceConnector.writeString(TransperentActivity.this, PreferenceConnector.NOTIFICATION_SOUND, uri.toString());
                }
            }
        }
        TransperentActivity.this.finish();
    }
}