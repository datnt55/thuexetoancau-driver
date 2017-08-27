package grab.com.thuexetoancau.driver.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.adapter.HistoryTripAdapter;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;

/**
 * Created by DatNT on 8/28/2017.
 */

public class HistoryTripActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView listHistory;
    private HistoryTripAdapter adapter;
    private Context mContext;
    private int userId;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeToRefresh;
    private RelativeLayout layoutNoTrip;
    private ApiUtilities mApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histoty_trip);
        mContext = this;
        userId = getIntent().getIntExtra(Defines.BUNDLE_USER, 0);
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lịch sử chuyến đi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        swipeToRefresh =(SwipeRefreshLayout) findViewById(R.id.swipe_view);
        layoutNoTrip = (RelativeLayout) findViewById(R.id.layout_no_trip);
        swipeToRefresh.setOnRefreshListener(this);
        listHistory = (RecyclerView) findViewById(R.id.list_favorite);
        listHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listHistory.setLayoutManager(layoutManager);

        mApi = new ApiUtilities(this);
        getHistoryTrip();
    }

    private void getHistoryTrip(){
        mApi.getHistoryTrip(userId, new ApiUtilities.ResponseTripListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                if (arrayTrip == null){
                    layoutNoTrip.setVisibility(View.VISIBLE);
                    listHistory.setVisibility(View.GONE);
                }else {
                    layoutNoTrip.setVisibility(View.GONE);
                    listHistory.setVisibility(View.VISIBLE);
                    adapter = new HistoryTripAdapter(mContext, arrayTrip);
                    listHistory.setAdapter(adapter);
                }
                if (swipeToRefresh.isRefreshing())
                    swipeToRefresh.setRefreshing(false);

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
    public void onRefresh() {
        getHistoryTrip();
    }
}
