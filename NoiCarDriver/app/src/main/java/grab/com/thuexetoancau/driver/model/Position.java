package grab.com.thuexetoancau.driver.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Position implements Serializable{
    private String placeId;
    private String primaryText;
    private String secondText;
    private double lat;
    private double lon;
    private String fullPlace;
    public Position(String placeId, String primaryText, String secondText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondText = secondText;
        this.fullPlace = primaryText +", "+secondText;
    }
    public Position(String fullPath) {
        this.fullPlace = fullPath;
    }
    public Position(String fullPath, LatLng latLng) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
        this.fullPlace = fullPath;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondText() {
        return secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getFullPlace() {
        return fullPlace;
    }

    public void setFullPlace(String fullPlace) {
        this.fullPlace = fullPlace;
    }

    public String getLatLngToString() {
        return lat + ","+ lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lon);
    }
}
