package com.example.aviram.alertstation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AVIRAM on 02/01/2016.
 */
public class CompanyData {

    private int company_id;
    private String company_name;
    private int indexInList;




    public CompanyData(JSONObject jsonObject,int indexInList)
    {
        try
        {
            int company_id = jsonObject.getInt("agencyId");
            String company_name=jsonObject.getString("agencyName");

            this.company_id=company_id;
            this.company_name=company_name;
            this.indexInList=indexInList;


        }
        catch (JSONException e)
        {

        }

    }

    public int getCompany_id() {
        return company_id;
    }

    public String getCompany_name() {
        return company_name;
    }
    public int getIndexInList() {
        return indexInList;
    }
}
