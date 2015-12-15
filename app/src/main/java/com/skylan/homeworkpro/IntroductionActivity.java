package com.skylan.homeworkpro;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


//import homeworkprioritizer.myfirstapp.R;

import android.content.Intent;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

public class IntroductionActivity extends ActionBarActivity {

    public static boolean returnToIntro = true;
    public static final String INTRO_SCREEN = "homeworkprioritizer.myfirstapp.intro_screen";
    public DataManager dbHelper;

    public static TextView introText;
    public static TextView proceedToApp;
    public static TextView inputActivity;
    public static TextView oldDisplay;
    public static TextView drawerLayoutTest;
    public static TextView navigatorAct;
    public static TextView SQLiteDBAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        inputActivity = (TextView) findViewById(R.id.button_input_activity);
        oldDisplay = (TextView) findViewById(R.id.button_old_display_activity);
        drawerLayoutTest = (TextView) findViewById(R.id.button_main_navigation_activity);
        navigatorAct = (TextView) findViewById(R.id.button_navigator_activity);
        SQLiteDBAct = (TextView) findViewById(R.id.button_view_sqlite_db_activity);

        dbHelper = new DataManager(this);
        //dbHelper.addData(DataManager.ASSIGNMENT_TABLE, "blurgtitle"+((int) Math.ceil(Math.random()*100)), 98, "AP Fart", 5,
        //7, 3, 234952345, "poopsquare", 234523452345234523452345.234523452345, 345, 0, 0, 0);

        inputActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                inputActivity.setBackgroundResource(R.color.AppGreen);
                returnToIntro = false;
                Intent intent = new Intent(IntroductionActivity.this,
                        AssignmentInputActivity.class);
                Bundle introBundle = new Bundle();
                introBundle.putBoolean("introKey", returnToIntro);
                intent.putExtra(INTRO_SCREEN, introBundle);
                startActivity(intent);
                finish();
            }
        });

        oldDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                oldDisplay.setBackgroundResource(R.color.AppGreen);
                returnToIntro = false;
                Intent intent = new Intent(IntroductionActivity.this,
                        DisplayAssignmentsActivity.class);
                Bundle introBundle = new Bundle();
                introBundle.putBoolean("introKey", returnToIntro);
                intent.putExtra(INTRO_SCREEN, introBundle);
                startActivity(intent);
                finish();
            }
        });

        drawerLayoutTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                drawerLayoutTest.setBackgroundResource(R.color.AppGreen);
                returnToIntro = false;
                Intent intent = new Intent(IntroductionActivity.this,
                        MainNavigationActivity.class);
                Bundle introBundle = new Bundle();
                introBundle.putBoolean("introKey", returnToIntro);
                intent.putExtra(INTRO_SCREEN, introBundle);
                startActivity(intent);
                finish();
            }
        });

        SQLiteDBAct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SQLiteDBAct.setBackgroundResource(R.color.AppGreen);
                returnToIntro = false;
                Intent intent = new Intent(IntroductionActivity.this,
                        AndroidDatabaseManager.class);
                Bundle introBundle = new Bundle();
                introBundle.putBoolean("introKey", returnToIntro);
                intent.putExtra(INTRO_SCREEN, introBundle);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menu/options button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, AboutPage.class);
                startActivity(intent);
                finish();
                return true;

            //Gonna have to fuck around with this shit because it ain't working
            case R.id.pretest_option:
                Intent intent2 = new Intent(this, Pretest.class);
                startActivity(intent2);
                finish();
                return true;
            //Gonna have to fuck around with this shit because it ain't working - End

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Menu/options button - End

}
