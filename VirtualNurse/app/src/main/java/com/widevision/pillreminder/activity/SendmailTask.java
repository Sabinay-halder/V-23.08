package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.widevision.pillreminder.mailSending.GmailSender;
import com.widevision.pillreminder.util.Constants;

/**
 * Created by mercury-one on 18/1/16.
 */
public class SendmailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;
    String aaa="";
    public SendmailTask(Activity activity
                      ) {
        sendMailActivity = activity;


    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GmailSender androidEmail = new GmailSender(args[0].toString(),
                    args[1].toString(), args[2].toString(), args[3].toString(),
                    args[4].toString());
            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
            publishProgress("Sending email....");
            androidEmail.sendEmail();
            publishProgress("Email Sent.");
            Log.i("SendMailTask", "Mail Sent.");
        }
        catch (Exception e) {
            statusDialog.dismiss();
            aaa="fail";

            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
        if(aaa.equals("fail")){
        Constants.alert(sendMailActivity, "Unable to send mail");
        }
        else{
            Constants.alert(sendMailActivity, "mail is sent");
        }
    }

}
