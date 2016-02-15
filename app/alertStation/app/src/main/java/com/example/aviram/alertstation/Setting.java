package com.example.aviram.alertstation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by AVIRAM on 04/01/2016.
 */
public class Setting extends Activity implements View.OnClickListener{
    private EditText etDistance;
    private Button btSave;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    CheckBox chNoti,chAlertClock;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        Initialization();
    }
    public void onClick(View v){
        String stDistance=etDistance.getText().toString();
        if (!stDistance.contains(".")&&(!stDistance.isEmpty()))
        {
            int levelNum=Integer.parseInt(stDistance);//get the date form the field
            if (levelNum>0)//check if the user add ok number
            {
                if (chNoti.isChecked())
                {
                    editor.putInt("CheckBoxNoti",1);//put the date in sharedPref
                }
                else
                {
                    editor.putInt("CheckBoxNoti",0);//put the date in sharedPref
                }
                if (chAlertClock.isChecked())
                {
                    editor.putInt("CheckBoxchAlertClock",1);//put the date in sharedPref
                }
                else
                {
                    editor.putInt("CheckBoxchAlertClock",0);//put the date in sharedPref
                }

                editor.putInt("DistanceFrom",levelNum);//put the date in sharedPref
                editor.apply();

                Intent Inetent = new Intent(this,Home.class);
                startActivity(Inetent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Distance must be up then 0", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "can't be empty \n Integer ", Toast.LENGTH_SHORT).show();
        }
    }
    private void Initialization() {
        //set Shared Preferences-for save the setting
        sharedPref = getSharedPreferences("prefDistanceFromStation", MODE_PRIVATE);
        editor = sharedPref.edit();

        etDistance=(EditText)findViewById(R.id.editTextDes);
        btSave=(Button)findViewById(R.id.btSaveDes);
        btSave.setOnClickListener(this);

        chNoti=(CheckBox)findViewById(R.id.checkBoxNoti);
        chAlertClock=(CheckBox)findViewById(R.id.checkBoxAlertClock);

        if ((sharedPref.getInt("CheckBoxNoti", 1)==1))
            chNoti.setChecked(true);
        else
            chNoti.setChecked(false);

        if ((sharedPref.getInt("CheckBoxchAlertClock", 0)==1))
            chAlertClock.setChecked(true);
        else
            chAlertClock.setChecked(false);


        etDistance.setText(Integer.toString(sharedPref.getInt("DistanceFrom", 200)));//get the date from sharedPref
    }
}
