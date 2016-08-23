package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kyleduo.switchbutton.SwitchButton;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.DrugsListTable;
import com.widevision.pillreminder.model.HideKeyFragment;
import com.widevision.pillreminder.showcaseview.OnShowcaseEventListener;
import com.widevision.pillreminder.showcaseview.ShowcaseView;
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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddDrugsActivity extends HideKeyFragment implements View.OnClickListener {
    private LayoutInflater mInflater;
    @Bind(R.id.add_drugs_cancel)ImageView mCancelI;
    @Bind(R.id.add_drugs_save)ImageView mSaveI;
    @Bind(R.id.drugName)AutoCompleteTextView mDrugNameEdt;
    @Bind(R.id.form_text)TextView mFormTextView;
    @Bind(R.id.imgPreview)ImageView mImgPrev;
    @Bind(R.id.drug_text)TextView mdrugTextView;
    @Bind(R.id.form_layout)RelativeLayout mFormLayout;
    @Bind(R.id.camera_layout)RelativeLayout mCameraLayout;
    @Bind(R.id.drug_name_layout)LinearLayout mDrugNameLayout;
    /////////////////////        AddReminder    ///////////////////////////////////////
    @Bind(R.id.enable_notification)SwitchButton mEnableNotification;
    @Bind(R.id.date_select_text)TextView mDateSelectText;
    //binders for repeat layout

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
    @Bind(R.id.editno)TextView mDosageText;
    @Bind(R.id.scroll)ScrollView scroll;

    String drugName, form, rxnum = "", smallImage = "",remindRefilDay="",aaa="" ;
    int tag = 0;
    String forms[],formsOral[];
    private FormAdapter adapt;
    private String mCurrentPhotoPath = "";
    Bitmap bitmap1;
    int h, w, sheight, swidth;
    private  ShowcaseView showcaseView;
///////walkthrough int//////
    int shonot=0,shoform=0,shoschedul=0,shotime=0,shorefill=0,shofood=0,shocam=0;
    /////////////////////////////////////variable declaration for Add Reminder///////////////////////////////////////////
    SimpleDateFormat sdf;
    private DatePickerDialog fromdate;
    private TimePickerDialog mytime;
    private AddRowAdapter mAdapter;
    ArrayList<ArrayTest> arrayListItems;
    StringBuilder repeatDay = new StringBuilder();
    String repeatText = "", start, end, index = "", repeatDate, quantity, alarmTimeRepeat, reminderDrugName = "",foodInst="",refilltimeUpdate="",flag = "1",hourString = "", minuteString = "", x = "", reminderDrugImage = "", reminderDrugForm = "",duration,dates="";
    int layout=0,noticlose=0,formclose=0,dosageclose=0,refillclose=0,cameraclose=0;
     String prefDateFormate="";
    //boolean value for day and week
    public boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday, once, daily, weekly, custom = false;
     Date mystart=new Date();
    List<String> timer, dosageQuantity, dosageUnitArr;
    int selected_value,custumdaycount = 0, i, remindertag = 0,rschedule=0,functag=0,rfood=0,aa=0;
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
        mSaveI.setOnClickListener(this);
        mCancelI.setOnClickListener(this);

        ///////////////////////////////////////////   listner for Add reminder     ////////////////////////////////////
        mEnableNotification.setChecked(true);

        //listner for repeat layout
        mRepeatOnce.setOnClickListener(this);
        mRepeatDaily.setOnClickListener(this);
        mRepeatWeekly.setOnClickListener(this);
        mCustomDays.setOnClickListener(this);

        mBeforeCheck.setImageResource(R.drawable.button_1);
        mAfterCheck.setImageResource(R.drawable.button_1);
       // refill listners//
        mDateRange.setOnClickListener(this);
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
        mSchedule.setOnClickListener(this);
        mRefillDayChoiceLayout.setOnClickListener(this);
        mDailyCheck.setImageResource(R.drawable.button_2);
        mRepeatWeekly.setVisibility(View.VISIBLE);

        daily=true;

        Calendar nows = Calendar.getInstance();
        defdate=nows.get(Calendar.DATE);
        defmon=nows.get(Calendar.MONTH);
        defyear=nows.get(Calendar.YEAR);

        mDrugNameEdt.setAdapter(new SuggetionAdapter(getParent(),mDrugNameEdt.getText().toString()));
        mDrugNameEdt.setAdapter(new SuggetionAdapter(getParent(),mDrugNameEdt.getText().toString()));

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

       Calendar now = Calendar.getInstance();
       int yy = now.get(Calendar.YEAR);
       int mm = now.get(Calendar.MONTH);
       int dd = now.get(Calendar.DAY_OF_MONTH);

        dates = dd + "-" + (++mm) + "-" + yy;
        String month = mm < 10 ? "0" + mm : "" + mm;
        String day = dd < 10 ? "0" + dd : "" + dd;
        prefDateFormate=PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        if(prefDateFormate.equals("MM-DD-YYYY")){
         mDateSelectText.setText(month+"-"+day+"-"+yy);
        }
       else if(prefDateFormate.equals("YYYY-MM-DD")){
            mDateSelectText.setText(yy+"-"+month+"-"+day);
        }
        else if(prefDateFormate.equals("DD-MM-YYYY")){
            mDateSelectText.setText(day+"-"+month+"-"+yy);
        }

        x = dates;
        mdrugTextView.setText("Add Drug Reminder");
        setupUI(findViewById(R.id.root));
        formsOral = new String[]{"Tablet","Capsule","Syrup","Powder","Ear Drop","Eye Drop","Ointment", "Toothpaste"};
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
       // mDrugNameEdt.setOnEditorActionListener(editorListener);

        timer = new ArrayList<>();
        dosageUnitArr = new ArrayList<>();
        dosageQuantity = new ArrayList<>();
        arrayListItems = new ArrayList<>();
       if (PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_NAME, "No").equals("No")) {
            showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.ADD_DRUG_NAME, R.id.drugName,R.layout.ok_button, "", getParent().getResources().getString(R.string.drug_name), R.style.CustomShowcaseTheme, false, listener);
        }

    }
    private void wheelOpen(final String list[]) {
        final Dialog dialog = new Dialog(getParent());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.form_layout1);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final WheelView country = (WheelView) dialog.findViewById(R.id.cityWheel);
        ImageView doneButton = (ImageView) dialog.findViewById(R.id.doneButton);
        ImageView cancel = (ImageView) dialog.findViewById(R.id.cancelButton);
        final TextView center_text = (TextView) dialog.findViewById(R.id.center_text);
        if(list==forms){
         adapt = new FormAdapter(getParent(), formsOral);
        }
        country.setViewAdapter(adapt);
        country.setVisibleItems(3);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list==forms){
                    mFormTextView.setText(formsOral[selected_value]);
                    dialog.dismiss();}
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

    ////////////////////////////////////// Add Reminder Methods///////////////////////////
    private void setDateField() {
        Calendar now = Calendar.getInstance();

        fromdate = new DatePickerDialog(getParent(), new DatePickerDialog.OnDateSetListener() {
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
               fromdate.dismiss();
            }

        }, defyear,defmon-1,defdate);
        fromdate.getDatePicker().setMinDate(now.getTimeInMillis());
        fromdate.setCancelable(false);
        fromdate.show();
    }
    //adding multiple alarm and dosage
    private void inflateEditRow(int i) {
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


    @Override
    public void onClick(View v) {

        if (Constants.buttonEnable) {
            Constants.setButtonEnable();
            switch (v.getId()) {
////////////////////////////////////////////////////new cases////////////////////////////////////
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
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
                        mnotification.startAnimation(slideDown);
                        noticlose = 1;
                    }
                    else{
                        mnotification.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
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
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
                         mFormLayout.startAnimation(slideDown);
                        mFormLayout.setClickable(true);
                        formclose=1;
                    }
                    else{
                        mFormLayout.setVisibility(View.GONE);
                        slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
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
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
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
                case R.id.addDosageAndTimeLayout:

                    if (mDrugNameEdt.getText().equals("")) {
                        Constants.alert(getParent(), "Please enter a drug first.");
                    } else if (mFormTextView.getText().equals("Select Form")) {
                        Constants.alert(getParent(), "Please select drug form.");
                    } else {
                        custumtimedose();
                    }
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
                    final View line = (View) promptsView.findViewById(R.id.line_down);
                    titletext.setText("Set Duration For Drug");
                    line.setVisibility(View.VISIBLE);
                    if(mdurationValue.getText().toString().equals("Select")){
                        userInput.setText("30");
                    }
                    // set dialog message
                   else if(mdurationValue.getText().toString().equals("Lifetime")){
                        userInput.setEnabled(false);
                        plus.setEnabled(false);
                        minus.setEnabled(false);
                        mDayText.setVisibility(View.GONE);
                        lifetime_img.setImageResource(R.drawable.button_2);
                        layout=1;
                }
                    else{
                        userInput.setText(duration);
                        userInput.setEnabled(true);
                        plus.setEnabled(true);
                        minus.setEnabled(true);
                        lifetime_img.setImageResource(R.drawable.button_1);
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
                        }}
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

                                        duration=userInput.getText().toString();

                                        if((duration.equals("0"))||((duration.equals("00")))||((duration.equals("000")))||((duration.equals("0000")))||((duration.equals("00000")))||((duration.equals("000000")))||((duration.equals("0000000")))||((duration.equals("00000000")))||((duration.equals("000000000")))||((duration.equals("0000000000")))){
                                            mdurationValue.setText("1");
                                            duration="1";
                                        }
                                        else {
                                            mdurationValue.setText(userInput.getText());
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
                case R.id.add_drugs_save:
                    functag = 1;

                    drugName = mDrugNameEdt.getText().toString();
                    form = mFormTextView.getText().toString();
                    //for reminder
                    reminderDrugName = mDrugNameEdt.getText().toString();
                    reminderDrugForm = mFormTextView.getText().toString();
                    start = dates.toString().trim();
                    reminderDrugImage = mCurrentPhotoPath.toString().trim();
                    //for refill
                    repeatText = duration;
                    aaa = mFoodInstEdit.getText().toString();
                    if ((aa==2)){
                        foodInst = "After Meal:"+""+ aaa;
            }
                   else if ((aa==1)){
                        foodInst = "Before Meal:"+""+ aaa;
                    }
                   else if(!aaa.equals("")){
                        foodInst = ":" +""+ aaa;
                    }

                    repeatValidate();
                    if (drugName.length() == 0) {
                        Constants.alert(getParent(), "Drug name is required. ");
                        functag=1;
                    } else if (form.equals("Select Form")) {
                        Constants.alert(getParent(), "Form is required. ");
                        functag=1;
                    } else if (start.equals("Select")) {
                        Constants.alert(getParent(), "Start date is required. ");
                        timer.clear();
                        dosageQuantity.clear();
                        dosageUnitArr.clear();
                        functag=1;
                    }
                    else if(mdurationValue.getText().toString().equals("Select")){
                        Constants.alert(getParent(), "Please select duration for drug reminder. ");
                        timer.clear();
                        dosageQuantity.clear();
                        dosageUnitArr.clear();
                        functag=1;
                    }
                    else if (index.equals("4") && !monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
                        Constants.alert(getParent(), "You should select atleast one day. ");
                        timer.clear();
                        dosageQuantity.clear();
                        dosageUnitArr.clear();
                        functag=1;
                    }


                    else if (arrayListItems.size() == 0) {
                        Constants.alert(getParent(), "Please enter dosage and Time. ");
                        functag=1;
                    }
                    else if(rxnum.equals("select")){
                        Constants.alert(getParent(), "Please select interval for drug reminder. ");
                        timer.clear();
                        dosageQuantity.clear();
                        dosageUnitArr.clear();
                        functag=1;
                    }
                    else if(!duration.equals("")){
                        Calendar now=Calendar.getInstance();
                        try {
                            mystart = dateFormat.parse(x);
                            now.setTime(mystart);
                            now.add(Calendar.DATE, Integer.parseInt(duration));  // number of days to add
                            String dt = dateFormat.format(now.getTime());  // dt is now the new date
                            end = dt;
                            functag=0;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    else if(duration.equals("")){
                        end = "";
                        functag=0;
                    }
                    if(functag==0) {
                        save();
                    }
                    break;

                case R.id.add_drugs_cancel:
                    onBackPressed();
                    break;
                case R.id.camera_open:

                    tag = 1;
                    Intent intent = new Intent(getParent(), TranseperentCameraActiviy.class);
                    startActivity(intent);
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
                    titletext2.setText("Set Days For Refill");
                    if(mRefillDayEdt.getText().toString().equals("Select")){
                        userInput2.setText("30");
                    }
                    else{
                        userInput2.setText(remindRefilDay);
                    }
                    // set dialog message
                    linearLife2.setVisibility(View.GONE);
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
                            remindRefilDay = userInput2.getText().toString().trim();
                            if((remindRefilDay.equals("0"))||((remindRefilDay.equals("00")))||((remindRefilDay.equals("000")))||((remindRefilDay.equals("0000")))||((remindRefilDay.equals("00000")))||((remindRefilDay.equals("000000")))||((remindRefilDay.equals("0000000")))||((remindRefilDay.equals("00000000")))||((remindRefilDay.equals("000000000")))||((remindRefilDay.equals("0000000000")))){
                                mRefillDayEdt.setText("1 Day");
                                remindRefilDay="1";
                            }
                            else {
                                mRefillDayEdt.setText(remindRefilDay + " Day(s)");
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

                ///////////////  case for Add Reminder ///////////////////////

                case R.id.start_date_layout:
                    setDateField();
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
                    if(rxnum.equals("")){
                        userInput1.setText("1");
                        mdayInterval_Text.setText("1 Day(s)");
                        mdayInterval_Text.setVisibility(View.VISIBLE);
                    }
                    else{
                        userInput1.setText(rxnum);
                        mdayInterval_Text.setText(rxnum+" Day(s)");
                        mdayInterval_Text.setVisibility(View.VISIBLE);

                    }
                    // set dialog message
                    linearLife.setVisibility(View.GONE);
                    plus1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if((!userInput1.getText().toString().equals("1"))&&(!userInput1.getText().toString().equals("0"))&&(!userInput1.getText().toString().equals(""))){
                            String count= userInput1.getText().toString();
                            int k = Integer.parseInt(count);
                            k++;
                            userInput1.setText(String.valueOf(k));

                        }}
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
                                    rxnum=userInput1.getText().toString();
                                    if((rxnum.equals("0"))||((rxnum.equals("00")))||((rxnum.equals("000")))||((rxnum.equals("0000")))||((rxnum.equals("00000")))||((rxnum.equals("000000")))||((rxnum.equals("0000000")))||((rxnum.equals("00000000")))||((rxnum.equals("000000000")))||((rxnum.equals("0000000000")))){
                                        rxnum="1";
                                    }
                                    mdayInterval_Text.setText(rxnum+" Day(s)");
                                    mdayInterval_Text.setVisibility(View.VISIBLE);

                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            rxnum="1";
                                            mdayInterval_Text.setText(rxnum+" Day(s)");
                                            mdayInterval_Text.setVisibility(View.VISIBLE);
                                            dialog.cancel();
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
                    mdayInterval_Text.setVisibility(View.INVISIBLE);
                    rxnum="";
                    break;
                case R.id.repeat_weekly:
                    once = false; daily = false;weekly = true;custom = false;
                    monday=false;tuesday=false;wednesday=false;thursday=false;friday=false;saturday=false;sunday=false;

                    mDayIntervalCheck.setImageResource(R.drawable.button_1);
                    mDailyCheck.setImageResource(R.drawable.button_1);
                    mWeeklyCheck.setImageResource(R.drawable.button_2);
                    mCustomCheck.setImageResource(R.drawable.button_1);
                    custumdaycount = 0;
                    mdayInterval_Text.setVisibility(View.INVISIBLE);
                    rxnum="";
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
                    //days dialog
                    custumdaydialog();
                    mdayInterval_Text.setVisibility(View.INVISIBLE);
                    rxnum="";
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
        titletext.setText("Set time and dosage");
        // set dialog message
        final AlertDialog alertDialog = alertDialogBuilder.create();

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "1";
                alarmTimeRepeat =quantity;
                mDosageText.setText("One time");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
                }
               alertDialog.dismiss();
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "2";
                alarmTimeRepeat =quantity;
                mDosageText.setText("Two times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
                }
                alertDialog.dismiss();
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "3";
                alarmTimeRepeat =quantity;
                mDosageText.setText("Three times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
                }
                alertDialog.dismiss();
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "4";
                alarmTimeRepeat =quantity;
                mDosageText.setText("Four times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
                }
                alertDialog.dismiss();
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "5";
                alarmTimeRepeat =quantity;
                mDosageText.setText("Five times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
                }
                alertDialog.dismiss();
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListItems = new ArrayList<>();
                quantity= "6";
                alarmTimeRepeat =quantity;
                mDosageText.setText("Six times");
                try{
                    i= Integer.parseInt(quantity);
                }
                catch (NumberFormatException e){}
                for(int k=0;k<i;k++) {
                    inflateEditRow(k);
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

    public void save() {

        remindertag = 0;

        for (int i = 0; i < arrayListItems.size(); i++) {
            timer.add(arrayListItems.get(i).getTimesave());
            dosageQuantity.add(arrayListItems.get(i).getDosageQuantity());
            dosageUnitArr.add(arrayListItems.get(i).getDosageUnit());
            remindertag = 0;
            if (arrayListItems.get(i).getTimesave().equals("")) {
                Constants.alert(getParent(), "Please enter time. ");
                timer.clear();
                dosageQuantity.clear();
                dosageUnitArr.clear();
                break;
            } else if (arrayListItems.get(i).getDosageQuantity().equals("")) {
                Constants.alert(getParent(), "Please enter dosage. ");
                timer.clear();
                dosageQuantity.clear();
                dosageUnitArr.clear();
                break;
            } else {
                remindertag = 1;
            }
        }
        if (remindertag == 1) {
            try {
                if (!Constants.isMyServiceRunning(AlarmService.class, this)) {
                    Intent intent = new Intent(AddDrugsActivity.this, AlarmService.class);
                    startService(intent);
                }
                DrugsListTable drugDetails = new DrugsListTable(
                        drugName,
                        form,
                        rxnum,
                        remindRefilDay,
                        foodInst,
                        mCurrentPhotoPath.toString(),
                        smallImage.toString(),
                        flag.toString().trim(),
                        repeatText,
                        index.toString().trim(),
                        repeatDate.toString().trim(),
                        repeatDay.toString().trim(),
                        start,
                        end,
                        dosageQuantity.toString().replace("[", "").replace("]", ""),
                        timer.toString().replace("[", "").replace("]", ""),
                        quantity,
                        alarmTimeRepeat.toString().trim(),
                        dosageUnitArr.toString().replace("[", "").replace("]", ""),
                        refilltimeUpdate.trim()

                );
                drugDetails.save();

                Intent in = new Intent(getParent(), DrugsActivity.class);
                TabGroupActivity parentAct = (TabGroupActivity) getParent();
                parentAct.startChildActivity("drug6", in);
            } catch (NumberFormatException e) {

            }

        }

    }

    public void repeatValidate() {

        if (once) {
            index = "1";
            repeatDay.append("");
            repeatDate = dates;

        }
       else if(daily) {
            index = "2";
            repeatDate = dates;
            repeatDay.append("");
        }
        else if(weekly) {
            index = "3";
            repeatDate = dates;
            repeatDay.append("");

        }
        else if(custom) {
            index = "4";
            repeatDate = dates;

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


    private OnShowcaseEventListener listener = new OnShowcaseEventListener() {
        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView1, int a) {
            if (a ==0 ){
                //ok click

            }else if (a ==1){
                //skip click
                mDrugNameLayout.setClickable(true);
                mDrugNameEdt.setFocusable(true);

                    mNotiClose.setClickable(true);
                    mFormCLose.setClickable(true);
                    mSchedule.setClickable(true);
                    mDosageClose.setClickable(true);
                    mRefillClose.setClickable(true);
                    mFoodInstruction.setClickable(true);
                    mCameraLayout.setClickable(true);
                    Constants.WALKTHROUGH_ITEM_CLICK = 1;
            }

            if (Constants.WALKTHROUGH_ITEM_CLICK == 0) {

                if (PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_NAME, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.ADD_DRUG_NAME, R.id.drug_name_layout,R.layout.ok_button, "", getParent().getResources().getString(R.string.drug_name), R.style.CustomShowcaseTheme, false, listener);

                } else if (PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_NAME, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.Drug_notification, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.Drug_notification, R.id.noti_close, R.layout.ok_button, "", getParent().getResources().getString(R.string.drugNotification), R.style.CustomShowcaseTheme, false, listener);

                }
                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.Drug_notification, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_FORM, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.ADD_DRUG_FORM, R.id.form_close, R.layout.ok_button, "", getParent().getResources().getString(R.string.drug_form), R.style.CustomShowcaseTheme, false, listener);

                }
                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_FORM, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_start, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.Reminder_start, R.id.schedule_close, R.layout.ok_button, "", getParent().getResources().getString(R.string.reminder_schedule), R.style.CustomShowcaseTheme, false, listener);

                }
                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_start, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_end, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.Reminder_end, R.id.dosage_close,R.layout.ok_button, "", getParent().getResources().getString(R.string.reminder_add_dosage_and_time), R.style.CustomShowcaseTheme, false, listener);

                    scroll.scrollTo(0,scroll.getBottom());

                }

                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_end, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_repeat_text, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.Reminder_repeat_text, R.id.refill_close,R.layout.ok_button, "", getParent().getResources().getString(R.string.reminder_refill_text), R.style.CustomShowcaseTheme, false, listener);

                }
                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_repeat_text, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_IMAGE, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.ADD_DRUG_IMAGE, R.id.food_instruction,R.layout.ok_button, "", getParent().getResources().getString(R.string.food_instruction), R.style.CustomShowcaseTheme, false, listener);

                    // scroll.scrollTo(0,scroll.getBottom());

                }

                else if (PreferenceConnector.readString(getParent(), PreferenceConnector.ADD_DRUG_IMAGE, "No").equals("Yes") && PreferenceConnector.readString(getParent(), PreferenceConnector.Reminder_dosage_time, "No").equals("No")) {
                    showcaseView = Constants.showWalkThrough(AddDrugsActivity.this, PreferenceConnector.Reminder_dosage_time, R.id.camera_layout, R.layout.ok_button, "", getParent().getResources().getString(R.string.camera_btn), R.style.CustomShowcaseTheme, false, listener);

                    shonot=1;
                    shocam=1;
                    shoform=1;
                    shoschedul=1;
                    shotime=1;
                    shorefill=1;
                    shofood=1;
                }

            } else {
                Constants.WALKTHROUGH_ITEM_CLICK = 0;

            }
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
           mDrugNameLayout.setClickable(true);
            mDrugNameEdt.setFocusable(true);
            mDrugNameEdt.setEnabled(true);
            if(shonot==1){
            mNotiClose.setClickable(true);}
            if(shoform==1){
            mFormCLose.setClickable(true);}
            if(shoschedul==1){
            mSchedule.setClickable(true);}
            if(shotime==1){
            mDosageClose.setClickable(true);}
            if(shorefill==1){
            mRefillClose.setClickable(true);}
            if(shofood==1){
            mFoodInstruction.setClickable(true);}
            if(shocam==1){

            mCameraLayout.setClickable(true);}
        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {
            mNotiClose.setClickable(false);
            mFormCLose.setClickable(false);
            mSchedule.setClickable(false);
            mDosageClose.setClickable(false);
            mRefillClose.setClickable(false);
            mFoodInstruction.setClickable(false);
            mCameraLayout.setClickable(false);
            mDrugNameEdt.setClickable(false);
            mDrugNameEdt.setEnabled(false);

        }

        @Override
        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

        }
    };





    @Override
    protected void onResume() {
        super.onResume();
        drugName = "";
        if (tag == 1) {

            mImgPrev.setVisibility(View.VISIBLE);
            mCurrentPhotoPath = PreferenceConnector.readString(getParent(), "imagepath", "");
            if (mCurrentPhotoPath == "") {
                mCurrentPhotoPath = "";
                smallImage = "";
            } else {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/saved_images");
                if(!myDir.exists()){
                    myDir.mkdirs();
                }

                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";

                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    if(new File(mCurrentPhotoPath).exists()) {
                        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);
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
        Activity context;
        ViewHolder1 holder1 = null;

        final ViewHolder1 finalHolder1 = holder1;

        public AddRowAdapter(Activity context) {
            this.context = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            row = mInflater.inflate(R.layout.row, parent, false);


            holder1 = new ViewHolder1(row);

            if(mFormTextView.getText().toString().equals("Capsule")){
              holder1.drugdosagequantitytext.setText("Capsule");
              holder1.drugDoseEdit.setText("1");
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

            ArrayTest test = arrayListItems.get(position);
            test.setDosageUnit(holder1.drugdosagequantitytext.getText().toString());
            notifyDataSetChanged();

            holder1.drugDoseEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    ArrayTest test2 = arrayListItems.get(position);
                    test2.setDosageQuantity(s.toString());
                }
            });
            holder1.drugDoseEdit.setText(arrayListItems.get(position).getDosageQuantity());

            holder1.timeselectrel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    mytime = new TimePickerDialog(getParent(), new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker,int hourOfDay, int minute) {
                                    hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                    minuteString = minute < 10 ? "0" + minute : "" + minute;
                                    ArrayTest test = arrayListItems.get(position);
                                    test.setTimesave(hourString + ":" + minuteString);
                                    try {
                                        sdf = new SimpleDateFormat("H:mm");
                                        Date dateObj = sdf.parse(arrayListItems.get(position).getTimesave());
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
                    mytime.show();
                }
            });

            try {
                sdf = new SimpleDateFormat("H:mm");
                Date dateObj1 = sdf.parse(arrayListItems.get(position).getTimesave());

                String aaas=new SimpleDateFormat("K:mm a").format(dateObj1).toUpperCase();
                String[] str_array = aaas.split(":");
                String stringa = str_array[0];
                String  stringb = str_array[1];
                if(stringa.equals("0")){
                    stringa="12";
                    holder1.timeselect.setText(stringa+":"+stringb);
                }
                else {
                    holder1.timeselect.setText(new SimpleDateFormat("K:mm a").format(dateObj1).toUpperCase());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return row;
        }
    }

    class ViewHolder1 {
        TextView timeselect;
        TextView drugdosagequantitytext;
        EditText drugDoseEdit;
        LinearLayout myrow;
        RelativeLayout timeselectrel;
        public ViewHolder1(View view) {
            // TODO Auto-generated constructor stub
            timeselect = (TextView) view.findViewById(R.id.add_time_text);
            drugdosagequantitytext = (TextView) view.findViewById(R.id.drug_dosage_text);
            drugDoseEdit = (EditText) view.findViewById(R.id.drug_dosage_edit);
            myrow= (LinearLayout) view.findViewById(R.id.ReminderDosageTimeLayout);
            timeselectrel= (RelativeLayout) view.findViewById(R.id.reminder_time_layout);
        }
    }
}
