package com.skylan.homeworkpro;

import com.orm.SugarRecord;

/**
 * Created by danielkawalsky on 3/6/15.
 */
public class SubjectInfo extends SugarRecord<SubjectInfo> {

    public String subjectName;
    public String itemHeaderTitle;
    public int subjectGrade;
    public boolean subjectArchived;
    public boolean subjectChecked;

    // still deciding on whether subjects can be archived or not... if they can then all assignments
    // should be archived with them (and can be restored the same way an assignment can.)
    // otherwise, deleting a subject would mean (deleting/archiving all assignments) > (user option)


    // MAJOR:
    // SubjectInfo now includes Archived checklist items with headerTitle value = "Archived"
    // These are: Archived, Completed, Overdue
    // Must be filtered out by SubjectAdapter or SubjectManagerFragments I'd assume

    public SubjectInfo () {}

    public SubjectInfo (String name, String headerTitle, int grade, boolean isArchived, boolean isChecked) {
        subjectName = name;
        itemHeaderTitle = headerTitle;
        subjectGrade = grade;
        subjectArchived = isArchived;
        subjectChecked = isChecked;
    }

}
