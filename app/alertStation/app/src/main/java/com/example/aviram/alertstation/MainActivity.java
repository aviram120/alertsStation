package com.example.aviram.alertstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends Activity implements View.OnClickListener,LocationListener {
    private final String SERVER_URL="http://alertsstation-1172.appspot.com";
    private Spinner spinner_copmany,spinner_city,spinner_line,spinner_station;
    private Button btSave;
    private RequestQueue queue;
    private ArrayList<CompanyData> _companyList;
    private ArrayList<CitesData> _citesList;
    private ArrayList<RoutesData> _routesList;
    private ArrayList<StopData> _stopsList;

    private ProgressDialog dialog;
    private ProgressDialog dialogLocation;
    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private LocationListener mLocationListener;
    private final long SECOND = 1000;
    private final long MIN_DISTANCE = 5;
    private Location _userLocation;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        activity=this;//save the activity

        //init location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = this;

        initialization();

        getCompanyFromServer();

    }

    public void addItemsOnSpinner(Spinner spinner_id,List<String> list)
    {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);//TODO change the design
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_id.setAdapter(dataAdapter);
    }

    private void initialization() {

        _userLocation = getLastKnownLocation();

        dialogLocation = new ProgressDialog(this);
        dialogLocation.setMessage("Getting Location...");

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

        _companyList = new ArrayList<CompanyData>(); //save all the 'company' from server-as object
        _routesList = new ArrayList<RoutesData>(); //save all the 'routes' from server-as object
        _citesList = new ArrayList<CitesData>();
        _stopsList = new ArrayList<StopData>();
    }

    public class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            Spinner spinner = (Spinner) parent;
            if(spinner.getId() == R.id.spinner_copmany)
            {
                //Log.i("aviramLog", "spinner_comapny ");
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

    private void getCompanyFromServer()
    {
        dialog.show();
        String url=SERVER_URL+"/api?act=1";
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
                                List < String > list_cop = new ArrayList<String>();//list for the spinner
                                CompanyData tempCompanyData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempCompanyData=new CompanyData(jsonCompany,i);
                                    _companyList.add(tempCompanyData);

                                    list_cop.add(jsonCompany.getString("agencyName"));

                                    //Log.i("aviramLog", i + "" + jsonCompany.getString("agencyName"));
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

    private void getCitesFromServer()
    {
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

    private void getRoutesFromServer()
    {
        //long cityLong = toAscii(spinner_city.getSelectedItem().toString());
        //Log.i("aviramLog","cityLong = " + cityLong);
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
                                List < String > list_routes = new ArrayList<String>();//list for the spinner
                                RoutesData tempRoutesData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempRoutesData=new RoutesData(jsonCompany,i);
                                    _routesList.add(tempRoutesData);

                                    list_routes.add(jsonCompany.getString("route_short_name"));
                                    //Log.i("aviramLog", "route id: " + jsonCompany.getString("route_id"));
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

    private void getStopsFromServer()
    {
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
                                List < String > list_stops = new ArrayList<String>();//list for the spinner
                                StopData tempStopData;
                                for(int i = 0; i < jsonEventList.length(); i++) {
                                    JSONObject jsonCompany = jsonEventList.getJSONObject(i);

                                    tempStopData = new StopData(jsonCompany,i);
                                    _stopsList.add(tempStopData);

                                    list_stops.add(jsonCompany.getString("stop_name"));
                                }
                                addItemsOnSpinner(spinner_station, list_stops);//add to spinner
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

    public void onClick(View v) {

        if (v.getId() == R.id.btSave)//right text view
        {
            /*Log.i("aviramLog","spinner1:getSelectedItem: " +spinner_copmany.getSelectedItem().toString());
            Log.i("aviramLog","spinner1:getSelectedItemId: "+spinner_copmany.getSelectedItemId());
            Log.i("aviramLog", "company name "+_companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_name());
            Log.i("aviramLog", "id "+_companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id());*/


           /* Log.i("aviramLog", "city_name " + spinner_city.getSelectedItem());
            getRoutesFromServer();*/

            /*Log.i("aviramLog", "id "+_companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id());
            String str = _companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_name().replaceAll("\\r\\n", "");
            Log.i("aviramLog", "company name: "+str);*/

            Log.i("aviramLog","getStop_name:"+_stopsList.get((int) spinner_station.getSelectedItemId()).getStop_name());
            Log.i("aviramLog", "getStop_lat:" + _stopsList.get((int) spinner_station.getSelectedItemId()).getStop_lat());
            Log.i("aviramLog","getStop_lon:"+_stopsList.get((int) spinner_station.getSelectedItemId()).getStop_lon());
            Log.i("aviramLog", "getStop_sequence:" + _stopsList.get((int) spinner_station.getSelectedItemId()).getStop_sequence());

            getLocationFromSystem();

        }
    }



    // =============================================================
    // GPS
    // =============================================================
    private void getLocationFromSystem() {
        // the function get the location from GPS and send it to server

        boolean isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isWIFIAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("aviramLog", "in my location gps"+isGPSAvailable);
        Log.i("aviramLog", "in my location wifi" + isWIFIAvailable);
        if (isGPSAvailable) {
            //get run time permission
            permissionManager = new PermissionManager(activity, new PermissionManager.OnPermissionListener() {
                public void OnPermissionChanged(boolean permissionGranted) {
                    Log.d("aviramLog", "permissionGranted: " + permissionGranted);
                    if (permissionGranted) {
                        try{//get location -updates
                            dialogLocation.show();
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND * 10, MIN_DISTANCE, mLocationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND * 10, MIN_DISTANCE, mLocationListener);

                        } catch (SecurityException e) {   }
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

        dialogLocation.hide();
        Log.i("aviramLog", "my Location " + location.toString());
        Log.i("aviramLog", "lat:" + _userLocation.getLatitude());
        Log.i("aviramLog", "long:" + _userLocation.getLongitude());
        Log.i("aviramLog", "acc:" + _userLocation.getAccuracy());

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void onProviderEnabled(String provider) {
        //provider enabled by user
    }
    public void onProviderDisabled(String provider) {
        //provider disabled by user
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //provider status changed
    }
    private Location getLastKnownLocation()
    {

        try
        {
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (SecurityException e)
        {
            printToast(this, "Can't get last known location");
        }

        return null;
    }


    // ================================================================
    // Print Toast
    // ================================================================
    public static void printToast(Context context, String str)
    {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }





}//end
