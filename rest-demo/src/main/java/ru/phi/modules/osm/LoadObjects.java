package ru.phi.modules.osm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public final class LoadObjects {

    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Element[] loadUpdated() throws IOException {
        try (Reader reader = new InputStreamReader(LoadObjects.class.getResourceAsStream("/u_objects.json"))) {
            return gson.fromJson(reader, Element[].class);
        }
    }

    public static final class Element {
        @SerializedName("id")
        public String id;
        @SerializedName("text")
        public String text;
        @SerializedName("polygon")
        public Location[] polygon;

        @SerializedName("Latitude")
        public double latitude;
        @SerializedName("Longtitude")
        public double longitude;
    }

    public static final class Location {
        @SerializedName("longitude")
        public double longitude;
        @SerializedName("latitude")
        public double latitude;
    }
}
