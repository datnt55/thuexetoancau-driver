package grab.com.thuexetoancau.driver.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.SharePreference;
import grab.com.thuexetoancau.driver.widget.AcceptBookDialog;

public class LoginActivity extends AppCompatActivity {
    private SharePreference preference;
    private Context mContext;
    private EditText edtPhone, edtPass;
    private TextInputLayout newPhone, newPass;
    private TextView txtRegister;
    private Button btnLogin;
    private LinearLayout layoutRegister;
    private ProgressDialog dialog;
    private ApiUtilities mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);
        preference = new SharePreference(this);
        mContext = this;
        mApi = new ApiUtilities(this);
        initComponents();
    }
    private void initComponents() {
        // Toolbar toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        edtPhone                = (EditText)            findViewById(R.id.edt_phone);
        edtPass                 = (EditText)            findViewById(R.id.edt_pass);
        newPhone                = (TextInputLayout)     findViewById(R.id.new_phone);
        newPass                 = (TextInputLayout)     findViewById(R.id.new_pass);
        btnLogin                = (Button)              findViewById(R.id.btn_login);
        txtRegister             = (TextView)            findViewById(R.id.txt_register);
        layoutRegister          = (LinearLayout)        findViewById(R.id.layout_register);

        txtRegister.setPaintFlags(txtRegister.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        btnLogin.setOnClickListener(login_click_listener);

        layoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            letsLogin();
        }
    };

    private void letsLogin() {
        String phone = edtPhone.getText().toString();
        String pass = edtPass.getText().toString();
        newPhone.setError("");
        newPass.setError("");
        if (phone == null || phone.equals("")) {
            newPhone.setError("Hãy nhập số điện thoại của bạn");
            requestFocus(edtPhone);
            return;
        }

        if (pass == null || pass.equals("")) {
            newPass.setError("Hãy nhập password của bạn");
            requestFocus(edtPass);
            return;
        }
        mApi.login(phone, pass, new ApiUtilities.LoginResponseListener() {
            @Override
            public void onSuccess(Trip trip) {
                Intent intent = null;
                if (trip != null) {
                    intent = new Intent(mContext, AcceptBookingActivity.class);
                    intent.putExtra(Defines.BUNDLE_TRIP, trip);
                }else {
                    intent = new Intent(mContext, ListBookingAroundActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}