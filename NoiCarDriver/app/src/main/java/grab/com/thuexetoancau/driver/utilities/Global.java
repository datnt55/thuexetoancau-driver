package grab.com.thuexetoancau.driver.utilities;

import android.os.CountDownTimer;

import java.util.ArrayList;

import grab.com.thuexetoancau.driver.model.Phone;

/**
 * Created by DatNT on 11/11/2016.
 */

public class Global {
    public static ArrayList<Phone> listPhone;
    public static int APP_SCREEN_HEIGHT = 0;
    public static int APP_SCREEN_WIDTH = 0;
    public static long serverTimeDiff;
    public static int TIME_BEFORE_AUCTION_LONG                 = 5*60*60*1000;
    public static int TIME_BEFORE_AUCTION_SHORT                 = 1*60*60*1000;
    public static int MAX_DISTANCE                              = 50000;
    public static int MIN_CURRENT_DISTANCE                      = 20;
    public static int LOOP_TIME                                 = 10000;
    public static int totalDistance                             = 0;
    public static boolean inTrip                                = false;
    public static CountDownTimer countDownTimer;
}
