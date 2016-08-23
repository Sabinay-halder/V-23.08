package com.widevision.pillreminder.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.widevision.pillreminder.R;
import com.widevision.pillreminder.model.HideKeyActivity;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.Extension;
import com.widevision.pillreminder.util.PreferenceConnector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PasswordActivity extends HideKeyActivity {
    @Bind(R.id.verifyPassword) EditText mVerifyPassword;
    @Bind(R.id.btnVerifyEnters)Button mBtnVerifyEnter;
    @Bind(R.id.forgot_password)TextView mForgetPassword;
    String pass,validatepassword,email;
     String fromEmail="feedback4apps@gmail.com";
     String fromPassword="success@rock";
    String subject="virtual nurse";
    private Extension ext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.password_layout);
        ButterKnife.bind(this);
        ext = Extension.getInstance();
            validatepassword= PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.Passworddata, "");
            email= PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.EmailData, "");

          mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(PasswordActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.clear_confirm_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView text = (TextView) dialog.findViewById(R.id.textView1);
                Button buttonyes = (Button) dialog.findViewById(R.id.btn_confirm_delete);
                Button buttoncancle = (Button) dialog.findViewById(R.id.btn_cancel_delete);
                text.setText("Your 4 digit PIN will be sent to "+email+", Are you sure you want to continue?");
                dialog.show();
                buttonyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constants.isNetworkAvailable(PasswordActivity.this,view)) {
                            new SendmailTask(PasswordActivity.this).execute(fromEmail,
                                    fromPassword, email, subject, "Your PIN for Virtual nurse is " + validatepassword);
                            dialog.dismiss();
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
        });
        mBtnVerifyEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pass=mVerifyPassword.getText().toString();

                    if(pass.equals(validatepassword)){
                        Intent intent=new Intent(PasswordActivity.this,TabCreator.class);
                        startActivity(intent);
                        PasswordActivity.this.finish();
                    }
                    else{
                        Constants.alert(PasswordActivity.this, "Please Enter Correct PIN.");
                  }
            }
        });

    }
}
