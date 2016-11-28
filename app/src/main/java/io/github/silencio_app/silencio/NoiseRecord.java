package io.github.silencio_app.silencio;

import java.util.Date;

/**
 * Created by vipin on 28/11/16.
 */
public class NoiseRecord {
    private String place;
    private float db_level;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public NoiseRecord(String place, Float db_level, String date){
        this.place = place;
        this.db_level = db_level;
        this.date = date;

    }

    public float getDb_level() {
        return db_level;
    }

    public void setDb_level(float db_level) {
        this.db_level = db_level;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
