package com.example.jmajyo.beacons.model;

import org.altbeacon.beacon.Identifier;

import java.util.Date;

import io.realm.RealmObject;

public class Bacon extends RealmObject{
    private Date date;
    private int major;
    private int minor;
    private String UUID;


    public Bacon() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
