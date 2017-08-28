package grab.com.thuexetoancau.driver.Fcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.ListBookingAroundActivity;
import grab.com.thuexetoancau.driver.activities.SplashActivity;
import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.ApiUtilities;
import grab.com.thuexetoancau.driver.utilities.Defines;
import grab.com.thuexetoancau.driver.utilities.Global;
import grab.com.thuexetoancau.driver.utilities.SharePreference;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //responseForPassenger("Có một cuốc mới dành cho bạn");
        String function = remoteMessage.getData().get("function");
        Log.e("FUNCTION",function);
        if (function.equals(Defines.FUNCTION_BOOK_GRAB)){
            String functionCase = remoteMessage.getData().get("case");
            if (functionCase.equals(Defines.CASE_FOUND_DRIVER)){
                Trip trip = handleFoundDriver(remoteMessage.getData());
                if (!isAppInForeground(this))
                    responseForPassenger(trip);
                final Intent intent = new Intent(Defines.BROADCAST_FOUND_CUSTOMER);
                // You can also include some extra data.
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                intent.putExtra(Defines.BUNDLE_TRIP,trip);
                broadcastManager.sendBroadcast(intent);



            }
        }/*else if (function.equals(Defines.FUNCTION_RECEIVE_TRIP)){
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.CASE_CANCEL_TRIP)) {
                String bookingId = remoteMessage.getData().get("id_booking");
                final Intent intent = new Intent(Defines.BROADCAST_CANCEL_TRIP);
                intent.putExtra(Defines.BUNDLE_BOOKING_ID,Integer.valueOf(bookingId));
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this))
                    cancelTrip();
            }
        }*/else if (function.equals(Defines.FUNCTION_CANCEL_TRIP)){
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.CASE_SUCCESS)) {
                String bookingId = remoteMessage.getData().get("id_booking");
                final Intent intent = new Intent(Defines.BROADCAST_CANCEL_TRIP);
                intent.putExtra(Defines.BUNDLE_BOOKING_ID,Integer.valueOf(bookingId));
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this))
                    cancelTrip();
            }
        }else if (function.equals(Defines.FUNCTION_REVIEW)){
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.CASE_SUCCESS)) {
                String customerName = remoteMessage.getData().get("custom_name");
                String star = remoteMessage.getData().get("custom_rating");
                reviewTrip(customerName, star);

            }
        }
    }

    private Trip handleFoundDriver(Map<String, String> data) {
        String id = data.get("id_booking");
        String startPoint = data.get("start_point_name");
        String endPoint = data.get("list_end_point_name");
        String price = data.get("price");
        String distance = data.get("distance");
        ArrayList<Position> listStopPoint = new ArrayList<>();
        Position start = new Position(startPoint);
        String startPrimary = startPoint.split(",")[0];
        String startSecond = "";
        if (startPrimary.length() < startPoint.length())
            startSecond = startPoint.substring(startPrimary.length()+1);
        start.setPrimaryText(startPrimary);
        start.setSecondText(startSecond);
        listStopPoint.add(start);
        String[] listEndPoint = endPoint.split("_");
        for (int i=0; i < listEndPoint.length ; i++) {
            Position position = new Position(listEndPoint[i]);
            String endPrimary = listEndPoint[i].split(",")[0];
            String endSecond = "";
            if (endPrimary.length() < listEndPoint[i].length())
                endSecond = listEndPoint[i].substring(endPrimary.length()+1);
            position.setPrimaryText(endPrimary);
            position.setSecondText(endSecond);
            listStopPoint.add(position);
        }
        Trip trip = new Trip( Integer.valueOf(id),listStopPoint, Integer.valueOf(distance), Integer.valueOf(price));
        return trip;
    }

    private void responseForPassenger(final Trip trip) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseMessagingService.this)
                        .setAutoCancel(true)
                        .setContentTitle("Thuê xe toàn cầu driver")
                        .setContentText("Có một cuốc mới dành cho bạn. Bấm vào đây để chấp nhận")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[] { 0, 100, 200, 300 });
                Intent intent = new Intent(FirebaseMessagingService.this,ListBookingAroundActivity.class);
                intent.putExtra(Defines.BUNDLE_FOUND_CUSTOMER,true);
                intent.putExtra(Defines.BUNDLE_TRIP_BACKGROUND,trip);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(Defines.NOTIFY_TAG,trip.getId(), builder.build());

                Global.countDownTimer = new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        SharePreference preference = new SharePreference(FirebaseMessagingService.this);
                        Log.e("COUNTER","finish");
                        ApiUtilities mApi = new ApiUtilities(FirebaseMessagingService.this);
                        mApi.driverNoReceiverTrip(trip.getId(),preference.getDriverId(),null);
                        Intent intent = new Intent(FirebaseMessagingService.this,ListBookingAroundActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        final NotificationCompat.Builder notification = new NotificationCompat.Builder(FirebaseMessagingService.this)
                                .setAutoCancel(true)
                                .setContentTitle("Thuê xe toàn cầu driver")
                                .setContentText("Bạn đã lỡ 1 chuyến đi")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setVibrate(new long[] { 0, 100, 200, 300 });
                        NotificationManager managerCancel = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        managerCancel.notify(Defines.NOTIFY_TAG,trip.getId(), notification.build());
                        return;
                    }

                }.start();
            }
        });

    }

    private void cancelTrip() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseMessagingService.this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu driver")
                .setContentText("Khách đã hủy chuyến đi")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] {1, 1, 1});
        Intent intent = new Intent(FirebaseMessagingService.this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    private void reviewTrip(String customerName, String star) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseMessagingService.this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu driver")
                .setContentText("Hành khách " +customerName+ " đã đánh giá "+ star + " sao cho bạn")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] {0, 100, 200, 300});
        if (!isAppInForeground(this)) {
            Intent intent = new Intent(FirebaseMessagingService.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    private boolean isAppInForeground(Context context)
    {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

            return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
        }
        else
        {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
            {
                return true;
            }

            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
    }
}
