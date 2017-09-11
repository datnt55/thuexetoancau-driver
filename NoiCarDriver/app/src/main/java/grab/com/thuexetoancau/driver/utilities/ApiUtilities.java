package grab.com.thuexetoancau.driver.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.model.User;


/**
 * Created by DatNT on 8/2/2017.
 */

public class ApiUtilities {
    private Context mContext;

    public ApiUtilities(Context mContext) {
        this.mContext = mContext;
    }

    public void login(final String phone, final String pass, final ProgressDialog dialog, final LoginResponseListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", phone);
        params.put("pass", pass);
        Log.i("params deleteDelivery", params.toString());
       /* final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();*/
        BaseService.getHttpClient().post(Defines.URL_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                // dialog.dismiss();
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONObject driverData = data.getJSONObject("driver_data");
                        String sBooking = data.getString("booking_data");
                        Trip trip = null;
                        if (!sBooking.equals("null")) {
                            JSONObject booking = data.getJSONObject("booking_data");
                            trip = parseBookingData(booking);
                        }
                        SharePreference preference = new SharePreference(mContext);
                        preference.savePassword(pass);
                        User user = saveVehicleInfor(driverData);
                        if (listener != null)
                            listener.onSuccess(user, trip);
                    }else
                        Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                    if (dialog != null)
                        dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                //  dialog.dismiss();
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    private User saveVehicleInfor(JSONObject result) {
        User user = null;
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
            String email = result.getString("email");
            String carModel = result.getString("car_model");
            String carMade = result.getString("car_made");
            String carYears = result.getString("car_years");
            int carSize = result.getInt("car_size");
            String carType = result.getString("car_type");
            long carPrice = 0;
            if (!result.getString("car_price").equals("null"))
                carPrice = result.getLong("car_price");
            long totalMoneys = result.getLong("total_moneys");
            String province = result.getString("province");
            String cardIdentify = result.getString("card_identify");
            String license = result.getString("license");
            int driverType = 0;
            if (!result.getString("driver_type").equals("null"))
                driverType = result.getInt("driver_type");
            int isCar = result.getInt("is_car");
            user = new User(name, phone, email, carModel, carMade, carYears, carSize, carNumber, carType, carPrice, totalMoneys, province, cardIdentify, license, driverType,isCar);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
    }

    public void requestCarName(String carMade, final CarNameResponseListener listener) {
        final ArrayList<String> arrCarModel = new ArrayList<>();
        RequestParams params;
        params = new RequestParams();
        params.put("keyword", carMade);
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_MODEL, params, new AsyncHttpResponseHandler() {

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

    public void getBookingAround(double lat, double lon, int status, final AroundBookingListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("ready", status);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_LIST_BOOKING_AROUND, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONArray bookingList = data.getJSONArray("bookinglist");
                        for (int i = 0; i < bookingList.length(); i++) {
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

    private Trip parseBookingData(JSONObject booking) {
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
            String startTime = null, backTime = null, note = null;
            if (booking.getString("start_time") != null)
                startTime = booking.getString("start_time");
            if (booking.getString("back_time") != null)
                backTime = booking.getString("back_time");
            if (booking.getString("note") != null)
                note = booking.getString("note");
            String bookingTime = booking.getString("book_time");
            String bookDateId = booking.getString("book_date_id");
            int statusBooking = booking.getInt("status_booking");
            int statusPayment = booking.getInt("status_payment");
            String cancelReason = null, guestPhone = null, guestName = null;
            if (booking.getString("cancel_reason") != null)
                cancelReason = booking.getString("cancel_reason");
            if (booking.getString("guest_phone") != null)
                guestPhone = booking.getString("guest_phone");
            if (booking.getString("guest_name") != null)
                guestName = booking.getString("guest_name");
            int carType = booking.getInt("car_type");
            int realDistance = 0, realPrice = 0;
            if (!booking.getString("real_distance").equals("null"))
                realDistance = booking.getInt("real_distance");
            if (!booking.getString("real_price").equals("null"))
                realPrice = booking.getInt("real_price");
            ArrayList<Position> listStopPoint = new ArrayList<Position>();
            Position from = new Position(startPointName, new LatLng(startPointLat, startPointLon));
            listStopPoint.add(from);
            String[] arrEndPointName = listEndPointName.split("_");
            String[] arrEndPointGeo = listEndPoin.split("_");
            for (int i = 0; i < arrEndPointName.length; i++) {
                double lat = Double.valueOf(arrEndPointGeo[i].split(",")[0]);
                double lon = Double.valueOf(arrEndPointGeo[i].split(",")[1]);
                Position position = new Position(arrEndPointName[i], new LatLng(lat, lon));
                listStopPoint.add(position);
            }
            trip = new Trip(id, useId, listStopPoint, carSize, isOneWay, distance, price, startTime, backTime, isMineTrip, customerName, customerPhone, guestName, guestPhone, note);
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

    public void receivedTrip(int bookingId, int driverId, final AcceptTripListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        params.put("driver_id", driverId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_RECEIVE_TRIP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        int userId = data.getInt("user_id");
                        String customName = data.getString("custom_name");
                        String customPhone = data.getString("custom_phone");
                        User user = new User(userId, customName, customPhone, "", "");
                        if (listener != null)
                            listener.onSuccess(user);
                    } else {
                        if (listener != null)
                            listener.onFailure();
                    }
                    Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void driverCancelTrip(int bookingId, int driverId, String driverPhone, String cancelReason, final CancelTripListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        params.put("driver_id", driverId);
        params.put("driver_phone", driverPhone);
        params.put("cancel_reason", cancelReason);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_CANCEL_TRIP, params, new AsyncHttpResponseHandler() {

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
                    if (json.getString("status").equals("success")) {
                        if (listener != null)
                            listener.onSuccess();
                    } else {
                        if (listener != null)
                            listener.onFailure();
                    }
                    Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
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
        params.put("id_booking", bookingId);
        params.put("driver_id", driverId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_NO_ACCEPT_TRIP, params, new AsyncHttpResponseHandler() {

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
                    if (json.getString("status").equals("success")) {
                        if (listener != null)
                            listener.onSuccess();
                    } else {
                        if (listener != null)
                            listener.onFailure();
                    }
                    Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void confirmTrip(int bookingId, int driverId, long realDistance, final ConfirmTripListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        params.put("driver_id", driverId);
        params.put("real_distance", realDistance);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff) * 13 + 27;
        params.put("key", key);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_CONFIRM_TRIP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        long price = data.getLong("price");
                        long distance = data.getLong("distance");
                        String dateTime = data.getString("date_time");
                        if (listener != null)
                            listener.onSuccess(price, distance, dateTime);
                    }
                    Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void getDriverMoney(int driverId,final DriverMoneyListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("driver_id", driverId);
//        DateTime current = new DateTime();
//        long key = (current.getMillis() + Global.serverTimeDiff) * 13 + 27;
//        params.put("key", key);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_MONEY_DRIVER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        long money = data.getLong("total_money");
                        if (listener != null)
                            listener.onSuccess(money);
                    }
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

    public void getTripInfo(int bookingId, final TripInformationListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_TRIP_INFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONObject listTrip = data.getJSONObject("list");
                        Trip trip = parseBookingData(listTrip);
                        if (listener != null)
                            listener.onSuccess(trip);
                    }
                    //Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void getScheduleTrip(int userId, final AroundBookingListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("driver_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_LIST_SCHEDULE,params, new AsyncHttpResponseHandler() {

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
                        JSONArray bookingList = data.getJSONArray("list");
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
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getHistoryTrip(int userId, final ResponseTripListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("driver_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_HISTORY,params, new AsyncHttpResponseHandler() {

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
                        JSONArray bookingList = data.getJSONArray("list");
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
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ResponseTripListener {
        void onSuccess(ArrayList<Trip> arrayTrip);
    }

    public interface LoginResponseListener {
        void onSuccess(User user, Trip trip);
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


    public interface AcceptTripListener {
        void onSuccess(User user);

        void onFailure();
    }

    public interface TripInformationListener {
        void onSuccess(Trip trip);
    }

    public interface ConfirmTripListener {
        void onSuccess(long price, long distance, String dateTime);
    }

    public interface DriverMoneyListener {
        void onSuccess(long price);
    }

}
