package com.widevision.pillreminder.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.kyleduo.switchbutton.SwitchButton;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.DrugsListTable;
import com.widevision.pillreminder.database.HistoryListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.model.HideKeyActivity;
import com.widevision.pillreminder.util.ArrayTest;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;
import com.widevision.pillreminder.widget.OnWheelChangedListener;
import com.widevision.pillreminder.widget.WheelView;
import com.widevision.pillreminder.widget.adapters.AbstractWheelTextAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 12/10/15.
 */
public class EditDrugActivity extends HideKeyActivity implements View.OnClickListener {
    @Bind(R.id.delete_button)Button mDeleteButton;
    @Bind(R.id.add_drugs_cancel)ImageView mCancelI;
    @Bind(R.id.add_drugs_save)ImageView mSaveI;
    @Bind(R.id.drugName)AutoCompleteTextView mDrugNameEdt;
    @Bind(R.id.editno)TextView mDosageText;
    @Bind(R.id.form_text)TextView mFormTextView;
    @Bind(R.id.camera_capture)ImageView mCameraCapture;
    @Bind(R.id.imgPreview)ImageView mImgPrev;
    @Bind(R.id.drug_text)TextView mdrugTextView;
    @Bind(R.id.form_layout)RelativeLayout mFormLayout;
    @Bind(R.id.camera_layout)RelativeLayout mCameraLayout;
    /////////////////////        AddReminder    ///////////////////////////////////////
    @Bind(R.id.enable_notification)SwitchButton mEnableNotification;

    @Bind(R.id.date_select_text)TextView mDateSelectText;


    //binders for repeat layout
    @Bind(R.id.repeat_down_layout)LinearLayout mRepeatDownLayout;
    @Bind(R.id.repeat_once)RelativeLayout mRepeatOnce;
    @Bind(R.id.custom_days)RelativeLayout mCustomDays;
    @Bind(R.id.repeat_daily)RelativeLayout mRepeatDaily;
    @Bind(R.id.repeat_weekly)RelativeLayout mRepeatWeekly;

    @Bind(R.id.day_interval_check)ImageView mDayIntervalCheck;
    @Bind(R.id.daily_check)ImageView mDailyCheck;
    @Bind(R.id.weekly_check)ImageView mWeeklyCheck;
    @Bind(R.id.custom_check)ImageView mCustomCheck;

    /////refill layout//////
    @Bind(R.id.refill_days_choice_layout)RelativeLayout mRefillDayChoiceLayout;
    @Bind(R.id.refill_day_edit)TextView mRefillDayEdt;

    ////food instrunction////
    @Bind(R.id.food_instruction)RelativeLayout mFoodInstruction;
    @Bind(R.id.food_linear)LinearLayout mFoodLayout;
    @Bind(R.id.food_before_layout)RelativeLayout mFoodbeforeLayout;
    @Bind(R.id.after_food_layout)RelativeLayout mFoodAfterLayout;

    @Bind(R.id.before_check)ImageView mBeforeCheck;
    @Bind(R.id.after_check)ImageView mAfterCheck;
    @Bind(R.id.listadd)ListView mListAdd;
    @Bind(R.id.foodInstEdit)EditText mFoodInstEdit;
    ////schedule////
   @Bind(R.id.schedule_close)RelativeLayout mSchedule;
    @Bind(R.id.schedule_layout)LinearLayout mScheduleLayout;

    @Bind(R.id.duration_value)TextView mdurationValue;
    @Bind(R.id.day_txt)TextView mDayText;
    @Bind(R.id.start_date_layout)RelativeLayout mStartss;
    @Bind(R.id.date_range_layout)RelativeLayout mDateRange;
    @Bind(R.id.addDosageAndTimeLayout)RelativeLayout mDrugDosage;
    @Bind(R.id.notification)RelativeLayout mnotification;

    //new layouts
    @Bind(R.id.noti_close)RelativeLayout mNotiClose;
    @Bind(R.id.form_close)RelativeLayout mFormCLose;
    @Bind(R.id.dosage_close)RelativeLayout mDosageClose;
    @Bind(R.id.refill_close)RelativeLayout mRefillClose;
    @Bind(R.id.camera_open)RelativeLayout mCameraOpen;
    @Bind(R.id.day_interval_text)TextView mdayInterval_Text;
    String drugName,form,repeatInterval,refillDay,foodInstruction="",drugNamee,form1,drugImage="",smallImage="",duration="";
    long drugId;
    Bundle extras;
    int selected_value,tag=0,layout=0;
    String forms[],formsOral[];
    private FormAdapter adapt;
    Bitmap bitmap1,bitmap2;
    int h, w,sheight,swidth;
    private String mCurrentPhotoPath="";

