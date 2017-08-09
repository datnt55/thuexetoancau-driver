package grab.com.thuexetoancau.driver.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by DatNT on 8/2/2017.
 */

public class ApiUtilities {
    private Context mContext;

    public ApiUtilities(Context mContext) {
        this.mContext = mContext;
    }

    public void login(final String phone, String pass, final LoginResponseListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", phone);
        params.put("pass",pass);
        Log.i("params deleteDelivery", params.toString());
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                dialog.dismiss();
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    if (data.length() == 0) {
                        Toast.makeText(mContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(mContext, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        saveVehicleInfor(jsonobject);
                    }
                    if (listener != null )
                        listener.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void saveVehicleInfor(JSONObject result) {
        SharePreference preference = new SharePreference(mContext);
        try {

            int id = result.getInt("id");
            preference.saveDriverId(id);
            String name = result.getString("name");
            preference.saveName(name);
            String phone = result.getString("phone");
            preference.savePhone(phone);

            String carNumber = result.getString("car_number");
            preference.saveCarNumber(carNumber);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void requestCarName(String carMade, final CarNameResponseListener listener) {
        final ArrayList<String> arrCarModel = new ArrayList<>();
        RequestParams params;
        params = new RequestParams();
        params.put("keyword",carMade);
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_MODEL,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        JSONObject result = arrayresult.getJSONObject(i);
                        String name = result.getString("name");
                        arrCarModel.add(name);
                    }
                    if (listener != null)
                        listener.onSuccess(arrCarModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }

    public interface LoginResponseListener {
        void onSuccess();
    }
    public interface CarNameResponseListener {
        void onSuccess(ArrayList<String> carName);
    }
}
