package grab.com.thuexetoancau.driver.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.adapter.ViewPagerAdapter;
import grab.com.thuexetoancau.driver.fragment.ImmediatelyBookFragment;
import grab.com.thuexetoancau.driver.fragment.LongRoadBookFragment;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.thread.DriverLocation;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.SharePreference;
import grab.com.thuexetoancau.driver.widget.AcceptBookDialog;

public class ListBookingAroundActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context mContext;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    /*private int[] tabIcons = {
            R.drawable.ic_close_black_24dp,
            R.drawable.car_connected
    };*/
    private SharePreference preference;
    private User user;
    private int bookingId;
    private AcceptBookDialog dialog;
    private ChangeStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_booking_around);
        preference = new SharePreference(this);
        mContext = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverTrip, new IntentFilter(Defines.BROADCAST_FOUND_CUSTOMER));
        LocalBroadcastManager.getInstance(this).registerReceiver(cancelTrip, new IntentFilter(Defines.BROADCAST_CANCEL_TRIP));

        if (getIntent().hasExtra(Defines.BUNDLE_FOUND_CUSTOMER)) {
            Global.countDownTimer.cancel();
            if (Global.count != null)
                Global.count.cancel();
            if (user == null) {
                ApiUtilities mApi = new ApiUtilities(this);
                mApi.login(preference.getPhone(), preference.getPassword(), null, new ApiUtilities.LoginResponseListener() {
                    @Override
                    public void onSuccess(User mUser, Trip mtrip) {
                        user = mUser;
                        Trip trip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_TRIP_BACKGROUND);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        AcceptBookDialog dialog = new AcceptBookDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Defines.BUNDLE_TRIP, trip);
                        bundle.putInt(Defines.BUNDLE_DRIVER_ID, preference.getDriverId());
                        dialog.setArguments(bundle);
                        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                        dialog.setCancelable(false);
                        dialog.show(fragmentManager, "Input Dialog");

                    }
                });
            }

        }

        initComponents();
    }

    BroadcastReceiver receiverTrip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Global.countDownTimer != null) {
                    Log.e("COUNTER", "cancel");
                    Global.countDownTimer.cancel();
                }
                if (Global.count != null)
                    Global.count.cancel();
                Trip trip = (Trip) intent.getSerializableExtra(Defines.BUNDLE_TRIP);
                bookingId = trip.getId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialog = new AcceptBookDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Defines.BUNDLE_TRIP, trip);
                bundle.putInt(Defines.BUNDLE_DRIVER_ID, preference.getDriverId());
                dialog.setArguments(bundle);
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                dialog.setCancelable(false);
                getSupportFragmentManager().beginTransaction().add(dialog, "tag").commitAllowingStateLoss();
            } catch (IllegalStateException e) {
            }
        }
    };

    BroadcastReceiver cancelTrip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                bookingId = intent.getIntExtra(Defines.BUNDLE_BOOKING_ID, 0);
                if (dialog != null)
                    if (dialog.getDialog().isShowing()) {
                        dialog.dismiss();
                    }
                Toast.makeText(ListBookingAroundActivity.this, "Khách đã hủy chuyến đi", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Defines.NOTIFY_TAG, bookingId);
        if (listener != null)
            listener.onRefresh();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chuyến xe quanh đây");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupTabView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        TextView txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
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
        if (!Global.isStartThread) {
            Thread t = new Thread(new DriverLocation(this));
            t.start();
            Global.isStartThread = true;
        }
    }

    // Init 2 fragment
    private void setupTabView() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
       /* tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);*/
    /*    tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.blue_light), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.grey_1), PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.blue_light), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.grey_1), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ImmediatelyBookFragment(), "Chuyến đi ngay");
        adapter.addFrag(new LongRoadBookFragment(), "Chuyến đi sau");
        viewPager.setAdapter(adapter);
    }

    public void changeStateListener(ChangeStateListener listener) {
        this.listener = listener;
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
        switch (id){
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
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        // set your desired icon here based on BookingLongTripAroundAdapter flag if you like
        if (preference.getStatus() == 0) {
            item.setIcon(ContextCompat.getDrawable(mContext, R.drawable.item_online));
            item.setTitle(getString(R.string.online));
        } else {
            item.setIcon(ContextCompat.getDrawable(mContext, R.drawable.item_offline));
            item.setTitle(getString(R.string.offline));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_status) {
            SharePreference preference = new SharePreference(this);
            if (preference.getStatus() == 0) {
                preference.saveStatus(1);
                item.setIcon(ContextCompat.getDrawable(mContext, R.drawable.item_offline));
                item.setTitle(getString(R.string.offline));
            } else {
                preference.saveStatus(0);
                item.setIcon(ContextCompat.getDrawable(mContext, R.drawable.item_online));
                item.setTitle(getString(R.string.online));
            }
            if (listener != null)
                listener.onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }


    public interface ChangeStateListener {
        void onRefresh();
    }
}
