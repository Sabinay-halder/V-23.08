package com.widevision.pillreminder.mailSending;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by mercury-one on 18/1/16.
 */
public class GmailSender{
 final String emailPort = "587";
final String smtpAuth = "true";
final String starttls = "true";
final String emailHost = "smtp.gmail.com";
String fromEmail;
String fromPassword;
String toEmail;
String emailSubject;
String emailBody;

Properties emailProperties;
Session mailSession;
MimeMessage emailMessage;

        public GmailSender() {

        }

        public GmailSender(String fromEmail, String fromPassword,
                     String toEmail, String emailSubject, String emailBody) {
            this.fromEmail = fromEmail;
            this.fromPassword = fromPassword;
            this.toEmail = toEmail;
            this.emailSubject = emailSubject;
            this.emailBody = emailBody;

            emailProperties =new Properties();
            emailProperties.put("mail.smtp.port", emailPort);
            emailProperties.put("mail.smtp.auth", smtpAuth);
            emailProperties.put("mail.smtp.starttls.enable", starttls);
         Log.i("GMail", "Mail server properties set.");
        }

        public MimeMessage createEmailMessage() throws
                MessagingException, UnsupportedEncodingException {

            mailSession = Session.getDefaultInstance(emailProperties, null);
            emailMessage = new MimeMessage(mailSession);

            emailMessage.setFrom(new InternetAddress(fromEmail));

                Log.i("GMail","toEmail: "+toEmail);
                emailMessage.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(toEmail));


            emailMessage.setSubject(emailSubject);
            emailMessage.setContent(emailBody, "text/html");// for a html email
            Log.i("GMail", "Email Message created.");
            return emailMessage;
        }

        public void sendEmail() throws MessagingException {

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(emailHost, fromEmail, fromPassword);
            Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());

            transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
            transport.close();
            Log.i("GMail", "Email sent successfully.");
        }

}