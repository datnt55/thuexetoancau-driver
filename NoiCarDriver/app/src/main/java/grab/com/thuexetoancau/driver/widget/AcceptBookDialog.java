package grab.com.thuexetoancau.driver.widget;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

/**
 * Created by DatNT on 8/2/2017.
 */

public class AcceptBookDialog extends DialogFragment implements View.OnClickListener{
    private ArcProgress progress;
    private Button btnAccept;
    private ApiUtilities mApi;
    private Trip trip;
    private TextView txtSource, txtSourceSecond;
    private TextView txtDestination, txtDestinationSecond;
    private TextView txtDistance, txtPrice;

    public AcceptBookDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Bundle bundle = getArguments();
        trip = (Trip) bundle.getSerializable(Defines.BUNDLE_TRIP);
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
        mApi = new ApiUtilities(getActivity());
        progress = (ArcProgress) view.findViewById(R.id.arc_progress);
        btnAccept = (Button) view.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                progress.setProgress(30 - (int) millisUntilFinished/1000);
            }

            public void onFinish() {
                progress.setProgress(0);
                cancelTrip();
                dismiss();
            }

       }.start();


    }

    private void cancelTrip() {
        SharePreference preference = new SharePreference(getActivity());
        mApi.driverCancelTrip(trip.getId(), preference.getDriverId(), preference.getPhone(), "", new ApiUtilities.CancelTripListener() {
            @Override
            public void onSuccess() {
                dismiss();
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void acceptTrip() {
        SharePreference preference = new SharePreference(getActivity());
        /*mApi.receivedTrip(trip.getId(), preference.getDriverId(), preference.getPhone(), "", new ApiUtilities.CancelTripListener() {
            @Override
            public void onSuccess() {
                dismiss();
            }

            @Override
            public void onFailure() {

            }
        });*/
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
        return new Dialog(getActivity(), getTheme()){
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
        }
    }
}