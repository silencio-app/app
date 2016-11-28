package io.github.silencio_app.silencio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vipin on 28/11/16.
 */
public class NoiseRecordBundle {
    private String place;
    private float avg_db;
    private Date start;
    private Date end;
    private static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public NoiseRecordBundle(float avg_db, Date end, String place, Date start) {
        this.avg_db = avg_db;
        this.end = end;
        this.place = place;
        this.start = start;
    }

    public float getAvg_db() {
        return avg_db;
    }

    public void setAvg_db(float avg_db) {
        this.avg_db = avg_db;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}
