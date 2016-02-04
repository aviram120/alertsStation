package com.example.aviram.alertstation;

import android.app.Activity;
import android.os.Bundle;
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
    private Button btSave;
    private RequestQueue queue;
    private ArrayList<CompanyData> _companyList;
    private ArrayList<CitesData> _citesList;
    private ArrayList<RoutesData> _routesList;
    private ArrayList<StopData> _stopsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
        String url=SERVER_URL+"/api?act=1";
        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
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
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);

    }

    private void getCitesFromServer()
    {

       String url = SERVER_URL+"/api?act=2&agency_id=" + _companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id();
       queue = Volley.newRequestQueue(this);

       StringRequest request = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>(){

                public void onResponse(String response) {
                    JSONArray jsonEventList;
                    try {
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
        String url=SERVER_URL+"/api?act=3&agency_id="+ _companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id()+
                "&city_id="+_citesList.get((int) spinner_city.getSelectedItemId()).getCity_id();
        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
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
                                    Log.i("aviramLog", "route id: " + jsonCompany.getString("route_id"));
                                }
                                addItemsOnSpinner(spinner_line, list_routes);//add to spinner
                            }catch (JSONException e){}
                        }

                    }

                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);

    }

    private void getStopsFromServer()
    {
        String url=SERVER_URL+"/api?act=4&route_id="+_routesList.get((int) spinner_line.getSelectedItemId()).getRoute_id();

        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    public void onResponse(String response) {
                        JSONArray jsonEventList;
                        try {
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
        }
    }


}
