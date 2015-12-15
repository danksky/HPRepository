package com.skylan.homeworkpro;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//import homeworkprioritizer.myfirstapp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayAssignmentsActivity extends Activity {

    private DataManager dbHelper;
    private SQLiteDatabase dataBase;
    private AlertDialog.Builder build;

    //An ass man, for sure
    public int[] assIds;
    public String[] assTitles;
    public int[] assGrades;
    public String[] assSubjects;
    public int[] assDays;
    public int[] assDifficulties;
    public int[] assTypes;
    public int[] assDates;
    public String[] assDueDateTexts;
    public double[] assDueDateMSecs;
    public double[] assUrgencies;
    //An ass man, for sure - End

    public static ArrayList<AssignmentInfo> assignmentList;

    public String allValuesInOneString;
    public String[] allValuesInStringArray;

    public ListView lv;
    //public TextView testText;
    public TextView displayHelp;
    public Button newEntry;
    public Button clearEntries;
    public ImageButton editButton;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_assignments);

        Intent intent = getIntent();
        Bundle entered = intent.getBundleExtra(AssignmentInputActivity.EXTRA_ENTERED);

        dbHelper = new DataManager(this);
        lv = (ListView) findViewById(R.id.assignmentListView);
        //testText = (TextView) findViewById(R.id.testText);


        //Buttons on the screen
        displayHelp = (TextView) findViewById(R.id.display_question_mark);
        newEntry = (Button) findViewById(R.id.button_new_entry);
        clearEntries = (Button) findViewById(R.id.button_clear_entries);
        //Buttons on the screen - End


        //editButton = (ImageButton) findViewById(R.id.button_edit);
        //http://stackoverflow.com/questions/22883450/how-to-make-uniquely-acting-buttons-within-each-item-of-listview/22884093?noredirect=1#22884093

        dataBase = dbHelper.getWritableDatabase();
        displayData();

        //testText.setText(getListOfAssignmentsAsStringValue());

        // REM'ed because custom adapter will be made

        newEntry.setOnClickListener(new View.OnClickListener() {

            //Establishing variables to avoid null value
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(DisplayAssignmentsActivity.this,
                        AssignmentInputActivity.class);
                startActivity(intent);
                finish();
            }
            //Establishing variables to avoid null value - End
        });



        //Delete all assignments and user interface UI
        clearEntries.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                build = new AlertDialog.Builder(DisplayAssignmentsActivity.this);
                build.setTitle("Delete ALL assignments?");
                build.setMessage("Are you sure you want to get rid of all your assignments?");
                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(
                                //toast is a message that pops up
                                getApplicationContext(), "ASSIGNMENTS DELETED", Toast.LENGTH_LONG).show();
                        dataBase.delete(DataManager.ASSIGNMENT_TABLE, null, null);
                        dialog.cancel();
                        displayData();
                    }
                });



                build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {dialog.cancel();}
                });
                AlertDialog alert = build.create();
                alert.show();
            }
        });
        //Delete all assignments and user interface UI - End


        //Help button and user interface UI
        displayHelp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                final Dialog helpDialog = new Dialog(DisplayAssignmentsActivity.this, R.style.CustomDialogTheme);
                helpDialog.setTitle("Help");
                helpDialog.setContentView(R.layout.dialog_help);

                TextView helpText = (TextView) helpDialog.findViewById(R.id.help_explanation);
                helpText.setText(R.string.display_help);
                helpDialog.show();

                Button dialogButton = (Button) helpDialog.findViewById(R.id.button_help_dialog);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        helpDialog.dismiss();
                    }
                });

            }
        });
        //Help button and user interface UI - End


        //Delete individual assignments via long click method and user interface UI
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {

                build = new AlertDialog.Builder(DisplayAssignmentsActivity.this);
                build.setTitle("Delete " + assTitles[arg2]);
                build.setMessage("Do you want to delete this assignment?");
                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(
                                //toast is a message that pops up
                                getApplicationContext(), assTitles[arg2] + " " + "is deleted.", Toast.LENGTH_SHORT).show();
                        dataBase.delete(DataManager.ASSIGNMENT_TABLE, DataManager.ASSIGNMENT_NUM +
                                "=" + assIds[arg2], null);
                        dialog.cancel();
                        displayData();
                    }
                });

                build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {dialog.cancel();}
                });
                AlertDialog alert = build.create();
                alert.show();

                return true;
            }
        });
        //Delete individual assignments via long click method and user interface UI - End

    }

	/*
	 * @Override protected void onResume() { displayData(); super.onResume(); }
	 */

    // retrieves data from SQLite Database and stores it in an AssignmentInfo
    // ArrayList
    public ArrayList<AssignmentInfo> getListOfAssignments() {
        ArrayList<AssignmentInfo> assList = new ArrayList<AssignmentInfo>();
        dataBase = dbHelper.getWritableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
                + DataManager.ASSIGNMENT_TABLE, null);

        assList.clear();

        if (mCursor.moveToFirst()) {
            do {
                assList.add(new AssignmentInfo(
                        //if gives error about type for constructor, check the .get[type] method here
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_NUM)),
                        mCursor.getString(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_NAME)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_CLASS_GRADE)),
                        mCursor.getString(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_CLASS_SUBJECT)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_DAYS_UNTIL_DUE)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_DIFFICULTY)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_TYPE)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_ENTRY_DATE)),
                        mCursor.getString(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_DUE_DATE_TEXT)),
                        mCursor.getDouble(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_DUE_DATE_MSEC)),
                        mCursor.getDouble(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_URGENCY_RATING)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_ARCHIVED)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_COMPLETED)),
                        mCursor.getInt(mCursor
                                .getColumnIndex(DataManager.ASSIGNMENT_OVERDUE))
                        ));

            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return assList;

    }

    private void displayData() {
        updateDays();
        assignmentList = dbHelper.getAssignmentInfoArrayList();
        sortListOfAssignments(assignmentList);
        populateArrays(assignmentList);
        AssignmentArrayAdapter adapter = new AssignmentArrayAdapter(this,
                assTitles,
                getListOfAssignmentsAsStringArrayValue(),
                getListOfAssignmentValuesAsStringArrayValue(),
                assSubjects);
        lv.setAdapter(adapter);
    }

    //Establishing due date and difference in days until due
    public void updateDays () {
        assignmentList = getListOfAssignments();
        dataBase = dbHelper.getWritableDatabase();
        final SimpleDateFormat df = new SimpleDateFormat("EEEE, LLLL d");
        final SimpleDateFormat dfWeekdayAndDate = new SimpleDateFormat("E. dd");
        final SimpleDateFormat dfDate = new SimpleDateFormat("LLLL d");
        final Calendar cal = Calendar.getInstance();
        final Calendar calThen = Calendar.getInstance();

        int dateNow = (int) (cal.getTimeInMillis() / 1000 / 60 / 60 / 24);
        int dateDue;
        int numDaysBetween;
        String newDateText;
        double newUrgency;


        for (int go = 0; go < assignmentList.size(); go++) {

            dateDue = (int) (assignmentList.get(go).assignmentDueDateMSec / 1000 / 60 / 60 / 24);
            numDaysBetween = dateDue - dateNow;
            newDateText = "'nodate'";

            if (numDaysBetween == 0) {
                newDateText = "'Today'"; // don't forget the single quotes for SQLite
            } else if (numDaysBetween == 1) {
                newDateText = "'Tomorrow'";
            } else if ((numDaysBetween >= 2)
                    &&(numDaysBetween <= 7)) {
                calThen.setTimeInMillis( ((long)(assignmentList.get(go).assignmentDueDateMSec)));
                newDateText = "'" + dfWeekdayAndDate.format(calThen.getTime()) + "'";
            } else if (numDaysBetween >= 8) {
                calThen.setTimeInMillis( ((long)(assignmentList.get(go).assignmentDueDateMSec)));
                newDateText = "'" + dfDate.format(calThen.getTime()) + "'";
            }

            newUrgency = AssignmentInputActivity.giveMeARating(assignmentList.get(go).assignmentClassGrade,
                    numDaysBetween, assignmentList.get(go).assignmentDifficulty,
                    assignmentList.get(go).assignmentType);

            ContentValues values = new ContentValues();
            values.put(DataManager.ASSIGNMENT_DAYS_UNTIL_DUE, 1);

            dataBase.update(DataManager.ASSIGNMENT_TABLE, values, ""+DataManager.ASSIGNMENT_NUM + " = " + (assignmentList.get(go).assignmentID)+"", null);

            dataBase.execSQL("UPDATE " + DataManager.ASSIGNMENT_TABLE + " SET " + DataManager.ASSIGNMENT_ENTRY_DATE + " = " +
                    dateNow + " WHERE " +  DataManager.ASSIGNMENT_NUM + " = " +  (assignmentList.get(go).assignmentID) + ";");
            dataBase.execSQL("UPDATE " + DataManager.ASSIGNMENT_TABLE + " SET " + DataManager.ASSIGNMENT_DAYS_UNTIL_DUE + " = " +
                    numDaysBetween + " WHERE " +  DataManager.ASSIGNMENT_NUM + " = " +  (assignmentList.get(go).assignmentID) + ";");
            dataBase.execSQL("UPDATE " + DataManager.ASSIGNMENT_TABLE + " SET " + DataManager.ASSIGNMENT_DUE_DATE_TEXT + " = " +
                    newDateText + " WHERE " +  DataManager.ASSIGNMENT_NUM + " = " +  (assignmentList.get(go).assignmentID) + ";");
            dataBase.execSQL("UPDATE " + DataManager.ASSIGNMENT_TABLE + " SET " + DataManager.ASSIGNMENT_URGENCY_RATING + " = " +
                    newUrgency + " WHERE " +  DataManager.ASSIGNMENT_NUM + " = " +  (assignmentList.get(go).assignmentID) + ";");

        }
        deletePastEntries();
    }
    public void deletePastEntries () {
        assignmentList = getListOfAssignments();
        dataBase = dbHelper.getWritableDatabase();
        for (int go = 0; go < assignmentList.size(); go++) {
            dataBase.execSQL("DELETE FROM " + DataManager.ASSIGNMENT_TABLE + " WHERE " +
                    DataManager.ASSIGNMENT_DAYS_UNTIL_DUE + " < " +  0 + ";");
        }

    }
    //Establishing due date and difference in days until due - End


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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Menu/options button - End

    public void populateArrays(ArrayList<AssignmentInfo> assignmentList) {

        assIds = new int[assignmentList.size()];
        assTitles = new String[assignmentList.size()];
        assGrades = new int[assignmentList.size()];
        assSubjects = new String[assignmentList.size()];
        assDays = new int[assignmentList.size()];
        assDifficulties = new int[assignmentList.size()];
        assTypes = new int[assignmentList.size()];
        assDates = new int[assignmentList.size()];
        assDueDateTexts = new String[assignmentList.size()];
        assDueDateMSecs = new double[assignmentList.size()];
        assUrgencies = new double[assignmentList.size()];

        for (int go = 0; go < assTitles.length; go++) {
            assIds[go] = assignmentList.get(go).assignmentID;
            assTitles[go] = assignmentList.get(go).assignmentTitle;
            assGrades[go] = assignmentList.get(go).assignmentClassGrade;
            assSubjects[go] = assignmentList.get(go).assignmentClassSubject;
            assDays[go] = assignmentList.get(go).assignmentDaysUntilDue;
            assDifficulties[go] = assignmentList.get(go).assignmentDifficulty;
            assTypes[go] = assignmentList.get(go).assignmentType;
            assDates[go] = assignmentList.get(go).assignmentDate;
            assDueDateTexts[go] = assignmentList.get(go).assignmentDueDateText;
            assDueDateMSecs[go] = assignmentList.get(go).assignmentDueDateMSec;
            assUrgencies[go] = assignmentList.get(go).assignmentUrgencyRating;
        }
    }

    public String getListOfAssignmentsAsStringValue() {
        String output = "";
        String sep = " * ";
        for (int go = 0; go < assTitles.length; go++) {
            output = output + assIds[go] + sep + assTitles[go] + sep + assGrades[go] + sep + assSubjects[go] + sep + assDays[go]
                    + sep + assDifficulties[go] + sep + assTypes[go]
                    + sep + assDates[go] + sep + assDueDateTexts[go]
                    + sep + assDueDateMSecs[go] + sep + assUrgencies[go] + "\n";
        }
        return output;
    }
    public String[] getListOfAssignmentsAsStringArrayValue() {
        String[] output = new String[assTitles.length];
        String sep = " * ";
        for (int go = 0; go < assTitles.length; go++) {
            output[go] =    "Grade - " //+ assGrades[go]
                    + "\n" + "Due - " //+ assDays[go]
                    + "\n" + "Difficulty - "// + assDifficulties[go]
            //+ "\n\t\t" + "Subject - " + assSubjects[go]
            ;    //<<<;;;
        }
        return output;
    }
    public String[] getListOfAssignmentValuesAsStringArrayValue() {
        String[] output = new String[assTitles.length];
        String sep = " * ";
        for (int go = 0; go < assTitles.length; go++) {
            output[go] =    ""+ assGrades[go]
                    + "\n"  + assDueDateTexts[go]
                    + "\n"  + assDifficulties[go]
            //	+ "\n"  + assSubjects[go]
            ;    //<<<;;;
        }
        return output;
    }
    public void sortListOfAssignments(ArrayList<AssignmentInfo> assignmentList) {
        AssignmentInfo slot;
        for (int prev = (assignmentList.size()-1); prev>=0; prev--) {
            for (int go = 0; go<prev; go++) {
                if (assignmentList.get(go).assignmentUrgencyRating < assignmentList.get(prev).assignmentUrgencyRating) {
                    slot = assignmentList.get(go);
                    assignmentList.set(go, assignmentList.get(prev));
                    assignmentList.set(prev, slot);
                }
            }
        }
        DisplayAssignmentsActivity.assignmentList = assignmentList;
    }


    //Back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AssignmentInputActivity.class);

        startActivity(intent);
        finish();
    }
    //Back button - End


}
