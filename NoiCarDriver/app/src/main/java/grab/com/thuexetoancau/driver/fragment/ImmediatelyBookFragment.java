package grab.com.thuexetoancau.driver.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.AcceptBookingActivity;
import grab.com.thuexetoancau.driver.activities.ListBookingAroundActivity;
import grab.com.thuexetoancau.driver.adapter.BookingImmediateAroundAdapter;
import grab.com.thuexetoancau.driver.adapter.BookingLongTripAroundAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

/**
 * Created by DatNT on 8/9/2017.
 */

public class ImmediatelyBookFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,  BookingImmediateAroundAdapter.OnItemClickListener{
    private RecyclerView listAroundBooking;
    private ArrayList<Trip> listTrip;
    private BookingImmediateAroundAdapter adapter;
    private RelativeLayout layoutNoBooking;
    private TextView txtNoBookMessage, tryAgain;
    private SwipeRefreshLayout swipeRefresh;
    private ApiUtilities mApi;
    private GPSTracker gpsTracker;
    private SharePreference preference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preference = new SharePreference(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_immediately_book, container, false);
        ((ListBookingAroundActivity) getActivity()).changeStateListener(new ListBookingAroundActivity.ChangeStateListener() {
            @Override
            public void onRefresh() {
                gpsTracker = new GPSTracker(getActivity());
                if (gpsTracker.handlePermissionsAndGetLocation()) {
                    if (gpsTracker.canGetLocation())
                        getDataFromServer();
                }
            }
        });
        initComponents(rootView);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initComponents(View rootView) {
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_booking_around);
        swipeRefresh.setOnRefreshListener(this);
        layoutNoBooking = (RelativeLayout) rootView.findViewById(R.id.layout_no_booking);
        tryAgain = (TextView) rootView.findViewById(R.id.txt_try_again);
        txtNoBookMessage = (TextView) rootView.findViewById(R.id.txt_no_book_message);
        listAroundBooking = (RecyclerView) rootView.findViewById(R.id.list_booking_around);
        listAroundBooking.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listAroundBooking.setLayoutManager(layoutManager);
        mApi = new ApiUtilities(getActivity());
        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                DialogUtils.settingRequestTurnOnLocation(getActivity());
            } else
                getDataFromServer();
        }
    }

    private void getDataFromServer(){
        SharePreference preference = new SharePreference(getActivity());
        if (preference.getStatus() == 1){
            showNoBookingLayout();
            return;
        }
        mApi.getBookingAround(gpsTracker.getLatitude(), gpsTracker.getLongitude(),1, new ApiUtilities.AroundBookingListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                swipeRefresh.setRefreshing(false);
                if (arrayTrip == null){
                    showNoBookingLayout();
                    return;
                }
                showBookingLayout();
                listTrip = arrayTrip;
                adapter = new BookingImmediateAroundAdapter(getActivity(), listTrip);
                listAroundBooking.setAdapter(adapter);
                adapter.setOnClickListener(ImmediatelyBookFragment.this);
            }
        });
    }

    private void showNoBookingLayout() {
        layoutNoBooking.setVisibility(View.VISIBLE);
        listAroundBooking.setVisibility(View.GONE);
        SharePreference preference = new SharePreference(getActivity());
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
        listAroundBooking.setVisibility(View.VISIBLE);
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
        if (!CommonUtilities.isOnline(getActivity())) {
            DialogUtils.showDialogNetworkError(getActivity(), null);
            swipeRefresh.setRefreshing(false);
            return ;
        }
        if (preference.getStatus() == 1){
            swipeRefresh.setRefreshing(false);
            return;
        }
        getDataFromServer();
    }

    @Override
    public void onClicked(final Trip trip) {
        DialogUtils.confirmReiceverTrip(getActivity(), new DialogUtils.YesNoListenter() {
            @Override
            public void onYes() {
                mApi.receivedTrip(trip.getId(),preference.getDriverId(), new ApiUtilities.AcceptTripListener() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(getActivity(), AcceptBookingActivity.class);
                        trip.setCustomerName(user.getName());
                        trip.setCustomerPhone(user.getPhone());
                        intent.putExtra(Defines.BUNDLE_TRIP,trip);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            }

            @Override
            public void onNo() {

            }
        });
    }
}