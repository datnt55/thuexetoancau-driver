package grab.com.thuexetoancau.driver.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.DateTime;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.DialogUtils;
import grab.com.thuexetoancau.driver.utilities.GPSTracker;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.PermissionUtils;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private ImageView imgLoading;
    private Context mContext;
    private GPSTracker gpsTracker;
    private ApiUtilities mApi;
    private LinearLayout layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        mApi = new ApiUtilities(this);
        if (PermissionUtils.checkAndRequestPermissions(this)){
            initComponents();
        }

    }

    private void initComponents() {
        setContentView(R.layout.activity_splash);
        mContext = this;
        preference = new SharePreference(this);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        checkOnline();
    }

    private void checkOnline() {
        if (!CommonUtilities.isOnline(this)){
            DialogUtils.showDialogNetworkError(this, new DialogUtils.TryAgain() {
                @Override
                public void onTryAgain() {
                    checkOnline();
                }
            });
            return;
        }

        mApi.getCurrentTime(new ApiUtilities.ServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                DateTime current = new DateTime();
                Global.serverTimeDiff = time - current.getMillis();
                layoutLoading.setVisibility(View.VISIBLE);
                if (preference.getRegId().equals("")) {
                    LocalBroadcastManager.getInstance(mContext).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
                }else {
                    GPSTracker gpsTracker = new GPSTracker(mContext);
                    if (gpsTracker.handlePermissionsAndGetLocation()) {
                        if (!gpsTracker.canGetLocation()) {
                            DialogUtils.settingRequestTurnOnLocation((Activity)mContext);
                        } else
                            goToApplication();
                    }


                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED))
                initComponents();
        }
    }

    private void goToApplication() {
        if (preference.getDriverId() != 0){
            ApiUtilities mApi = new ApiUtilities(this);
            mApi.login(preference.getPhone(), preference.getPassword(),null, new ApiUtilities.LoginResponseListener() {
                @Override
                public void onSuccess(User user, Trip trip) {
                    Intent intent = null;
                    if (trip != null) {
                        intent = new Intent(mContext, AcceptBookingActivity.class);
                        intent.putExtra(Defines.BUNDLE_USER, user);
                        intent.putExtra(Defines.BUNDLE_TRIP, trip);
                    }else {
                        intent = new Intent(mContext, ListBookingAroundActivity.class);
                        intent.putExtra(Defines.BUNDLE_USER, user);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Defines.REQUEST_LOCATION_ENABLE){
            GPSTracker gpsTracker = new GPSTracker(this);
            if (gpsTracker.canGetLocation()) {
                if (gpsTracker.getLongitude() == 0 && gpsTracker.getLatitude() == 0) {
                    gpsTracker.getLocationCoodinate(new GPSTracker.LocateListener() {
                        @Override
                        public void onLocate(double mlongitude, double mlatitude) {
                            goToApplication();
                        }
                    });
                } else {
                    goToApplication();
                }
            }else
                DialogUtils.settingRequestTurnOnLocation((Activity)mContext);
        }
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("token");
            if(token != null)
            {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }


        }
    };
}
