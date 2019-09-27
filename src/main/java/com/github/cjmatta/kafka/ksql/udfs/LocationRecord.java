package com.github.cjmatta.kafka.ksql.udfs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationRecord {

    private String city;
    private String country;
    private String subdivision;
    private LatLonRecord latLonRecord;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public LatLonRecord getLatLonRecord() {
        return latLonRecord;
    }

    @JsonProperty("location")
    public void setLatLonRecord(LatLonRecord latLonRecord) {
        this.latLonRecord = latLonRecord;
    }

    @Override
    public String toString() {
        return "LocationRecord{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", subdivision='" + subdivision + '\'' +
                ", latLonRecord=" + latLonRecord +
                '}';
    }

}
