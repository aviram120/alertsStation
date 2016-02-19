package com.example.aviram.alertstation;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Yaacov on 18/01/2016.
 */
public class RoutesData  {
    private int route_id;
    private String route_short_name;
    private int indexInList;

    public RoutesData(JSONObject jsonObject,int indexInList) {
        try
        {
            int routes_id = jsonObject.getInt("route_id");
            String routes_short_name=jsonObject.getString("route_short_name");

            this.route_id = routes_id;
            this.route_short_name = routes_short_name;
            this.indexInList=indexInList;
        }
        catch (JSONException e)
        {

        }
    }
    public int getRoute_id() {
        return route_id;
    }
    public String getRoute_short_name() {
        return route_short_name;
    }
    public int getIndexInList() {
        return indexInList;
    }

}
