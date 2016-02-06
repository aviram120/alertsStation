package com.example.aviram.alertstation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmReceiver extends WakefulBroadcastReceiver implements LocationListener {
    private int alarmId = 0;
    private AlarmManager alarmManager;
    private Task task;

    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private LocationListener mLocationListener;
    private final long SECOND = 1000;
    private final long MIN_DISTANCE = 5;
    private Location _userLocation,stationLocation;
    private Activity activity;
    private Context context;
    private StopData stopStation;
    private boolean runTask;
    private Button btSave,btCancel;

    public AlarmReceiver() {

    }
    public void setAlarm(Activity activity,LocationManager locationManagerFromMain,StopData stopStation,Button btSave,Button btCancel) {
        //the function get activity-of the main, and stopStation that the user choose
        //the function start the trigger of checking the distance of the user from the station

        this.btSave=btSave;
        this.btCancel=btCancel;

        this.activity=activity;
        this.context=activity.getApplicationContext();


        //init location
        this.locationManager = locationManagerFromMain;
        mLocationListener = this;

        // _userLocation = getLastKnownLocation();
        this.stopStation=stopStation;
        stationLocation=new Location("");
        stationLocation.setLatitude(stopStation.getStop_lat());
        stationLocation.setLongitude(stopStation.getStop_lon());

        getLocationFromSystem();//get location from GPS and start the task

    }
    public void cancelAlarm() {
        Log.i("aviramLog", "cancelAlarm");

        btSave.setEnabled(true);
        btCancel.setEnabled(false);

        runTask=false;
        alarmManager = ( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(sender);

    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        //the function start when the alert is ON


        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        /*Vibrator vibrator;
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);*/


        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }


    // =============================================================
    // Task
    // =============================================================
    private class Task extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            int i=0;
            while(runTask)//the button 'start alert' press
            {
                try {
                    Thread.sleep(SECOND*4);//
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
                i++;
                if (i==5)
                    break;
            }

            return i;
        }
        protected void onProgressUpdate(Integer... value) { //in run time

            if (_userLocation!=null)
            {
                Log.i("aviramLog", "my Location " + _userLocation.toString());//check location
                Location userLocation=new Location("");
                userLocation.setLongitude(_userLocation.getLongitude());
                userLocation.setLatitude(_userLocation.getLatitude());

                float distance=userLocation.distanceTo(stationLocation);
                Log.i("aviramLog", "distance " +distance);//check location
            }
            else
            {
                Log.i("aviramLog", "_userLocation==null");
            }
        }
        protected void onPostExecute(Integer result) {
            if (!runTask)//stop alert
            {
                return;
            }
            Log.i("aviramLog", "onPostExecute i:" + result);

            btSave.setEnabled(true);
            btCancel.setEnabled(false);

            alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, i, 0);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pi);

            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, 8000, pi);

            }
    }


    // =============================================================
    // GPS
    // =============================================================
    private void getLocationFromSystem() {
        // the function get the location from GPS

        boolean isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isWIFIAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("aviramLog", "in my location gps" + isGPSAvailable);
        Log.i("aviramLog", "in my location wifi" + isWIFIAvailable);
        if (isGPSAvailable) {
            //get run time permission
            permissionManager = new PermissionManager(activity, new PermissionManager.OnPermissionListener() {
                public void OnPermissionChanged(boolean permissionGranted) {
                    Log.d("aviramLog", "permissionGranted: " + permissionGranted);
                    if (permissionGranted) {
                        try{//get location -updates
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND , MIN_DISTANCE, mLocationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND , MIN_DISTANCE, mLocationListener);
                        } catch (SecurityException e) {   }
                    }
                }
            });
            btSave.setEnabled(false);
            btCancel.setEnabled(true);

            runTask=true;
            task = new Task();//make a new task
            task.execute();
        }
        else//if the location not available
        {
            showSettingsAlert();
        }
    }
    private void showSettingsAlert() {
        //the function show a alert dialog-of setting
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void onLocationChanged(Location location) {
        //the function get the location and send the data to server to get the weather forecast
        _userLocation=location;
    }
    /*public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
    public void onProviderEnabled(String provider) {
        //provider enabled by user
    }
    public void onProviderDisabled(String provider) {
        //provider disabled by user
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //provider status changed
    }
    private Location getLastKnownLocation() {
        try
        {
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (SecurityException e)
        {
            Toast.makeText(activity, "Can't get last known location", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

}

