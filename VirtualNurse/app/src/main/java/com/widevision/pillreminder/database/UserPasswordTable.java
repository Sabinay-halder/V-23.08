package com.widevision.pillreminder.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-one on 14/8/15.
 */

@Table(name = "UserPasswordTable")
public class UserPasswordTable extends Model {

    @Column(name = "_password")
    public String userPassword;


    public UserPasswordTable() {
        super();


    }

    public UserPasswordTable(String userPassword) {
        super();
        this.userPassword = userPassword;

    }


}
