package com.skylan.homeworkpro;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



//import homeworkprioritizer.myfirstapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class Pretest extends ActionBarActivity {

    public Button buttonToMain;
    public int studyAptitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretest);

        studyAptitude = 0;

        buttonToMain = (Button) findViewById(R.id.button_back_to_home);
        buttonToMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pretest.this,
                        AssignmentInputActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    ;



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();


        studyAptitude = 0;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.Q1A1:
                if (checked)
                    studyAptitude+=4;
                break;
            case R.id.Q1A2:
                if (checked)
                    studyAptitude+=3;
                break;
            case R.id.Q1A3:
                if (checked)
                    studyAptitude+=2;
                break;
            case R.id.Q1A4:
                if (checked)
                    studyAptitude+=1;
                break;


            case R.id.Q2A1:
                if (checked)
                    studyAptitude+=4;
                break;
            case R.id.Q2A2:
                if (checked)
                    studyAptitude+=3;
                break;
            case R.id.Q2A3:
                if (checked)
                    studyAptitude+=2;
                break;
            case R.id.Q2A4:
                if (checked)
                    studyAptitude+=1;
                break;


            case R.id.Q3A1:
                if (checked)
                    studyAptitude+=4;
                break;
            case R.id.Q3A2:
                if (checked)
                    studyAptitude+=3;
                break;
            case R.id.Q3A3:
                if (checked)
                    studyAptitude+=2;
                break;
            case R.id.Q3A4:
                if (checked)
                    studyAptitude+=1;
                break;

        }
    }



}



