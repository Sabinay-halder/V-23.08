package com.widevision.pillreminder.dao;

import java.io.Serializable;

/**
 * Created by mercury-one on 6/1/16.
 */
public class BeanInApp implements Serializable{


    private String id;
    private String version;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}
