package grab.com.thuexetoancau.driver.widget;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.AcceptBookingActivity;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

/**
 * Created by DatNT on 8/2/2017.
 */

public class PaymentDialog extends DialogFragment implements View.OnClickListener{
    private Button btnFinishTrip;
    private TextView txtCustomerName, txtDate, txtMoney;
    private TextView txtPrice;
    private String customerName, tripDate;
    private int price;
    private BillSuccessListenr listener;
    private SharePreference preference;
    public PaymentDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Bundle bundle = getArguments();
        preference = new SharePreference(getActivity());
        customerName = bundle.getString(Defines.BUNDLE_CUSTOMER_NAME);
        tripDate = bundle.getString(Defines.BUNDLE_TRIP_DATE);
        price = bundle.getInt(Defines.BUNDLE_PRICE);
        View view = inflater.inflate(R.layout.dialog_bill, container);
        getDialog().setTitle("Thanh toán");
        initComponents(view);
        ApiUtilities mApi = new ApiUtilities(getActivity());
        mApi.getDriverMoney(preference.getDriverId(), new ApiUtilities.DriverMoneyListener() {
            @Override
            public void onSuccess(long price) {
                txtMoney.setText(CommonUtilities.convertRealCurrency((int) price) +" vnđ");
            }
        });
        return view;
    }

    public void setOnFinishTripListener(BillSuccessListenr listener){
        this.listener = listener;
    }

    private void initComponents(View view) {
       txtCustomerName = (TextView) view.findViewById(R.id.txt_customer_name);
        txtDate = (TextView) view.findViewById(R.id.txt_date);
        txtPrice = (TextView) view.findViewById(R.id.txt_price);
        btnFinishTrip = (Button) view.findViewById(R.id.btn_end_trip);
        txtMoney= (TextView) view.findViewById(R.id.txt_money);
        txtCustomerName.setText(customerName);
        txtDate.setText(tripDate);
        txtPrice.setText(CommonUtilities.convertCurrency(price) +" vnđ");
        btnFinishTrip.setOnClickListener(this);
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
            case R.id.btn_end_trip:
                dismiss();
                if (listener != null)
                    listener.onFinishTrip();
                break;
        }
    }

    public interface BillSuccessListenr{
        void onFinishTrip();
    }
}