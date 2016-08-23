package com.widevision.pillreminder.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-one on 14/8/15.
 */

@Table(name = "DrugListsTable")
public class DrugsListTable extends Model {

    @Column(name = "_drugName")
    public String drugName;
    @Column(name = "_form")
    public String form;
    @Column(name = "_rx_Number")
    public String rx_Number;
    @Column(name = "_doctorName")
    public String doctorName;
    @Column(name = "_pharmacyName")
    public String pharmacyName;
    @Column(name = "_drugImage")
    public String drugImage;
    @Column(name = "_smallDrugImage")
    public String smallDrugImage;
    @Column(name = "_doNotify")
    public String doNotify;
    @Column(name = "_repeat")
    public String repeat;
    @Column(name = "_index")
    public String index;
    @Column(name = "_date")
    public String repeatDate;
    @Column(name = "_day")
    public String repeatDay;
    @Column(name = "_startDate")
    public String startDate;
    @Column(name = "_endDate")
    public String endDate;
    @Column(name = "_dosage")
    public String dosage;
    @Column(name = "_time")
    public String time;
    @Column(name = "_quantity")
    public String quantity;
    @Column(name = "_alarmTimeRepeat")
    public String alarmTimeRepeat;
    @Column(name = "_dosageUnit")
    public String dosageUnit;
    @Column(name = "_snooze")
    public String snooze;

    public DrugsListTable() {
        super();


    }

    public DrugsListTable(String drugName, String form, String rx_Number, String doctorName, String pharmacyName, String drugImage, String smallDrugImage, String doNotify, String repeat, String index, String repeatDate, String repeatDay, String startDate, String endDate, String dosage, String time, String quantity, String alarmTimeRepeat, String dosageUnit, String snooze) {
        this.drugName = drugName;
        this.form = form;
        this.rx_Number = rx_Number;
        this.doctorName = doctorName;
        this.pharmacyName = pharmacyName;
        this.drugImage = drugImage;
        this.smallDrugImage = smallDrugImage;
        this.doNotify = doNotify;
        this.repeat = repeat;
        this.index = index;
        this.repeatDate = repeatDate;
        this.repeatDay = repeatDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dosage = dosage;
        this.time = time;
        this.quantity = quantity;
        this.alarmTimeRepeat = alarmTimeRepeat;
        this.dosageUnit = dosageUnit;
        this.snooze = snooze;

    }
}
