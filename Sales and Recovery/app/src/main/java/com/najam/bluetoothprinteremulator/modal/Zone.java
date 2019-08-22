package com.najam.bluetoothprinteremulator.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by HP on 11/12/2017.
 */

public class Zone implements Parcelable {
    private String zoneID;
    private String zoneName;
    private ArrayList<SubZone> subZonses;
    public Zone(){
        subZonses= new ArrayList<SubZone>();
    }
    public Zone(Parcel in){
        this.zoneID = in.readString();
        this.zoneName = in.readString();
         in.readTypedList(this.getSubZonses(), SubZone.CREATOR);
    }

    public static final Parcelable.Creator<Zone> CREATOR
            = new Parcelable.Creator<Zone>()
    {
        public Zone createFromParcel(Parcel in)
        {
            return new Zone(in);
        }

        public Zone[] newArray (int size)
        {
            return new Zone[size];
        }
    };


    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public ArrayList<SubZone> getSubZonses() {
        if(this.subZonses == null){
            this.subZonses = new ArrayList<SubZone>();
        }
        return subZonses;
    }

    public void setSubZonses(ArrayList<SubZone> subZonses) {
        this.subZonses = subZonses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString (this.zoneID);
        dest.writeString (this.zoneName);
        dest.writeTypedList(this.subZonses);
    }


    @Override
    public String toString() {
        return zoneID +' ' + zoneName;
    }
}
