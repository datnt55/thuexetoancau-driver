package grab.com.thuexetoancau.driver.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Position {
    private String placeId;
    private String primaryText;
    private String secondText;
    private LatLng latLng;
    private String fullPlace;
    public Position(String placeId, String primaryText, String secondText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondText = secondText;
        this.fullPlace = primaryText +", "+secondText;
    }

    public Position(String fullPath, LatLng latLng) {
        this.latLng = latLng;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getFullPlace() {
        return fullPlace;
    }

    public void setFullPlace(String fullPlace) {
        this.fullPlace = fullPlace;
    }

    public String getLatLngToString() {
        return latLng.latitude + ","+ latLng.longitude;
    }
}
