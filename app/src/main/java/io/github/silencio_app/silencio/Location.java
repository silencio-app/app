package io.github.silencio_app.silencio;

/**
 * Created by vipin on 28/11/16.
 */
public class Location {
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
}
