package grab.com.thuexetoancau.driver.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import grab.com.thuexetoancau.driver.utilities.SharePreference;
import grab.com.thuexetoancau.driver.widget.AcceptBookDialog;

public class ListBookingAroundActivity extends BaseActivity implements
        BookingAroundAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
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
    private TextView txtNoBookMessage, tryAgain;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_booking_around);
        mApi = new ApiUtilities(this);
        mContext = this;
        if (getIntent().hasExtra(Defines.BUNDLE_TRIP_BACKGROUND)){
            Trip trip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_TRIP_BACKGROUND);
            FragmentManager fragmentManager = getSupportFragmentManager();
            AcceptBookDialog dialog = new AcceptBookDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Defines.BUNDLE_TRIP,trip);
            dialog.setArguments(bundle);
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            dialog.setCancelable(false);
            dialog.show(fragmentManager, "Input Dialog");
        }
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chuyến xe quanh đây");
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_booking_around);
        swipeRefresh.setOnRefreshListener(this);
        layoutNoBooking = (RelativeLayout) findViewById(R.id.layout_no_booking);
        tryAgain = (TextView) findViewById(R.id.txt_try_again);
        txtNoBookMessage = (TextView) findViewById(R.id.txt_no_book_message);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        SharePreference preference = new SharePreference(this);
        if (preference.getStatus() == 1){
            showNoBookingLayout();
            return;
        }
        mApi.getBookingAround(gpsTracker.getLatitude(), gpsTracker.getLongitude(), new ApiUtilities.AroundBookingListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                swipeRefresh.setRefreshing(false);
                if (arrayTrip == null){
                    showNoBookingLayout();
                    return;
                }
                showBookingLayout();
                listTrip = arrayTrip;
                adapter = new BookingAroundAdapter(mContext, listTrip);
                listAroundBooking.setAdapter(adapter);
                adapter.setOnClickListener(ListBookingAroundActivity.this);
            }
        });
    }

    private void showNoBookingLayout() {
        layoutNoBooking.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        SharePreference preference = new SharePreference(this);
        if (preference.getStatus() == 1){
            txtNoBookMessage.setText(getString(R.string.you_re_offline));
            tryAgain.setVisibility(View.GONE);
        }else{
            txtNoBookMessage.setText(getString(R.string.no_booking_message));
            tryAgain.setVisibility(View.VISIBLE);
            tryAgain.setOnClickListener(this);
        }

    }

    private void showBookingLayout() {
        layoutNoBooking.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
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

        if (id == R.id.nav_log_out) {
            SharePreference preference = new SharePreference(this);
            preference.saveDriverId(0);
            Intent intent = new Intent(mContext, SplashActivity.class);
            startActivity(intent);
            finish();
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

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        getDataFromServer();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharePreference preference = new SharePreference(this);
        MenuItem item = menu.findItem(R.id.action_status);
        // set your desired icon here based on a flag if you like
        if (preference.getStatus() == 0){
            item.setIcon(ContextCompat.getDrawable(mContext,R.drawable.item_online));
            item.setTitle(getString(R.string.online));
        }else {
            item.setIcon(ContextCompat.getDrawable(mContext,R.drawable.item_offline));
            item.setTitle(getString(R.string.offline));
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_status) {
            SharePreference preference = new SharePreference(this);
            if (preference.getStatus() == 0){
                preference.saveStatus(1);
                item.setIcon(ContextCompat.getDrawable(mContext,R.drawable.item_offline));
                item.setTitle(getString(R.string.offline));
            }else {
                preference.saveStatus(0);
                item.setIcon(ContextCompat.getDrawable(mContext,R.drawable.item_online));
                item.setTitle(getString(R.string.online));
            }
            getDataFromServer();
        }

        return super.onOptionsItemSelected(item);
    }

}
