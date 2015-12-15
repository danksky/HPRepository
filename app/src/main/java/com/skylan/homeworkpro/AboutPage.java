package com.skylan.homeworkpro;

        import android.os.Bundle;
        import android.app.Activity;
        import android.content.Intent;
        import android.view.View;
        import android.widget.Button;

public class AboutPage extends Activity {

    public Button backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        backHome = (Button) findViewById(R.id.button_back_to_home);
        backHome.setOnClickListener(new View.OnClickListener() {


            //Brings user from the about page to the main screen if button clicked
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutPage.this,
                        AssignmentInputActivity.class);
                startActivity(intent);
                finish();
            }
            //Brings user from the about page to the main screen if button clicked - End
        });

    }
    ;



    //When back button pressed, returns user to main screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AssignmentInputActivity.class);

        startActivity(intent);
        finish();
    }
    //When back button pressed, returns user to main screen - End

}
