package grab.com.thuexetoancau.driver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.CommonUtilities;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

public class DriverWalletActivity extends AppCompatActivity {
    private SharePreference preference;
    private ApiUtilities mApi;
    private TextView txtMoney;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_wallet);
        txtMoney = (TextView) findViewById(R.id.txt_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ví");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mApi = new ApiUtilities(this);
        preference = new SharePreference(this);
        mApi.getDriverMoney(preference.getDriverId(), new ApiUtilities.DriverMoneyListener() {
            @Override
            public void onSuccess(long price) {
                txtMoney.setText(CommonUtilities.convertRealCurrency((int) price) +" vnđ");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
