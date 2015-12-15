package com.skylan.homeworkpro;

/**
 * Created by danielkawalsky on 2/28/15.
 */

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class DBRecordsLayer extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 8;

    private static final String TAG = "DBRecordsLayer";
    private static final String DATABASE_NAME = "assignment.db";//To use SQLite Manager, change to petdb.db ; Go to DDMS mode, find in /data/data/[APP_NAME]/databases/

    public static final String ASSIGNMENT_TABLE = "Assignment_Table";

    public static final String ASSIGNMENT_NUM = "Assignment_Number_id";
    public static final String ASSIGNMENT_NAME = "Assignment_Name";
    public static final String ASSIGNMENT_CLASS_GRADE = "Assignment_Class_Grade";
    public static final String ASSIGNMENT_CLASS_SUBJECT = "Assignment_Class_Subject";
    public static final String ASSIGNMENT_DAYS_UNTIL_DUE = "Assignment_Days_Until_Due";
    public static final String ASSIGNMENT_DIFFICULTY = "Assignment_Difficulty";
    public static final String ASSIGNMENT_TYPE = "Assignment_Type";
    public static final String ASSIGNMENT_URGENCY_RATING = "Assignment_Urgency_Rating";
    public static final String ASSIGNMENT_ENTRY_DATE = "Assignment_Entry_Date";
    public static final String ASSIGNMENT_DUE_DATE_TEXT = "Assignment_Due_Date_Text";
    public static final String ASSIGNMENT_DUE_DATE_MSEC = "Assignment_Due_Date_Ms";


    public static final String[] COLUMN_TITLES = new String[] {ASSIGNMENT_NUM, ASSIGNMENT_NAME,
            ASSIGNMENT_CLASS_GRADE, ASSIGNMENT_DAYS_UNTIL_DUE,
            ASSIGNMENT_DIFFICULTY, ASSIGNMENT_URGENCY_RATING};

    //SQL Syntax Components
    public static final String COMMA_SEP = ", ";
    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_INT = " INTEGER";
    public static final String TYPE_REAL = " REAL";
    public static final String TYPE_DATE= " DATETIME";

    private static final String SQL_CREATE_ENTRIES =
            //"CREATE DATABASE " + DATABASE_NAME +
            " CREATE TABLE " 			+ ASSIGNMENT_TABLE 	+ " (" +
                    ASSIGNMENT_NUM 				+ TYPE_INT 		+ " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ASSIGNMENT_NAME 			+ TYPE_TEXT 	+ COMMA_SEP +
                    ASSIGNMENT_CLASS_GRADE 		+ TYPE_INT 		+ COMMA_SEP +
                    ASSIGNMENT_CLASS_SUBJECT 	+ TYPE_TEXT 	+ COMMA_SEP +
                    ASSIGNMENT_DAYS_UNTIL_DUE 	+ TYPE_INT		+ COMMA_SEP +
                    ASSIGNMENT_DIFFICULTY 		+ TYPE_INT		+ COMMA_SEP +
                    ASSIGNMENT_TYPE 			+ TYPE_INT		+ COMMA_SEP +
                    ASSIGNMENT_ENTRY_DATE		+ TYPE_INT		+ COMMA_SEP +
                    ASSIGNMENT_DUE_DATE_TEXT	+ TYPE_TEXT		+ COMMA_SEP +
                    ASSIGNMENT_DUE_DATE_MSEC	+ TYPE_REAL		+ COMMA_SEP +
                    ASSIGNMENT_URGENCY_RATING 	+ TYPE_REAL 	+
                    ");";

    public DBRecordsLayer(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // TODO Auto-generated constructor stub
    }

    // **** http://www.tutorialspoint.com/sqlite/index.htm
    // Important website because SQLite syntax is different from normal
    // SQL syntax, not just because of hot girl on home page.

    @Override
    public void onCreate(SQLiteDatabase db) {
        //getWritableDatabase(); - REM'ed because recursive error, no longer protests after this.
        createTables(db);
        //System.out.println(SQL_CREATE_ENTRIES);
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If you need to add a column
        if (newVersion > oldVersion) {
            // db.execSQL("ALTER TABLE " + ASSIGNMENT_TABLE + " ADD COLUMN "+ ASSIGNMENT_DUE_DATE_MSEC  + TYPE_REAL);
        }
    }

    //WHAT IS CONTAINED BELOW:
    //method returns a Cursor object which contains methods to return strings, ints, whatnot from databases
    //Direction: make cursor return string lists from database, each string array is stored as part of
    //inputted objects that are stored in assList ArrayList
	/*
	 * public Cursor getAssignmentNames(SQLiteDatabase db) { return
	 * db.query(false, ASSIGNMENT_TABLE, COLUMN_TITLES, " WHERE " +
	 * ASSIGNMENT_NAME + " ", null, null, null,
	 * " ORDER BY "+ASSIGNMENT_URGENCY_RATING, null); }
	 *
	 * public Cursor getAssignmentClassGrades(SQLiteDatabase db) { return
	 * db.query(false, ASSIGNMENT_TABLE, COLUMN_TITLES, " WHERE " +
	 * ASSIGNMENT_CLASS_GRADE + " ", null, null, null,
	 * " ORDER BY "+ASSIGNMENT_URGENCY_RATING, null); }
	 *
	 * public Cursor getAssignmentDays(SQLiteDatabase db) { return
	 * db.query(false, ASSIGNMENT_TABLE, COLUMN_TITLES, " WHERE " +
	 * ASSIGNMENT_DAYS_UNTIL_DUE + " ", null, null, null,
	 * " ORDER BY "+ASSIGNMENT_URGENCY_RATING, null); }
	 *
	 * public Cursor getAssignmentDifficulties(SQLiteDatabase db) { return
	 * db.query(false, ASSIGNMENT_TABLE, COLUMN_TITLES, " WHERE " +
	 * ASSIGNMENT_DIFFICULTY + " ", null, null, null,
	 * " ORDER BY "+ASSIGNMENT_URGENCY_RATING, null); }
	 *
	 * public Cursor getAssignmentRatings(SQLiteDatabase db) { return
	 * db.query(false, ASSIGNMENT_TABLE, COLUMN_TITLES, " WHERE " +
	 * ASSIGNMENT_URGENCY_RATING + " ", null, null, null,
	 * " ORDER BY "+ASSIGNMENT_URGENCY_RATING, null); }
	 *
	 * public boolean cursorsAreAfterLast (Cursor a, Cursor b, Cursor c, Cursor
	 * d, Cursor e) { boolean verdict = (a.isAfterLast() & b.isAfterLast() &
	 * c.isAfterLast() & d.isAfterLast() & e.isAfterLast()); return verdict; }
	 *
	 * public ArrayList<Inputted> getAssignmentInfoArrayList (SQLiteDatabase db) {
	 * Cursor names = getAssignmentNames(db); Cursor classGrades =
	 * getAssignmentClassGrades(db); Cursor days = getAssignmentDays(db); Cursor
	 * difficulties = getAssignmentDifficulties(db); Cursor ratings =
	 * getAssignmentRatings(db);
	 *
	 * ArrayList<Inputted> assList = new ArrayList<Inputted>();
	 * names.moveToFirst(); classGrades.moveToFirst(); days.moveToFirst();
	 * difficulties.moveToFirst(); ratings.moveToFirst();
	 *
	 * while (!cursorsAreAfterLast(names, classGrades, days, difficulties,
	 * ratings) ) { int go = 0; assList.add(new Inputted(names.getString(go),
	 * classGrades.getInt(go), days.getInt(go), difficulties.getInt(go),
	 * ratings.getDouble(go))); names.moveToNext(); classGrades.moveToNext();
	 * days.moveToNext(); difficulties.moveToNext(); ratings.moveToNext();
	 *
	 * go++; } return assList; //must return assList }
	 */

    public void addData(String tableName, String atitle, int grade, String subject, int days,
                        int difficulty, int type, int date, String dueDateText, double dueDateMSec, double rating){
        getWritableDatabase().execSQL("INSERT INTO " + ASSIGNMENT_TABLE + " ( "
                        + ASSIGNMENT_NAME + "," + ASSIGNMENT_CLASS_GRADE + "," + ASSIGNMENT_CLASS_SUBJECT + "," + ASSIGNMENT_DAYS_UNTIL_DUE
                        + "," + ASSIGNMENT_DIFFICULTY + "," + ASSIGNMENT_TYPE + "," + ASSIGNMENT_ENTRY_DATE + "," + ASSIGNMENT_DUE_DATE_TEXT
                        + "," + ASSIGNMENT_DUE_DATE_MSEC + "," + ASSIGNMENT_URGENCY_RATING + ")"
                        + " VALUES ("
                        + "'" + atitle 		+ "',"
                        + grade + ","
                        + "'" + subject 	+ "',"
                        + days + ","
                        + difficulty + ","
                        + type + ","
                        + date + ","
                        + "'" + dueDateText + "',"
                        + dueDateMSec + ","
                        + rating + ");"
        );
    }

    public void removeData(String tableName, String atitle, int grade, int days, String subject, int difficulty, int type, int date, String dueDateText, double dueDateMSec, double rating, int idnum) {
        getWritableDatabase().execSQL("DELETE FROM " + ASSIGNMENT_TABLE + " WHERE " + ASSIGNMENT_NUM + " = " +  idnum + ";");
    }

    public void removeAllData() {
        //http://www.tutorialspoint.com/sqlite/sqlite_delete_query.htm
    }


    private void createTables(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);

		/*
		String q = "";

		q = "CREATE TABLE Crew_Records ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Code TEXT, "
				+ "DayEndFixedTime TEXT, " + "DayEndFrom TEXT, "
				+ "DayEndTo TEXT, " + "DayStartFrom TEXT, "
				+ "DayStartTo TEXT, " + "Name TEXT" + ");";
		db.execSQL(q);
		 */
    }

}
