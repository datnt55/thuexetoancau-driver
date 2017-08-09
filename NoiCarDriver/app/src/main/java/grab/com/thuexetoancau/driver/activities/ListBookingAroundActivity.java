package grab.com.thuexetoancau.driver.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.adapter.BookingAroundAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;

public class ListBookingAroundActivity extends AppCompatActivity {
    private RecyclerView listAroundBooking;
    private ArrayList<Trip> listTrip;
    private BookingAroundAdapter adapter;
    private ApiUtilities mApi;
    private GPSTracker gpsTracker;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_booking_around);
        mApi = new ApiUtilities(this);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
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




    }

    private void getDataFromServer(){
        mApi.getBookingAround(gpsTracker.getLatitude(), gpsTracker.getLongitude(), new ApiUtilities.AroundBookingListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                listTrip = arrayTrip;
                adapter = new BookingAroundAdapter(mContext, listTrip);
                listAroundBooking.setAdapter(adapter);
            }
        });
    }
}
