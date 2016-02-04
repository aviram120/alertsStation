package com.example.aviram.alertstation;

import org.json.JSONException;
import org.json.JSONObject;

public class StopData {
    private String stop_name;
    private String stop_lat;
    private String stop_lon;
    private int stop_sequence;
    private int indexInList;

    public StopData(JSONObject jsonObject,int indexInList)
    {
        try
        {
            int stop_sequence = jsonObject.getInt("stop_sequence");
            String stop_name=jsonObject.getString("stop_name");
            String stop_lat=jsonObject.getString("stop_lat");
            String stop_lon=jsonObject.getString("stop_lon");

            this.stop_sequence = stop_sequence;
            this.stop_name = stop_name;
            this.stop_lat = stop_lat;
            this.stop_lon = stop_lon;
            this.indexInList=indexInList;
        }
        catch (JSONException e)
        {

        }
    }


    public String getStop_name() {
        return stop_name;
    }

    public String getStop_lat() {
        return stop_lat;
    }

    public String getStop_lon() {
        return stop_lon;
    }

    public int getStop_sequence() {
        return stop_sequence;
    }

    public int getIndexInList() {
        return indexInList;
    }
}