    /////////////////////////////////////variables for editReminder//////////////////////////
    private DatePickerDialog fromdate1;
    private TimePickerDialog mytime1;
    private SimpleDateFormat dateFormatter,timeFormatter;
    private AddRowAdapter mAdapter;
    ArrayList<ArrayTest> arrayListItems;
    //boolean value for day and week
    public boolean monday,tuesday,wednesday,thursday,friday,saturday,sunday,once,daily,weekly,custom=false;
    //new int
    int noticlose=0,formclose=0,dosageclose=0,refillclose=0,cameraclose=0;
    String repeatDaytemp,dosageTemp,stringa="",stringb="",flag="1",dosageUnit="",reminderRefillUpdate= "",hourString="",minuteString="",x="",repeatTimeTemp="",selectionIndex,refilByDayBool="no";
    Date mystart=new Date();
    int rcount=0,custumdaycount=0,repeatAlarm,i,remindertag=0,rschedule=0,rfood=0,aa=0,functag=0;
    List<String> timer3,dosage3,dosageUnit3;
    private LayoutInflater mInflater;
    String index,repeatDate,dates,aaa="";
    StringBuilder repeatDay = new StringBuilder();
    String start,end,quantity,alarmTimeRepeat,time9,prefDateFormate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    int defdate,defmon,defyear;
    //animation
    Animation slideDown;
    Animation slideUP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adddrugs);
        ButterKnife.bind(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        mdrugTextView.setText("Edit Drug Reminder");
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = false;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medecine2, dimensions);
        h = dimensions.outHeight;
        w = dimensions.outWidth;
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = false;
        Bitmap sample = BitmapFactory.decodeResource(getResources(), R.drawable.sample, op);
        sheight = op.outHeight;
        swidth = op.outWidth;
        mImgPrev.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
        mDeleteButton.setOnClickListener(this);

        mSaveI.setOnClickListener(this);
        mCancelI.setOnClickListener(this);

        ///////////////////////////////////////////   listner for Add reminder     ////////////////////////////////////
        mEnableNotification.setChecked(true);

        //listner for repeat layout
        mRepeatOnce.setOnClickListener(this);
        mRepeatDaily.setOnClickListener(this);
        mRepeatWeekly.setOnClickListener(this);
        mRepeatDownLayout.setOnClickListener(this);
        mCustomDays.setOnClickListener(this);

        mBeforeCheck.setImageResource(R.drawable.button_1);
        mAfterCheck.setImageResource(R.drawable.button_1);
        mDateRange.setOnClickListener(this);
       mSchedule.setOnClickListener(this);
        //food iinstruction listner////
        mFoodInstruction.setOnClickListener(this);
        mFoodbeforeLayout.setOnClickListener(this);
        mFoodAfterLayout.setOnClickListener(this);
        ///new listners
        mNotiClose.setOnClickListener(this);
        mFormCLose.setOnClickListener(this);
        mDosageClose.setOnClickListener(this);
        mRefillClose.setOnClickListener(this);
        mCameraLayout.setOnClickListener(this);
        mFormLayout.setOnClickListener(this);
        mStartss.setOnClickListener(this);
        mDrugDosage.setOnClickListener(this);
        mCameraOpen.setOnClickListener(this);
        mRefillDayChoiceLayout.setOnClickListener(this);
        setupUI(findViewById(R.id.root));
        arrayListItems = new ArrayList<>();
        extras= getIntent().getExtras();
        if(extras != null) {
            drugName = extras.getString("drugname");
            drugId=extras.getLong("drugid1");
            form=extras.getString("form");
            repeatInterval = extras.getString("repeatInterval");
            refillDay =extras.getString("refillDay");
            foodInstruction =extras.getString("foodInstruction");
            mCurrentPhotoPath=extras.getString("drugimage");
            smallImage=extras.getString("smalldrugimage");
            ///////////////////extras for reminder/////////////////
            dosageTemp = extras.getString("reminderdosage");
            flag = extras.getString("reminderdoNotify");
            end =extras.getString("reminderendDate");
            start =extras.getString("reminderstartDate");
            index=extras.getString("reminderindex");
            duration=extras.getString("reminderDuration");
            repeatDate=extras.getString("reminderrepeatdate");
            repeatDaytemp=extras.getString("reminderrepeatDay");
            repeatTimeTemp=extras.getString("remindertime");
            quantity=extras.getString("reminderquantity");
            alarmTimeRepeat=extras.getString("reminderquantity");
            dosageUnit=extras.getString("reminderdosageUnit");
            reminderRefillUpdate=extras.getString("reminderRefillUpdate");
        }

        if(!repeatInterval.equals("")){
            mdayInterval_Text.setText(repeatInterval+" Day(s)");
            mdayInterval_Text.setVisibility(View.VISIBLE);
        }
        Calendar nows = Calendar.getInstance();
        defdate=nows.get(Calendar.DATE);
        defmon=nows.get(Calendar.MONTH);
        defyear=nows.get(Calendar.YEAR);

        Log.e("image---",drugImage);
        mDrugNameEdt.setText(drugName);
        mFormTextView.setText(form);
        formsOral = new String[]{"Tablet","Capsule","Syrup","Powder","Ear Drop","Eye Drop","Ointment", "Toothpaste"};
      if(drugImage.length()!=0) {
          mImgPrev.setVisibility(View.VISIBLE);
      }

   try {
       BitmapFactory.Options opts = new BitmapFactory.Options();
       opts.inSampleSize = 2;

       bitmap2 = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);

         mImgPrev.setImageBitmap( Bitmap.createScaledBitmap(bitmap2,swidth ,sheight, false));
   } catch (Exception e) {
   e.printStackTrace();
    }

          ////////   setting data for reminder ////////////
          try {
              repeatAlarm = Integer.parseInt(alarmTimeRepeat);
          } catch (NumberFormatException e) {
          }
          for (i = 0; i < repeatAlarm; i++) {

              String[] seperatedTime = repeatTimeTemp.split(",");
              String[] sepratedUnit = dosageTemp.split(",");
              String[] seprateddosageUnit = dosageUnit.split(",");
              ArrayTest te = new ArrayTest();
              te.setTimesave(seperatedTime[i]);
              te.setDosageQuantity(sepratedUnit[i]);
              te.setDosageUnit(seprateddosageUnit[i]);
              arrayListItems.add(te);
              inflateEditRow(i);
          }

        if(repeatAlarm==1){
            mDosageText.setText("One time");
        }
       else if(repeatAlarm==2){
            mDosageText.setText("Two times");
        }
        else if(repeatAlarm==3){
            mDosageText.setText("Three times");
        }
        else if(repeatAlarm==4){
            mDosageText.setText("Four times");
        }
        else if(repeatAlarm==5){
            mDosageText.setText("Five times");
        }
        else if(repeatAlarm==6){
            mDosageText.setText("Six times");
        }
        dates=start;
        prefDateFormate=PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        if(prefDateFormate.equals("MM-DD-YYYY")){
            String[] split=start.split("-");
            String fordate = split[0];
            String formonth = split[1];
            String foryear=split[2];
            String mm =Integer.parseInt(formonth) < 10 ? "0" + formonth : "" + formonth;
            String dd = Integer.parseInt(fordate) < 10 ? "0" + fordate : "" + fordate;
            mDateSelectText.setText(mm+"-"+dd+"-"+foryear);
        }
       else if(prefDateFormate.equals("DD-MM-YYYY")){
            String[] split=start.split("-");
            String fordate = split[0];
            String formonth = split[1];
            String foryear=split[2];
            String mm =Integer.parseInt(formonth) < 10 ? "0" + formonth : "" + formonth;
            String dd = Integer.parseInt(fordate) < 10 ? "0" + fordate : "" + fordate;
            mDateSelectText.setText(dd+"-"+mm+"-"+foryear);
        }
        else if(prefDateFormate.equals("YYYY-MM-DD")){
            String[] split=start.split("-");
            String fordate = split[0];
            String formonth = split[1];
            String foryear=split[2];
            String mm =Integer.parseInt(formonth) < 10 ? "0" + formonth : "" + formonth;
            String dd = Integer.parseInt(fordate) < 10 ? "0" + fordate : "" + fordate;
            mDateSelectText.setText(foryear+"-"+mm+"-"+dd);
        }


        x = start;
        if(!foodInstruction.equals("")){
            String[] str_array = foodInstruction.split(":");
            stringa = str_array[0];
            stringb = str_array[1];
            if(stringa.equals("After Meal")){
                mAfterCheck.setImageResource(R.drawable.button_2);
                mFoodInstEdit.setText(stringb);
                aa=2;
            }
           else if(stringa.equals("Before Meal")){
             mBeforeCheck.setImageResource(R.drawable.button_2);
                mFoodInstEdit.setText(stringb);
                aa=1;
            }
        }
                if(flag.equals("1")){
              mEnableNotification.setChecked(true);
              flag = "1";
          }
          else{
              mEnableNotification.setChecked(false);
              flag = "0";
          }

        if(end.equals("")){
            mdurationValue.setText("Lifetime");
            mDayText.setVisibility(View.GONE);
            duration="";
        }
        else{

            mdurationValue.setText(duration);
            mDayText.setVisibility(View.VISIBLE);
            duration=mdurationValue.getText().toString();
        }
        if((duration.equals("1"))||(duration.equals("2"))||(duration.equals("3"))||(duration.equals("4"))||(duration.equals("5"))||(duration.equals("6"))){
            mRepeatWeekly.setVisibility(View.GONE);
        }
        else{
            mRepeatWeekly.setVisibility(View.VISIBLE);
        }
          if(index.equals("1")){
              mDayIntervalCheck.setImageResource(R.drawable.button_2);
              rcount=1;
              once=true;
              daily=false;
              weekly=false;
              custom=false;
          }
          else  if(index.equals("2")){
              mDailyCheck.setImageResource(R.drawable.button_2);
              rcount=1;
              daily=true;
              once=false;
              weekly=false;
              custom=false;
          }
          else if(index.equals("3")){
              mWeeklyCheck.setImageResource(R.drawable.button_2);
              rcount=1;
              weekly=true;
              daily=false;
              once=false;
              custom=false;
          }
          else if(index.equals("4")){
              mCustomCheck.setImageResource(R.drawable.button_2);
              custumdaycount=1;
              custom=true;
              daily=false;
              weekly=false;
              once=false;
          }

        timer3=new ArrayList<>();
        dosage3=new ArrayList<>();
        dosageUnit3=new ArrayList<>();

        mEnableNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flag = "1";
                } else {
                    flag = "0";
                }
            }
        });

        if(!refillDay.equals("")){

            mRefillDayEdt.setText(refillDay+" Day(s");
        }
        mListAdd.setVisibility(View.GONE);
    }
    private void inflateEditRow(final int i) {

        mAdapter = new AddRowAdapter(this);
        mListAdd.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListAdd);


    }
    private void inflateEditRowClick(final int k) {
        ArrayTest te = new ArrayTest();
        te.setDosageQuantity("1");
        te.setTimesave("");
        te.setDosageUnit("");
        arrayListItems.add(te);
        mAdapter = new AddRowAdapter(this);
        mListAdd.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListAdd);


    }
    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void setDateField(){
        Calendar now = Calendar.getInstance();
      fromdate1 = new DatePickerDialog(getParent(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                dates = dayOfMonth + "-" + (++monthOfYear) + "-" + year;
                defdate=dayOfMonth;
                defmon=monthOfYear;
                defyear=year;
                String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                if(prefDateFormate.equals("MM-DD-YYYY")){
                    mDateSelectText.setText(month+"-"+day+"-"+year);
                }
                else if(prefDateFormate.equals("DD-MM-YYYY")){
                    mDateSelectText.setText(day+"-"+month+"-"+year);
                }
                else if(prefDateFormate.equals("YYYY-MM-DD")){
                    mDateSelectText.setText(year+"-"+month+"-"+day);
                }

                x = dates;

                try {
                    mystart = dateFormat.parse(x);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                fromdate1.dismiss();

            }

        },defyear,defmon-1,defdate);
        fromdate1.getDatePicker().setMinDate(now.getTimeInMillis());
        fromdate1.setCancelable(false);
        fromdate1.show();
    }

    private void wheelOpen(final String list[]) {
        final Dialog dialog = new Dialog(getParent());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.form_layout1);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        final WheelView country = (WheelView)dialog.findViewById(R.id.cityWheel);
        ImageView doneButton = (ImageView)dialog.findViewById(R.id.doneButton);
        ImageView cancel = (ImageView)dialog.findViewById(R.id.cancelButton);
        final TextView center_text = (TextView)dialog.findViewById(R.id.center_text);
        adapt = new FormAdapter(getParent(), formsOral);
        country.setViewAdapter(adapt);
        country.setVisibleItems(3);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(list==forms) {
                        mFormTextView.setText(formsOral[selected_value]);
                        dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selected_value = newValue;
              center_text.setText(formsOral[newValue]);
            }
        });
        country.setCurrentItem(1);
        dialog.show();

    }
    @Override
    public void onClick(View v) {
        if (Constants.buttonEnable) {
            Constants.setButtonEnable();
            switch (v.getId()) {
                case R.id.noti_close:
                    if(noticlose==0) {
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        //closing other layouts

                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);
                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }

                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }
                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            mScheduleLayout.startAnimation(slideUP);
                            rschedule = 0;
                        }
                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }
                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }
                        mnotification.setVisibility(View.VISIBLE);

                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);
                        mnotification.startAnimation(slideDown);

                        noticlose = 1;
                    }


                    else{
                        mnotification.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mnotification.startAnimation(slideUP);

                        noticlose=0;
                    }

                    break;
                case R.id.form_close:
                    if(formclose==0) {

                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        //closing other layouts
                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);

                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }

                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }
                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            mScheduleLayout.startAnimation(slideUP);
                            rschedule = 0;
                        }
                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }
                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }
                        mFormLayout.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        mFormLayout.startAnimation(slideDown);

                        mFormLayout.setClickable(true);
                        formclose=1;
                    }
                    else{
                        mFormLayout.setVisibility(View.GONE);

                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mFormLayout.startAnimation(slideUP);
                        mFormLayout.setClickable(false);
                        formclose=0;
                    }
                    break;

                case R.id.dosage_close:
                    if(dosageclose==0) {
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);

                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }
                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);

                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }


                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            mScheduleLayout.startAnimation(slideUP);
                            rschedule = 0;
                        }
                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }
                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }
                        mDrugDosage.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        mDrugDosage.startAnimation(slideDown);
                        mListAdd.setVisibility(View.VISIBLE);
                        mListAdd.startAnimation(slideDown);
                        mDrugDosage.setClickable(true);
                        dosageclose=1;
                    }
                    else{
                        mDrugDosage.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mDrugDosage.startAnimation(slideUP);
                        mListAdd.setVisibility(View.GONE);
                        mListAdd.startAnimation(slideUP);
                        mDrugDosage.setClickable(false);
                        dosageclose=0;
                    }
                    break;
                case R.id.refill_close:
                    if(refillclose==0) {

                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }
                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }

                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }
                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            mScheduleLayout.startAnimation(slideUP);
                            rschedule = 0;
                        }

                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }
                        mRefillDayChoiceLayout.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        mRefillDayChoiceLayout.startAnimation(slideDown);
                        refillclose=1;
                        mRefillDayChoiceLayout.setClickable(true);
                    }
                    else{
                        mRefillDayChoiceLayout.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mRefillDayChoiceLayout.startAnimation(slideUP);
                        mRefillDayChoiceLayout.setClickable(false);
                        refillclose=0;
                    }
                    break;
                case R.id.camera_layout:
                    if(cameraclose==0) {
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }
                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);

                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }

                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }
                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            mScheduleLayout.startAnimation(slideUP);
                            rschedule = 0;
                        }
                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }
                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        mCameraOpen.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        mCameraOpen.startAnimation(slideDown);
                        mCameraOpen.setClickable(true);
                        cameraclose=1;

                    }
                    else{
                        mCameraOpen.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mCameraOpen.startAnimation(slideUP);
                        mCameraOpen.setClickable(false);
                        cameraclose=0;
                    }
                    break;
                case R.id.form_layout:
                        wheelOpen(forms);
                    break;

                case R.id.refill_days_choice_layout:
                    LayoutInflater li2 = LayoutInflater.from(getParent());
                    View promptsView2 = li2.inflate(R.layout.duration, null);
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(getParent());
                    alertDialogBuilder2.setView(promptsView2);
                    final EditText userInput2 = (EditText) promptsView2.findViewById(R.id.editTextDialogUserInput);
                    final TextView plus2 = (TextView) promptsView2.findViewById(R.id.btn_plus);
                    final TextView minus2 = (TextView) promptsView2.findViewById(R.id.btn_minus);
                    final TextView titletext2 = (TextView) promptsView2.findViewById(R.id.textView1);
                    final LinearLayout linearLife2 = (LinearLayout) promptsView2.findViewById(R.id.lifetime_layout);
                    titletext2.setText("Set days for Refill");
                    // set dialog message
                    linearLife2.setVisibility(View.GONE);
                    if(!refillDay.equals("")){
                        userInput2.setText(refillDay);
                    }
                    else{
                        userInput2.setText("30");
                    }
                    plus2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((!userInput2.getText().toString().equals("1")) && (!userInput2.getText().toString().equals("0")) && (!userInput2.getText().toString().equals(""))) {
                                String count = userInput2.getText().toString();
                                int k = Integer.parseInt(count);
                                k++;
                                userInput2.setText(String.valueOf(k));
                            }
                        }
                    });
                    minus2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if ((!userInput2.getText().toString().equals("1")) && (!userInput2.getText().toString().equals("0")) && (!userInput2.getText().toString().equals(""))) {
                                String count = userInput2.getText().toString();
                                int k = Integer.parseInt(count);
                                k = k - 1;
                                userInput2.setText(String.valueOf(k));
                            }
                        }
                    });
                    alertDialogBuilder2.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            refillDay = userInput2.getText().toString().trim();
                            if((refillDay.equals("0"))||((refillDay.equals("00")))||((refillDay.equals("000")))||((refillDay.equals("0000")))||((refillDay.equals("00000")))||((refillDay.equals("000000")))||((refillDay.equals("0000000")))||((refillDay.equals("00000000")))||((refillDay.equals("000000000")))||((refillDay.equals("0000000000")))){
                                mRefillDayEdt.setText("1 Day");
                                refillDay="1";
                            }
                            else {
                                mRefillDayEdt.setText(refillDay + " Day(s)");
                            }


                        }
                    })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alertDialog2 = alertDialogBuilder2.create();
                    alertDialog2.show();
                    break;
                case R.id.add_drugs_save:
                    functag = 1;
                    selectionIndex = index;
                    start =dates;
                    drugNamee = mDrugNameEdt.getText().toString();
                    form1 = mFormTextView.getText().toString();
                   // duration = mdurationValue.getText().toString();

                    repeatValidate();
                    if (drugNamee.length() == 0) {
                        Constants.alert(getParent(), "Drug name is required. ");
                        functag=1;
                    }
                    else if (repeatInterval.equals("select")) {
                        Constants.alert(getParent(), "Please select interval for drug reminder. ");
                        timer3.clear();
                        dosageUnit3.clear();
                        functag=1;
                    }

                 else if (index.equals("")) {
                        Constants.alert(getParent(), "You should select atleast one day. ");
                        timer3.clear();
                        dosageUnit3.clear();
                        functag=1;
                    }
                    else if (index.equals("4") && !monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
                        Constants.alert(getParent(), "You should select atleast one day. ");
                        timer3.clear();
                        dosageUnit3.clear();
                        functag=1;
                    }
                    else{
                        functag=0;
                    }
                   if(functag==0) {
                        save();
                    }

                    break;
                case R.id.add_drugs_cancel:
                    onBackPressed();

                    break;
                case R.id.delete_button:
                    deleteDrug();
                    break;
                case R.id.camera_open:
                    tag = 1;
                    Intent intent = new Intent(getParent(), TranseperentCameraActiviy.class);
                    startActivity(intent);
                    break;
                case R.id.addDosageAndTimeLayout:
                        custumtimedose();
                    break;

                case R.id.date_range_layout:

                    LayoutInflater li = LayoutInflater.from(getParent());
                    View promptsView = li.inflate(R.layout.duration, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParent());
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    final TextView plus = (TextView) promptsView.findViewById(R.id.btn_plus);
                    final TextView minus = (TextView) promptsView.findViewById(R.id.btn_minus);
                    final TextView titletext = (TextView) promptsView.findViewById(R.id.textView1);
                    final LinearLayout lifetime_layout = (LinearLayout) promptsView.findViewById(R.id.lifetime_layout);
                    final LinearLayout duration_layout = (LinearLayout) promptsView.findViewById(R.id.duration_layout);
                    final ImageView lifetime_img = (ImageView) promptsView.findViewById(R.id.lifetime_img);
                    titletext.setText("Set duration for drug");
                    final View line = (View) promptsView.findViewById(R.id.line_down);
                    line.setVisibility(View.VISIBLE);
                    // set dialog message
                    if(mdurationValue.getText().toString().equals("Lifetime")){
                        userInput.setEnabled(false);
                        plus.setEnabled(false);
                        minus.setEnabled(false);
                        lifetime_img.setImageResource(R.drawable.button_2);
                        mDayText.setVisibility(View.GONE);
                        layout=1;
                    }
                    if(!duration.equals("")){
                        userInput.setEnabled(true);
                        userInput.setText(duration);
                        plus.setEnabled(true);
                        minus.setEnabled(true);
                        lifetime_img.setImageResource(R.drawable.button_1);
                        mDayText.setVisibility(View.VISIBLE);
                        layout=0;
                    }
                    else{
                        userInput.setEnabled(true);
                        userInput.setText("30");
                        plus.setEnabled(true);
                        minus.setEnabled(true);
                        lifetime_img.setImageResource(R.drawable.button_1);
                        mDayText.setVisibility(View.VISIBLE);
                        layout=0;
                    }


                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((!userInput.getText().toString().equals("1")) && (!userInput.getText().toString().equals("0")) && (!userInput.getText().toString().equals(""))) {
                                String count = userInput.getText().toString();
                                int k = Integer.parseInt(count);
                                k++;
                                userInput.setText(String.valueOf(k));
                            }
                        }
                    });
                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if ((!userInput.getText().toString().equals("1")) && (!userInput.getText().toString().equals("0")) && (!userInput.getText().toString().equals(""))) {
                                String count = userInput.getText().toString();
                                int k = Integer.parseInt(count);
                                k = k - 1;
                                userInput.setText(String.valueOf(k));
                            }

                        }
                    });
                    lifetime_layout.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            userInput.setEnabled(false);
                            plus.setEnabled(false);
                            minus.setEnabled(false);
                            lifetime_img.setImageResource(R.drawable.button_2);
                            layout=1;

                        }
                    });
                    duration_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout=0;
                            userInput.setEnabled(true);
                            plus.setEnabled(true);
                            minus.setEnabled(true);
                            lifetime_img.setImageResource(R.drawable.button_1);

                        }
                    });
                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(layout==0){

                                        duration=mdurationValue.getText().toString();

                                        if((userInput.getText().toString().equals("0"))||((userInput.getText().toString().equals("00")))||((userInput.getText().toString().equals("000")))||((userInput.getText().toString().equals("0000")))||((userInput.getText().toString().equals("00000")))||((userInput.getText().toString().equals("000000")))||((userInput.getText().toString().equals("0000000")))||((userInput.getText().toString().equals("00000000")))||((userInput.getText().toString().equals("000000000")))||((userInput.getText().toString().equals("0000000000")))){
                                            mdurationValue.setText("1");
                                            duration="1";
                                        }
                                        else {
                                            mdurationValue.setText(userInput.getText());
                                            duration=mdurationValue.getText().toString();
                                        }
                                        mDayText.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        mdurationValue.setText("Lifetime");
                                        mDayText.setVisibility(View.GONE);
                                        duration="";
                                    }

                                    if((duration.equals("1"))||(duration.equals("2"))||(duration.equals("3"))||(duration.equals("4"))||(duration.equals("5"))||(duration.equals("6"))){
                                        mRepeatWeekly.setVisibility(View.GONE);
                                    }
                                    else{
                                        mRepeatWeekly.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;

                case R.id.start_date_layout:
                    setDateField();
                    break;
                case R.id.schedule_close:
                    if (rschedule == 0) {
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);

                        //closing other layouts

                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }
                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }

                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }

                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }
                        if(rfood==1) {
                            mFoodLayout.setVisibility(View.GONE);
                            mFoodLayout.startAnimation(slideUP);
                            mFoodLayout.setClickable(false);
                            rfood = 0;
                        }
                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }

                        mScheduleLayout.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);
                        mScheduleLayout.startAnimation(slideDown);
                        mScheduleLayout.setClickable(true);
                        mStartss.setClickable(true);
                        rschedule = 1;

                    } else {
                        mScheduleLayout.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mScheduleLayout.startAnimation(slideUP);
                        mStartss.setClickable(false);
                        mScheduleLayout.setClickable(false);
                        rschedule = 0;
                    }
                    break;
                //cases for repeat layout
                case R.id.repeat_once:
                    once = true;daily = false;weekly = false;custom = false;
                    monday=false;tuesday=false;wednesday=false;thursday=false;friday=false;saturday=false;sunday=false;
                    LayoutInflater li1 = LayoutInflater.from(getParent());
                    View promptsView1 = li1.inflate(R.layout.duration, null);
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getParent());
                    alertDialogBuilder1.setView(promptsView1);
                    final EditText userInput1 = (EditText) promptsView1.findViewById(R.id.editTextDialogUserInput);
                    final TextView plus1 = (TextView) promptsView1.findViewById(R.id.btn_plus);
                    final TextView minus1 = (TextView) promptsView1.findViewById(R.id.btn_minus);
                    final TextView titletext1 = (TextView) promptsView1.findViewById(R.id.textView1);
                    final LinearLayout linearLife = (LinearLayout) promptsView1.findViewById(R.id.lifetime_layout);
                    titletext1.setText("Set interval for drug");
                    // set dialog message
                    linearLife.setVisibility(View.GONE);
                    if(!repeatInterval.equals("")){
                      userInput1.setText(repeatInterval);
                    }

                    plus1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if((!userInput1.getText().toString().equals("1"))&&(!userInput1.getText().toString().equals("0"))&&(!userInput1.getText().toString().equals(""))) {
                                String count = userInput1.getText().toString();
                                int k = Integer.parseInt(count);
                                k++;
                                userInput1.setText(String.valueOf(k));
                            }
                        }
                    });
                    minus1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if((!userInput1.getText().toString().equals("1"))&&(!userInput1.getText().toString().equals("0"))&&(!userInput1.getText().toString().equals(""))){
                                String count = userInput1.getText().toString();
                                int k = Integer.parseInt(count);
                                k = k - 1;
                                userInput1.setText(String.valueOf(k));
                            }
                        }
                    });
                    alertDialogBuilder1.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            repeatInterval=userInput1.getText().toString();
                            if((repeatInterval.equals("0"))||((repeatInterval.equals("00")))||((repeatInterval.equals("000")))||((repeatInterval.equals("0000")))||((repeatInterval.equals("00000")))||((repeatInterval.equals("000000")))||((repeatInterval.equals("0000000")))||((repeatInterval.equals("00000000")))||((repeatInterval.equals("000000000")))||((repeatInterval.equals("0000000000")))){
                                repeatInterval="1";
                            }
                            mdayInterval_Text.setText(repeatInterval+" Day(s)");
                            mdayInterval_Text.setVisibility(View.VISIBLE);

                        }
                    })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                    repeatInterval="1";
                                    mdayInterval_Text.setText("1 Day(s)");
                                    mdayInterval_Text.setVisibility(View.VISIBLE);

                                }
                            });

                    AlertDialog alertDialog1 = alertDialogBuilder1.create();
                    alertDialog1.show();
                    mDayIntervalCheck.setImageResource(R.drawable.button_2);
                    mDailyCheck.setImageResource(R.drawable.button_1);
                    mWeeklyCheck.setImageResource(R.drawable.button_1);
                    mCustomCheck.setImageResource(R.drawable.button_1);
                    custumdaycount = 0;
                    break;
                case R.id.repeat_daily:
                    once = false;daily = true;weekly = false;custom = false;
                    monday=false;tuesday=false;wednesday=false;thursday=false;friday=false;saturday=false;sunday=false;
                    mDayIntervalCheck.setImageResource(R.drawable.button_1);
                    mDailyCheck.setImageResource(R.drawable.button_2);
                    mWeeklyCheck.setImageResource(R.drawable.button_1);
                    mCustomCheck.setImageResource(R.drawable.button_1);
                    custumdaycount = 0;
                    repeatInterval="";
                    mdayInterval_Text.setVisibility(View.INVISIBLE);

                    break;
                case R.id.repeat_weekly:
                    once = false; daily = false;weekly = true;custom = false;
                    monday=false;tuesday=false;wednesday=false;thursday=false;friday=false;saturday=false;sunday=false;

                    mDayIntervalCheck.setImageResource(R.drawable.button_1);
                    mDailyCheck.setImageResource(R.drawable.button_1);
                    mWeeklyCheck.setImageResource(R.drawable.button_2);
                    mCustomCheck.setImageResource(R.drawable.button_1);
                    custumdaycount = 0;
                    repeatInterval="";
                    mdayInterval_Text.setVisibility(View.INVISIBLE);
                    break;
                case R.id.custom_days:
                    once = false;
                    daily = false;
                    weekly = false;
                    custom = true;

                    mDayIntervalCheck.setImageResource(R.drawable.button_1);
                    mDailyCheck.setImageResource(R.drawable.button_1);
                    mWeeklyCheck.setImageResource(R.drawable.button_1);
                    mCustomCheck.setImageResource(R.drawable.button_2);
                    repeatInterval="";
                    mdayInterval_Text.setVisibility(View.INVISIBLE);
                    //days dialog
                    custumdaydialog();
                    break;
                ////cases for food instruction
                case R.id.food_instruction:
                    if(rfood==0) {
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
                        //closing other layouts
                        if(noticlose==1) {
                            mnotification.setVisibility(View.GONE);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mnotification.startAnimation(slideUP);
                            noticlose = 0;
                        }
                        if(formclose==1) {
                            mFormLayout.setVisibility(View.GONE);
                            mFormLayout.setClickable(false);
                            slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                            mFormLayout.startAnimation(slideUP);
                            formclose = 0;
                        }
                        if(rschedule==1) {
                            mScheduleLayout.setVisibility(View.GONE);
                            mScheduleLayout.startAnimation(slideUP);
                            mScheduleLayout.setClickable(false);
                            mStartss.setClickable(false);
                            rschedule = 0;
                        }
                        if(dosageclose==1) {
                            mDrugDosage.setVisibility(View.GONE);
                            mDrugDosage.startAnimation(slideUP);
                            mListAdd.setVisibility(View.GONE);
                            mListAdd.startAnimation(slideUP);
                            mDrugDosage.setClickable(false);
                            dosageclose = 0;
                        }
                        if(refillclose==1) {
                            mRefillDayChoiceLayout.setVisibility(View.GONE);
                            mRefillDayChoiceLayout.startAnimation(slideUP);
                            mRefillDayChoiceLayout.setClickable(false);
                            refillclose = 0;
                        }

                        if(cameraclose==1) {
                            mCameraOpen.setVisibility(View.GONE);
                            mCameraOpen.startAnimation(slideUP);
                            mCameraOpen.setClickable(false);
                            cameraclose = 0;
                        }

                        mFoodLayout.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);
                        mFoodLayout.startAnimation(slideDown);
                        mFoodLayout.setClickable(true);
                        rfood=1;

                    }
                    else{
                        mFoodLayout.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        mFoodLayout.startAnimation(slideUP);
                        mFoodLayout.setClickable(false);
                        rfood=0;
                    }
                    break;
                case R.id.food_before_layout:
                    mBeforeCheck.setImageResource(R.drawable.button_2);
                    mAfterCheck.setImageResource(R.drawable.button_1);

                    aa=1;

                    break;
                case R.id.after_food_layout:
                    mBeforeCheck.setImageResource(R.drawable.button_1);
                    mAfterCheck.setImageResource(R.drawable.button_2);

                    aa=2;
                    break;
            }
        }
    }

    public void custumtimedose() {
        LayoutInflater li = LayoutInflater.from(getParent());
        View promptsView = li.inflate(R.layout.timedosedialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParent());
        alertDialogBuilder.setView(promptsView);
        final TextView titletext = (TextView) promptsView.findViewById(R.id.textView1);
        final TextView one = (TextView) promptsView.findViewById(R.id.once_day_txt);
        final TextView two = (TextView) promptsView.findViewById(R.id.twice_day_txt);
        final TextView three = (TextView) promptsView.findViewById(R.id.three_day_txt);
        final TextView four = (TextView) promptsView.findViewById(R.id.four_day_txt);
        final TextView five = (TextView) promptsView.findViewById(R.id.five_day_txt);
        final TextView six = (TextView) promptsView.findViewById(R.id.six_day_txt);
        titletext.setText("Set time and dosage : ");
        // set dialog message
        final AlertDialog alertDialog = alertDialogBuilder.create();

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "1";
                mDosageText.setText("One time");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "2";
                mDosageText.setText("Two times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "3";
                mDosageText.setText("Three times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "4";
                mDosageText.setText("Four times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "5";
                mDosageText.setText("Five times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "6";
                mDosageText.setText("Six times");

                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRowClick(k);
                }
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialog.show();
    }

    public void custumdaydialog(){
        LayoutInflater li = LayoutInflater.from(getParent());
        View dialog = li.inflate(R.layout.custom_day_select_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParent());

        alertDialogBuilder.setView(dialog);

        LinearLayout mRepeatDayMondaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_monday_select);
        LinearLayout mRepeatDayTuesdaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_tuesday_select);
        LinearLayout mRepeatDayWednesdaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_wednesday_select);
        LinearLayout mRepeatDayThursdaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_thursday_select);
        LinearLayout mRepeatDayFridaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_friday_select);
        LinearLayout mRepeatDaySaturdaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_saturday_select);
        LinearLayout mRepeatDaySundaySelect = (LinearLayout) dialog.findViewById(R.id.repeat_day_sunday_select);
        //binder for checkbox
        final ImageView mCheckMon = (ImageView) dialog.findViewById(R.id.chek_mon);
        final  ImageView mCheckTue = (ImageView) dialog.findViewById(R.id.chek_tue);
        final ImageView mCheckWed = (ImageView) dialog.findViewById(R.id.chek_wed);
        final  ImageView mCheckThus = (ImageView) dialog.findViewById(R.id.chek_thus);
        final ImageView mCheckFri = (ImageView) dialog.findViewById(R.id.chek_fri);
        final  ImageView mCheckSat = (ImageView) dialog.findViewById(R.id.chek_sat);
        final  ImageView mCheckSun = (ImageView) dialog.findViewById(R.id.chek_sun);

        final TextView titletext1 = (TextView) dialog.findViewById(R.id.textView1);
        titletext1.setText("Set interval for drug : ");
        if(monday){
            mCheckMon.setImageResource(R.drawable.checked);
        }
        if(tuesday){
            mCheckTue.setImageResource(R.drawable.checked);
        }
        if(wednesday){
            mCheckWed.setImageResource(R.drawable.checked);
        }
        if(thursday){
            mCheckThus.setImageResource(R.drawable.checked);
        }
        if(friday){
            mCheckFri.setImageResource(R.drawable.checked);
        }
        if(saturday){
            mCheckSat.setImageResource(R.drawable.checked);
        }
        if(sunday){
            mCheckSun.setImageResource(R.drawable.checked);
        }
        ///click listners///
        mRepeatDayMondaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!monday) {
                    mCheckMon.setImageResource(R.drawable.checked);
                    monday = true;
                } else {
                    mCheckMon.setImageResource(R.drawable.checkbox);
                    monday = false;
                }
            }
        });
        mRepeatDayTuesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tuesday) {
                    mCheckTue.setImageResource(R.drawable.checked);
                    tuesday = true;
                } else {
                    mCheckTue.setImageResource(R.drawable.checkbox);
                    tuesday = false;
                }
            }
        });
        mRepeatDayWednesdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wednesday) {
                    mCheckWed.setImageResource(R.drawable.checked);
                    wednesday = true;
                } else {
                    mCheckWed.setImageResource(R.drawable.checkbox);
                    wednesday = false;
                }
            }
        });
        mRepeatDayThursdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thursday) {
                    mCheckThus.setImageResource(R.drawable.checked);
                    thursday = true;
                } else {
                    mCheckThus.setImageResource(R.drawable.checkbox);
                    thursday = false;
                }
            }
        });
        mRepeatDayFridaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!friday) {
                    mCheckFri.setImageResource(R.drawable.checked);
                    friday = true;
                } else {
                    mCheckFri.setImageResource(R.drawable.checkbox);
                    friday = false;
                }
            }
        });
        mRepeatDaySaturdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!saturday) {
                    mCheckSat.setImageResource(R.drawable.checked);
                    saturday = true;
                } else {
                    mCheckSat.setImageResource(R.drawable.checkbox);
                    saturday = false;
                }
            }
        });
        mRepeatDaySundaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sunday) {
                    mCheckSun.setImageResource(R.drawable.checked);
                    sunday = true;
                } else {
                    mCheckSun.setImageResource(R.drawable.checkbox);
                    sunday = false;
                }
            }
        });
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

    private void deleteDrug() {
            final Dialog dialog = new Dialog(getParent());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_confirm_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView text = (TextView) dialog.findViewById(R.id.textView1);
            Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
            Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
            text.setText("Do you want to delete the Drug Reminder?");
            dialog.show();
            buttonyes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new Delete().from(DrugsListTable.class).where("Id = ?", drugId).execute();

                        new Delete().from(NotificationTable.class).where("_drugName = ?", drugName).execute();
                        NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manaager.cancelAll();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                   onBackPressed();
                }

            });
            buttoncancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }


    public void save() {
        remindertag=0;
         aaa = mFoodInstEdit.getText().toString();
        if ((aa==2)){
            foodInstruction = "After Meal:"+"" + aaa;
        }
        else if ((aa==1)){
            foodInstruction = "Before Meal:"+"" + aaa;
        }



        if(!duration.equals("")){
            Calendar now=Calendar.getInstance();
            try {
                mystart = dateFormat.parse(x);
                now.setTime(mystart);
                now.add(Calendar.DATE, Integer.parseInt(duration));  // number of days to add
                String dt = dateFormat.format(now.getTime());  // dt is now the new date
                end = dt;

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        else if(duration.equals("")){
            end = "";
        }
        alarmTimeRepeat = String.valueOf(arrayListItems.size());
        quantity = String.valueOf(arrayListItems.size());
        for (int j = 0; j <arrayListItems.size(); j++) {
            dosage3.add(arrayListItems.get(j).getDosageQuantity());
            timer3.add(arrayListItems.get(j).getTimesave());
            dosageUnit3.add(arrayListItems.get(j).getDosageUnit());

            tag = 0;

            if (arrayListItems.get(j).getTimesave().equals("")) {
                Constants.alert(getParent(), "Please enter time. ");
                dosage3.clear();
                timer3.clear();
                dosageUnit3.clear();
                break;
            } else if (arrayListItems.get(j).getDosageQuantity().equals("")) {
                Constants.alert(getParent(), "Please enter dosage. ");
                dosage3.clear();
                timer3.clear();
                dosageUnit3.clear();
                break;
            } else {
                remindertag = 1;
            }
        }
        if(remindertag==1)
              {
            try {
                if (!Constants.isMyServiceRunning(AlarmService.class, this)) {
                    Intent intent = new Intent(EditDrugActivity.this, AlarmService.class);
                    startService(intent);
                }
         DrugsListTable item=new Select().from(DrugsListTable.class).where("Id= ?",drugId).executeSingle();
                item.drugName=drugNamee;
                item.form=form1;
                item.doctorName=refillDay;
                item.pharmacyName=foodInstruction;
                item.drugImage=mCurrentPhotoPath;
                item.smallDrugImage=smallImage;
                item.dosage = dosage3.toString().replace("[", "").replace("]", "");
                item.doNotify = flag;
                item.endDate = end;
                item.startDate = start;
                item.index = index;
                item.repeat = duration;
                item.repeatDate = repeatDate;
                item.repeatDay = repeatDay.toString();
                item.time =  timer3.toString().replace("[", "").replace("]", "");
                item.quantity = quantity;
                item.alarmTimeRepeat = alarmTimeRepeat;
                item.dosageUnit=dosageUnit3.toString().replace("[", "").replace("]", "");
                item.rx_Number=repeatInterval.toString().trim();
                   item.save();

                List<HistoryListTable> values = new Select() .all().from(HistoryListTable.class).execute();
                if(values.size()!=0) {
                    for (HistoryListTable v : values) {
                        if (v.drugName.equals(drugName)) {
                                v.drugName = drugNamee;

                                v.save();
                        }
                    }
                }
                List<NotificationTable> notvalues = new Select() .all().from(NotificationTable.class).execute();
                if(notvalues.size()!=0) {
                    for (NotificationTable v : notvalues) {
                        if (v.drugName.equals(drugName)) {
                            v.drugName = drugNamee;
                            v.drugForm=form;
                            v.save();
                        }
                    }
                }
                NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manaager.cancelAll();
                onBackPressed();
                Log.e(VirtualNurse.TAG,
                        "success");


            } catch (NumberFormatException e) {
                Log.e(VirtualNurse.TAG,
                "Error while converting String to Integer");
            }

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(tag==1) {
            mImgPrev.setVisibility(View.VISIBLE);
            mCurrentPhotoPath = PreferenceConnector.readString(getParent(), "imagepath", "");
            if (mCurrentPhotoPath == "") {
                mCurrentPhotoPath = "";
                smallImage = "";
            }
            else {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/saved_images");
                myDir.mkdirs();
                String fname = "JPEG_" + "_";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    if(new File(mCurrentPhotoPath).exists()) {
                        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);
                        //  bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        mImgPrev.setImageBitmap(Bitmap.createScaledBitmap(bitmap1, swidth, sheight, false));
                        Bitmap b2 = bitmap1;
                        Bitmap z = Bitmap.createScaledBitmap(b2, w, h, false);
                        FileOutputStream out = new FileOutputStream(file);
                        z.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                smallImage = file.getAbsolutePath();
            }
        }
    }
    public void repeatValidate() {
        if (once) {
            index = "1";
            repeatDate =dates;
            repeatDay.append("");

        }
        if (daily) {
            index = "2";
            repeatDate = dates;
            repeatDay.append("");


        }
        if (weekly) {
            index = "3";
            repeatDate =dates;
            repeatDay.append("");

        }
        if(custom){
            index="4";
            repeatDate=dates;

            if (monday) {
                repeatDay.append("1");
                repeatDay.append(",");
            }
            if (tuesday) {
                repeatDay.append("2");
                repeatDay.append(",");
            }
            if (wednesday) {
                repeatDay.append("3");
                repeatDay.append(",");

            }
            if (thursday) {
                repeatDay.append("4");
                repeatDay.append(",");

            }
            if (friday) {
                repeatDay.append("5");
                repeatDay.append(",");

            }
            if (saturday) {
                repeatDay.append("6");
                repeatDay.append(",");
            }
            if (sunday) {
                repeatDay.append("7");
            }
        }


    }

    private class FormAdapter extends AbstractWheelTextAdapter {
        String[] forms;

        protected FormAdapter(Context context, String[] countries) {
            super(context, R.layout.form_item_layout, NO_RESOURCE);
            this.forms = countries;
            setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            TextView img = (TextView) view.findViewById(R.id.country_name);
            img.setText(forms[index]);
            return view;
        }

        @Override
        public int getItemsCount() {
            return forms.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return forms[index];
        }
    }
    private class AddRowAdapter extends BaseAdapter {
        Context context;
        ViewHolder1 holder1=null;
        final ViewHolder1 finalHolder1 = holder1;

        public AddRowAdapter(Context context) {
            this.context = context;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if (arrayListItems != null && !arrayListItems.isEmpty()) {
                return arrayListItems.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (arrayListItems != null && !arrayListItems.isEmpty()) {
                return arrayListItems.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            View row=convertView;
            row = mInflater.inflate(R.layout.row, parent, false);
            holder1 = new ViewHolder1(row);
            if(mFormTextView.getText().toString().equals("Capsule")){
                holder1.drugdosagequantitytext.setText("Capsule");

            }else if (mFormTextView.getText().toString().equals("Tablet")) {
                holder1.drugdosagequantitytext.setText("Tablet");

            }

            else if ((mFormTextView.getText().toString().equals("Syrup"))||((mFormTextView.getText().toString().equals("Powder")))){
                holder1.drugdosagequantitytext.setText("T.S");

            }
            else if ((mFormTextView.getText().toString().equals("Ear Drop"))||(mFormTextView.getText().toString().equals("Eye Drop"))) {
                holder1.drugdosagequantitytext.setText("Drop");

            }
            else if ((mFormTextView.getText().toString().equals("Toothpaste"))||(mFormTextView.getText().toString().equals("Ointment"))) {
                holder1.drugdosagequantitytext.setText("gm");
            }
            try {

                holder1.drugDoseEdit.setText(arrayListItems.get(i).getDosageQuantity());
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                final Date dateObj = sdf.parse(arrayListItems.get(i).getTimesave());
                holder1.timeselect.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());


            }catch (ArrayIndexOutOfBoundsException d){

            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            //for dosage unit

            ArrayTest test = arrayListItems.get(i);
            test.setDosageUnit(holder1.drugdosagequantitytext.getText().toString());

            holder1.timeselectrel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    mytime1 = new TimePickerDialog(getParent(), new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker,int hourOfDay, int minute) {
                            hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                            minuteString = minute < 10 ? "0" + minute : "" + minute;
                            time9=hourString + ":" + minuteString;
                            ArrayTest test= arrayListItems.get(i);
                            test.setTimesave(hourString + ":" + minuteString);
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                Date dateObj = sdf.parse(arrayListItems.get(i).getTimesave());
                                String aaas=new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase();
                                String[] str_array = aaas.split(":");
                                String stringa = str_array[0];
                                String  stringb = str_array[1];
                                if(stringa.equals("0")){
                                    stringa="12";
                                    holder1.timeselect.setText(stringa+":"+stringb);
                                }
                                else {
                                    holder1.timeselect.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());
                                }
                                notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE), false
                    );
                    mytime1.show();
                }
            });
            try {
                SimpleDateFormat df = new SimpleDateFormat("H:mm");
                Date dateObj = df.parse(arrayListItems.get(i).getTimesave());
                String aaas=new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase();
                String[] str_array = aaas.split(":");
                String stringa = str_array[0];
                String  stringb = str_array[1];
                if(stringa.equals("0")){
                    stringa="12";
                    holder1.timeselect.setText(stringa+":"+stringb);
                }
                else {
                    holder1.timeselect.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());
                }
                // notify();
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder1.drugDoseEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    ArrayTest test2 = arrayListItems.get(i);
                    test2.setDosageQuantity(s.toString());
                }
            });
            holder1.drugDoseEdit.setText(arrayListItems.get(i).getDosageQuantity());
            return row;
        }
    }
    class ViewHolder1 {
        TextView timeselect;
        TextView drugdosagequantitytext;
        RelativeLayout timeselectrel;
        EditText drugDoseEdit;


        public ViewHolder1(View view) {
            // TODO Auto-generated constructor stub
            timeselect = (TextView) view.findViewById(R.id.add_time_text);
            drugdosagequantitytext = (TextView) view.findViewById(R.id.drug_dosage_text);
            drugDoseEdit = (EditText) view.findViewById(R.id.drug_dosage_edit);
            timeselectrel= (RelativeLayout) view.findViewById(R.id.reminder_time_layout);
        }
    }
}
