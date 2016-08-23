package com.widevision.pillreminder.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by mercury-one on 1/9/15.
 */
public class RingtoneActivity extends ListActivity {

    ListAdapter listadapter;
    Cursor mCursor2;
    RingtoneManager mRingtoneManager2;
    Uri localuri2;
    int position;


    @Override
    protected void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        mCursor2.close();
        this.closeOptionsMenu();
        this.finish();
    }

    @Override
    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
        mCursor2.close();
        this.closeOptionsMenu();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mRingtoneManager2 = new RingtoneManager(this); //adds ringtonemanager
        mRingtoneManager2.setType(RingtoneManager.TYPE_RINGTONE); //sets the type to ringtones
        mRingtoneManager2.setIncludeDrm(true); //get list of ringtones to include DRM

        mCursor2 = mRingtoneManager2.getCursor(); //appends my cursor to the ringtonemanager

        startManagingCursor(mCursor2); //starts the cursor query

//prints output for diagnostics
        String test = mCursor2.getString(mCursor2.getColumnIndexOrThrow(RingtoneManager.EXTRA_RINGTONE_TITLE));


        String[] from = {mCursor2.getColumnName(RingtoneManager.TITLE_COLUMN_INDEX)}; // get the list items for the listadapter could be TITLE or URI

        int[] to = {android.R.id.text1}; //sets the items from above string to listview

//new listadapter, created to use android checked template
        SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_checked, mCursor2, from, to);
        setListAdapter(listadapter);

//prints output for diagnostics
        String test2 = listadapter.toString();


//adds listview so I can get data from it
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
// TODO Auto-generated method stub
        super.onListItemClick(lv, v, position, id);

//just a system test to make sure the items check are being registered
        int i = lv.getCheckedItemCount();
        System.out.println(i);

//gets the ids of the selected items
        long[] ids = lv.getCheckItemIds();
        String string_ids = ids.toString();

//creates a uri for the items selected
        Uri localuri = ContentUris.withAppendedId(MediaStore.Audio.Media.getContentUriForPath(string_ids), id);
        String diag = localuri.toString();
        System.out.println(diag);

//shows a toast to make sure the uri selected has the correct path
        Toast toasted = Toast.makeText(this, diag, Toast.LENGTH_SHORT);
        toasted.show();

    }


}
