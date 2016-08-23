package com.widevision.pillreminder.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-one on 17/10/15.
 */
@Table(name = "NotificationTable")
public class NotificationTable  extends Model {
    @Column(name = "_drugId")
    public String drugId;
    @Column(name = "_drugName")
    public String drugName;
    @Column(name = "_notificationDate")
    public String notificationdate;
    @Column(name = "_notificationTime")
    public String notificationTime;
    @Column(name = "_dosage")
    public String  dosage;
    @Column(name = "_drugForm")
    public String  drugForm;
    @Column(name = "_drugImage")
    public String  drugImage;
    @Column(name = "_drugQuantity1")
    public String  drugQuantity;
    @Column(name = "_snooze1")
    public String  snooze;
    @Column(name = "_notify1")
    public String  notify;
    @Column(name = "_notiId1")
    public String  notiId;
    @Column(name = "_currentTime")
    public String  currentTime;
    public NotificationTable() {
        super();
    }

    public NotificationTable(String drugId, String drugName, String notificationdate, String notificationTime, String dosage, String drugForm, String drugImage, String drugQuantity, String snooze, String notify, String notiId, String currentTime) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.notificationdate = notificationdate;
        this.notificationTime = notificationTime;
        this.dosage = dosage;
        this.drugForm = drugForm;
        this.drugImage = drugImage;
        this.drugQuantity = drugQuantity;
        this.snooze = snooze;
        this.notify = notify;
        this.notiId = notiId;
        this.currentTime = currentTime;
    }
}

