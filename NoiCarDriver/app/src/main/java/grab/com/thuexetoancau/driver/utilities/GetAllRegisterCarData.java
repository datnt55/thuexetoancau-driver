package grab.com.thuexetoancau.driver.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DatNT on 10/27/2016.
 */

public class GetAllRegisterCarData {
    private onDataReceived onReceived;
    private Context mContext;
    private ProgressDialog dialog;
    private ArrayList<String> aCarMade,aCarType,aCarSize ;
    public GetAllRegisterCarData(Context mContext){
        this.mContext = mContext;
    }
    public void getAllCarData( onDataReceived onDataReceived){
        this.onReceived = onDataReceived;
        getAllData();
    }
    private void getAllData(){
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        aCarMade = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_CAR_MADE, new AsyncHttpResponseHandler() {

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
                        aCarMade.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAllCarType();


            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllCarType() {
        aCarType = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_CAR_TYPE, new AsyncHttpResponseHandler() {

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
                        String result = arrayresult.getString(i);
                        aCarType.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAllCarSize();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllCarSize() {
        aCarSize = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_CAR_SIZE, new AsyncHttpResponseHandler() {

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
                        String result = arrayresult.getString(i);
                        aCarSize.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (onReceived != null)
                    onReceived.onReceived(aCarMade, aCarType, aCarSize);
                /*ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, aVehicleType);
                txtCarName.setAdapter(adapterProvinceFrom);
                txtCarName.setThreshold(1);*/
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface onDataReceived{
        public void onReceived(ArrayList<String> aCarMade, ArrayList<String> aCarTypes, ArrayList<String> aCarSize);
    }
}
