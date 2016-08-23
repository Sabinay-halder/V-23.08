package com.widevision.pillreminder.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.widevision.pillreminder.database.DrugsListTable;
import com.widevision.pillreminder.database.HistoryListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReciever extends BroadcastReceiver {
    Bundle extras;
    String actiontaken,drugtaken,dosagetaken,snooze,current,takendate, takentime,skipdate, skiptime,quantitytaken,drugId,actionskip,drugskip,dosageskip,actionsnooze,drugsnooze,dosagesnooze,quantitysnooze,quantityskip;
    //for refill
    String refillaction,lateraction,refilldrugName,laterdrugName;
    long refilldrugid,laterdrugid;
    int refillnotiId,laternotiId;
    int notificationIdtaken,notificationIdskip,notificationIdsnooze;
    long drugidtaken,drugidskip,drugidsnooze;
    NotificationTable item;

       @Override
    public void onReceive(Context context, Intent intent) {
         if (("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))||("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(intent.getAction()))) {
               Intent pushIntent = new Intent(context, AlarmService.class);
               context.startService(pushIntent);
           Log.v("TEST", "Service loaded at start");
       }
        snooze=  PreferenceConnector.readString(context, "snooze", "");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
        String time = tdf.format(c.getTime());
        String formattedDate = df.format(c.getTime());
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        extras = intent.getExtras();

        if (extras != null) {
            actiontaken = extras.getString("taken");
            drugtaken = extras.getString("drugtaken");
            dosagetaken = extras.getString("dosagetaken");
            drugidtaken = extras.getLong("drugIdtaken");
            notificationIdtaken = extras.getInt("notitaken");
            quantitytaken = extras.getString("quantitytaken");

            actionskip = extras.getString("skip");
            drugskip = extras.getString("drugskip");
            dosageskip = extras.getString("dosageskip");
            drugidskip = extras.getLong("drugIdskip");
            notificationIdskip = extras.getInt("notiskip");
            quantityskip = extras.getString("quantityskip");

            actionsnooze = extras.getString("snooze");
            drugsnooze = extras.getString("drugsnooze");
            dosagesnooze = extras.getString("dosagesnooze");
            drugidsnooze = extras.getLong("drugIdsnooze");
            notificationIdsnooze = extras.getInt("notisnooze");
            quantitysnooze = extras.getString("quantitysnooze");
            ///for refill
            refillaction=extras.getString("refill");
            lateraction=extras.getString("later");
            refilldrugName=extras.getString("refilldrugName");
            laterdrugName=extras.getString("laterdrugName");
            refilldrugid = extras.getLong("refillId");
            laterdrugid = extras.getLong("laterId");
            refillnotiId = extras.getInt("refillnoti");
            laternotiId = extras.getInt("laternoti");
        }
            if ((actiontaken!=null)&&(actiontaken.equals("taken"))) {
            manager.cancel(notificationIdtaken);
                String name,dosage,drugquantity;
                String status = "taken";
                    name = drugtaken;
                    dosage = dosagetaken;
                takendate = formattedDate;
                takentime = time;
                drugquantity=quantitytaken;
                drugId= String.valueOf(drugidtaken);
                try {
                    HistoryListTable historyList;
                    historyList = new HistoryListTable(
                            drugId.trim(),
                            name.trim(),
                            dosage.trim(),
                            takendate.trim(),
                            status.trim(),
                            takentime.trim(),
                            drugquantity.trim()
                    );

                    historyList.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }


    try {

     new Delete().from(NotificationTable.class).where("_notiId1= ?", notificationIdtaken).execute();

    } catch (Exception e) {
        e.printStackTrace();
    }

            } else if ((actionsnooze!=null)&&(actionsnooze.equals("snooze"))) {
                     manager.cancel(notificationIdsnooze);
                     current=Constants.getCurrentTime();
                if (!Constants.isMyServiceRunning(AlarmService.class, context)) {
                    Intent intent1 = new Intent(context, AlarmService.class);
                    context.startService(intent1);
                }

                try {
                   item =new Select().from(NotificationTable.class).where("_notiId1= ?",notificationIdsnooze).executeSingle();
                    item.currentTime=current;
                    item.snooze="yes";
                    item.save();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if ((actionskip!=null)&&(actionskip.equals("skip"))) {
                manager.cancel(notificationIdskip);
                String sname = "",sdosage = "",sdrugquantity="",sdrugId="",status = "skipped";
                sname = drugskip;
                sdosage = dosageskip;
                skipdate = formattedDate;
                skiptime = time;
                sdrugId= String.valueOf(drugidskip);
                sdrugquantity=quantityskip;
                try {
                    HistoryListTable historyList;
                    historyList = new HistoryListTable(
                            sdrugId.trim(),
                            sname.trim(),
                            sdosage.trim(),
                            skipdate.trim(),
                            status.trim(),
                            skiptime.trim(),
                            sdrugquantity.trim()
                    );
                    historyList.save();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //for deletion from reminder table
                try {
                    new Delete().from(NotificationTable.class).where("_notiId1= ?", notificationIdskip).execute();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if ((refillaction!=null)&&(refillaction.equals("refill"))) {
                manager.cancel(refillnotiId);

                try {
                    DrugsListTable val=new Select().from(DrugsListTable.class).where("Id= ?",refilldrugid).executeSingle();
                    val.snooze="yes";
                    val.save();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if ((lateraction!=null)&&(lateraction.equals("later"))) {
                manager.cancel(laternotiId);


            }

       }
}
