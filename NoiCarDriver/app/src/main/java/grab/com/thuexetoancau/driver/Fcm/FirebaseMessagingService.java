package grab.com.thuexetoancau.driver.Fcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import grab.com.thuexetoancau.driver.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        responseForPassenger(remoteMessage.getData().get("message"));


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
        String ac = getCurrentClass();
        manager.notify(0,builder.build());
    }

    public String getCurrentClass() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        String className = componentInfo.getClassName();
        return className;
    }
}
