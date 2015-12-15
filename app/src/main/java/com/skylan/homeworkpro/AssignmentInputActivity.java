package com.skylan.homeworkpro;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AssignmentInputActivity extends ActionBarActivity {

    public final static String EXTRA_ENTERED = "homeworkprioritizer.myfirstapp.UserInput";
    public static final String CLASSES_PREFS = "AssignmentClassesPreferencesList";
    public static final String[] DATE_OPTIONS = { "Due date?", "Today", "Tomorrow", "Set date..." };
    public static final String[] DATE_OPTIONS_2 = { "Due date?", "Today", "Tomorrow" };
    public static Calendar cal;
    public static TextView inputHelp;
    public static TextView sendIt;
    public static TextView skipToList;
    public static EditText inputTitle;
    public static EditText inputGrade;
    //  public static EditText inputDays;
    public static EditText inputDifficulty;
    public static Spinner spinner;
    public static Spinner spinnerClass;
    public static Spinner dateDropper;
    public static RelativeLayout subjectBackBox;
    public static RelativeLayout typeBackBox;
    public static RelativeLayout dateBackBox;
    public static CalendarView calView;
    public static AlertDialog dateDialog;
    public boolean newEntryInputScreen = true;
    public String assTitle;
    public int assGrade = 999;
    public String assSubject;
    public int assSubjectPos = 0;
    public int numprefs;
    public int assDays = 999;
    public int assDifficulty = 1;
    public int assType = 7;
    public String assDueDateText = "empty_date";
    public int assDate;
    public double assDueDateMSec=0;
    public double assRating = 0;
    public boolean assArchived = false;
    public boolean assCompleted = false;
    public boolean assOverdue = false;
    public String[] homework_array;
    public ArrayList classesList;
    public ArrayList<CharSequence> dateOptionsList;
    public ArrayList<CharSequence> dateOptionsList2;
    SharedPreferences classesPrefsList;
    private DataManager dbHelper;
    private SQLiteDatabase dataBase;
    private AlertDialog.Builder build;

    public static int booleanToInt (boolean input) {
        int verdict;
        if (input) {
            verdict = 1;
        } else {
            verdict = 0;
        }
        return verdict;
    }

    public static double giveMeARating(int grade, int days, int difficulty, int type) {

        // Provision to make due tomorrow most important

        double priority = 0;
        double aDays = (double) days;
        double aGrade = (double) grade;
        double aDifficulty = (double) difficulty;
        double aType = (double) type;


        Pretest pre = new Pretest();
        double studyAptitude = pre.studyAptitude;


        //Preliminary checks to ensure algorithm functions properly
        if (aDays <= 0) {
            aDays = 1;
        }

        if (aDifficulty < 1) {
            aDifficulty = 1;
        }

        if (aDifficulty > 10) {
            aDifficulty = 10;
        }

        if (aGrade < 60) {
            aGrade = 60;
        }

        if (aGrade > 110) {
            aGrade = 110;
        }
        //end


        if (studyAptitude >= 11){


            if (aDays == 1) {
                priority += 1000;
            }

            if (aType == 1) {
                priority += ((25 / (.25 * aDays)) + (100 - aGrade)
                        + (3 * aDifficulty) + 30);
            }

            else if (aType == 2) {
                priority += ((22.5 / (.5 * aDays)) + (100 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 3) {
                priority += ((20 / (.5 * aDays)) + (100 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 4) {
                priority += ((22.5 / (.5 * aDays)) + (100 - aGrade)
                        + (2.5 * aDifficulty) + 20);
            }

            else if (aType == 5) {
                priority += ((35 / aDays) + ((100 - aGrade) / 2)
                        + (2 * aDifficulty) + 15);
            }

            else if (aType == 6) {
                priority += ((35 / (1.1667 * aDays)) + ((100 - aGrade) / 2.667)
                        + (1.5 * aDifficulty) + 10);
            }

            else if (aType == 7) {
                priority += ((30 / (1.33 * aDays)) + ((100 - aGrade) / 2.667)
                        + aDifficulty + 5);
            }
        }


        if (studyAptitude >= 8 && studyAptitude <=10){
            if (aDays == 1) {
                priority += 1000;
            }

            if (aType == 1) {
                priority += ((25 / (.7 * aDays)) + (95 - aGrade)
                        + (3 * aDifficulty) + 25);
            }

            else if (aType == 2) {
                priority += ((22.5 / (.8 * aDays)) + (95 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 3) {
                priority += ((20 / (.8 * aDays)) + (95 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 4) {
                priority += ((22.5 / (.8 * aDays)) + (95 - aGrade)
                        + (2.5 * aDifficulty) + 20);
            }

            else if (aType == 5) {
                priority += ((35 / aDays) + ((95 - aGrade) / 2)
                        + (2 * aDifficulty) + 15);
            }

            else if (aType == 6) {
                priority += ((35 / (1.1667 * aDays)) + ((95 - aGrade) / 2.667)
                        + (1.5 * aDifficulty) + 10);
            }

            else if (aType == 7) {
                priority += ((30 / (1.33 * aDays)) + ((95 - aGrade) / 2.667)
                        + aDifficulty + 5);
            }

        }


        //Student 3 - Procrastinator
        if (studyAptitude >= 5 && studyAptitude <=7){
            if (aDays == 1) {
                priority += 1000;
            }

            if (aType == 1) {
                priority += ((25 / (aDays)) + (90 - aGrade)
                        + (3 * aDifficulty) + 25);
            }

            else if (aType == 2) {
                priority += ((22.5 / (1.25 * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 3) {
                priority += ((20 / (1.25 * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 4) {
                priority += ((22.5 / (1.25 * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 20);
            }

            else if (aType == 5) {
                priority += ((35 / (aDays * aDays)) + ((90 - aGrade) / 2)
                        + (2 * aDifficulty) + 15);
            }

            else if (aType == 6) {
                priority += ((30 / (aDays * aDays)) + ((90 - aGrade) / 2.667)
                        + (1.5 * aDifficulty) + 10);
            }

            else if (aType == 7) {
                priority += ((25 / (aDays * aDays)) + ((90 - aGrade) / 2.667)
                        + aDifficulty + 5);
            }

        }


        //Student 4 - Last-minuter
        if (studyAptitude <= 4){
            if (aDays == 1) {
                priority += 1000;
            }

            if (aType == 1) {
                priority += ((25 / (aDays)) + (90 - aGrade)
                        + (3 * aDifficulty) + 30);
            }

            else if (aType == 2) {
                priority += ((22.5 / (1.5 * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 3) {
                priority += ((20 / (1.5 * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 25);
            }

            else if (aType == 4) {
                priority += ((17.5 / (aDays * aDays)) + (90 - aGrade)
                        + (2.5 * aDifficulty) + 20);
            }

            else if (aType == 5) {
                priority += ((20 / (aDays * aDays * aDays * aDays)) + ((90 - aGrade) / 2)
                        + (2 * aDifficulty) + 15);
            }

            else if (aType == 6) {
                priority += ((20 / (aDays * aDays * aDays)) + ((90 - aGrade) / 2.667)
                        + (1.5 * aDifficulty) + 10);
            }

            else if (aType == 7) {
                priority += ((20 / (aDays * aDays * aDays * aDays)) + ((90 - aGrade) / 2.667)
                        + aDifficulty + 5);
            }

        }

        return priority;
    }


    // VISUAL FORMAT IDEA:
	/*
	 * * * http://stackoverflow.com/questions/4310525/android-on-edittext-changed-
	 * listener IDEA: visual format greenish (whiter than edittext background)
	 * overall background as numbers entered make the rating go up, the
	 * background for edit boxes (more solid hue) go closer to red (R<O<Y<G) as
	 * well as urgency. Each can be divided to make a percentage so boxes will
	 * get redder faster example: difficulty could be 9 (90%) but the urgency
	 * would only be the weight of difficulty out of 130 (~ 8-15%) so doesn't
	 * get as red as easily.
	 * * * will need to write: if-then statements (inequality
	 * ranges) find the color scheme and either make hexidec. values from
	 * research of color to int to hexidec. or just find the values for a ton of
	 * colors...probably not as smooth because hard-coded, but may end up being
	 * more practical (not good coding practice if we want to improve)
	 * * * Should establish one set hue of green for default graphics so nothing
	 * clashes by off-coloration
	 * * * Color conversion: Because only using RED to GREEN, keep blue as 0.
	 * Begins by having Green at 256 when urgency 0. Once % Urgency reaches 50%,
	 * Red has reached 256 as well, then from 50% on, Green decreases to 0. Opacity
	 * will work because a white rectangle will be behind each EditText, so have an
	 * opacity value set for use. Opacity hex charts online.
	 * Rectangles should be solid white in XML, then replaced with opaque ones in
	 * code... not sure if this will work as a layers concept, but worth a shot
	 * http://stackoverflow.com/questions/20943679/how-can-i-place-an-opaque-white-rectangle-behind-my-translucent-edittext-backgro
	 *
	 *
	 */

    // Users should be able to move items somehow.
    // Would benefit from being able to send statistics (what they moved) via email with attached Read-only
    // Experience needed to create read-only text and we need a contacts page
    // that being said, user customizeability and Settings activity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.about_info:
                Intent intent = new Intent(this, Pretest.class);
                this.startActivity(intent);
                finish();
                return true;


            case R.id.action_settings:
                intent = new Intent(this, AboutPage.class);
                this.startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_input);
        sendIt = (TextView) findViewById(R.id.textViewDone);
        skipToList = (TextView) findViewById(R.id.textViewSkip);
        inputHelp = (TextView) findViewById(R.id.input_question_mark);
        inputTitle = (EditText) findViewById(R.id.edit_title);
        inputGrade = (EditText) findViewById(R.id.edit_grade);
        //	inputDays = (EditText) findViewById(R.id.edit_days); replaced by dateDropper
        inputDifficulty = (EditText) findViewById(R.id.edit_difficulty);

        spinner = (Spinner) findViewById(R.id.homework_list);
        spinnerClass = (Spinner) findViewById(R.id.class_list);
        dateDropper = (Spinner) findViewById(R.id.date_dropdown);
        subjectBackBox = (RelativeLayout) findViewById(R.id.display_assignments_subject_bar);
        typeBackBox = (RelativeLayout) findViewById(R.id.display_assignments_type_bar);
        dateBackBox = (RelativeLayout) findViewById(R.id.display_assignments_date_bar);

        dateOptionsList = new ArrayList<CharSequence>();
        dateOptionsList2 = new ArrayList<CharSequence>();
        dateOptionsList.add("Due date?");
        dateOptionsList.add("Today");
        dateOptionsList.add("Tomorrow");
        dateOptionsList.add("Set date...");
        dateOptionsList2.add("Due date?");
        dateOptionsList2.add("Today");
        dateOptionsList2.add("Tomorrow");
        classesPrefsList = getSharedPreferences(CLASSES_PREFS, 0);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.homework_array, R.layout.new_scroller);
        classesList = getClassesArrayListSharedPreferences();
        final ArrayAdapter<CharSequence> classAdapter = new ArrayAdapter<CharSequence>(this, R.layout.new_scroller, classesList);
        final ArrayAdapter<CharSequence> dateAdapter = new ArrayAdapter<CharSequence>(this, R.layout.new_scroller, dateOptionsList);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(typeAdapter);
        spinnerClass.setAdapter(classAdapter);
        dateDropper.setAdapter(dateAdapter);
        cal = Calendar.getInstance();
        if (newEntryInputScreen) {
            assignDefaultBackgroundColors();
        }

        //final Animation slideOffAnim = AnimationUtils.loadAnimation(this, R.anim.edittext_slide_off);

        dbHelper = new DataManager(this);
        dataBase = dbHelper.getWritableDatabase(); // creates/opens database initially

        // Box color changer, so far.
        inputGrade.addTextChangedListener(new TextWatcher() {

            double rating = 0;
            double fullRating = 0;

            public void afterTextChanged(Editable s) {
                assignValues();
                rating = ((double) assGrade / 110) * 512; // 0 - 256
                if (rating > 512) {
                    rating = 512;
                }
                String hex = giveMeAHexValue(rating);
                inputGrade.setBackgroundColor(Color.parseColor(hex));
                // * * replace with a set colors method (that does all [boxes
                // 	   and background])
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //runs the INSTANT before the text is changed
                //String hex = giveMeAHexValue(512);
                //inputGrade.setBackgroundColor(Color.parseColor(hex));
            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {}

        });
		/*
		inputDays.addTextChangedListener(new TextWatcher() {
			double rating = 0;
			double fullRating = 0;

			public void afterTextChanged(Editable s) {
				assignValues();
				rating = ((double) assDays / 10) * 512; // 0 - 256
				if (rating > 512) {
					rating = 512;
				}
				String hex = giveMeAHexValue(rating);
				inputDays.setBackgroundColor(Color.parseColor(hex));
				// * * replace with a set colors method (that does all [boxes
				// 	   and background])
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		*/
        inputDifficulty.addTextChangedListener(new TextWatcher() {
            double rating = 0;
            double fullRating = 0;

            public void afterTextChanged(Editable s) {
                assignValues();
                rating = ((double) assDifficulty / 10) * 512; // 0 - 256
                rating = 512 - rating;
                if (rating < 0) {
                    rating = 0;
                } else if (rating >512) {
                    rating = 512;
                }
                String hex = giveMeAHexValue(rating);
                inputDifficulty.setBackgroundColor(Color.parseColor(hex));
                // * * replace with a set colors method (that does all [boxes
                // 	   and background])
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                String hex = giveMeAHexValue(512);
                inputDifficulty.setBackgroundColor(Color.parseColor(hex));
            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {}
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                double rating = 0;
                double fullRating = 0;
                newEntryInputScreen = false;
                assignValues();
                rating = ((double) (assType-1) / 6) * 512; // 0 - 256
                String hex = giveMeAHexValue(rating);
                if (rating > 512) {
                    rating = 512;
                    hex = giveMeAHexValue(rating);
                }
                typeBackBox.setBackgroundColor(Color.parseColor(hex));

                if (assType == 0) {
                    typeBackBox.setBackgroundResource(R.drawable.blue_fading_for_spinners);
                }
                // * * replace with a set colors method (that does all [boxes
                // and background])
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NewApi")
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                assSubject = spinnerClass.getItemAtPosition(pos).toString();
                assSubjectPos = (int) id;
                if (id == 0) { //Set to default "Due date?"
                    subjectBackBox.setBackgroundResource(R.drawable.blue_fading_for_spinners);
                }
                else {
                    newEntryInputScreen = false;
                    //subjectBackBox.setBackground(null);
                    subjectBackBox.setBackgroundColor(Color.parseColor("#10000000"));
                    // have each new entry be "insert"ed to arrayadapter so create is at end
                    // the newer, the closer to the top
                    if (id==(classAdapter.getCount()-2))
                    // eventually uses as element value to check array or SharedPrefs if matches
                    // "Create New+" or not < so doesn't prompt for no reason
                    {
                        build = new AlertDialog.Builder(AssignmentInputActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View alertview = inflater.inflate(R.layout.create_subject_dialog, null);

                    // Pass null as the parent view because its going in the dialog layout
                    build.setView(alertview);
                    final EditText inputClass = (EditText) alertview.findViewById(R.id.dialog_edit_subject_card_name);
                    build.setTitle("New Class Entry")
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    String enteredClass = inputClass.getText().toString();

                                    // enter data into list of classes (to stay and display) somehow
                                    assSubject = enteredClass;
                                    classesList = getClassesArrayListSharedPreferences();
                                    classesPrefsList = getSharedPreferences(CLASSES_PREFS, 0);
                                    int numprefs = classesPrefsList.getAll().values().size();
                                    SharedPreferences.Editor editor = classesPrefsList.edit();
                                    editor.putString("ClassList"+numprefs, enteredClass);
                                    // numprefs is the size of the ClassList preferences which
                                    // starts at element 0, I'm assuming
                                    editor.commit();
                                    classesList = getClassesArrayListSharedPreferences();
                                    classAdapter.clear();
                                    classAdapter.addAll(classesList);
                                    classAdapter.notifyDataSetChanged();
                                    spinnerClass.setSelection(1);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = build.create();
                    alert.show();
                }
                    if (id==(classAdapter.getCount()-1))
                    // "Remove all" or not < so doesn't prompt for no reason
                    {
                        build = new AlertDialog.Builder(AssignmentInputActivity.this);
                        build.setMessage("Are you sure you would like to remove all subject entries?");
                        build.setTitle("Remove all subjects?")
                                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // enter data into list of classes (to stay and display) somehow

                                        classesList = getClassesArrayListSharedPreferences();
                                        classesPrefsList = getSharedPreferences(CLASSES_PREFS, 0);
                                        SharedPreferences.Editor editor = classesPrefsList.edit();
                                        editor.clear();
                                        editor.commit();
                                        classesList = getClassesArrayListSharedPreferences();
                                        classAdapter.clear();
                                        classAdapter.addAll(classesList);
                                        classAdapter.notifyDataSetChanged();
                                        spinnerClass.setSelection(0);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = build.create();
                        alert.show();
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        dateDropper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SimpleDateFormat")
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // Create an instance of SimpleDateFormat used for formatting
                // the string representation of date (month/day/year)
                // Monday, January 02
                final SimpleDateFormat df = new SimpleDateFormat("EEEE, LLLL dd");
                final Calendar cal = Calendar.getInstance();
                final Calendar calThen = Calendar.getInstance();

                assDate = (int)( cal.getTimeInMillis() / 1000 / 60 / 60 / 24);
                assDueDateMSec = cal.getTimeInMillis();
                java.util.Date today = Calendar.getInstance().getTime();
                assDueDateText = df.format(today);

                double rating = 0;
                double fullRating = 0;


                //View dropperView = inflater.inflate(R.layout.new_scroller, null);
                //final TextView dateDropperText = (TextView) dropperView.findViewById(R.id.date_dropper_text);
                if (id == 0) { //Set to default "Due date?"
                    dateBackBox.setBackgroundResource(R.drawable.blue_fading_for_spinners);
                }
                if (id == 1 ) { //Today
                    assDays = 0;
                    assDate = (int)( cal.getTimeInMillis() );
                    assDueDateMSec = cal.getTimeInMillis();
                    assDueDateText = df.format(today);

                    rating = ((double) assDays / 10) * 512; // 0 - 256
                    if (rating > 512) {
                        rating = 512;
                    }
                    String hex = giveMeAHexValue(rating);
                    dateBackBox.setBackgroundColor(Color.parseColor(hex));
                }

                if (id == 2) { //Tomorrow
                    assDays = 1;
                    // Monday, January 02
                    assDate = 	(int)( cal.getTimeInMillis() + 86400000 );
                    assDueDateMSec = ( cal.getTimeInMillis() + 86400000 );
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    java.util.Date tomorrow = cal.getTime();
                    assDueDateText = df.format(tomorrow);
                    rating = ((double) assDays / 10) * 512; // 0 - 256
                    if (rating > 512) {
                        rating = 512;
                    }
                    String hex = giveMeAHexValue(rating);
                    dateBackBox.setBackgroundColor(Color.parseColor(hex));
                }
                if (id == 3) { //Set Date
                    build = new AlertDialog.Builder(AssignmentInputActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View alertview = inflater.inflate(R.layout.calendar_view, null);

                    // Pass null as the parent view because its going in the dialog layout
                    build.setView(alertview);
                    final CalendarView calView = (CalendarView) alertview.findViewById(R.id.calendarView);
                    calView.setOnDateChangeListener(new OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                            assDueDateText = df.format(calView.getDate());
                        }
                    });
                    build.setTitle("Assignment Due:")
                            .setPositiveButton("Set date", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dateAdapter.clear();
                                    dateAdapter.addAll(dateOptionsList2);
                                    dateAdapter.add(assDueDateText);
                                    dateAdapter.notifyDataSetChanged();

                                    int dateNow = (int) ( cal.getTimeInMillis() / 1000 / 60 / 60 / 24);
                                    int dateThen = (int) ( calView.getDate() / 1000 / 60 / 60 / 24 );
                                    assDueDateMSec = calView.getDate();
                                    assDays = dateThen - dateNow;
                                    assDate = dateThen;
                                    double rating = ((double) assDays / 10) * 512; // 0 - 256
                                    if (rating > 512) {
                                        rating = 512;
                                    }
                                    String hex = giveMeAHexValue(rating);
                                    dateBackBox.setBackgroundColor(Color.parseColor(hex));
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = build.create();
                    alert.show();

                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        skipToList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //skipToList.startAnimation(slideOffAnim);
                skipToList.setBackgroundResource(R.color.AppGreen);
                Intent intent = new Intent(AssignmentInputActivity.this,
                        DisplayAssignmentsActivity.class);

                startActivity(intent);
                finish();
            }
        });
        sendIt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                assignValues();


                String input = assTitle + "\n" + assGrade + "\n" + assDays
                        + "\n" + assDifficulty + "\n" + assRating;
                //assTitle += "|" + input;

				/*
				 * ALTERNATIVE WAY TO ENTER DATA ContentValues values = new
				 * ContentValues();
				 *
				 * values.put(DBRecordsLayer.ASSIGNMENT_NAME, assTitle);
				 * values.put(DBRecordsLayer.ASSIGNMENT_CLASS_GRADE, assGrade);
				 * values.put(DBRecordsLayer.ASSIGNMENT_DAYS_UNTIL_DUE,
				 * assDays); values.put(DBRecordsLayer.ASSIGNMENT_DIFFICULTY,
				 * assDifficulty);
				 * values.put(DBRecordsLayer.ASSIGNMENT_URGENCY_RATING,
				 * assRating);
				 *
				 * // insert data into database
				 * dataBase.insert(DBRecordsLayer.ASSIGNMENT_TABLE, null,
				 * values);
				 */

                if (formsAreFilled(assGrade, assDays, assDifficulty, assType, assSubjectPos)) {
                    sendIt.setBackgroundResource(R.color.AppGreen);
                    Intent intent = new Intent(AssignmentInputActivity.this,
                            DisplayAssignmentsActivity.class);
                    Bundle entered = new Bundle();
                    entered.putString("titleKey", assTitle);
                    entered.putInt("gradeKey", assGrade);
                    entered.putInt("daysKey", assDays);
                    entered.putInt("difficultyKey", assDifficulty);
                    entered.putInt("typeKey", assType);
                    entered.putInt("dateKey", assDate);
                    entered.putDouble("ratingKey", assRating);
                    entered.putString("subjectKey", assSubject);

                    // needs code to make sure each form is filled out

                    intent.putExtra(EXTRA_ENTERED, entered);
                    dbHelper.addData(DataManager.ASSIGNMENT_TABLE, assTitle,
                            assGrade, assSubject, assDays, assDifficulty, assType,
                            assDate, assDueDateText, assDueDateMSec, assRating,
                            booleanToInt(assArchived), booleanToInt(assCompleted), booleanToInt(assOverdue));
                    // rather than just the title, send a Bundle with all
                    // information

                    startActivity(intent);
                    finish();
                } else {

                    build = new AlertDialog.Builder(AssignmentInputActivity.this);
                    build.setTitle("Incomplete Submission");
                    build.setMessage("Please complete the assignment submission form.");

                    build.setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = build.create();
                    alert.show();
                }

                // close database
                dataBase.close();

            }
        });
        inputHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
				/*build = new AlertDialog.Builder(AssignmentInputActivity.this);
				LayoutInflater inflater = getLayoutInflater();
				View alertview = inflater.inflate(R.layout.dialog_help, null);
				//alertview.setBackgroundResource(R.drawable.white_translucent_background);
			    build.setView(alertview);
			    build.setMessage(R.string.input_help);
			    build.setTitle("Help")
			           .setNegativeButton("Done", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                   dialog.cancel();
			               }
			           });
				AlertDialog alert = build.create();
				alert.show();*/

                final Dialog helpDialog = new Dialog(AssignmentInputActivity.this, R.style.CustomDialogTheme);
                helpDialog.setTitle("Help");
                helpDialog.setContentView(R.layout.dialog_help);

                TextView helpText = (TextView) helpDialog.findViewById(R.id.help_explanation);
                helpText.setText(R.string.input_help);
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
    }

    private void assignDefaultBackgroundColors() {
        String defaultGray = "#10000000";
        inputGrade.setBackgroundResource(R.drawable.blue_fading_for_edittexts);
        inputDifficulty.setBackgroundResource(R.drawable.blue_fading_for_edittexts);
        typeBackBox.setBackgroundResource(R.drawable.blue_fading_for_spinners);//these two don't work, had to put if's in actual methods
        dateBackBox.setBackgroundResource(R.drawable.blue_fading_for_spinners);

    }

    private boolean formsAreFilled(int form1, int form2, int form3,
                                   int typeScrollerSelected, int subjectScrollerSelected) {
        boolean verdict = false;
        if ((form1 > 0) && (form2 >= 0) && (form3 > 0)
                && (typeScrollerSelected != 0)
                && (subjectScrollerSelected!=0)
                && (subjectScrollerSelected!=(classesList.size()-2)
                && (subjectScrollerSelected!=(classesList.size()-1))
        ))
            verdict = true;
        return verdict;

    }

    public static String giveMeAHexValue(double rated) {
        int i = (int) rated;
        String part = "00";
        String complete = "";
        if (rated<=256) {
            String hex = Integer.toHexString(i);
            if (hex.length() <= 1) {
                complete = "#4DFF0" + hex + part;
            } else if (hex.length() == 2) {
                complete = "#4DFF" + hex + part;
            } else if (hex.length() > 2) {
                hex = Integer.toHexString(255);
                complete = "#4DFF" + hex + part;
            }

        } else if (rated>256) {
            i= 256 - Math.abs(256 - i);
            String hex = Integer.toHexString(i);
            if (hex.length() <= 1) {
                complete = "#4D0" + hex + "FF" + part;
            } else if (hex.length() == 2) {
                complete = "#4D" +hex + "FF" + part;
            } else if (i < 0) {
                hex = Integer.toHexString(0);
                complete = "#4D" + "0" + hex + "FF" + part;
            }
        }
        return complete;
    }
    public void assignValues() {
        int tempType;
        assTitle = "" + inputTitle.getText().toString();
        assGrade = Integer.parseInt("0"
                + inputGrade.getText().toString());
        //assDays = Integer
        //		.parseInt("0" + inputDays.getText().toString());
        assDifficulty = Integer.parseInt("0"
                + inputDifficulty.getText().toString());
        assType = (int) spinner.getSelectedItemId();
        tempType = assType;
        if (assType == 0) {
            tempType = 7;
        }
        //assDueDateText = ""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DATE);
        //assDate = Integer.parseInt(assDueDateText);//January is 0

        assRating = giveMeARating(assGrade, assDays, assDifficulty,
                tempType);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ArrayList getClassesArrayListSharedPreferences() {
        //String asdf;
        String classElement;
        classesPrefsList = getSharedPreferences(CLASSES_PREFS, 0);
        SharedPreferences.Editor editor = classesPrefsList.edit();
        numprefs = classesPrefsList.getAll().values().size();
        ArrayList subjectList = new ArrayList();
        subjectList.add("Assignment Class?");
        for (int go = 0; go < numprefs+5; go++){
            //asdf = "hello" + go;
            classElement = classesPrefsList.getString("ClassList" + (go), "");
            if (classElement.equals("")) {}
            //from most to least recent
            else subjectList.add(1, classElement);
            editor.commit();
        }
        subjectList.add("Create New+");
        subjectList.add("--Remove All--");

        return subjectList;
    }

}


