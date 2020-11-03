/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest.assets;

/**
 *
 * @author mint
 */
public class Aircraft {
    private String icao24;
    private String callsign;
    private String origin_country;
    private double longitude;
    private double latitude;

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getOrigin_country() {
        return origin_country;
    }

    public void setOrigin_country(String origin_country) {
        this.origin_country = origin_country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Aircraft{" + "icao24=" + icao24 + ", callsign=" + callsign + ", origin_country=" + origin_country + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }

    public Aircraft(String icao24, String callsign, String origin_country, double longitude, double latitude) {
        this.icao24 = icao24;
        this.callsign = callsign;
        this.origin_country = origin_country;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
}
