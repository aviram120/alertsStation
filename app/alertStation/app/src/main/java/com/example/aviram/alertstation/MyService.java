package com.example.aviram.alertstation;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service implements LocationListener{

    private Intent intent;
    private LocationManager locationManager;
    private LocationListener mLocationListener;
    private final long SECOND = 1000;
    private final long MIN_DISTANCE = 5;
    private StopData stopData;
    private float distanceFromStation;
    private int checkBoxNoti,checkBoxchAlertClock;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public IBinder onBind(Intent arg0) {
        return null;
    }
    private void getBundleInformation() {
        //the function get data from Bundle

        JSONObject stopObj;
        String obj=intent.getExtras().getString("object");
        Log.i("aviramLog", "object " + obj);
        try
        {
            stopObj = new JSONObject(obj);
            stopData=new StopData(stopObj,-1);//convert to object
        }
        catch (JSONException e)
        {

        }
        distanceFromStation=intent.getExtras().getInt("distanceFrom");
        Log.i("aviramLog", "distanceFrom " + distanceFromStation);

        checkBoxchAlertClock=intent.getExtras().getInt("checkBoxchAlertClock");
        checkBoxNoti=intent.getExtras().getInt("checkBoxNoti");

        Log.i("aviramLog", "checkBoxchAlertClock " + checkBoxchAlertClock);
        Log.i("aviramLog", "checkBoxNoti " + checkBoxNoti);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start the Service

        // Let it continue running until it is stopped.
        Log.i("aviramLog", "flags " + flags);
        Log.i("aviramLog", "startId " + startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        this.intent = intent;

        sharedPref = getSharedPreferences("prefDistanceFromStation", MODE_PRIVATE);
        editor = sharedPref.edit();

        getBundleInformation();//get the data from getExtras-intent

        //init location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = this;
        try{//get location -updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND, MIN_DISTANCE, mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND, MIN_DISTANCE, mLocationListener);
        } catch (SecurityException e) {   }


        return START_REDELIVER_INTENT;
    }
    public void onDestroy() {
        //stop the service

        super.onDestroy();
        try
        {
            locationManager.removeUpdates(mLocationListener);
        } catch (SecurityException e) {   }

        editor.putInt("alertStatus",0);
        editor.apply();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
    private static void notify(Context context, String title, String text) {

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.icon00);
        builder.setContentTitle(title);
        builder.setContentText(text);

        int notificationID = 0;
        nm.notify(notificationID, builder.build());
    }
    private void startAlarm() {

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
    }
    private void checkIfArriveToStation(float distance,float Accuracy) {
        //the function check if the user arrive to the station

        if (distanceFromStation >= distance + Accuracy / 2) {
            if (checkBoxNoti==1)
                notify(getApplicationContext(), "Bus Bell-Alarm", this.getString(R.string.NotificatioText) + "\n" + "תחנת: " + stopData.getStop_name());
            if (checkBoxchAlertClock==1)
                startAlarm();

            stopSelf();//stop the Service and sent alert
        }
    }


    // =============================================================
    // LocationListener
    // =============================================================
    @Override
    public void onLocationChanged(Location userLocation) {
        //get location of the user

        Location stationLocation=new Location("");
        stationLocation.setLatitude(stopData.getStop_lat());
        stationLocation.setLongitude(stopData.getStop_lon());

        float distance=userLocation.distanceTo(stationLocation);
        Toast.makeText(this, "distance"+distance, Toast.LENGTH_SHORT).show();
        Log.i("aviramLog", "distance " + distance);
        Log.i("aviramLog", "location" + userLocation);

        checkIfArriveToStation(distance,userLocation.getAccuracy());

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void onProviderEnabled(String provider) {
    }
    public void onProviderDisabled(String provider) {

    }
}