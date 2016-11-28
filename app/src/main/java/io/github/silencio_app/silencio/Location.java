package io.github.silencio_app.silencio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vipin on 28/11/16.
 */
public class Location implements Parcelable{
    private String name;
    private float db_level;
    private String mac;

    public Location(String name, float db_level, String mac ) {
        this.db_level = db_level;
        this.mac = mac;
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public float getDb_level() {
        return db_level;
    }

    public void setDb_level(float db_level) {
        this.db_level = db_level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    protected Location(Parcel in) {
        name = in.readString();
        db_level = in.readFloat();
        mac = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeFloat(db_level);
        parcel.writeString(mac);
    }
}
