package grab.com.thuexetoancau.driver.utilities;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by DatNT on 6/29/2016.
 */
public class Defines {
    public static  final String HOSTNAME                        = "http://thuexetoancau.vn/";
    public static  final String URL_REGISTER                    = HOSTNAME + "api/driverRegister";
    public static  final String URL_POST_DRIVER_GPS             = HOSTNAME + "api/postDriverGPS";
    public static  final String URL_LOGIN                       = HOSTNAME + "api/login";
    public static  final String URL_LIST_BOOKING_AROUND         = HOSTNAME + "api2/getListBookingNoiArround";
    public static  final String URL_RECEIVE_TRIP                 = HOSTNAME + "api2/receivedTrip";
    public static  final String URL_CANCEL_TRIP                 = HOSTNAME + "api2/driverCancelTrip";
    public static  final String URL_CONFIRM_TRIP                = HOSTNAME + "api2/confirmTrip";
    public static  final String URL_NO_ACCEPT_TRIP              = HOSTNAME + "api2/noReceivedTrip";
    public static  final String URL_GET_TRIP_INFO              = HOSTNAME + "api2/getTripInfo";
    public static  final String URL_GET_CAR_TYPE            = HOSTNAME + "api/getListCarType";
    public static  final String URL_GET_CAR_MADE            = HOSTNAME + "api/getCarMadeList";
    public static  final String URL_GET_CAR_MODEL           = HOSTNAME + "api/getCarModelListFromMade";
    public static  final String URL_GET_AIRPORT             = HOSTNAME + "api/getAirportName";
    public static  final String URL_BOOKING_LOG             = HOSTNAME + "api/bookingLog";
    public static  final String URL_GET_CAR_SIZE            = HOSTNAME + "api/getCarSize";
    public static  final String URL_GET_CAR_HIRE_TYPE       = HOSTNAME + "api/getCarHireType";
    public static  final String URL_BOOKING_TICKET          = HOSTNAME + "api/booking";
    public static  final String URL_GET_BOOKING_LOG         = HOSTNAME + "api/getBookingLog";
    public static  final String URL_GET_BOOKING_CUSTOMER    = HOSTNAME + "api/getBookingForCustomer";
    public static  final String URL_GET_LIST_BOOKING_LOG    = HOSTNAME + "api/getlistbookinglog";
    public static  final String URL_GET_DRIVER_BY_ID        = HOSTNAME + "api/getDriverById";
    public static  final String URL_GET_MONEY_DRIVER        = HOSTNAME + "api/getMoneyDriver";
    public static  final String URL_CONFIRM                 = HOSTNAME + "api/confirm";
    public static  final String URL_WHO_WIN                 = HOSTNAME + "api/whoWin";

    public static  final String URL_GET_CAR_AROUND          = HOSTNAME + "api/getaround";
    public static  final String URL_GET_STATUS_DRIVER       = HOSTNAME + "api/getStatusDriver";
    public static  final String URL_BOOKING_SUCCESS         = HOSTNAME + "api/getBookingSuccess";
    public static  final String URL_CAR_REGISTATION         = HOSTNAME + "api/searchCarNumber";
    public static  final String URL_STATISTIC               = HOSTNAME + "api/searchTran";
    public static  final String URL_SALARY                  = HOSTNAME + "api/searchTranSalary";
    public static  final String URL_PHONE                     = "http://country.io/phone.json";


    public static  final String DIALOG_CONFIRM_TRIP              = "trip";
    public static final int REQUEST_CODE_PICKER                 = 100;
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static final int REQUEST_CODE_CONTACT_PERMISSIONS = 235;
    public static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    public static final int GOOGLE_API_CLIENT_ID = 0;
    public static final int FRAMEWORK_REQUEST_CODE = 1;
    public static final int DIRECTION_ENDPOINT = 2;
    public static final int DIRECTION_NEW_STOP_POINT = 3;
    public static final String TYPE_POINT = "type direction point";
    public static final String POSITION_POINT = "position direction point";
    public static final String BUNDLE_USER = "bundle user";
    public static final String BUNDLE_TRIP_BACKGROUND = "bundle trip background";
    public static final String BUNDLE_TRIP = "bundle trip";
    public static final String BUNDLE_NOTIFY_TRIP = "bundle notify trip";
    public static final String BUNDLE_CUSTOMER_NAME = "bundle customer name";
    public static final String BUNDLE_PRICE= "bundle price";
    public static final String BUNDLE_TRIP_DATE= "bundle trip date";

    public static final String BROADCAST_CANCEL_TRIP = "broadcast cancel Trip";
    public static final String BROADCAST_FOUND_CUSTOMER = "broadcast found customer";

    public static final String BUNDLE_FOUND_CUSTOMER = "bundle found customer";
    public static final String BUNDLE_BOOKING_ID = "bundle booking id";
    // Main Screen Dimension, will be set when app startup

    // Notification
    public static final String FUNCTION_BOOK_GRAB = "bookingGrab";
    public static final String FUNCTION_RECEIVE_TRIP = "receivedTrip";
    public static final String FUNCTION_CANCEL_TRIP = "cancelTrip";
    public static final String CASE_FOUND_DRIVER = "founddriver";
    public static final String CASE_CANCEL_TRIP = "cancelTrip";
    public static final String CASE_SUCCESS = "success";

    // Notification tag
    public static final String NOTIFY_TAG = "notify tag";

}
