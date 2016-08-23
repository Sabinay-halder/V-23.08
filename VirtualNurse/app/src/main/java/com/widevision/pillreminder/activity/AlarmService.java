package com.widevision.pillreminder.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.DrugsListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mercury-one on 4/9/15.
 */
public class AlarmService extends Service {
    private static Timer timer = new Timer();
    private static Timer timer2 = new Timer();
    private Context ctx;
    int m,n,o,p,s;
    Random random;
    private NotificationManager mNotificationManager;
    String dateTime="",endDateTime="",repeatDate="";
    String snoozetimer,idnot=" ";
    int alarmrepeat,height,width;
    Long drugiddd;
    Bitmap bitmap1;
    private PowerManager.WakeLock wl;


    public void onCreate() {
        super.onCreate();
        ctx = this;
        startService();
        BitmapFactory.Options dimensions=new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = false;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medecine2, dimensions);
        height = dimensions.outHeight;
        width =  dimensions.outWidth;
        PowerManager mgr = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
        wl = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wl.acquire();

    }
    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
        timer2.scheduleAtFixedRate(new mainTask2(), 0, 18000000);
    }
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        snoozetimer = PreferenceConnector.readString(getApplicationContext(), "snooze", "");

        super.onStart(intent, startId);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("start sticky","start sticky");
        return START_STICKY;
    }
    private class mainTask extends TimerTask {

        public void run()
        {
            getDateAndTime();
          getSnoozeTime();
        }
    }
    private class mainTask2 extends TimerTask {

        public void run()
        {
            getRefilling();
        }
    }



    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    public void saveNotification(String name, String dosage, long drugId, String quantity, String drugForm, String drugImage, String pharmacy, int m){
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
            String time = tdf.format(c.getTime());
            String formattedDate = df.format(c.getTime());
            String notidate, notitime,drugform,drugimage="",notiDrugid,drugquantity,snooze="no",pharmacyname="",notiId,currenttime="";
            notidate = formattedDate.toString();
            notitime = time.toString();
            notiDrugid= String.valueOf(drugId);
            drugform=drugForm;
            drugimage=drugImage;
            drugquantity=quantity;
            pharmacyname=pharmacy;
            notiId= String.valueOf(m);
            NotificationTable notificationList;
            notificationList = new NotificationTable(
                    notiDrugid.toString().trim(),
                    name.toString().trim(),
                    notidate.toString().trim(),
                    notitime.toString().trim(),
                    dosage.toString().trim(),
                    drugform.toString().trim(),drugimage.toString().trim(),
                    drugquantity.toString().trim(),
                    snooze.toString().trim(),pharmacyname.toString(),
                    notiId.toString().trim(),
                    currenttime.toString().trim()
            );
           notificationList.save();

        } catch (Exception e) {
            e.printStackTrace();
        }    }


    public void getDateAndTime() {
        List<DrugsListTable> values = new Select().all().from(DrugsListTable.class).execute();
        DrugsListTable items;
        if (values.size() != 0) {
            for (DrugsListTable item : values) {

                try {
                    alarmrepeat = Integer.parseInt(item.alarmTimeRepeat);
                } catch (NumberFormatException e) {
                }

                if (item.endDate.equals("")) {
                    try {
                        for (int j = 0; j < item.time.length(); j++) {
                            String[] separated = item.time.split(",");
                            String[] seperatedosage = item.dosage.split(",");
                            Log.e("Alarmservice-----", separated[j]);
                            dateTime = item.startDate + " " + separated[j].trim();
                            repeatDate = item.repeatDate + " " + separated[j].trim();

                            String current = Constants.getCurrentTime();
                            DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateTimePattern);
                            DateTime repeatdate = dtf.parseDateTime(repeatDate);
                            DateTime date = dtf.parseDateTime(dateTime);
                            DateTime sysDate = dtf.parseDateTime(current);
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");


                            if (item.doNotify.equals("1")) {
                                //for repeat once
                                if (item.index.equals("1")) {

                                    if (repeatdate.equals(sysDate)) {
                                        random = new Random();
                                        m = random.nextInt(9999 - 1000) + 1000;
                                        genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m,item.pharmacyName);
                                        saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                        if (!item.alarmTimeRepeat.equals("0")) {
                                            int aa = alarmrepeat -1;
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.alarmTimeRepeat = String.valueOf(aa);
                                            items.save();
                                        }
                                        if (item.alarmTimeRepeat.equals("0")) {

                                            DateTime repeatdater = repeatdate.plusDays(Integer.parseInt(item.rx_Number));
                                            String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.repeatDate = sdt;
                                            items.alarmTimeRepeat = items.quantity;
                                            items.save();
                                        }
                                    }

                                }

                                if (item.index.equals("2")) {
                                    if (repeatdate.equals(sysDate)) {
                                        random = new Random();
                                        m = random.nextInt(9999 - 1000) + 1000;
                                        genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                        saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                        if (!item.alarmTimeRepeat.equals("0")) {
                                           int aa= alarmrepeat-1;
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.alarmTimeRepeat= String.valueOf(aa);
                                            items.save();
                                        }
                                        if (item.alarmTimeRepeat.equals("0")) {

                                            DateTime repeatdater = repeatdate.plusDays(1);
                                            String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.repeatDate = sdt;
                                            items.alarmTimeRepeat=items.quantity;
                                            items.save();
                                        }
                                    }

                                }
                                if (item.index.equals("3")) {

                                    if (repeatdate.equals(sysDate)) {
                                        random = new Random();
                                        m = random.nextInt(9999 - 1000) + 1000;
                                        genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                        saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                        if (!item.alarmTimeRepeat.equals("0")) {
                                            int aa= alarmrepeat-1;
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.alarmTimeRepeat= String.valueOf(aa);
                                            items.save();
                                        }
                                        if (item.alarmTimeRepeat.equals("0")) {
                                            DateTime repeatdater = repeatdate.plusDays(7);
                                            String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.repeatDate = sdt;
                                            items.alarmTimeRepeat=items.quantity;
                                            items.save();
                                        }

                                    }

                                }
                                if (item.index.equals("4")) {

                                    if (repeatdate.equals(sysDate)) {
                                        int i = repeatdate.getDayOfWeek();
                                        if (item.alarmTimeRepeat.equals("0")) {
                                            DateTime repeatdater = repeatdate.plusDays(1);
                                            String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                            items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                            items.repeatDate = sdt;
                                            items.alarmTimeRepeat=items.quantity;
                                            items.save();
                                        }
                                        if (item.repeatDay.contains("1") && (i == 1)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("2") && (i == 2)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("3") && (i == 3)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("4") && (i == 4)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("5") && (i == 5)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("6") && (i == 6)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        } else if (item.repeatDay.contains("7") && (i == 7)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {
                    try {
                        for (int j = 0; j < item.time.length(); j++) {
                            String[] separated = item.time.split(",");
                            String[] seperatedosage = item.dosage.split(",");
                            Log.e("value1", item.time);
                            dateTime = item.startDate + " " + separated[j].trim();
                            endDateTime = item.endDate + " " + separated[j].trim();
                            repeatDate = item.repeatDate + " " + separated[j].trim();
                            String current = Constants.getCurrentTime();
                            DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateTimePattern);
                            DateTime repeatdate = dtf.parseDateTime(repeatDate);
                            DateTime date = dtf.parseDateTime(dateTime);
                            DateTime endDate = dtf.parseDateTime(endDateTime);
                            DateTime sysDate = dtf.parseDateTime(current);
                            int datecompare = endDate.compareTo(sysDate);

                            if (item.doNotify.equals("1")) {
                                //for repeat once
                                if (item.index.equals("1")) {
                                    if (datecompare >= 0) {//means end date is greater than current date
                                       if (repeatdate.equals(sysDate)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa = alarmrepeat - 1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat = String.valueOf(aa);
                                                items.save();
                                            }
                                            if (item.alarmTimeRepeat.equals("0")) {

                                                DateTime repeatdater = repeatdate.plusDays(Integer.parseInt(item.rx_Number));
                                                String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.repeatDate = sdt;
                                                items.alarmTimeRepeat = items.quantity;
                                                items.save();
                                            }
                                        }

                                    }
                                }

//for daily
                                if (item.index.equals("2")) {
                                    if (datecompare >= 0)//means end date is greater than current date
                                    {
                                        if (repeatdate.equals(sysDate)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            } if (item.alarmTimeRepeat.equals("0")) {
                                                DateTime repeatdater = repeatdate.plusDays(1);
                                                String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.repeatDate = sdt;
                                                items.alarmTimeRepeat = items.quantity;
                                                items.save();
                                            }
                                        }

                                    }
                                }
//for weekly
                                if (item.index.equals("3")) {
                                    if (datecompare >= 0) {
                                        if (repeatdate.equals(sysDate)) {
                                            random = new Random();
                                            m = random.nextInt(9999 - 1000) + 1000;
                                            genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                            saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                            if (!item.alarmTimeRepeat.equals("0")) {
                                                int aa= alarmrepeat-1;
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.alarmTimeRepeat= String.valueOf(aa);
                                                items.save();
                                            } if (item.alarmTimeRepeat.equals("0")) {
                                                DateTime repeatdater = repeatdate.plusDays(7);
                                                String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.repeatDate = sdt;
                                                items.alarmTimeRepeat = items.quantity;
                                                items.save();
                                            }
                                        }
                                    }
                                }
                                //for custom
                                if (item.index.equals("4")) {
                                    if (datecompare >= 0)//means end date is greater than current date
                                    {
                                        if (repeatdate.equals(sysDate)) {
                                            int i = repeatdate.getDayOfWeek();
                                            if (item.alarmTimeRepeat.equals("0")) {
                                                DateTime repeatdater = repeatdate.plusDays(1);
                                                String sdt = repeatdater.toString("dd-MM-yyyy").toString();
                                                items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                items.repeatDate = sdt;
                                                items.alarmTimeRepeat = items.quantity;
                                                items.save();
                                            }
                                            if (item.repeatDay.contains("1") && (i == 1)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            } else if (item.repeatDay.contains("2") && (i == 2)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }

                                            } else if (item.repeatDay.contains("3") && (i == 3)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            } else if (item.repeatDay.contains("4") && (i == 4)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            } else if (item.repeatDay.contains("5") && (i == 5)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            } else if (item.repeatDay.contains("6") && (i == 6)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;
                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            } else if (item.repeatDay.contains("7") && (i == 7)) {
                                                random = new Random();
                                                m = random.nextInt(9999 - 1000) + 1000;

                                                genrateNotificationwithoutend(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, m, item.pharmacyName);
                                                saveNotification(item.drugName, seperatedosage[j], item.getId(), item.quantity, item.form, item.drugImage, item.pharmacyName, m);
                                                if (!item.alarmTimeRepeat.equals("0")) {
                                                    int aa= alarmrepeat-1;
                                                    items = new Select().from(DrugsListTable.class).where("Id= ?", item.getId()).executeSingle();
                                                    items.alarmTimeRepeat= String.valueOf(aa);
                                                    items.save();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        }
    }

    private void genrateNotificationwithoutend(String drugname, String dosage, Long drugId, String quantity, String drugForm, String drugImage, int m, String pharmacyName) {
        snoozetimer = PreferenceConnector.readString(getApplicationContext(), "snooze", "");
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) this.getSystemService(ns);
        Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(getPackageName(),R.layout.custom_notification_status);

        n = random.nextInt(9999 - 999) + 1000;
        o = random.nextInt(9999 - 1001) + 1000;
        p = random.nextInt(9999 - 1002) + 1000;
        if(drugForm.equals("Syrup")){
            notificationView.setTextViewText(R.id.noti_title, "Time for"+" " +dosage+" teaspoon "+drugname+" "+drugForm);

        }
        if(!pharmacyName.equals("")) {
            String[] str_array = pharmacyName.split(":");
          String  stringa = str_array[0];
          String  stringb = str_array[1];
            if (drugForm.equals("Syrup")) {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " teaspoon " + drugname + " " + drugForm + " " + stringa+" "+stringb);

            } else {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " " + drugname + " " + drugForm + " " + stringa+" "+stringb);
            }
        }
       else {
            if (drugForm.equals("Syrup")) {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " teaspoon " + drugname + " " + drugForm);

            } else {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " " + drugname + " " + drugForm);
            }
        }

        if(drugImage.equals("")){
            notificationView.setImageViewResource(R.id.noitShowImageView,R.drawable.medecine);
        }
        else{
            try {
                notificationView.setImageViewResource(R.id.notiShowImageViewTrans,R.drawable.transparent);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                    bitmap1 = BitmapFactory.decodeFile(drugImage, opts);
            } catch (Exception e) {
                e.printStackTrace();
            }
            notificationView.setImageViewBitmap(R.id.noitShowImageView, Bitmap.createScaledBitmap(bitmap1, width, height, false));
        }

        notification.contentView = notificationView;
        String aa=PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, "");
        if(aa.equals("")){

            notification.sound=Uri.parse(String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)));
        }
     else {
            notification.sound = Uri.parse(PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, ""));
        }


        Intent taken = new Intent(this, AlarmReciever.class);
        taken.putExtra("taken", "taken");
        taken.putExtra("drugtaken",drugname);
        taken.putExtra("dosagetaken",dosage);
        taken.putExtra("drugIdtaken",drugId);
        taken.putExtra("notitaken", m);
        taken.putExtra("quantitytaken",quantity);
        PendingIntent pTaken = PendingIntent.getBroadcast(this, n, taken,0);
        notificationView.setOnClickPendingIntent(R.id.texttaken, pTaken);
        if(snoozetimer.equals("")) {
            notificationView.setViewVisibility(R.id.textsnooze, View.INVISIBLE);
        }
        else {
            notificationView.setViewVisibility(R.id.textsnooze, View.VISIBLE);
            Intent snooze = new Intent(this, AlarmReciever.class);
            snooze.putExtra("snooze", "snooze");
            snooze.putExtra("drugsnooze", drugname);
            snooze.putExtra("dosagesnooze", dosage);
            snooze.putExtra("drugIdsnooze", drugId);
            snooze.putExtra("quantitysnooze",quantity);
            snooze.putExtra("notisnooze",m);
            PendingIntent pSnooze = PendingIntent.getBroadcast(this, o, snooze,0);
            notificationView.setOnClickPendingIntent(R.id.textsnooze, pSnooze);
        }

        //skip listener
        Intent skip = new Intent(this, AlarmReciever.class);
        skip.putExtra("skip", "skip");
        skip.putExtra("drugskip",drugname);
        skip.putExtra("dosageskip",dosage);
        skip.putExtra("drugIdskip",drugId);
        skip.putExtra("quantityskip",quantity);
        skip.putExtra("notiskip", m);
        PendingIntent pSkip = PendingIntent.getBroadcast(this, p, skip,0);
        notificationView.setOnClickPendingIntent(R.id.textskip, pSkip);
        mNotificationManager.notify(m, notification);

    }
/////////for refilling ///////////////
    public void getRefilling(){
        List<DrugsListTable> values = new Select().all().from(DrugsListTable.class).execute();
        if(values.size()!=0){
            for (DrugsListTable itemss : values){
                if(!itemss.doctorName.equals("")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
                    String time = tdf.format(c.getTime());
                    String  refill = itemss.startDate + " " + time;
                    String current = Constants.getCurrentTime();
                    DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateTimePattern);
                    DateTime refildate = dtf.parseDateTime(refill);
                    DateTime aa=refildate.plusDays(Integer.parseInt(itemss.doctorName));
                    DateTime sysDate = dtf.parseDateTime(current);
                    if(aa.equals(sysDate)) {
                        if (itemss.snooze.equals("")) {
                            random = new Random();
                            s = random.nextInt(9999 - 998) + 1000;
                        generateRefillNoti(itemss.drugName, itemss.getId(),itemss.drugImage,s);
                        }
                    }
                }
            }
        }
    }

    private void generateRefillNoti(String drugName, Long id, String drugImage, int s) {

        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) this.getSystemService(ns);
        Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(getPackageName(),R.layout.custom_refill_notification);
        random= new Random();
        n = random.nextInt(9999 - 103) + 1000;
        o = random.nextInt(9999 - 979) + 1000;
        p = random.nextInt(9999 - 939) + 1000;

        notificationView.setTextViewText(R.id.noti_title, "Refill"+" " +drugName+" ");
        notification.contentView = notificationView;
        String aa=PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, "");
        if(aa.equals("")){
            //   notification.sound=Uri.parse("content://media/internal/audio/media/38");
            notification.sound=Uri.parse(String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)));
        }
        else {
            notification.sound = Uri.parse(PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, ""));
        }
        if(drugImage.equals("")){
            notificationView.setImageViewResource(R.id.noitShowImageView,R.drawable.medecine);
        }
        else{
            try {
                notificationView.setImageViewResource(R.id.notiShowImageViewTrans,R.drawable.transparent);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                bitmap1 = BitmapFactory.decodeFile(drugImage, opts);
            } catch (Exception e) {
                e.printStackTrace();
            }
            notificationView.setImageViewBitmap(R.id.noitShowImageView, Bitmap.createScaledBitmap(bitmap1, width, height, false));
        }

        Intent refill = new Intent(this, AlarmReciever.class);
        refill.putExtra("refill", "refill");
        refill.putExtra("refilldrugName",drugName);
        refill.putExtra("refillId",id);
        refill.putExtra("refillnoti",s);
          PendingIntent pRefill = PendingIntent.getBroadcast(this, n, refill,0);
          notificationView.setOnClickPendingIntent(R.id.texttaken, pRefill);

            Intent later = new Intent(this, AlarmReciever.class);
            later.putExtra("later", "later");
            later.putExtra("laterdrugName", drugName);
            later.putExtra("laterId", id);
            later.putExtra("laternoti",s);
            PendingIntent pLater = PendingIntent.getBroadcast(this, o, later,0);
            notificationView.setOnClickPendingIntent(R.id.textsnooze, pLater);
            mNotificationManager.notify(s, notification);
    }

              ////for snooze////
               public void getSnoozeTime() {
              List<NotificationTable> snoozevalue = new Select().all().from(NotificationTable.class).execute();
              if(snoozevalue.size()!=0) {
              for (NotificationTable snoozeitem : snoozevalue) {
              if ((snoozeitem.currentTime != null) && (!snoozeitem.currentTime.equals(""))) {
                    Log.e("drugid", String.valueOf(snoozeitem.getId()));
                    String current = Constants.getCurrentTime();
                    DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateTimePattern);
                    DateTime sysDate = dtf.parseDateTime(current);
                    DateTime snoozefinal = dtf.parseDateTime(current);

                    snoozetimer = PreferenceConnector.readString(getApplicationContext(), "snooze", "");

                    try {
                        drugiddd = Long.parseLong(idnot);

                    } catch (NumberFormatException nfe) {

                    }
                    if (snoozetimer.equals("5")) {
                        snoozefinal = dtf.parseDateTime(snoozeitem.currentTime).plusMinutes(5);
                    } else if (snoozetimer.equals("15")) {
                        snoozefinal = dtf.parseDateTime(snoozeitem.currentTime).plusMinutes(15);
                    } else if (snoozetimer.equals("30")) {
                        snoozefinal = dtf.parseDateTime(snoozeitem.currentTime).plusMinutes(30);
                    }
                        if (snoozeitem.snooze.equals("yes")) {
                            if (snoozefinal.equals(sysDate)) {
                                generateSnoozeNoti(snoozeitem.drugName, snoozeitem.dosage, snoozeitem.getId(), snoozeitem.drugQuantity, snoozeitem.drugForm, snoozeitem.drugImage, snoozeitem.notiId,snoozeitem.notify);
                                updateNotification(snoozeitem.notiId);
                            }

                    }
                }
            }
        }
    }


    public void generateSnoozeNoti(String drugname, String dosage, Long drugId, String quantity, String drugForm, String drugImage, String notiId, String instruction) {
        snoozetimer = PreferenceConnector.readString(getApplicationContext(), "snooze", "");
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) this.getSystemService(ns);
        Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(getPackageName(),R.layout.custom_notification_status);
        random= new Random();
        n = random.nextInt(9999 - 100) + 1000;
        o = random.nextInt(9999 - 999) + 1000;
        p = random.nextInt(9999 - 998) + 1000;

        if(!instruction.equals("")) {
            String[] str_array = instruction.split(":");
            String  stringa = str_array[0];
            String  stringb = str_array[1];
            if (drugForm.equals("Syrup")) {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " teaspoon " + drugname + " " + drugForm + " " + stringa+" "+stringb);

            } else {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " " + drugname + " " + drugForm + " " + stringa+" "+stringb);
            }
        }
        else {
            if (drugForm.equals("Syrup")) {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " teaspoon " + drugname + " " + drugForm);

            } else {
                notificationView.setTextViewText(R.id.noti_title, "Time for" + " " + dosage + " " + drugname + " " + drugForm);
            }
        }

        if(drugImage.equals("")){
            notificationView.setImageViewResource(R.id.noitShowImageView,R.drawable.medecine);
        }
        else{
            try {
                notificationView.setImageViewResource(R.id.notiShowImageViewTrans,R.drawable.transparent);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;

                bitmap1 = BitmapFactory.decodeFile(drugImage, opts);

            } catch (Exception e) {
                e.printStackTrace();
            }
            notificationView.setImageViewBitmap(R.id.noitShowImageView, Bitmap.createScaledBitmap(bitmap1, width, height, false));
        }
         notification.contentView = notificationView;
        String aa=PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, "");
        if(aa.equals("")){

            notification.sound=Uri.parse(String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)));
        }
        else {
            notification.sound = Uri.parse(PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.NOTIFICATION_SOUND, ""));
        }
        Intent taken = new Intent(this, AlarmReciever.class);
        taken.putExtra("taken", "taken");
        taken.putExtra("drugtaken", drugname);
        taken.putExtra("dosagetaken", dosage);
        taken.putExtra("drugIdtaken", drugId);
        try {
            taken.putExtra("notitaken", Integer.parseInt(notiId));
        }
        catch (NumberFormatException e) { }
        taken.putExtra("quantitytaken", quantity);

        PendingIntent pTaken = PendingIntent.getBroadcast(this, n, taken, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.texttaken, pTaken);


        Intent snooze = new Intent(this, AlarmReciever.class);
        snooze.putExtra("snooze", "snooze");
        snooze.putExtra("drugsnooze", drugname);
        snooze.putExtra("dosagesnooze", dosage);
        snooze.putExtra("drugIdsnooze", drugId);
        try {
            snooze.putExtra("notisnooze", Integer.parseInt(notiId));
        }catch (NumberFormatException e){}
        snooze.putExtra("quantitysnooze", quantity);

        PendingIntent pSnooze = PendingIntent.getBroadcast(this,o, snooze, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.textsnooze, pSnooze);


        //skip listener
        Intent skip = new Intent(this, AlarmReciever.class);
        skip.putExtra("skip", "skip");
        skip.putExtra("drugskip", drugname);
        skip.putExtra("dosageskip", dosage);
        skip.putExtra("drugIdskip", drugId);
        try{
        skip.putExtra("notiskip",Integer.parseInt(notiId));}
        catch (NumberFormatException e){}
        skip.putExtra("quantityskip", quantity);

        PendingIntent pSkip = PendingIntent.getBroadcast(this, p, skip, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.textskip, pSkip);

        try {
            mNotificationManager.notify(Integer.parseInt(notiId), notification);
        }
        catch (NumberFormatException e){}
    }


    public void updateNotification(String notiId) {
        try {
            NotificationTable item =new Select().from(NotificationTable.class).where("_notiId1= ?",notiId).executeSingle();

            item.snooze="no";
            item.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


