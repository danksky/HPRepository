package com.skylan.homeworkpro;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by danielkawalsky on 12/13/15.
 */
public class AssignmentManager extends DataManager{

    public static ArrayList<AssignmentInfo> completeAssignmentList;
    public static ArrayList<AssignmentInfo> activeAssignmentList;
    public static ArrayList<AssignmentInfo> archivedAssignmentList;
    public static ArrayList<SubjectInfo> subjectList;
    public static ArrayList<String> subjectStringList;

    public AssignmentManager (Context context) { //this constructor allows use of object dbHelper
        super(context);
        completeAssignmentList = getAssignmentInfoArrayList();
        archivedAssignmentList = new ArrayList<AssignmentInfo>();
        subjectList = getSubjectInfoArrayList();
        subjectStringList = getSubjectInfoStringArrayList();
    }

    public ArrayList<AssignmentInfo> getAppropriateAssignmentList (boolean isArchivedMode) {
        if (isArchivedMode) {
            return getArchivedAssignmentList();
        }
        else {
            return getActiveAssignmentList();
        }
    }

    public ArrayList<AssignmentInfo> getActiveAssignmentList () {
        activeAssignmentList = completeAssignmentList;
        // Sorter (according to assignment urgency first)
        AssignmentInfo slot;
        int oneIndexAbove;
        int swaps;
        do {
            swaps = 0;
            for (int go = 0; go < activeAssignmentList.size() - 1; go++) {
            oneIndexAbove = go + 1;
            // If not as urgent, move to further down list.
            // Bubble Sort

                if (activeAssignmentList.get(go).assignmentUrgencyRating < activeAssignmentList.get(oneIndexAbove).assignmentUrgencyRating) {
                    slot = activeAssignmentList.get(go);
                    activeAssignmentList.set(go, activeAssignmentList.get(oneIndexAbove));
                    activeAssignmentList.set(oneIndexAbove, slot);
                    swaps++;
                }
            }

        } while (swaps > 0);
        
        // Filter (removes the archived ones)
        for (int go = activeAssignmentList.size() - 1; go >= 0; go--) {
            if (activeAssignmentList.get(go).assignmentArchived) {
                activeAssignmentList.remove(go);
            }
        }

        // Filter 2 (removes unchecked subjects)
        for (int go = subjectList.size() - 1; go >= 0; go--) {
            if (!subjectList.get(go).subjectChecked) {
                for (int asdf = activeAssignmentList.size() - 1; asdf >= 0; asdf--) {
                    if (activeAssignmentList.get(asdf).assignmentClassSubject.equals(subjectList.get(go).subjectName)) {
                        activeAssignmentList.remove(asdf);
                    }
                }
            }
        }
        return activeAssignmentList;
    }
    
    public ArrayList<AssignmentInfo> getArchivedAssignmentList () {
        ArrayList<String> tempSubjectStringList;
        tempSubjectStringList = subjectStringList;

        // Populates archivedAssignmentList while
        // Sorting alphabetically according to Subject

        // As of right now, archived subjects (because they no longer exist) do not show assignments archived by their archive
        // cannot be added by associated subject name if subject no longer exists
        Collections.sort(tempSubjectStringList);
        for(int go = 0; go < tempSubjectStringList.size(); go++) {
            for (int asdf = 0; asdf < completeAssignmentList.size(); asdf++) {
                if (completeAssignmentList.get(asdf).assignmentClassSubject.equals(tempSubjectStringList.get(go))) {
                    archivedAssignmentList.add(completeAssignmentList.get(asdf));
                }
            }
        }

        // Filter (removes the active [unarchived] ones)

        for (int go = archivedAssignmentList.size() - 1; go >= 0; go--) {
            if (!archivedAssignmentList.get(go).assignmentArchived) {
                archivedAssignmentList.remove(go);
            }
        }

        // Filter 2 (removes completed or overdue if either is unchecked)
        // INCREDIBLY INEFFICIENT: consider sharedpreferences

        for (int go = 0; go < subjectList.size(); go++) {

            // Checks if Completed is unchecked
           if ((subjectList.get(go).subjectName.equals("Completed")) && !(subjectList.get(go).subjectChecked))  {
                for (int bro = archivedAssignmentList.size()-1; bro <= 0; bro--) {
                    if (archivedAssignmentList.get(bro).assignmentCompleted) {
                        archivedAssignmentList.remove(bro);
                    }
                }
            }

            // Checks if Overdue is unchecked
            if ((subjectList.get(go).subjectName.equals("Overdue")) && !(subjectList.get(go).subjectChecked))  {
                for (int bro = archivedAssignmentList.size()-1; bro <= 0; bro--) {
                    if (archivedAssignmentList.get(bro).assignmentOverdue) {
                        archivedAssignmentList.remove(bro);
                    }
                }
            }
        }
        return archivedAssignmentList;
    }

    public static void newAssignment (ArrayList<AssignmentInfo> assList, AssignmentInfo newAss) {
        assList.add(newAss);
        AssignmentInfo slot;
        int oneIndexAbove;
        for (int go = 0; go < assList.size() - 1; go++) {
            oneIndexAbove = go + 1;
            // If not as urgent, move to further down list.
            if (assList.get(go).assignmentUrgencyRating < assList.get(oneIndexAbove).assignmentUrgencyRating) {
                slot = assList.get(go);
                assList.set(go, completeAssignmentList.get(oneIndexAbove));
                assList.set(oneIndexAbove, slot);
            }
        }
        // assList.notify();
    }

    public int getUniqueID() {
        int maxID = 0;
        for (int go = 0; go < completeAssignmentList.size(); go++) {
            if (completeAssignmentList.get(go).assignmentID > maxID) {
                maxID = completeAssignmentList.get(go).assignmentID;
            }
        }
        return (maxID+1);
    }
}
