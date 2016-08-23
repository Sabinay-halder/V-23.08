package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.DrugsListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by mercury-one on 10/8/15.
 */
public class DrugsActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.add_drugs)
    ImageView mAddDrugNameImage;
    @Bind(R.id.edit_drug)
    ImageView mEditDrugImage;
    @Bind(R.id.drugs_lists)
    ListView mDrugListAll;
    @Bind(R.id.noDrugsItemsList)
    TextView mTextEmptyList;
   @Bind(R.id.noDrugsItemsListImage)ImageView mNoDrugItemsListImage;
    @Bind(R.id.pendingNotification)TextView mPendingNotification;
    @Bind(R.id.linearmid)LinearLayout mLinearMid;
    @Bind(R.id.reminder_text)TextView mReminderText;
    private LayoutInflater mInflater;
    private DrugsListViewAdapter mAdapter;
    // private SetImage msetImage;
    String editview = "0";
    Bitmap bitmap1;
    String eDrugName,erepeatInterval,eRefillDay,eForm,eFoodInstruction,edrugImg,eSmalldrgImg,freeUser="";
    DateTime addeddate,current;
    //for reminders//
    String ereminderdosage,ereminderStartDate,ereminderEndDate,ereminderDoNotify,ereminderIndex,ereminderDuration,ereminderRepeatDate,ereminderRepeatDay,ereminderTime,ereminderQuantity,ereminderMultipleAlarm,ereminderDosageUnit,ereminderDrugImage,ereminderForm,eRefillUpdate;
    Long eDrugId;
    int h, w,pendingNoti,badgeCount = 0,visits,rowposition = -1;
    SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
    List<DrugsListTable> value;
    Dialog dialog;
    String check_two,subs;
    DateTimeFormatter dtf;
    File review = new File(Environment.getExternalStorageDirectory() + "/" + Constants.Directory_path);
    public static String items[] = {"com.widevision.pillreminder.monthly", "com.widevision.pillreminder.sixmonth", "com.widevision.pillreminder.annual"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drugs_main);
        ButterKnife.bind(this);
        mEditDrugImage.setOnClickListener(this);
        mAddDrugNameImage.setOnClickListener(this);
        mNoDrugItemsListImage.setOnClickListener(this);
        mInflater = LayoutInflater.from(this);
        value = new Select().all().from(DrugsListTable.class).execute();
        if (value.size() == 0) {
            mEditDrugImage.setVisibility(View.INVISIBLE);
            mNoDrugItemsListImage.setVisibility(View.VISIBLE);
            mTextEmptyList.setVisibility(View.VISIBLE);
            mAddDrugNameImage.setVisibility(View.GONE);
            mLinearMid.setVisibility(View.VISIBLE);
            mDrugListAll.setVisibility(View.GONE);

        } else {
            mEditDrugImage.setVisibility(View.VISIBLE);
            mNoDrugItemsListImage.setVisibility(View.GONE);
            mTextEmptyList.setVisibility(View.GONE);
            mAddDrugNameImage.setVisibility(View.VISIBLE);
            mLinearMid.setVisibility(View.INVISIBLE);
            mDrugListAll.setVisibility(View.VISIBLE);
            initListWithData();
        }
        freeUser = PreferenceConnector.readString(getParent(), PreferenceConnector.Free_Subscription, "");
        dtf = DateTimeFormat.forPattern(Constants.datePattern);
        addeddate = dtf.parseDateTime(Constants.getCurrentDate()).plusMonths(2);
        current = dtf.parseDateTime(Constants.getCurrentDate());
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = false;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medecine2, dimensions);
        h = dimensions.outHeight;
        w = dimensions.outWidth;
        visits = Constants.visit++;
        Log.e("height--", String.valueOf(h));
        Log.e("width---", String.valueOf(w));
        notification();
     check_two =PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.Free_Two_Month, "");
     subs=PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.subscription, "");
        /////////subscription/////////

        if (PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.Check_daily, "").equals(Constants.getCurrentDate())) {

        }
        else {
            PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.Check_daily, Constants.getCurrentDate());
            if ((!review.exists())&&(!check_two.equals(Constants.getCurrentDate()))&&(subs.equals(""))) {
                {
                    reviewPopup();
                }
            }
        }

    }
    private void reviewPopup(){
        dialog = new Dialog(getParent());
        final Dialog dialog = new Dialog(getParent());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.clear_confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView text = (TextView) dialog.findViewById(R.id.textView1);
        Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
        Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
        text.setText("Rate us 5 stars on Play Store and get 2 months subscription free");
        dialog.show();
        buttonyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?id=com.widevision.pillreminder")));
                    review.mkdir();
                    PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.Free_Two_Month, String.valueOf(addeddate));
                    PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.Free_Subscription, "no");
                    dialog.dismiss();
                } catch (android.content.ActivityNotFoundException anfe) {
                }
            }

        });
        buttoncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }

    private void notification(){
        List<NotificationTable> notificationListItems = new Select().all().from(NotificationTable.class).execute();
        if(notificationListItems.size()==0){
            mPendingNotification.setVisibility(View.GONE);
        }
        else{
            pendingNoti=notificationListItems.size();
            mPendingNotification.setVisibility(View.VISIBLE);
            mPendingNotification.setText(String.valueOf(pendingNoti));

            badgeCount=notificationListItems.size();
            ShortcutBadger.with(getApplicationContext()).count(badgeCount);

            mPendingNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DrugsActivity.this,NotificationActivity.class);
                    TabGroupActivity parentActivity = (TabGroupActivity) getParent();
                    parentActivity.startChildActivity("notification", intent);
                }
            });
            mReminderText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DrugsActivity.this,NotificationActivity.class);
                    TabGroupActivity parentActivity = (TabGroupActivity) getParent();
                    parentActivity.startChildActivity("notification", intent);
                }
            });

        }
    }

    private void initListWithData() {
        List<DrugsListTable> drugListItems = new Select().from(DrugsListTable.class).orderBy("Id DESC").execute();
        mAdapter = new DrugsListViewAdapter(this);
        mAdapter.setData(drugListItems);
        mDrugListAll.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        if (Constants.buttonEnable) {
            Constants.setButtonEnable();
            switch (v.getId()) {

                case R.id.add_drugs:

                    if((value.size()>1)&&(freeUser.equals("yes"))){
                        subscribeDialog();
                   }
                    else if ((PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.Free_Two_Month, "").equals( String.valueOf(current)))) {
                        PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.Free_Subscription, "yes");
                        subscribeDialog();
                           }
                        else{

                    Intent intent = new Intent(getParent(), AddDrugsActivity.class);
                    TabGroupActivity parentActivity = (TabGroupActivity) getParent();
                    parentActivity.startChildActivity("drug2", intent);
                         }
                    break;

                case R.id.edit_drug:
                    if(editview.equals("1")) {
                        Intent inten = new Intent(getParent(), EditDrugActivity.class);
                        inten.putExtra("drugname", eDrugName);
                        inten.putExtra("form", eForm);
                        inten.putExtra("repeatInterval", erepeatInterval);
                        inten.putExtra("refillDay", eRefillDay);
                        inten.putExtra("foodInstruction", eFoodInstruction);
                        inten.putExtra("drugid1", eDrugId);
                        inten.putExtra("drugimage", edrugImg);
                        inten.putExtra("smalldrugimage", eSmalldrgImg);
                        ///////Reminder/////////
                        inten.putExtra("reminderdosage", ereminderdosage);
                        inten.putExtra("reminderstartDate", ereminderStartDate);
                        inten.putExtra("reminderendDate", ereminderEndDate);
                        inten.putExtra("reminderdoNotify", ereminderDoNotify);
                        inten.putExtra("reminderindex", ereminderIndex);
                        inten.putExtra("reminderDuration", ereminderDuration);
                        inten.putExtra("reminderrepeatdate", ereminderRepeatDate);
                        inten.putExtra("reminderrepeatDay", ereminderRepeatDay);
                        inten.putExtra("remindertime", ereminderTime);
                        inten.putExtra("reminderquantity", ereminderQuantity);
                        inten.putExtra("remindermultipleAlarmTime", ereminderMultipleAlarm);
                        inten.putExtra("reminderdosageUnit", ereminderDosageUnit);
                        inten.putExtra("reminderRefillUpdate", eRefillUpdate);
                        TabGroupActivity pActivity = (TabGroupActivity) getParent();
                        pActivity.startChildActivity("drug3", inten);
                    }
                    else{
                        Constants.alert(getParent(), "Please select a drug first. ");
                    }
                    break;

                case R.id.noDrugsItemsListImage:

                    Intent in = new Intent(getParent(), AddDrugsActivity.class);
                    TabGroupActivity parentAct = (TabGroupActivity) getParent();
                    parentAct.startChildActivity("drug4", in);
                    break;
            }
        }
    }
    public void subscribeDialog(){
        final Dialog dialog = new Dialog(getParent());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.clear_confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView text = (TextView) dialog.findViewById(R.id.textView1);
        Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
        Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
        text.setText("To add more Drugs, Please subscribe.");
        dialog.show();
        buttonyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                TabCreator.tabHost.setCurrentTab(3);

            }

        });
        buttoncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        editview = "0";
        rowposition = -1;
        eDrugName="";
        erepeatInterval="";eRefillDay="";eForm="";eFoodInstruction="";edrugImg="";eSmalldrgImg="";
        dtf= DateTimeFormat.forPattern(Constants.datePattern);
        addeddate = dtf.parseDateTime(Constants.getCurrentDate()).plusMonths(2);
        current=dtf.parseDateTime(Constants.getCurrentDate());
        List<DrugsListTable> value = new Select().all().from(DrugsListTable.class).execute();
        if (value.size() == 0) {
            mEditDrugImage.setVisibility(View.INVISIBLE);
            mNoDrugItemsListImage.setVisibility(View.VISIBLE);
            mTextEmptyList.setVisibility(View.VISIBLE);
            mAddDrugNameImage.setVisibility(View.GONE);
            mLinearMid.setVisibility(View.VISIBLE);
            mDrugListAll.setVisibility(View.GONE);

        } else {
            mEditDrugImage.setVisibility(View.VISIBLE);
            mNoDrugItemsListImage.setVisibility(View.GONE);
            mTextEmptyList.setVisibility(View.GONE);
            mAddDrugNameImage.setVisibility(View.VISIBLE);
            mLinearMid.setVisibility(View.INVISIBLE);
            mDrugListAll.setVisibility(View.VISIBLE);
           initListWithData();
        }
           }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public class DrugsListViewAdapter extends BaseAdapter {
        Context context;
        private List<DrugsListTable> mData;
        private LayoutInflater mInflater;
        public DrugsListViewAdapter(Context context) {
            this.context = context;
            // TODO Auto-generated constructor stub
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public List<DrugsListTable> getData()

        {
            return mData;
        }
        public void setData(List<DrugsListTable> data) {
            this.mData = data;
            if (data != null) {
                notifyDataSetChanged();
            }
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (mData != null && !mData.isEmpty()) {
                return mData.size();
            }
            return 0;
        }
        @Override
        public DrugsListTable getItem(int position) {
            // TODO Auto-generated method stub
            if (mData != null && !mData.isEmpty()) {
                return mData.get(position);
            }
            return null;
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View row = convertView;
            ViewHolder1 holder = null;
            row = mInflater.inflate(R.layout.drug_list_items, parent, false);
            holder = new ViewHolder1(row);
            row.setTag(holder);


            if (mData != null && !mData.isEmpty()) {
                final DrugsListTable currentPerson = mData.get(position);

                initValues(holder, currentPerson, position);
                final ViewHolder1 finalHolder = holder;
                final View finalRow = row;

                if (rowposition == position) {
                    editview="1";
                    holder.drugImagesTrans.setImageResource(R.drawable.transparent_blue);
                    finalRow.setBackgroundColor((Color.parseColor("#a9ddff")));

                } else {
                    finalRow.setBackgroundResource(R.color.white);
                    holder.drugImagesTrans.setImageResource(R.drawable.transparent);
                }
                    holder.linearhorizontal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rowposition = position;
                        eDrugId=mData.get(position).getId();
                        eDrugName=mData.get(position).drugName;
                        eForm=mData.get(position).form;
                        erepeatInterval=mData.get(position).rx_Number;
                        eRefillDay=mData.get(position).doctorName;
                        eFoodInstruction=mData.get(position).pharmacyName;
                        edrugImg=mData.get(position).drugImage;
                        eSmalldrgImg=mData.get(position).smallDrugImage;
                        //for reminder//
                        ereminderdosage=mData.get(position).dosage;
                        ereminderStartDate=mData.get(position).startDate;
                        ereminderEndDate=mData.get(position).endDate;
                        ereminderDoNotify=mData.get(position).doNotify;
                        ereminderIndex=mData.get(position).index;
                        ereminderDuration=mData.get(position).repeat;
                        ereminderRepeatDate=mData.get(position).repeatDate;
                        ereminderRepeatDay=mData.get(position).repeatDay;
                        ereminderTime=mData.get(position).time;
                        ereminderQuantity=mData.get(position).quantity;
                        ereminderMultipleAlarm=mData.get(position).alarmTimeRepeat;
                        ereminderDosageUnit=mData.get(position).dosageUnit;
                        eRefillUpdate=mData.get(position).snooze;
                        Log.e("position=---", String.valueOf(rowposition));
                        notifyDataSetChanged();
                    }
                });
            }
            return row;
        }

        public void initValues(ViewHolder1 holder, DrugsListTable currentReminder,int position) {
            int a= Integer.parseInt(currentReminder.quantity);
            for (int i = 0; i < a; i++) {
               String[] seperatedTime = currentReminder.time.split(",");
                LayoutInflater layoutInflater;
                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View v = layoutInflater.inflate(R.layout.drug_time_data, holder.linearhorizontal, false);
                holder.linearhorizontal.addView(v);
                TextView home_item_text = (TextView) v.findViewById(R.id.timerdata);

                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");

                try {
                    final Date dateObj = sdf.parse(seperatedTime[i]);
                    String aaas=new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase();
                    String[] str_array = aaas.split(":");
                    String stringa = str_array[0];
                    String  stringb = str_array[1];
                    if(stringa.equals("0")){
                        stringa="12";
                        home_item_text.setText(stringa+":"+stringb);
                    }
                    else {
                        home_item_text.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.drugName.setText(currentReminder.drugName+" "+currentReminder.form);
            if(!currentReminder.pharmacyName.equals("")) {
                String[] str_array = currentReminder.pharmacyName.split(":");
                String  stringa = str_array[0];
                String  stringb = str_array[1];
                holder.foodLayout.setVisibility(View.VISIBLE);
                holder.foodins.setText(stringa+" "+stringb);
            }
            else{
                holder.foodLayout.setVisibility(View.GONE);
            }
            if(currentReminder.index.equals("1")){
                holder.repeat.setText(currentReminder.quantity+" time(s)"+" Once");
            }
            if(currentReminder.index.equals("2")){
                holder.repeat.setText(currentReminder.quantity+" time(s)"+" Daily");
            }
            if(currentReminder.index.equals("3")){
                holder.repeat.setText(currentReminder.quantity+" time(s)"+" Weekly");
            }
            if(currentReminder.index.equals("4")){
                holder.repeat.setText(currentReminder.quantity+" time(s)"+" Custom");
            }



            if (currentReminder.drugImage.equals("")) {
                holder.drugImages.setImageResource(R.drawable.add_drug_button_new);
            } else {
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;

                    bitmap1 = BitmapFactory.decodeFile(currentReminder.drugImage, opts);
                    holder.drugImagesTrans.setImageResource(R.drawable.transparent);
                    holder.drugImages.setImageBitmap(Bitmap.createScaledBitmap(bitmap1, w, h, false));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

            class ViewHolder1 {
            TextView drugName;
            TextView repeat;
            ImageView drugImages;
            ImageView drugImagesTrans;
            TextView foodins;
            TextView mPendingNotification;
            LinearLayout linearhorizontal;
            LinearLayout foodLayout;
            HorizontalScrollView horizontalScrollView;
            public ViewHolder1(View view) {
                // TODO Auto-generated constructor stub
                repeat = (TextView) view.findViewById(R.id.takendate);
                drugName = (TextView) view.findViewById(R.id.listDrugNameTextView);
                drugImages= (ImageView) view.findViewById(R.id.listShowImageView);
                drugImagesTrans= (ImageView) view.findViewById(R.id.drugShowImageViewTrans);
                mPendingNotification=(TextView)view.findViewById(R.id.pendingNotification);
                linearhorizontal = (LinearLayout) view.findViewById(R.id.linearaa);
                horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalscroll);
                foodins = (TextView) view.findViewById(R.id.food_inst);
                foodLayout = (LinearLayout) view.findViewById(R.id.food_inst_layout);
            }
        }
    }
}