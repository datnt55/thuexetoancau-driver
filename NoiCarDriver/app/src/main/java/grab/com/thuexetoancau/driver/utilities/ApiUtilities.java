package grab.com.thuexetoancau.driver.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;


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
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONObject driverData = data.getJSONObject("driver_data");
                        saveVehicleInfor(driverData);
                        if (listener != null)
                            listener.onSuccess();
                    }
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

    public void getBookingAround(double lat, double lon, final AroundBookingListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("lat",lat);
        params.put("lon",lon);
        params.put("ready", 1);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_LIST_BOOKING_AROUND,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONArray bookingList = data.getJSONArray("bookinglist");
                        for (int i = 0 ; i < bookingList.length(); i++) {
                            JSONObject booking = bookingList.getJSONObject(i);
                            Trip trip = parseBookingData(booking);
                            arrayTrip.add(trip);
                        }
                        if (listener != null)
                            listener.onSuccess(arrayTrip);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
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

    private Trip parseBookingData(JSONObject booking){
        Trip trip = null;
        try {
            int id = booking.getInt("id");
            int useId = booking.getInt("user_id");
            String customerPhone = booking.getString("custom_phone");
            String customerName = booking.getString("custom_name");
            int carSize = booking.getInt("car_size");
            String startPointName = booking.getString("start_point_name");
            String listEndPointName = booking.getString("list_end_point_name");
            double startPointLon = booking.getDouble("start_point_lon");
            double startPointLat = booking.getDouble("start_point_lat");
            String listEndPointLon = booking.getString("list_end_point_lon");
            String listEndPointLat = booking.getString("list_end_point_lat");
            String listEndPoin = booking.getString("list_end_point");
            int isOneWay = booking.getInt("is_one_way");
            int isMineTrip = booking.getInt("is_mine_trip");
            int price = booking.getInt("price");
            int distance = booking.getInt("distance");
            String startTime = null ,backTime = null, note = null ;
            if (booking.getString("start_time")!= null)
                startTime = booking.getString("start_time");
            if (booking.getString("back_time")!= null)
                backTime = booking.getString("back_time");
            if (booking.getString("note")!= null)
                note = booking.getString("note");
            String bookingTime = booking.getString("book_time");
            String bookDateId = booking.getString("book_date_id");
            int statusBooking = booking.getInt("status_booking");
            int statusPayment = booking.getInt("status_payment");
            String cancelReason = null, guestPhone = null , guestName = null;
            if (booking.getString("cancel_reason")!= null)
                cancelReason = booking.getString("cancel_reason");
            if (booking.getString("guest_phone")!= null)
                guestPhone = booking.getString("guest_phone");
            if (booking.getString("guest_name")!= null)
                guestName = booking.getString("guest_name");
            int carType = booking.getInt("car_type");
            int realDistance = 0, realPrice = 0;
            if (!booking.getString("real_distance").equals("null"))
                realDistance = booking.getInt("real_distance");
            if (!booking.getString("real_price").equals("null"))
                realPrice = booking.getInt("real_price");
            ArrayList<Position> listStopPoint = new ArrayList<Position>();
            Position from = new Position(startPointName,new LatLng(startPointLat,startPointLon));
            listStopPoint.add(from);
            String[] arrEndPointName = listEndPointName.split("_");
            String[] arrEndPointGeo = listEndPoin.split("_");
            for (int i = 0 ; i <arrEndPointName.length; i++){
                double lat = Double.valueOf(arrEndPointGeo[i].split(",")[0]);
                double lon = Double.valueOf(arrEndPointGeo[i].split(",")[1]);
                Position position = new Position(arrEndPointName[i],new LatLng(lat,lon));
                listStopPoint.add(position);
            }
            trip = new Trip(id,useId,listStopPoint,carSize,isOneWay,distance,price,startTime,backTime,isMineTrip,customerName,customerPhone,guestName,guestPhone,note);
            trip.setBookingDateId(bookDateId);
            trip.setBookingTime(bookingTime);
            trip.setStatusBooking(statusBooking);
            trip.setStatusPayment(statusPayment);
            trip.setCancelReason(cancelReason);
            trip.setCarType(carType);
            trip.setRealDistance(realDistance);
            trip.setRealPrice(realPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trip;
    }

    public void receivedTrip(int bookingId, int driverId, final AroundBookingListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking",bookingId);
        params.put("driver_id",driverId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_RECEIVE_TRIP,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        int userId = data.getInt("user_id");
                        if (listener != null)
                            listener.onSuccess(arrayTrip);
                    }
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                dialog.dismiss();
            }
        });
    }

    public void driverCancelTrip(int bookingId, int driverId, String driverPhone,String cancelReason, final CancelTripListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking",bookingId);
        params.put("driver_id",driverId);
        params.put("driver_phone",driverPhone);
        params.put("cancel_reason",cancelReason);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_CANCEL_TRIP,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                JSONObject json = null;
                try {
                    json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        if (listener != null)
                            listener.onSuccess();
                    }else{
                        if (listener != null)
                            listener.onFailure();
                    }
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                dialog.dismiss();
            }
        });
    }
    public void driverNoReceiverTrip(int bookingId, int driverId, final CancelTripListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking",bookingId);
        params.put("driver_id",driverId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_NO_ACCEPT_TRIP,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                JSONObject json = null;
                try {
                    json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        if (listener != null)
                            listener.onSuccess();
                    }else{
                        if (listener != null)
                            listener.onFailure();
                    }
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
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

    public interface AroundBookingListener {
        void onSuccess(ArrayList<Trip> arrayTrip);
    }

    public interface CancelTripListener {
        void onSuccess();
        void onFailure();
    }

}
