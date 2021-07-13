package com.aseemsethi.bookappoauth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.aseemsethi.bookappoauth.ui.main.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttService extends Service {
    final String TAG = "BookAppOauth: MQTT";
    String CHANNEL_ID = "default";
    NotificationManager mNotificationManager;
    Notification notification;
    int counter = 1;
    MqttHelper mqttHelper;
    final static String MQTTSUBSCRIBE_ACTION = "MQTTSUBSCRIBE_ACTION";

    public MqttService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String action=null;
        Log.d(TAG, "Started mqttService");
        if (intent == null) {
            Log.d(TAG, "Intent is null..possible due to app restart");
            //action = MQTTMSG_ACTION;
        } else
            action = intent.getAction();
        Log.d(TAG,"ACTION: " + action);

        //onTaskRemoved(intent);
        Toast.makeText(getApplicationContext(),"MQTT Service in Background",
                Toast.LENGTH_SHORT).show();
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                "my_channel",
                NotificationManager.IMPORTANCE_HIGH);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mNotificationManager.createNotificationChannel(mChannel);
        try {
            startMqtt();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // The following "startForeground" with a notification is what makes
        // the service run in the background and not get killed, when the app gets
        // killed by the user.
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification noti = new Notification.Builder(this, "default")
                .setContentTitle("MQTT:")
                .setContentText("Background")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .build();
        startForeground(1, noti);  // this is the noti that is shown when running in background
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String msg) {
        Log.d(TAG, "Send Notification...");
        String[] arrOfStr = msg.split(":", 4);
        String title = arrOfStr[1].trim();
        String body = arrOfStr[2].trim();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification noti = new Notification.Builder(this, "default")
                .setContentTitle(title + ":" + counter)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .build();
        mNotificationManager.notify(1, noti);
    }

    private void startMqtt() throws MqttException {
        Log.d(TAG, "startMqtt");
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String msg = mqttMessage.toString();
                //Log.d(TAG, "MQTT Msg recvd: " + msg);
                String[] arrOfStr = msg.split(":", 4);
                Log.d(TAG, "MQTT Msg recvd:" + arrOfStr[0] + " : " + arrOfStr[1] +
                        " : " + arrOfStr[2]);
                sendNotification(msg);
                if ((arrOfStr[1].trim()).equals("4ffe1a")) {
                    //Log.d(TAG, "MQTT Msg recvd from: 4ffe1a");
                    //TextView v1 = (TextView) root.findViewById(R.id.sensorValue1);
                    //v1.setText(arrOfStr[2]);
                    //sendNotification(msg);
                } else if ((arrOfStr[1].trim()).equals("f6e01a")) {
                    //Log.d(TAG, "MQTT Msg recvd from: f6e01a");
                    //TextView v1 = (TextView) root.findViewById(R.id.sensorValue2);
                    //v1.setText(arrOfStr[2]);
                    //sendNotification(msg);
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG, "msg delivered");
            }
        });
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Mqtt Service task removed");
        //Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        //restartServiceIntent.setPackage(getPackageName());
        //startService(restartServiceIntent);

        Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, MqttService.class);
        serviceIntent.setAction(MqttService.MQTTSUBSCRIBE_ACTION);
        serviceIntent.putExtra("topic", "aseemsethi");
        startService(serviceIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}