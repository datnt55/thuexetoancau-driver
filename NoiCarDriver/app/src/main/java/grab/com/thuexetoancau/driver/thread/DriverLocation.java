package grab.com.thuexetoancau.driver.thread;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import javax.microedition.khronos.opengles.GL;

import grab.com.thuexetoancau.driver.utilities.BaseService;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.MarkerAnimation;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

/**
 * Created by DatNT on 8/11/2017.
 */

public class DriverLocation implements Runnable {
    private double latitude;
    private double longtitude;
    private SharePreference preference;
    private Activity mActivity;
    private LatLng prevLatLn;
    private Marker mMarker;
    public DriverLocation(Activity activity) {
        this.mActivity = activity;
        preference = new SharePreference(mActivity);
    }

    public void setMarker (Marker marker){
        this.mMarker = marker;
    }

    public void run() {
        try {
            while (true) {
                if (preference.getStatus()== 0){
                    Global.isPostGPS = true;
                }
                sendLocationToServer();
                Thread.sleep(Global.LOOP_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void sendLocationToServer() {
        RequestParams params;
        params = new RequestParams();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                GPSTracker gps = new GPSTracker(mActivity);
                if (gps.canGetLocation()) {
                    longtitude = gps.getLongitude();
                    latitude = gps.getLatitude();
                    /*if (Global.inTrip) {
                        if (prevLatLn != null)
                            Global.totalDistance += CommonUtilities.distanceInMeter(prevLatLn, new LatLng(latitude, longtitude));
                       *//* MarkerAnimation.animateMarker(gps.getLocation(),mMarker);
                        Toast.makeText(mActivity, Global.totalDistance+"",Toast.LENGTH_SHORT).show();*//*
                    }*/
                }else
                    return;
            }
        });


        prevLatLn = new LatLng(latitude, longtitude);
        params.put("car_number", preference.getCarNumber());
        params.put("lat", latitude);
        params.put("lon", longtitude);
        params.put("status", preference.getStatus());
        params.put("phone", preference.getPhone());
        params.put("car_type", 1);
        params.put("os", 1);
        params.put("reg_id", preference.getRegId());
        params.put("driver_id", preference.getDriverId());
        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_POST_DRIVER_GPS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                if (preference.getStatus() == 0)
                    Global.isPostGPS = true;
                else
                    Global.isPostGPS = false;


            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", "error");
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }
}