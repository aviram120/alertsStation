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
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_id.setAdapter(dataAdapter);
    }

    private void initialization() {
        spinner_copmany = (Spinner) findViewById(R.id.spinner_copmany);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_line = (Spinner) findViewById(R.id.spinner_line);
        spinner_station = (Spinner) findViewById(R.id.spinner_station);

        btSave=(Button)findViewById(R.id.btSave);
        btSave.setOnClickListener(this);

        _companyList=new ArrayList<CompanyData>();//save all the 'company' from server-as object

        /*List < String > list_cop = new ArrayList<String>();

        list_cop.add("אגד");
        list_cop.add("דן");
        list_cop.add("מטרופולין");
        list_cop.add("סופרבוס");
        addItemsOnSpinner(spinner_copmany, list_cop);*/

        List < String > list_city = new ArrayList<String>();
        list_city.add("ירושלים");
        list_city.add("תל אביב");
        list_city.add("חיפה");
        list_city.add("אילת");
        addItemsOnSpinner(spinner_city, list_city);

        List < String > list_line = new ArrayList<String>();
        list_line.add("קו 22");
        list_line.add("קו 18");
        list_line.add("קו 480");
        list_line.add("קו 21");
        addItemsOnSpinner(spinner_line, list_line);

        List < String > list_station = new ArrayList<String>();
        list_station.add("רחוב 1");
        list_station.add("רחוב 2");
        list_station.add("רחוב 3");
        list_station.add("רחוב 4");
        addItemsOnSpinner(spinner_station, list_station);
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

                                    Log.i("aviramLog", i + "" + jsonCompany.getString("agencyName"));
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
    public void onClick(View v) {


        if (v.getId() == R.id.btSave)//right text view
        {
            Log.i("aviramLog","spinner1:getSelectedItem: "+spinner_copmany.getSelectedItem());
            Log.i("aviramLog","spinner1:getSelectedItemId: "+spinner_copmany.getSelectedItemId());
            Log.i("aviramLog", "company name"+_companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_name());
            Log.i("aviramLog", "id"+_companyList.get((int) spinner_copmany.getSelectedItemId()).getCompany_id());

        }
    }


}
