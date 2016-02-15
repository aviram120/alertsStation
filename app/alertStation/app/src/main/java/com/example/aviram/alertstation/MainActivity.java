package com.example.aviram.alertstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener{
    private final String SERVER_URL="http://alertsstation-1172.appspot.com";
    private Spinner spinner_copmany,spinner_city,spinner_line,spinner_station;
    private Button btSave,btCancel;
    private RequestQueue queue;
    private ArrayList<CompanyData> _companyList;
    private ArrayList<CitesData> _citesList;
    private ArrayList<RoutesData> _routesList;
    private ArrayList<StopData> _stopsList;

    private ProgressDialog dialog;
    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private Activity activity;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int distanceFrom;
    private int checkBoxNoti,checkBoxchAlertClock;
    private boolean exitFromApp;//if user exit from app when the alert is on - and enter to the app again

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        activity=this;//save the activity

        //init location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //mLocationListener = this;

        initialization();

        if (sharedPref.getInt("alertStatus",0)==1)//check if alert already on
        {
            btSave.setEnabled(false);
            btCancel.setEnabled(true);

            spinner_copmany.setEnabled(false);
            spinner_station.setEnabled(false);
            spinner_city.setEnabled(false);
            spinner_line.setEnabled(false);

            exitFromApp=true;
        }
        else//alert is off
        {
            getCompanyFromServer();
        }

    }
    public void addItemsOnSpinner(Spinner spinner_id,List<String> list) {
        //the function put list to spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);//TODO change the design
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_id.setAdapter(dataAdapter);
    }
    private void initialization() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Date...");

        spinner_copmany = (Spinner) findViewById(R.id.spinner_copmany);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_line = (Spinner) findViewById(R.id.spinner_line);
        spinner_station = (Spinner) findViewById(R.id.spinner_station);

        spinner_copmany.setOnItemSelectedListener(new OnItemSelectedListener());
        spinner_city.setOnItemSelectedListener(new OnItemSelectedListener());
        spinner_line.setOnItemSelectedListener(new OnItemSelectedListener());

        btSave=(Button)findViewById(R.id.btSave);
        btSave.setOnClickListener(this);
        btSave.setEnabled(false);

        btCancel=(Button)findViewById(R.id.btCancel);
        btCancel.setOnClickListener(this);
        btCancel.setEnabled(false);

        _companyList = new ArrayList<CompanyData>(); //save all the 'company' from server-as object
        _routesList = new ArrayList<RoutesData>(); //save all the 'routes' from server-as object
        _citesList = new ArrayList<CitesData>();//save all the 'Cites' from server-as object
        _stopsList = new ArrayList<StopData>();//save all the 'station' from server-as object

        //set Shared Preferences-for save the setting
        sharedPref = getSharedPreferences("prefDistanceFromStation", MODE_PRIVATE);
        editor = sharedPref.edit();

        exitFromApp=false;

        getDataFromSP();//get data from Shared Preferences

    }
    private void getDataFromSP() {
        //the function get the data from Shared Preferences

        if (sharedPref.getInt("DistanceFrom",-1)==-1)//first time the app open
        {
            editor.putInt("DistanceFrom", 200);
            distanceFrom=200;
            editor.apply();
            Log.i("aviramLog","first level");
        }

        distanceFrom=sharedPref.getInt("DistanceFrom",200);

        //get alert setting
        checkBoxNoti=sharedPref.getInt("CheckBoxNoti", 1);
        checkBoxchAlertClock=sharedPref.getInt("CheckBoxchAlertClock", 0);
    }
    public class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            Spinner spinner = (Spinner) parent;
            if(spinner.getId() == R.id.spinner_copmany)
            {
                getCitesFromServer();
            }
            if(spinner.getId() == R.id.spinner_city)
            {
                getRoutesFromServer();
            }
            if(spinner.getId() == R.id.spinner_line)
            {
                getStopsFromServer();
            }

        }
        public void onNothingSelected(AdapterView parent) {}
    }
    public void onClick(View v) {

        if (v.getId() == R.id.btSave)//start alert
        {
            dialog.dismiss();
            getLocationFromSystem();//check if GPS in on- and start service
        }
        if (v.getId()==R.id.btCancel) {
            btSave.setEnabled(true);
            btCancel.setEnabled(false);

            stopService(new Intent(getBaseContext(), MyService.class));

            /*
            Toast.makeText(activity.getApplicationContext(),this.getString(R.string.alertIsOff), Toast.LENGTH_SHORT).show();
            */

            if (exitFromApp)//if the user exit from the app
            {
                exitFromApp=false;
                spinner_copmany.setEnabled(true);
                spinner_station.setEnabled(true);
                spinner_city.setEnabled(true);
                spinner_line.setEnabled(true);
                getCompanyFromServer();
            }
        }
    }


    // =============================================================
    // API Request
    // =============================================================
    private void getCompanyFromServer() {
        //the function get from server the company(name , id)

        dialog.show();
        String url=SERVER_URL+"/api?act=1";
        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
                            dialog.hide();//stop the dialog
                            response = response.replaceAll("\\r\\n", "");
                            jsonEventList = new JSONArray(response);
                        } catch (JSONException e) {
                            return;
                        }

                        if(jsonEventList == null)
                            return ;
                        else
                        {
                            try{
                                List < String > list_cop = new ArrayList<String>();//list for the spinner
                                CompanyData tempCompanyData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempCompanyData=new CompanyData(jsonCompany,i);
                                    _companyList.add(tempCompanyData);

                                    list_cop.add(jsonCompany.getString("agencyName"));//add to list
                                }
                                addItemsOnSpinner(spinner_copmany, list_cop);//add to spinner
                            }catch (JSONException e){}
                        }
                    }

                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                dialog.hide();
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);

    }
    private void getCitesFromServer() {
        //the function get all cites from server by company

       dialog.show();
       String url = SERVER_URL+"/api?act=2&agency_id=" + _companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id();
       queue = Volley.newRequestQueue(this);

       StringRequest request = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>(){

                public void onResponse(String response) {
                    JSONArray jsonEventList;
                    try {
                        dialog.hide();//stop thr dialog
                        response = response.replaceAll("\\r\\n", "");
                        jsonEventList = new JSONArray(response);


                    } catch (JSONException e) {
                        return;
                    }

                    if(jsonEventList == null)
                        return ;
                    else
                    {
                        try{
                            _citesList.clear();
                            List <String> list_cites = new ArrayList<String>();//list for the spinner
                            CitesData tempCitesData;
                            for(int i = 0; i < jsonEventList.length(); i++) {
                                JSONObject jsonCites = jsonEventList.getJSONObject(i);

                                tempCitesData = new CitesData(jsonCites,i);
                                _citesList.add(tempCitesData);

                                list_cites.add(jsonCites.getString("city_name"));
                            }
                            addItemsOnSpinner(spinner_city, list_cites);//add to spinner
                        }catch (JSONException e){}
                    }
                }
            },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                dialog.hide();//stop thr dialog
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
       });

       queue.add(request);
    }
    private void getRoutesFromServer() {
        //the function get all Routes from server by agency_id and city_id

        dialog.show();
        String url=SERVER_URL+"/api?act=3&agency_id="+ _companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id()+
                "&city_id="+_citesList.get((int) spinner_city.getSelectedItemId()).getCity_id();
        queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
                            dialog.hide();//stop thr dialog
                            response = response.replaceAll("\\r\\n", "");
                            jsonEventList = new JSONArray(response);
                        } catch (JSONException e) {
                            return;
                        }

                        if(jsonEventList == null)
                            return ;
                        else
                        {
                            try{
                                _routesList.clear();
                                List < String > list_routes = new ArrayList<String>();//list for the spinner
                                RoutesData tempRoutesData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempRoutesData=new RoutesData(jsonCompany,i);
                                    _routesList.add(tempRoutesData);

                                    list_routes.add(jsonCompany.getString("route_short_name"));
                                }
                                addItemsOnSpinner(spinner_line, list_routes);//add to spinner
                            }catch (JSONException e){}
                        }

                    }

                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                dialog.hide();//stop thr dialog
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);

    }
    private void getStopsFromServer() {
        //the function get all Stops station from server by route_id

        dialog.show();
        String url=SERVER_URL+"/api?act=4&route_id="+_routesList.get((int) spinner_line.getSelectedItemId()).getRoute_id();

        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
                            dialog.hide();//stop thr dialog
                            response = response.replaceAll("\\r\\n", "");
                            jsonEventList = new JSONArray(response);
                        } catch (JSONException e) {
                            return;
                        }

                        if(jsonEventList == null)
                            return ;
                        else
                        {
                            try{
                                _stopsList.clear();
                                List < String > list_stops = new ArrayList<String>();//list for the spinner
                                StopData tempStopData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempStopData = new StopData(jsonCompany,i);
                                    _stopsList.add(tempStopData);

                                    list_stops.add(jsonCompany.getString("stop_name"));
                                }
                                addItemsOnSpinner(spinner_station, list_stops);//add to spinner

                                btSave.setEnabled(true);
                            }catch (JSONException e){}
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                dialog.hide();//stop thr dialog
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);

    }


    // =============================================================
    // GPS
    // =============================================================
    private void getLocationFromSystem() {
        // the function get the location from GPS and start the service

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

                        StopData tempStop=null;
                        tempStop=_stopsList.get((int) spinner_station.getSelectedItemId());//station info

                        Intent service=new Intent(getBaseContext(), MyService.class);
                        JSONObject obj = (tempStop.convertToJSON(tempStop));

                        //put to Bundle
                        service.putExtra("object", obj.toString());
                        service.putExtra("distanceFrom",distanceFrom);
                        service.putExtra("checkBoxNoti",checkBoxNoti);
                        service.putExtra("checkBoxchAlertClock",checkBoxchAlertClock);

                        editor.putInt("alertStatus",1);
                        editor.apply();

                        /*
                        Toast.makeText(activity.getApplicationContext(),this.getString(R.string.alertIsOn), Toast.LENGTH_SHORT).show();
                        */
                        btSave.setEnabled(false);
                        btCancel.setEnabled(true);

                        startService(service);//start server
                    }
                }
            });
        }
        else//if the location not available
        {
            showSettingsAlert();
        }
    }
    private void showSettingsAlert() {
        //the function show a alert dialog-of setting(if the GPS is off)

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

}//end
