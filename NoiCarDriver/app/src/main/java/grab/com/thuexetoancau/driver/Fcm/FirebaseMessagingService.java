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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.ListBookingAroundActivity;
import grab.com.thuexetoancau.driver.activities.SplashActivity;
import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.Defines;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //responseForPassenger("Có một cuốc mới dành cho bạn");
        String function = remoteMessage.getData().get("function");
        if (function.equals(Defines.FUNCTION_BOOK_GRAB)){
            String functionCase = remoteMessage.getData().get("case");
            if (functionCase.equals(Defines.CASE_FOUND_DRIVER)){
                Trip trip = handleFoundDriver(remoteMessage.getData());
                if (isAppInForeground(this)) {
                    final Intent intent = new Intent("newBooking");
                    // You can also include some extra data.
                    final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    intent.putExtra(Defines.BUNDLE_TRIP,trip);
                    broadcastManager.sendBroadcast(intent);
                }else
                    responseForPassenger("Có một cuốc mới dành cho bạn. Bấm vào đây để chấp nhận", trip);
            }else if (functionCase.equals(Defines.CASE_FOUND_DRIVER)){

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

    private void responseForPassenger(String message, Trip trip) {
        Intent intent = new Intent(this,ListBookingAroundActivity.class);
        intent.putExtra(Defines.BUNDLE_TRIP,trip);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {1, 1, 1});


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
