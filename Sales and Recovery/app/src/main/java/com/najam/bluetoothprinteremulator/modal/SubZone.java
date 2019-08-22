package com.najam.bluetoothprinteremulator.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class SubZone implements Parcelable, Comparable<SubZone>{
    private String zoneID;
    private String sZoneID;
    private String sZoneName;
    private ArrayList<Account> accounts;
    public SubZone(){
        accounts = new ArrayList<Account>();
    }
    public SubZone(Parcel in){
        this.zoneID = in.readString();
        this.sZoneID = in.readString();
        this.sZoneName = in.readString();

    }
    public String getZoneID() {
        return zoneID;
    }



    public static final Parcelable.Creator<SubZone> CREATOR
            = new Parcelable.Creator<SubZone>()
    {
        public SubZone createFromParcel(Parcel in)
        {
            return new SubZone(in);
        }

        public SubZone[] newArray (int size)
        {
            return new SubZone[size];
        }
    };

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getsZoneID() {
        return sZoneID;
    }

    public void setsZoneID(String sZoneID) {
        this.sZoneID = sZoneID;
    }

    public String getsZoneName() {
        return sZoneName;
    }

    public void setsZoneName(String sZoneName) {
        this.sZoneName = sZoneName;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return sZoneID + ' ' + sZoneName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.zoneID);
        dest.writeString(this.sZoneID);
        dest.writeString(this.sZoneName);
    }

    @Override
    public int compareTo(SubZone o) {
        return this.getsZoneID().compareTo(o.getsZoneID());
    }

}
