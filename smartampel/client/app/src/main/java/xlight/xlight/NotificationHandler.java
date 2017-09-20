package xlight.xlight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

class NotificationHandler {
    
    private static NotificationHandler instance;
    private NotificationManager notificationManager;
    private MainActivityFragment f;
    private Map<String, Integer> uuid2LastState = new HashMap<>();
    private final int GLOBAL_ID = 1337;
    
    private NotificationHandler() {
    }
    
    private NotificationHandler(MainActivityFragment f) {
        this.f = f;
        this.notificationManager = (NotificationManager)
                f.getApplicationContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
    }
    
    static NotificationHandler getInstance(MainActivityFragment f) {
        if (instance == null) {
            instance = new NotificationHandler(f);
        }
        return instance;
    }
    
    void handleNotifications(List<Structs.XLightState> states) {
        for (Structs.XLightState state : states) {
            handleSingleNotification(state);
        }
    }
    
    private void handleSingleNotification(Structs.XLightState state) {
        
        // ignoring anything but red or green
        if (state.remoteState != 1 && state.remoteState != 2) {
            return;
        }
        
        // case: state not in list
        if (!uuid2LastState.containsKey(state.uuid)
                && state.lightNearby) {
            uuid2LastState.put(state.uuid, state.remoteState);
            createXLightNotify(Structs.LIGHT_STATES.values()[state.remoteState] + " LIGHT!");
            return;
        }
        
        // case: state in list but unchanged
        if (uuid2LastState.get(state.uuid) != null &&
                (uuid2LastState.get(state.uuid) == state.remoteState)
                && state.lightNearby) {
            return;
        }
        
        if (!state.lightNearby) {
            removeNotification();
            uuid2LastState.remove(state.uuid);
            return;
        }
        
        uuid2LastState.put(state.uuid, state.remoteState);
        createXLightNotify(Structs.LIGHT_STATES.values()[state.remoteState] + " LIGHT!");
        
    }
    
    private void removeNotification() {
        notificationManager.cancel(GLOBAL_ID);
    }
    
    private void createXLightNotify(String message) {
        
        Intent intent = new Intent(f.getActivity(), MainActivityFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                f.getActivity(), 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_TITLE = "XLights";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                f.getActivity());
        notificationBuilder.setSmallIcon(R.drawable.car);
        notificationBuilder.setContentTitle(NOTIFICATION_TITLE);
        notificationBuilder.setContentText(message);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message));
        notificationBuilder.setVibrate(new long[]{1000});
        notificationBuilder.setLights(Color.GREEN, 3000, 3000);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setContentIntent(pendingIntent);
        
        notificationManager.notify(GLOBAL_ID,
                notificationBuilder.build());
    }
}
