package io.github.silencio_app.silencio;

/**
 * Created by vipin on 28/11/16.
 */
public class Location {
    private String name;
    private float db_level;

    public Location(float db_level, String name) {
        this.db_level = db_level;
        this.name = name;
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
