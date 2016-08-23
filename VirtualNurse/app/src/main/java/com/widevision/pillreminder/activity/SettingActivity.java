package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.kyleduo.switchbutton.SwitchButton;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.HistoryListTable;
import com.widevision.pillreminder.database.NotificationTable;
import com.widevision.pillreminder.database.UserPasswordTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 10/8/15.
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.setting_passcode_button)
    SwitchButton mSettingPasscodeButton;
    @Bind(R.id.setting_snooze_button)
    SwitchButton mSettingSnoozeButton;
    @Bind(R.id.setting_clear_history_layout)LinearLayout mSettingClearHistoryLayout;
    @Bind(R.id.setting_clear_notification_layout)LinearLayout mSettingClearNotificationLayout;
    @Bind(R.id.setting_sound_pick)TextView mSettingsoundPick;
    @Bind(R.id.setting_passcode_edit)ImageView mSettingPasscodeEdit;
    @Bind(R.id.setting_date_formate_pick)TextView mSettingDateFormateLayout;
    public String passcodeflag = "1";
    private String password, repeatpassord,oldpassword,email,check_pass;
    String sound,dateformateselect="";
    int a=0;
    String popUpContents[];
    PopupWindow popupWindowUnits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        ButterKnife.bind(this);

        String snoozeEnable=  PreferenceConnector.readString(getApplicationContext(),"snoozeEnable","");
        dateformateselect=PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        if(snoozeEnable.equals("")){
            mSettingSnoozeButton.setChecked(true);
            PreferenceConnector.writeString(getApplicationContext(), "snooze", "5");
        }
       else if(snoozeEnable.equals("no")){
            mSettingSnoozeButton.setChecked(false);
            PreferenceConnector.writeString(getApplicationContext(), "snooze", "");
        }
        if(!dateformateselect.equals("")){
           mSettingDateFormateLayout.setText(dateformateselect);
        }
        List<String> unit_list;
        unit_list = new ArrayList<String>();
        unit_list.add("MM-DD-YYYY::1");
        unit_list.add("DD-MM-YYYY::2");
        unit_list.add("YYYY-MM-DD::3");
        popUpContents = new String[unit_list.size()];
        unit_list.toArray(popUpContents);


        mSettingPasscodeEdit.setOnClickListener(this);
        String bb=PreferenceConnector.readString(getApplicationContext(), "passcode", "");
        if (bb.equals("yes")) {
            mSettingPasscodeEdit.setVisibility(View.VISIBLE);

        }

        sound = PreferenceConnector.readString(SettingActivity.this, PreferenceConnector.NOTIFICATION_SOUND_TEXT, "");
        if(!sound.equals(""))
        {
            mSettingsoundPick.setText(sound);
        }
        else{
            mSettingsoundPick.setText("Default tone");
        }

        String passcode=  PreferenceConnector.readString(getApplicationContext(),"passcode","");
        String snooze=  PreferenceConnector.readString(getApplicationContext(),"snooze","");

        if (passcode.equalsIgnoreCase("yes")) {
            mSettingPasscodeButton.setChecked(true);
        }
        else{
            mSettingPasscodeButton.setChecked(false);
        }

        if ((snooze.equals("5"))||(snooze.equals("15"))||(snooze.equals("30"))) {
            mSettingSnoozeButton.setChecked(true);
        }
        else{
            mSettingSnoozeButton.setChecked(false);
        }
        mSettingClearHistoryLayout.setOnClickListener(this);
        mSettingClearNotificationLayout.setOnClickListener(this);
        mSettingsoundPick.setOnClickListener(this);
        mSettingDateFormateLayout.setOnClickListener(this);

        //-------------passcode button---------------
        mSettingPasscodeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_pass= PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.EmailData, "");

                    if (check_pass.equals("")) {

                        final Dialog dialog = new Dialog(getParent());
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.passcode_dialog);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        final EditText useremail = (EditText) dialog.findViewById(R.id.dialog_user_email);
                        final EditText userPassword = (EditText) dialog.findViewById(R.id.dialog_user_password);
                        final EditText userPasswordRepeat = (EditText) dialog.findViewById(R.id.dialog_user_password_repeat);
                        Button buttonsave = (Button) dialog.findViewById(R.id.btn_save_password);
                        Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancle_password);

                        dialog.show();

                        buttonsave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                email = useremail.getText().toString();
                                password = userPassword.getText().toString();
                                repeatpassord = userPasswordRepeat.getText().toString();
                                if ((email.length() == 0) || (!Constants.email_validation(email))) {
                                    Constants.alert(getParent(), "Please enter a valid email. ");
                                }
                                else if (password.length()!=4) {
                                    Constants.alert(getParent(), "PIN should contain 4 digits. ");
                                }
                                else if (!repeatpassord.matches(password)) {
                                    Constants.alert(getParent(), "PIN and Confirm PIN should be same.");
                                }
                                else{

                                 PreferenceConnector.writeString(getParent(),PreferenceConnector.Passworddata, password);
                                 PreferenceConnector.writeString(getParent(),PreferenceConnector.EmailData, email);

                                        dialog.dismiss();
                                        Constants.alert(getParent(), "Email and PIN are set. ");
                                        PreferenceConnector.writeString(getApplicationContext(), "passcode", "yes");
                                        mSettingPasscodeButton.setChecked(true);

                                    }

                                }


                        });
                        buttoncancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                mSettingPasscodeButton.setChecked(false);

                            }
                        });
                    }
                    else{

                        PreferenceConnector.writeString(getApplicationContext(), "passcode", "yes");
                        passcodeflag = "1";
                    }
                }
                else {
                    PreferenceConnector.writeString(getApplicationContext(), "passcode", "no");

                    passcodeflag = "0";
                }

            }
        });



        mSettingSnoozeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischeck) {
                if(ischeck){

                    LayoutInflater li = LayoutInflater.from(getParent());
                    View promptsView = li.inflate(R.layout.snooze_dialog, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParent());
                    alertDialogBuilder.setView(promptsView);

                    final TextView five = (TextView) promptsView.findViewById(R.id.five_minutes);
                    final TextView fifteen = (TextView) promptsView.findViewById(R.id.fifteen_minutes);
                    final TextView thirty = (TextView) promptsView.findViewById(R.id.thirty_minutes);




                    five.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                         five.setBackgroundColor((Color.parseColor("#a9ddff")));
                            fifteen.setBackgroundColor((Color.parseColor("#ffffff")));
                            thirty.setBackgroundColor((Color.parseColor("#ffffff")));
                           a=5;

                        }
                    });
                    fifteen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fifteen.setBackgroundColor((Color.parseColor("#a9ddff")));
                            thirty.setBackgroundColor((Color.parseColor("#ffffff")));
                            five.setBackgroundColor((Color.parseColor("#ffffff")));
                           a=15;

                        }
                    });
                    thirty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            thirty.setBackgroundColor((Color.parseColor("#a9ddff")));
                            fifteen.setBackgroundColor((Color.parseColor("#ffffff")));
                            five.setBackgroundColor((Color.parseColor("#ffffff")));
                            a=30;

                        }
                    });
                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           if(a==5){
                               PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "yes");
                               PreferenceConnector.writeString(getApplicationContext(), "snooze", "5");
                               mSettingSnoozeButton.setChecked(true);
                               a=100;
                               dialog.cancel();
                           }
                           else if(a==15){
                                PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "yes");
                                PreferenceConnector.writeString(getApplicationContext(), "snooze", "15");
                               mSettingSnoozeButton.setChecked(true);
                               a=100;
                               dialog.cancel();
                            }
                           else if(a==30){
                               PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "yes");
                               PreferenceConnector.writeString(getApplicationContext(), "snooze", "15");
                               mSettingSnoozeButton.setChecked(true);
                               a=100;
                               dialog.cancel();
                           }
                            else{
                               PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "no");
                               PreferenceConnector.writeString(getApplicationContext(), "snooze", "");
                               mSettingSnoozeButton.setChecked(false);

                               dialog.cancel();
                           }

                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "no");
                                    PreferenceConnector.writeString(getApplicationContext(), "snooze", "");
                                    mSettingSnoozeButton.setChecked(false);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                else {
                    PreferenceConnector.writeString(getApplicationContext(), "snooze", "");
                    PreferenceConnector.writeString(getApplicationContext(), "snoozeEnable", "no");
                }
            }
        });
    }

    private PopupWindow popupWindowUnits(final View view, String[] list) {
        popupWindowUnits = new PopupWindow(this);
        // the drop down list is a list view
        ListView listViewUnits = new ListView(this);
        // set our adapter and pass our pop up window contents
        listViewUnits.setAdapter(unitsAdapter(list));
        listViewUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedItemTag = ((TextView) v).getTag().toString();

                Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(10);
                v.startAnimation(fadeInAnimation);

                // dismiss the pop up
                popupWindowUnits.dismiss();
                String selectedItemText = ((TextView) v).getText().toString();
                    mSettingDateFormateLayout.setText(selectedItemText);
                if(selectedItemText.equals("MM-DD-YYYY")){
                    PreferenceConnector.writeString(getApplicationContext(), "dateselectionformate", "MM-DD-YYYY");
                }
                else if(selectedItemText.equals("YYYY-MM-DD")){
                    PreferenceConnector.writeString(getApplicationContext(), "dateselectionformate", "YYYY-MM-DD");
                }
                else if(selectedItemText.equals("DD-MM-YYYY")){
                    PreferenceConnector.writeString(getApplicationContext(), "dateselectionformate", "DD-MM-YYYY");
                }
            }
        });

        popupWindowUnits.setFocusable(true);
        popupWindowUnits.setWidth(400);
        //popupWindowUnits.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowUnits.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowUnits.setContentView(listViewUnits);
        popupWindowUnits.showAsDropDown(view, -5, 0);
        return popupWindowUnits;
    }

    private ArrayAdapter<String> unitsAdapter(String unitsArray[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unitsArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String item = getItem(position);
                String[] itemArr = item.split("::");
                String text = itemArr[0];
                String id = itemArr[1];
                // visual settings for the list item
                TextView listItem = new TextView(SettingActivity.this);

                listItem.setText(text);
                listItem.setTag(id);
                listItem.setTextSize(20);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);

                return listItem;

            }
        };
        return adapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_clear_history_layout:
                List<HistoryListTable> hvalues = new Select().all().from(HistoryListTable.class).execute();
                if (!hvalues.isEmpty()) {

                    final Dialog dialog = new Dialog(getParent());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.clear_confirm_dialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    TextView text = (TextView) dialog.findViewById(R.id.textView1);
                    Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
                    Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
                    text.setText("Do you want to Clear History?");
                    dialog.show();
                    buttonyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                new Delete().from(HistoryListTable.class).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();

                        }

                    });

                    buttoncancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                        }
                    });


                } else {
                    Constants.alert(getParent(), "No history found. ");
                }

                break;
            case R.id.setting_clear_notification_layout:
                List<NotificationTable> nvalues = new Select().all().from(NotificationTable.class).execute();
                if (!nvalues.isEmpty()) {

                    final Dialog dialog = new Dialog(getParent());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.clear_confirm_dialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    TextView text = (TextView) dialog.findViewById(R.id.textView1);
                    Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
                    Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
                    text.setText("Do you want to Clear Notifications?");
                    dialog.show();
                    buttonyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                new Delete().from(NotificationTable.class).execute();

                                NotificationManager manaager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                manaager.cancelAll();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();

                        }

                    });


                    buttoncancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                        }
                    });


                } else {
                    Constants.alert(getParent(), "No notification found. ");

                }

                break;

            case R.id.setting_sound_pick:
                Intent intent = new Intent(getParent(), TransperentActivity.class);
                startActivity(intent);

                break;
            case R.id.setting_passcode_edit:
                final Dialog dialog = new Dialog(getParent());
                dialog.setCanceledOnTouchOutside(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.passcode_edit_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final EditText olduserPassword = (EditText) dialog.findViewById(R.id.dialog_user_old_password);
                final EditText userPassword = (EditText) dialog.findViewById(R.id.dialog_user_password);
                final EditText userPasswordRepeat = (EditText) dialog.findViewById(R.id.dialog_user_password_repeat);
                // layout.setBackgroundResource(R.drawable.bg_c);
                Button buttonsave = (Button) dialog.findViewById(R.id.btn_save_password);
                Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancle_password);

                dialog.show();

                buttonsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        oldpassword=olduserPassword.getText().toString();
                        password = userPassword.getText().toString();
                        repeatpassord = userPasswordRepeat.getText().toString();
                        List<UserPasswordTable> values = new Select().all().from(UserPasswordTable.class).execute();
                        for(UserPasswordTable item : values){
                            if ((password.length()>0)&&(repeatpassord.matches(password))&&(item.userPassword.matches(oldpassword))) {
                                PreferenceConnector.writeString(getParent(),PreferenceConnector.Passworddata, password);
                                dialog.dismiss();
                                Constants.alert(getParent(), "PIN is updated. ");


                            }
                            else if(!item.userPassword.matches(oldpassword)) {
                                Constants.alert(getParent(), "Please enter correct old PIN. ");
                            }
                            else{
                                Constants.alert(getParent(), "PIN not matched. ");
                            }
                       }
                    }
                });
                buttoncancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       dialog.dismiss();
                    }
                });
                break;
            case R.id.setting_date_formate_pick:
                popupWindowUnits(mSettingDateFormateLayout, popUpContents);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sound = PreferenceConnector.readString(getParent(), PreferenceConnector.NOTIFICATION_SOUND_TEXT, "");
        if(!sound.equals(""))
        {
            mSettingsoundPick.setText(sound);
        }
        else{
            mSettingsoundPick.setText("Select Sound");
        }
        List<UserPasswordTable> values = new Select().all().from(UserPasswordTable.class).execute();
        if (!values.isEmpty()) {
            mSettingPasscodeEdit.setVisibility(View.VISIBLE);
            mSettingsoundPick.setOnClickListener(this);
        }
    }
}
