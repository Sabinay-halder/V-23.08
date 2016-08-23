package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.HistoryListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by mercury-one on 6/10/15.
 */
public class NotificationActivity extends Activity {
   @Bind(R.id.notification_lists)
   ListView mNotiList;
    @Bind(R.id.noNotificationItemsList)
    TextView mNoItemInList;
    @Bind(R.id.cancel_notification)
    LinearLayout mCancelNotification;
    String d="";
   private LayoutInflater mInflater;
   private NotificationListViewAdapter mAdapter;
    String takendate, takentime,snooze,name = "",dosage = "",status="",current="",drugquantity="",drugId="",prefDateFormate="";
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
    String time = tdf.format(c.getTime());
    String formattedDate = df.format(c.getTime());
    int height,width;
    Bitmap bitmap1;
   ArrayList<String> drugiddd;
    List<NotificationTable> notificationListItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_layout);
        ButterKnife.bind(this);
        prefDateFormate= PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        ShortcutBadger.with(getApplicationContext()).remove();
        snooze = PreferenceConnector.readString(getApplicationContext(), "snooze", "");
        mCancelNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
          initListWithData();
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = false;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medecine2, dimensions);
        height = dimensions.outHeight;
        width =  dimensions.outWidth;
        drugiddd=new ArrayList<>();
    }
   private void initListWithData() {
       notificationListItems =new Select().from(NotificationTable.class).orderBy("Id desc").execute();
       mAdapter = new NotificationListViewAdapter(this);
        mAdapter.setData(notificationListItems);
     //  mAdapter.getCount();

        mNotiList.setAdapter(mAdapter);
      mNotiList.setEmptyView(mNoItemInList);
       notificationListItems.size();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    protected void onResume() {
        super.onResume();
       initListWithData();


    }

    public class NotificationListViewAdapter  extends BaseAdapter {
        Context context;
        private List<NotificationTable> mData;
        private LayoutInflater mInflater;

        public NotificationListViewAdapter(Context context){
            this.context = context;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public List<NotificationTable> getData()
        {
            return mData;
        }
        public void setData(List<NotificationTable> data)
        {
            this.mData = data;
            if (data != null) {
                notifyDataSetChanged();
            }
        }
        @Override
        public int getCount() {
            if (mData != null && !mData.isEmpty()) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mData != null && !mData.isEmpty()) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View row=convertView;
            ViewHolder1 holder=null;

            row = mInflater.inflate(R.layout.notification_list_items, parent, false);
            holder = new ViewHolder1(row);
            if (mData != null && !mData.isEmpty()) {
                NotificationTable currentPerson = mData.get(position);
                initValues(holder, currentPerson,position);
            }
            final ViewHolder1 finalHolder = holder;


            return row;
        }
        public void initValues(ViewHolder1 holder, final NotificationTable notification, final int position) {
          if(snooze.equals("")){
              holder.notiSnooze.setVisibility(View.GONE);
          }
            else{
              holder.notiSnooze.setVisibility(View.VISIBLE);
          }

            if(prefDateFormate.equals("MM-DD-YYYY")){
                String[] split=notification.notificationdate.split("-");
                String fordate = split[0];
                String formonth = split[1];
                String foryear=split[2];
                holder.notiDate.setText(formonth + "-" + fordate + "-" + foryear);
            }
            else if(prefDateFormate.equals("DD-MM-YYYY")){
                holder.notiDate.setText(notification.notificationdate);
            }
           else if(prefDateFormate.equals("YYYY-MM-DD")){
                String[] split=notification.notificationdate.split("-");
                String fordate = split[0];
                String formonth = split[1];
                String foryear=split[2];
                holder.notiDate.setText(foryear + "-" + formonth + "-" + fordate);
            }

            if(!notification.notify.equals("")){
                String[] str_array = notification.notify.split(":");
                String  stringa = str_array[0];
                String  stringb = str_array[1];
                holder.dosage.setText(notification.dosage+" "+notification.drugForm+" "+stringa+" "+stringb);
            }
            else {
                holder.dosage.setText(notification.dosage + " " + notification.drugForm);
            }
            if (notification.drugImage.equals("")) {
                holder.drugImages.setImageResource(R.drawable.add_drug_button_new);
            } else {
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    bitmap1 = BitmapFactory.decodeFile(notification.drugImage, opts);
                    holder.drugImagesTrans.setImageResource(R.drawable.transparent);
                    holder.drugImages.setImageBitmap(Bitmap.createScaledBitmap(bitmap1, width, height, false));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            String a=notification.notificationTime;

            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                final Date dateObj = sdf.parse(a);
                String aaas=new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase();
                String[] str_array = aaas.split(":");
                String stringa = str_array[0];
                String  stringb = str_array[1];
                if(stringa.equals("0")){
                    stringa="12";
                    holder.noTime.setText(stringa+":"+stringb);
                }
                else {
                    holder.noTime.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.notiDrug.setText(notification.drugName);
              if(notification.snooze.equals("yes")){
                  holder.notiSkip.setVisibility(View.GONE);
                  holder.notiTaken.setVisibility(View.GONE);
                  holder.notiSnooze.setVisibility(View.GONE);
                  holder.notisnoozed.setVisibility(View.VISIBLE);
              }
            else{
                  holder.notiSkip.setVisibility(View.VISIBLE);
                  holder.notiTaken.setVisibility(View.VISIBLE);
                  holder.notisnoozed.setVisibility(View.GONE);
                  if(snooze.equals("")){
                      holder.notiSnooze.setVisibility(View.GONE);
                  }
                  else{
                      holder.notiSnooze.setVisibility(View.VISIBLE);
                  }
              }
            holder.notiTaken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manaager.cancelAll();

                    status = "taken";
                    name = notification.drugName;
                    dosage = notification.dosage;
                    takendate = formattedDate.toString();
                    takentime = time.toString();
                    drugquantity=notification.drugForm;
                    drugId=notification.drugId;
                    try {
                        HistoryListTable historyList;
                        historyList = new HistoryListTable(
                                drugId.toString().trim(),
                                name.toString().trim(),
                                dosage.toString().trim(),
                                takendate.toString().trim(),
                                status.toString().trim(),
                                takentime.toString().trim(),
                                drugquantity.toString().trim()

                        );

                        historyList.save();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                        new Delete().from(NotificationTable.class).where("_notiId1 = ?", notification.notiId).execute();

                        mData.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.notiSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manaager.cancelAll();

                    status = "skipped";
                    name = notification.drugName;
                    dosage = notification.dosage;
                    takendate = formattedDate.toString();
                    takentime = time.toString();
                    drugquantity=notification.drugForm;
                    drugId=notification.drugId;
                    try {
                        HistoryListTable historyLis;
                        historyLis = new HistoryListTable(
                                drugId.toString().trim(),
                                name.toString().trim(),
                                dosage.toString().trim(),
                                takendate.toString().trim(),
                                status.toString().trim(),
                                takentime.toString().trim(),
                                drugquantity.toString().trim()

                        );

                        historyLis.save();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                        new Delete().from(NotificationTable.class).where("_notiId1 = ?", notification.notiId).execute();
                        mData.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            holder.notiSnooze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manaager.cancelAll();
                        current= Constants.getCurrentTime();
                    if (!Constants.isMyServiceRunning(AlarmService.class, context)) {
                        Intent intent = new Intent(getParent(), AlarmService.class);
                        startService(intent);
                    }
                   try {
                       NotificationTable item=new Select().from(NotificationTable.class).where("_notiId1 = ?",notification.notiId).executeSingle();
                       item.currentTime=current;
                       item.snooze="yes";
                       item.save();

                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        class ViewHolder1 {
            TextView notiDate;
            TextView noTime;
            ImageView drugImages;
            ImageView drugImagesTrans;
            TextView dosage;
            TextView notiDrug;
            ImageView notiTaken,notiSkip,notiSnooze,notisnoozed;

            public ViewHolder1(View view) {
                // TODO Auto-generated constructor stub
                notiDate = (TextView) view
                        .findViewById(R.id.listDateTextView);

                noTime = (TextView) view
                        .findViewById(R.id.listDosageTime);
                drugImages= (ImageView) view
                        .findViewById(R.id.listShowImageView);
                dosage=(TextView)view.findViewById(R.id.listNotDosageTextView);

                notiTaken=(ImageView)view.findViewById(R.id.listTakenTextView);
                notiSkip=(ImageView)view.findViewById(R.id.listSkipTextView);
                notiSnooze=(ImageView)view.findViewById(R.id.listSnoozeTextView);
                notisnoozed=(ImageView)view.findViewById(R.id.listSnoozedTextView);
                notiDrug = (TextView) view
                        .findViewById(R.id.listNotDrugTextView);
                drugImagesTrans= (ImageView) view
                        .findViewById(R.id.notiShowImageViewTrans);

            }
        }
    }
}

