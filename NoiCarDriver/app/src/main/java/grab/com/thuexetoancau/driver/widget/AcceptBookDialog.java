package grab.com.thuexetoancau.driver.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.AcceptBookingActivity;
import grab.com.thuexetoancau.driver.activities.ListBookingAroundActivity;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.Global;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by DatNT on 8/2/2017.
 */

public class AcceptBookDialog extends DialogFragment implements View.OnClickListener{
    private ArcProgress progress;
    private Button btnAccept, btnDeny;
    private ApiUtilities mApi;
    private Trip trip;
    private TextView txtSource, txtSourceSecond;
    private TextView txtDestination, txtDestinationSecond;
    private TextView txtDistance, txtPrice;
    private int driverId;
    private Context mContext;

    public AcceptBookDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Bundle bundle = getArguments();
        trip = (Trip) bundle.getSerializable(Defines.BUNDLE_TRIP);
        driverId = bundle.getInt(Defines.BUNDLE_DRIVER_ID);
        View view = inflater.inflate(R.layout.dialog_new_booking, container);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        txtSource = (TextView) view.findViewById(R.id.text_source);
        txtSourceSecond = (TextView) view.findViewById(R.id.text_source_seconde);
        txtDestination = (TextView) view.findViewById(R.id.txt_destination);
        txtDestinationSecond = (TextView) view.findViewById(R.id.txt_destination_second);
        txtDistance = (TextView) view.findViewById(R.id.txt_distance);
        txtPrice = (TextView) view.findViewById(R.id.txt_price);
        txtSource.setText( trip.getListStopPoints().get(0).getPrimaryText());
        txtSourceSecond.setText( trip.getListStopPoints().get(0).getSecondText());
        int size = trip.getListStopPoints().size();
        txtDestination.setText( trip.getListStopPoints().get(size-1).getPrimaryText());
        txtDestinationSecond.setText( trip.getListStopPoints().get(size-1).getSecondText());
        txtDistance.setText(CommonUtilities.convertToKilometer(trip.getDistance()));
        txtPrice.setText(CommonUtilities.convertCurrency(trip.getPrice())+" vnđ");
        mApi = new ApiUtilities(mContext);
        progress = (ArcProgress) view.findViewById(R.id.arc_progress);
        btnAccept = (Button) view.findViewById(R.id.btn_accept);
        btnDeny = (Button) view.findViewById(R.id.btn_deny);
        btnAccept.setOnClickListener(this);
        btnDeny.setOnClickListener(this);
        Global.count = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                progress.setProgress(30 - (int) millisUntilFinished/1000);
            }

            public void onFinish() {
                Global.count = null;
                Log.e("TRIP","cancel");
                progress.setProgress(0);
                cancelTrip();
                Intent intent = new Intent(mContext,ListBookingAroundActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                final NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext)
                        .setAutoCancel(true)
                        .setContentTitle("Thuê xe toàn cầu driver")
                        .setContentText("Bạn đã lỡ 1 chuyến đi")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[] {1, 1, 1});
                NotificationManager managerCancel = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                managerCancel.notify(Defines.NOTIFY_TAG,trip.getId(), notification.build());
            }

       }.start();


    }

    private void cancelTrip() {
        Global.inTrip = false;
        mApi.driverNoReceiverTrip(trip.getId(), driverId, new ApiUtilities.CancelTripListener() {
            @Override
            public void onSuccess() {
                AcceptBookDialog.this.dismissAllowingStateLoss();
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void acceptTrip() {
        Global.count.cancel();
        Global.count = null;
        mApi.receivedTrip(trip.getId(),driverId, new ApiUtilities.AcceptTripListener() {
            @Override
            public void onSuccess(User user) {
                AcceptBookDialog.this.dismissAllowingStateLoss();
                Global.receiveTrip = true;
                Intent intent = new Intent(mContext, AcceptBookingActivity.class);
                trip.setCustomerName(user.getName());
                trip.setCustomerPhone(user.getPhone());
                intent.putExtra(Defines.BUNDLE_TRIP,trip);
                intent.putExtra(Defines.BUNDLE_NOTIFY_TRIP,"");
                startActivity(intent);
                ((Activity)mContext).finish();
            }

            @Override
            public void onFailure() {
                AcceptBookDialog.this.dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x ), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(mContext, getTheme()){
            @Override
            public void onBackPressed() {
                //AcceptBookDialog.this.dismiss();
                return;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_accept:
                acceptTrip();
                break;
            case R.id.btn_deny:
                Global.count.cancel();
                Global.count = null;
                cancelTrip();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
}