package com.example.aviram.alertstation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by AVIRAM on 04/01/2016.
 */
public class Home extends Activity implements View.OnClickListener
{
    private Button btApp,btSetting,btAbout;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home);
        Initialization();
    }
    private void Initialization()
    {
        btApp=(Button)findViewById(R.id.buttonTOapp);
        btApp.setOnClickListener(this);

        btSetting=(Button)findViewById(R.id.btSetting);
        btSetting.setOnClickListener(this);

        btAbout=(Button)findViewById(R.id.btAbout);
        btAbout.setOnClickListener(this);
    }
    public void onClick(View v){
        if (v.getId()==R.id.buttonTOapp)
        {
            Intent Inetent = new Intent(this,MainActivity.class);
            startActivity(Inetent);
        }
        if (v.getId()==R.id.btSetting)
        {
            Intent Inetent = new Intent(this,Setting.class);
            startActivity(Inetent);
        }

        if (v.getId()==R.id.btAbout)
        {
            Intent Inetent = new Intent(this,About.class);
            startActivity(Inetent);
        }


    }



}
