package io.github.silencio_app.silencio;

/**
 * Created by vipin on 28/11/16.
 */
public class NoiseRecordBundle {
    private String place;
    private float avg_db;
    private String start;
    private String end;

    public NoiseRecordBundle(String place, float avg_db, String start, String end) {
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}
