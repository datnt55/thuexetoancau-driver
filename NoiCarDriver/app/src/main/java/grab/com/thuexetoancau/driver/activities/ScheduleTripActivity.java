package grab.com.thuexetoancau.driver.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.adapter.ScheduleTripAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

public class ScheduleTripActivity extends AppCompatActivity implements ScheduleTripAdapter.ItemClickListener{

    private RecyclerView listFavorite;
    private ScheduleTripAdapter adapter;
    private Context mContext;
    private int userId;
    private Toolbar toolbar;
    private int bookingId, tripType;
    private User driver;
    private ApiUtilities mApi;
    private SharePreference preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_trip);
        mContext = this;
        preference = new SharePreference(this);
        mApi = new ApiUtilities(this);
        if (getIntent().hasExtra(Defines.BUNDLE_USER))
            userId = getIntent().getIntExtra(Defines.BUNDLE_USER, 0);
        else
            userId = preference.getDriverId();
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lich trình chuyến đi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listFavorite = (RecyclerView) findViewById(R.id.list_schedule);
        listFavorite.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listFavorite.setLayoutManager(layoutManager);

        mApi.getScheduleTrip(userId, new ApiUtilities.AroundBookingListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                adapter = new ScheduleTripAdapter(mContext, arrayTrip);
                listFavorite.setAdapter(adapter);
                adapter.setOnItemClickListener(ScheduleTripActivity.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(final Trip trip) {
        Intent intent = new Intent(mContext, AcceptBookingActivity.class);
        trip.setStatus(Defines.BOOKING_WELCOME_CUSTOMER);
        intent.putExtra(Defines.BUNDLE_TRIP,trip);
        startActivity(intent);
        ((Activity)mContext).finish();
    }
}
