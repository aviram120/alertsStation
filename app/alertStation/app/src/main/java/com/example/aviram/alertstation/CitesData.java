package com.example.aviram.alertstation;

import org.json.JSONException;
import org.json.JSONObject;

public class CitesData
{
    private int city_id;
    private String city_name;
    private int indexInList;

    public CitesData(JSONObject jsonObject,int indexInList) {
        try
        {
            int city_id = jsonObject.getInt("city_id");
            String city_name=jsonObject.getString("city_name");

            this.city_id = city_id;
            this.city_name = city_name;
            this.indexInList=indexInList;
        }
        catch (JSONException e)
        {

        }
    }
    public int getCity_id() {
        return city_id;
    }
    public String getCity_name() {
        return city_name;
    }
    public int getIndexInList() {
        return indexInList;
    }
}
