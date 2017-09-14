package grab.com.thuexetoancau.driver.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import grab.com.thuexetoancau.driver.DirectionFinder.DirectionFinder;
import grab.com.thuexetoancau.driver.DirectionFinder.DirectionFinderListener;
import grab.com.thuexetoancau.driver.DirectionFinder.Route;
import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.thread.DriverLocation;
import grab.com.thuexetoancau.driver.thread.LocationProvide;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.MarkerAnimation;
import grab.com.thuexetoancau.driver.utilities.SharePreference;
import grab.com.thuexetoancau.driver.widget.AcceptBookDialog;
import grab.com.thuexetoancau.driver.widget.CustomerInfoLayout;
import grab.com.thuexetoancau.driver.widget.PaymentDialog;

public class AcceptBookingActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        PaymentDialog.BillSuccessListenr,
        DirectionFinderListener,
        NavigationView.OnNavigationItemSelectedListener,
        LocationProvide.OnUpdateLocation,
        OnMapReadyCallback{

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Marker currentLocation;
    private Trip customerTrip;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private CustomerInfoLayout layoutCustomer;
    private Context mContext;
    private Toolbar toolbar;
    private TextView txtCustomerName, txtSource, txtDestination, txtNote;
    private BottomNavigationView navigation;
    private ApiUtilities mApi;
    private Button btnFinishTrip;
    private User user;
    private TextView txtName, txtEmail;
    private LocationProvide locationProvide;
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_booking);
        mContext = this;
        preference = new SharePreference(this);
        preference.saveStatus(1);
        mApi = new ApiUtilities(this);
        if (getIntent().hasExtra(Defines.BUNDLE_TRIP)) {
            customerTrip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_TRIP);
        }
        if (getIntent().hasExtra(Defines.BUNDLE_NOTIFY_TRIP)) {
            mApi.getTripInfo(customerTrip.getId(), new ApiUtilities.TripInformationListener() {
                @Override
                public void onSuccess(Trip trip) {
                    customerTrip = trip;
                    SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    map.getMapAsync(AcceptBookingActivity.this);
                    initComponents();
                }
            });
        }else {
            SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            map.getMapAsync(this);
            initComponents();
        }
        if (!Global.isStartThread) {
            Global.threadLocation = new DriverLocation(this);
            Thread t  = new Thread(Global.threadLocation);
            t.start();
            Global.isStartThread = true;
        }
    }

    private void initComponents(){
        layoutCustomer = (CustomerInfoLayout) findViewById(R.id.fragment_customer_infor);
        txtCustomerName = (TextView) layoutCustomer.findViewById(R.id.customer_name);
        txtSource = (TextView) layoutCustomer.findViewById(R.id.customer_from);
        txtDestination = (TextView) layoutCustomer.findViewById(R.id.customer_to);
        txtNote = (TextView) layoutCustomer.findViewById(R.id.customer_note);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        btnFinishTrip = (Button) findViewById(R.id.btn_finish_trip);
        navigation.setOnNavigationItemSelectedListener(this);
        txtCustomerName.setText(customerTrip.getCustomerName());
        txtSource.setText(customerTrip.getListStopPoints().get(0).getFullPlace());
        int size = customerTrip.getListStopPoints().size();
        txtDestination.setText(customerTrip.getListStopPoints().get(size - 1).getFullPlace());
        if(customerTrip.getNote().equals("null"))
            txtNote.setText("");
        else
            txtNote.setText(customerTrip.getNote());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (customerTrip.getStatus() == Defines.BOOKING_IN_PROGRESS) {
            getSupportActionBar().setTitle("Đang trong chuyến đi");
            Global.inTrip = true;
            btnFinishTrip.setVisibility(View.VISIBLE);
            navigation.setVisibility(View.GONE);
            btnFinishTrip.setOnClickListener(this);
        } else if (customerTrip.getStatus() == Defines.BOOKING_WELCOME_CUSTOMER)
            getSupportActionBar().setTitle("Đang trên đường đón khách");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
        ImageView imgEdit = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_edit);
        ImageView imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        imgAvatar.setImageResource(R.drawable.driver);
        if (getIntent().hasExtra(Defines.BUNDLE_USER)) {
            user = (User) getIntent().getSerializableExtra(Defines.BUNDLE_USER);
            txtName.setText(user.getName());
            if (!user.getEmail().equals("null"))
                txtEmail.setText(user.getEmail());
            else
                txtEmail.setText("");
        } else {
            txtName.setText(preference.getName());
            txtEmail.setText("");
        }
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ConfigureAccountActivity.class);
                startActivityForResult(intent, Defines.CONFIGURE_CODE);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.check_connection, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentPosition();
        locationProvide = new LocationProvide(this,this);
        locationProvide.startUpdatesButtonHandler();
    }

    private void getCurrentPosition() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                DialogUtils.settingRequestTurnOnLocation(this);
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                .title("Vị trí của bạn"));
      /*  CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation.getPosition())             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates BookingLongTripAroundAdapter CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        Global.threadLocation.setMarker(currentLocation);
        sendRequestFindDirection();
    }

    private void sendRequestFindDirection() {
        removeAllMarker();
        try {
            for (Position location : customerTrip.getListStopPoints()) {
                markerList.add(mMap.addMarker(new MarkerOptions()
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                        .title(location.getPrimaryText())
                        .position(location.getLatLng())));
            }
            new DirectionFinder(this, customerTrip.getListStopPoints()).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void removeAllMarker(){
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
        polylinePaths.clear();
        for (Marker marker : markerList)
            marker.remove();
        markerList.clear();
    }

    private void updateMapCamera(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = (int) CommonUtilities.convertDpToPixel(40,mContext); // offset from edges of the map in pixels
        mMap.setPadding(padding,measureView(toolbar)+measureView(layoutCustomer)+(int)CommonUtilities.convertDpToPixel(50,mContext),padding, measureView(navigation)+(int)CommonUtilities.convertDpToPixel(20,mContext));
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        mMap.animateCamera(cu);
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    //======================================== Direction Finder implement ==========================
    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(12);
            PolylineOptions polylineOptions1 = new PolylineOptions().
                    geodesic(true).
                    color(ContextCompat.getColor(mContext,R.color.blue_light)).
                    width(8);
            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                polylineOptions1.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            polylinePaths.add(mMap.addPolyline(polylineOptions1));
        }
        updateMapCamera();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.navigation_call:
                Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+customerTrip.getCustomerPhone()));
                startActivity(callIntent);
                break;
            case R.id.navigation_cancel:
                cancelTrip();
                break;
            case R.id.navigation_welcome:
                goToReceiveCustomer();
                break;
            case R.id.nav_history:
                Intent intentHisroty = new Intent(mContext, HistoryTripActivity.class);
                intentHisroty.putExtra(Defines.BUNDLE_USER, preference.getDriverId());
                startActivity(intentHisroty);
                break;
            case R.id.nav_log_out:
                DialogUtils.showLoginDialog((Activity) mContext, new DialogUtils.YesNoListenter() {
                    @Override
                    public void onYes() {
                        SharePreference preference = new SharePreference(mContext);
                        preference.saveDriverId(0);
                        Intent intent = new Intent(mContext, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
            case R.id.nav_schedule:
                Intent schedule = new Intent(mContext, ScheduleTripActivity.class);
                startActivity(schedule);
                break;

            case R.id.nav_wallet:
                Intent wallet = new Intent(mContext, DriverWalletActivity.class);
                startActivity(wallet);
                break;
        }

        return true;
    }

    private void cancelTrip() {
        DialogUtils.showCancelTripConfirm((Activity) mContext, new DialogUtils.ConfirmListenter() {
            @Override
            public void onConfirm(String reason) {
                ApiUtilities mApi = new ApiUtilities(mContext);
                final SharePreference preference = new SharePreference(mContext);
                mApi.driverCancelTrip(customerTrip.getId(), preference.getDriverId(), preference.getPhone(), reason, new ApiUtilities.CancelTripListener() {
                    @Override
                    public void onSuccess() {
                        Global.inTrip = false;
                        preference.saveStatus(0);
                        Intent intent = new Intent(AcceptBookingActivity.this, ListBookingAroundActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        });
    }

    private void goToReceiveCustomer() {
        Global.inTrip = true;
        Global.totalDistance = 0;
        btnFinishTrip.setVisibility(View.VISIBLE);
        navigation.setVisibility(View.GONE);
        btnFinishTrip.setOnClickListener(this);
        SharePreference preference = new SharePreference(mContext);
        mApi.catchCustomer(customerTrip.getId(), preference.getDriverId(),null);
        toolbar.setTitle("Đang trong chuyến đi");
    }

    public void showCurrentLocation(View v){
        showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation.getPosition())             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates BookingLongTripAroundAdapter CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish_trip:
                int distance = calculateRealDistance();
                SharePreference preference = new SharePreference(mContext);
                mApi.confirmTrip(customerTrip.getId(), preference.getDriverId(), distance, new ApiUtilities.ConfirmTripListener() {
                    @Override
                    public void onSuccess(long price, long distance, String dateTime) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        PaymentDialog dialog = new PaymentDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString(Defines.BUNDLE_CUSTOMER_NAME,customerTrip.getCustomerName());
                        bundle.putInt(Defines.BUNDLE_PRICE, (int) price);
                        bundle.putString(Defines.BUNDLE_TRIP_DATE,dateTime);
                        dialog.setArguments(bundle);
                        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.TitleDialog);
                        dialog.setCancelable(false);
                        dialog.setOnFinishTripListener(AcceptBookingActivity.this);
                        dialog.show(fragmentManager, "Input Dialog");
                        locationProvide.stopLocationUpdates();
                        Global.inTrip = false;
                        Global.totalDistance = 0;
                    }
                });
        }
    }

    private int calculateRealDistance() {
        if (customerTrip.getDistance() > Global.totalDistance)
            return Global.totalDistance;
        else {
            if (customerTrip.getDistance() - Global.totalDistance <=  (Global.totalDistance*5/100)){
                return Global.totalDistance;
            }else {
                return customerTrip.getDistance()+ (Global.totalDistance*5/100);
            }
        }
    }

    @Override
    public void onFinishTrip() {
        SharePreference preference = new SharePreference(this);
        preference.saveStatus(0);
        Intent intent = new Intent(AcceptBookingActivity.this, ListBookingAroundActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onStopUpdate() {

    }

    @Override
    public void onUpdate(Location mCurrentLocation) {
        MarkerAnimation.animateMarker(mCurrentLocation,currentLocation);
        LatLng endPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if (Global.inTrip)
            Global.totalDistance += CommonUtilities.distanceInMeter(currentLocation.getPosition(),endPosition);
       //Toast.makeText(this, Global.totalDistance+"",Toast.LENGTH_SHORT).show();
    }
}
