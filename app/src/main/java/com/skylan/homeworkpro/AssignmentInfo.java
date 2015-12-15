package com.skylan.homeworkpro;

import com.orm.SugarRecord;

/**
 * Created by danielkawalsky on 3/5/15.
 */
public class AssignmentInfo {
    //object attributes (below)
    public  int			assignmentID;
    public  String 		assignmentTitle;
    public  int 		assignmentClassGrade;
    public  String 		assignmentClassSubject;
    public  int 		assignmentDaysUntilDue;
    public  int 		assignmentDifficulty;
    public  int			assignmentType;
    public  int 		assignmentDate;
    public 	String		assignmentDueDateText;
    public  double		assignmentDueDateMSec;
    public  double 		assignmentUrgencyRating;
    public  boolean     assignmentArchived;
    public  boolean     assignmentCompleted;
    public  boolean     assignmentOverdue;

    AssignmentInfo () {}

    // ** The AssignmentInfo object uses booleans, but to create an object with booleans,
    // ** ints must be passed in through the SQLite database

    public AssignmentInfo (int id, String atitle, int grade, String subject, int days, int difficulty,
                     int type, int date, String dueDateText, double dueDateMSec, double rating, int archived, int completed, int overdue) {

        assignmentID = id;
        assignmentTitle = atitle;
        assignmentClassGrade = grade;
        assignmentClassSubject = subject;
        assignmentDaysUntilDue = days;
        assignmentDifficulty = difficulty;
        assignmentType = type;
        assignmentDate = date;
        assignmentDueDateText = dueDateText;
        assignmentDueDateMSec = dueDateMSec;
        assignmentUrgencyRating = rating;
        assignmentArchived = (archived == 1);
        assignmentCompleted = (completed == 1);
        assignmentOverdue = (overdue == 1);
    }
}
