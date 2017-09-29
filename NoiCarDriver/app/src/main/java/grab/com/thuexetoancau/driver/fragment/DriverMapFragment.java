package grab.com.thuexetoancau.driver.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.AcceptBookingActivity;
import grab.com.thuexetoancau.driver.activities.ListBookingAroundActivity;
import grab.com.thuexetoancau.driver.adapter.BookingImmediateAroundAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.thread.LocationProvide;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;
import grab.com.thuexetoancau.driver.utilities.MarkerAnimation;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

/**
 * Created by DatNT on 8/9/2017.
 */

public class DriverMapFragment extends Fragment implements LocationProvide.OnUpdateLocation, OnMapReadyCallback {
    private LocationProvide locationProvide;
    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Marker currentLocation;
    private FrameLayout layoutFixGps;
    private MarkerAnimation animation ;
    private static View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_driver_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        animation = new MarkerAnimation(getActivity());
        layoutFixGps = (FrameLayout) view.findViewById(R.id.fix_gps);
        layoutFixGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation.getPosition())             // Sets the center of the map to current location
                        .zoom(16)                   // Sets the zoom
                        .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                        .build();                   // Creates BookingLongTripAroundAdapter CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.driver_map);
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void getCurrentPosition() {
        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                DialogUtils.settingRequestTurnOnLocation(getActivity());
            } else {
                showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            }
        }
    }

    private void showCurrentLocationToMap(double latitude, double longitude) {
        if (currentLocation != null)
            currentLocation.remove();
        currentLocation = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_map))
                .title("Vị trí của bạn"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation.getPosition())             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates BookingLongTripAroundAdapter CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentPosition();
        locationProvide = new LocationProvide(getActivity(),this);
        locationProvide.startUpdatesButtonHandler();
    }

    @Override
    public void onStopUpdate() {

    }

    @Override
    public void onUpdate(Location mCurrentLocation) {
        animation.animateMarker(mCurrentLocation,currentLocation);
    }
}