package com.example.aviram.alertstation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by AVIRAM on 04/01/2016.
 */
public class Setting extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        Initialization();
    }

    private void Initialization() {

    }
}
