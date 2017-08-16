package grab.com.thuexetoancau.driver.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.User;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.BaseService;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.GetAllRegisterCarData;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

public class RegisterActivity extends AppCompatActivity implements GetAllRegisterCarData.onDataReceived {
    private EditText edtName, edtPhone, edtPass, edtIdendify, edtLicense, edtCarNumber;
    private EditText txtCarMade, txtCarSize, txtCarYear, txtCarType;
    private TextView txtWarn;
    private TextView txtCarModel;
    private Context mContext;
    private Button btnRegister;
    private ProgressDialog dialog;
    private ArrayList<String> aCarMade, arrCarModel, aCarType, aCarSize;
    private SharePreference preference;
    private String size;
    private ApiUtilities mApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        mApi = new ApiUtilities(this);
        preference = new SharePreference(this);
        GetAllRegisterCarData data = new GetAllRegisterCarData(this);
        data.getAllCarData(this);
        initComponents();

    }
    private void initComponents() {
        edtName     = (EditText)    findViewById(R.id.edt_name);
        edtPhone    = (EditText)    findViewById(R.id.edt_phone);
        edtPass     = (EditText)    findViewById(R.id.edt_pass);
        edtIdendify = (EditText)    findViewById(R.id.edt_identify);
        edtLicense  = (EditText)    findViewById(R.id.edt_license);
        edtCarNumber= (EditText)    findViewById(R.id.edt_car_number);
        txtCarMade  = (EditText)    findViewById(R.id.edt_car_made);

        txtCarSize  = (EditText)    findViewById(R.id.edt_size);
        txtCarYear  = (EditText)    findViewById(R.id.edt_year);
        txtCarType  = (EditText)    findViewById(R.id.edt_car_type);

        txtWarn     = (TextView)    findViewById(R.id.txt_warn);

        txtCarModel = (TextView)    findViewById(R.id.edt_car_model);

        btnRegister = (Button)      findViewById(R.id.btn_confirm);
        btnRegister.setOnClickListener(register_driver_listener);
        txtCarYear.setOnClickListener(click_to_produce_year_listener);
        txtCarMade.setOnClickListener(click_to_car_made_listener);
        txtCarType.setOnClickListener(click_to_car_type_listener);
        txtCarSize.setOnClickListener(click_to_car_size_listener);

        txtCarModel.setOnClickListener(click_to_car_model_listener);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private void showDialogCarMade() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Thông báo")
                .setMessage("Bạn phải nhập hãng xe trước")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        txtCarModel.setText("");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private View.OnClickListener click_to_car_made_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn hãng xe")
                    .setSingleChoiceItems(aCarMade.toArray(new CharSequence[aCarMade.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type =aCarMade.get(which);
                            txtCarMade.setText(type);
                            txtCarModel.setText("");
                            mApi.requestCarName(txtCarMade.getText().toString(), new ApiUtilities.CarNameResponseListener() {
                                @Override
                                public void onSuccess(ArrayList<String> carName) {
                                    arrCarModel = carName;
                                }
                            });
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    };

    private View.OnClickListener click_to_car_model_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (txtCarMade.getText().toString().equals("")) {
                Toast.makeText(mContext, "Chưa chọn hãng xe", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(arrCarModel.toArray(new CharSequence[arrCarModel.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type =arrCarModel.get(which);
                            txtCarModel.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    };

    private View.OnClickListener click_to_car_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(aCarType.toArray(new CharSequence[aCarType.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type =aCarType.get(which);
                            txtCarType.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    };
    private View.OnClickListener click_to_car_size_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn số chỗ")
                    .setSingleChoiceItems(aCarSize.toArray(new CharSequence[aCarSize.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type =aCarSize.get(which);
                            size = type.split(" ")[0];
                            txtCarSize.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    };
    private View.OnClickListener click_to_produce_year_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            final Dialog dialog = new Dialog(mContext);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_year_picker);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            Button btnSet = (Button) dialog.findViewById(R.id.btn_set);
            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
            np.setMaxValue(year );
            np.setMinValue(year - 100);
            np.setValue(year - 1);
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            setDividerColor(np, android.R.color.white);
            //setDividerColor(np, android.R.color.white);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                }
            });
            btnSet.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    txtCarYear.setText(String.valueOf(np.getValue()));
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };
    private void setDividerColor(NumberPicker picker, int color){
        Field[] pickerField = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerField){
            if (pf.getName().equals("mSelectionDivider")){
                pf.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(color);
                try {
                    pf.set(picker, colorDrawable);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private View.OnClickListener register_driver_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!checkParams())
                requestRegisterDriver();
        }
    };

    private boolean checkParams() {
        if (edtName.getText().toString().equals("")|| edtName.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập tên");
            return true;
        }
        if (edtPhone.getText().toString().equals("")|| edtPhone.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số điện thoại");
            return true;
        }
        if (edtPass.getText().toString().equals("")|| edtPass.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập mật khẩu");
            return true;
        }
        if (txtCarMade.getText().toString().equals("")|| txtCarMade.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập hãng xe");
            return true;
        }
        if (txtCarModel.getText().toString().equals("")|| txtCarModel.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập tên xe");
            return true;
        }
        if (txtCarSize.getText().toString().equals("")|| txtCarSize.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số chỗ của xe");
            return true;
        }
        if (txtCarYear.getText().toString().equals("")|| txtCarYear.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập năm sản xuất xe");
            return true;
        }
        if (txtCarType.getText().toString().equals("")|| txtCarType.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập loại xe");
            return true;
        }
        if (edtCarNumber.getText().toString().equals("")|| edtCarNumber.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập biển số xe");
            return true;
        }

        if (edtIdendify.getText().toString().equals("")|| edtIdendify.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số chứng minh thư");
            return true;
        }
        if (edtLicense.getText().toString().equals("")|| edtLicense.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số bằng lái xe");
            return true;
        }

        return false;
    }

    private void requestRegisterDriver() {
        RequestParams params;
        params = new RequestParams();
        params.put("id", "");
        params.put("name", edtName.getText().toString());
        params.put("phone", edtPhone.getText().toString());
        params.put("pass", edtPass.getText().toString());
        params.put("car_made", txtCarMade.getText().toString());
        params.put("car_model", txtCarModel.getText().toString());
        params.put("car_size", size);
        params.put("car_year", txtCarYear.getText().toString());
        params.put("car_type", txtCarType.getText().toString());
        params.put("car_number", edtCarNumber.getText().toString());
        params.put("card_identify", edtIdendify.getText().toString());
        params.put("license", edtLicense.getText().toString());
        params.put("regId", preference.getToken());
        params.put("os", 1);
        Log.i("params deleteDelivery", params.toString());
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int id = Integer.valueOf(new String(responseBody));
                if (id > 0){
                    preference.saveDriverId(id);
                    preference.savePhone(edtPhone.getText().toString());
                    preference.saveName(edtName.getText().toString());
                    preference.saveCarNumber(edtCarNumber.getText().toString());
                    Intent intent = new Intent(mContext, ListBookingAroundActivity.class);
                    String name =  edtName.getText().toString();
                    String phone =  edtPhone.getText().toString();
                    String carMade =  txtCarMade.getText().toString();
                    String carModel =  txtCarModel.getText().toString();
                    String carYear =  txtCarYear.getText().toString();
                    String carType =  txtCarType.getText().toString();
                    String carNumber =  edtCarNumber.getText().toString();
                    String identity =  edtIdendify.getText().toString();
                    String license =  edtLicense.getText().toString();
                    User user = new User(name,phone,"",carModel,carMade,carYear,Integer.valueOf(size),carNumber,carType,0,0,"",identity,license);
                    intent.putExtra(Defines.BUNDLE_USER, user);
                    startActivity(intent);
                    finish();

                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, getResources().getString(R.string.register_failure), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.register_failure), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onReceived(ArrayList<String> aCarMade, ArrayList<String> aCarTypes, ArrayList<String> aCarSize) {
        this.aCarSize = new ArrayList<>();
        this.aCarMade = aCarMade;
        this.aCarType = aCarTypes;
        for (String item : aCarSize)
            this.aCarSize.add(item+" chỗ");
    }

}
