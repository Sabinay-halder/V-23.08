package com.widevision.pillreminder.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-one on 14/8/15.
 */

@Table(name = "HistoryLists")
public class HistoryListTable extends Model {
    @Column(name = "_drugId")
    public String drugId;
    @Column(name = "_drugName")
    public String drugName;
    @Column(name = "_dosage")
    public String dosage;
    @Column(name = "_date")
    public String date;
    @Column(name = "_status")
    public String status;
    @Column(name = "takenTime")
    public String takenTime;
    @Column(name = "_quantity")
    public String quantity;


    public HistoryListTable() {
    }

    public HistoryListTable(String drugId, String drugName, String dosage, String date, String status, String takenTime, String quantity) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.dosage = dosage;
        this.date = date;
        this.status = status;
        this.takenTime = takenTime;
        this.quantity = quantity;
    }
}
