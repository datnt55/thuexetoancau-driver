package grab.com.thuexetoancau.driver.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.adapter.BookingAroundAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.thread.DriverLocation;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;

public class ListBookingAroundActivity extends AppCompatActivity implements
        BookingAroundAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{
    private RecyclerView listAroundBooking;
    private ArrayList<Trip> listTrip;
    private BookingAroundAdapter adapter;
    private ApiUtilities mApi;
    private GPSTracker gpsTracker;
    private Context mContext;
    private Toolbar toolbar;
    private RelativeLayout layoutNoBooking;
    private TextView tryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_booking_around);
        mApi = new ApiUtilities(this);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chuyến xe quanh đây");
        layoutNoBooking = (RelativeLayout) findViewById(R.id.layout_no_booking);
        tryAgain = (TextView) findViewById(R.id.txt_try_again);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listAroundBooking = (RecyclerView) findViewById(R.id.list_booking_around);
        listAroundBooking.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listAroundBooking.setLayoutManager(layoutManager);
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                DialogUtils.settingRequestTurnOnLocation(this);
            } else
                getDataFromServer();
        }
        Thread t = new Thread(new DriverLocation(this));
        t.start();
    }

    private void getDataFromServer(){
        mApi.getBookingAround(gpsTracker.getLatitude(), gpsTracker.getLongitude(), new ApiUtilities.AroundBookingListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                if (arrayTrip == null){
                    showNoBookingLayout();
                    return;
                }
                listTrip = arrayTrip;
                adapter = new BookingAroundAdapter(mContext, listTrip);
                listAroundBooking.setAdapter(adapter);
                adapter.setOnClickListener(ListBookingAroundActivity.this);
            }
        });
    }

    private void showNoBookingLayout() {
        layoutNoBooking.setVisibility(View.VISIBLE);
        listAroundBooking.setVisibility(View.GONE);
        tryAgain.setOnClickListener(this);
    }

    @Override
    public void onClicked(Trip trip) {
        Intent intent = new Intent(mContext, AcceptBookingActivity.class);
        intent.putExtra(Defines.BUNDLE_TRIP, trip);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_try_again:
                layoutNoBooking.setVisibility(View.GONE);
                listAroundBooking.setVisibility(View.VISIBLE);
                getDataFromServer();
                break;
        }
    }
}
