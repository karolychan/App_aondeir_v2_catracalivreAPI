package com.example.erick.aondeir;

/**
 * Created by Erick , Caroline , Isabela.
 */

public class City {
    private String cityName;
    private String geoLocation;
    private String range;

    public City(String cityName, String geoLocation, String range) {
        this.cityName = cityName;
        this.geoLocation = geoLocation;
        this.range = range;
    }

    public String getCityName() {
        return cityName;
    }

    public String getGeoLocation() { return geoLocation; }

    public String getRange() { return range; }
}
