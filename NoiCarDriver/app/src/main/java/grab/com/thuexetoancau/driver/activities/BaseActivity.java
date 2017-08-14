package grab.com.thuexetoancau.driver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.widget.AcceptBookDialog;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("newBooking"));
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Trip trip = (Trip) intent.getSerializableExtra(Defines.BUNDLE_TRIP);
                FragmentManager fragmentManager = getSupportFragmentManager();
                AcceptBookDialog dialog = new AcceptBookDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Defines.BUNDLE_TRIP, trip);
                dialog.setArguments(bundle);
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                dialog.setCancelable(false);
                dialog.show(fragmentManager, "Input Dialog");
            } catch (IllegalStateException e) {
            }
        }
    };

}
