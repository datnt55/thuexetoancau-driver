package grab.com.thuexetoancau.driver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.PermissionUtils;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private ImageView imgLoading;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtils.checkAndRequestPermissions(this)){
            initComponents();
        }

    }

    private void initComponents() {
        setContentView(R.layout.activity_splash);
        mContext = this;
        preference = new SharePreference(this);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        if (preference.getRegId().equals("")) {
            LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToApplication();
                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_ID_MULTIPLE_PERMISSIONS)
            if ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED))
                initComponents();
    }

    private void goToApplication() {
        if (preference.getDriverId() != 0){
            ApiUtilities mApi = new ApiUtilities(this);
            mApi.login(preference.getPhone(), preference.getPassword(), new ApiUtilities.LoginResponseListener() {
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
