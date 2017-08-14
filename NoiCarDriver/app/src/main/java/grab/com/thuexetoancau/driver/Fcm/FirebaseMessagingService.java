package grab.com.thuexetoancau.driver.Fcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grab.com.thuexetoancau.driver.R;
import grab.com.thuexetoancau.driver.activities.SplashActivity;
import grab.com.thuexetoancau.driver.model.Position;
import grab.com.thuexetoancau.driver.model.Trip;
import grab.com.thuexetoancau.driver.utilities.Defines;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        responseForPassenger("Có một cuốc mới dành cho bạn");
        String function = remoteMessage.getData().get("function");
        if (function.equals(Defines.FUNCTION_BOOK_GRAB)){
            String functionCase = remoteMessage.getData().get("case");
            if (functionCase.equals(Defines.CASE_FOUND_DRIVER)){
                handleFoundDriver(remoteMessage.getData());
                responseForPassenger("Có một cuốc mới dành cho bạn");
            }
        }
    }

    private void handleFoundDriver(Map<String, String> data) {
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
        final Intent intent = new Intent("newBooking");
        // You can also include some extra data.
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        intent.putExtra(Defines.BUNDLE_TRIP,trip);
        broadcastManager.sendBroadcast(intent);
    }

    private void responseForPassenger(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] {1, 1, 1});
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    public Class getCurrentClass() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getClass();
    }
}
