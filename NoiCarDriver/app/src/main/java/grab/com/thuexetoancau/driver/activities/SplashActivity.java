package grab.com.thuexetoancau.driver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private ImageView imgLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new SharePreference(this);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }
    @Override
    protected void onResume() {
        super.onResume();
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

    private void goToApplication() {
        if (preference.getDriverId() != 0){
            Intent i = new Intent(SplashActivity.this, ListBookingAroundActivity.class);
            startActivity(i);
            finish();
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
