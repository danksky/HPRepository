package com.skylan.homeworkpro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielkawalsky on 7/5/15.
 */
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>  {

    // This class binds each AssignmentInfo object to an individual card
    // It's responsible for (currently) longClick deletion, as well.
    // (It also colors each assignment card.)
    private DataManager dbHelper;
    private SQLiteDatabase dataBase;
    private List<AssignmentInfo> assignmentList;

    public AssignmentAdapter(ArrayList<AssignmentInfo> assignmentList, DataManager dbHelper ) {
        this.assignmentList = assignmentList;
        this.dbHelper = dbHelper;
    }

    public int getItemCount() {
        return assignmentList.size();
    }

    public void onBindViewHolder(final AssignmentViewHolder assignmentViewHolder, int i) {
        final AssignmentInfo ai = assignmentList.get(i);
        assignmentViewHolder.vAssignmentName.setText(ai.assignmentTitle);
        assignmentViewHolder.vAssignmentType.setText(""+ai.assignmentType);
        assignmentViewHolder.vAssignmentDue.setText(ai.assignmentDueDateText);
        assignmentViewHolder.vAssignmentSubject.setText(ai.assignmentClassSubject);
        //assignmentViewHolder.vSubjectLayout.setBackgroundColor(Color.parseColor(giveSubjectHexValue((double) si.assignmentGrade)));

        assignmentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(assignmentViewHolder.itemView.getContext());
                builder.setTitle("Delete Assignment"); //needs to become Archive option!!!
                builder.setMessage("Do you want to delete the assignment \"" + ai.assignmentTitle + "\"?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ai.delete();
                        /* Toast.makeText(
                                builder.getContext(), ai.assignmentTitle + " " + "is deleted.", Toast.LENGTH_SHORT).show();
                        dataBase.delete(DataManager.ASSIGNMENT_TABLE, DataManager.ASSIGNMENT_NUM +
                                "=" + ai.assignmentID, null);*/
                        dataBase = dbHelper.getWritableDatabase();
                        dataBase.delete(DataManager.ASSIGNMENT_TABLE, DataManager.ASSIGNMENT_NUM +
                                "=" + ai.assignmentID, null);
                        /*dbHelper.removeData(DataManager.ASSIGNMENT_TABLE, ai.assignmentTitle,
                                ai.assignmentClassGrade, ai.assignmentDaysUntilDue, ai.assignmentClassSubject,
                                ai.assignmentDifficulty, ai.assignmentType, ai.assignmentDate,
                                ai.assignmentDueDateText, ai.assignmentDueDateMSec, ai.assignmentUrgencyRating,
                                ai.assignmentArchived, ai.assignmentCompleted,
                                ai.assignmentOverdue, ai.assignmentID);
                                */
                        //assignmentList = dbHelper.getAssignmentInfoArrayList();
                        assignmentList = dbHelper.getAssignmentInfoArrayList();
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });


    }
    public AssignmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.assignment_card_layout, viewGroup, false);

        return new AssignmentViewHolder(itemView);
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {

        protected TextView vAssignmentName;
        protected TextView vAssignmentType;
        protected TextView vAssignmentDue;
        protected TextView vAssignmentSubject;
        protected RelativeLayout vAssignmentLayout;
        protected CheckBox vAssignmentCheckBox;

        public AssignmentViewHolder (View v) {
            super (v);
            vAssignmentName = (TextView) v.findViewById(R.id.assignment_card_name_textView);
            vAssignmentType = (TextView) v.findViewById(R.id.assignment_card_type_textView);
            vAssignmentDue = (TextView) v.findViewById(R.id.assignment_card_due_textView);
            vAssignmentSubject = (TextView) v.findViewById(R.id.assignment_card_subject_textView);
            vAssignmentLayout = (RelativeLayout) v.findViewById(R.id.assignment_card_relative_layout);
            vAssignmentCheckBox = (CheckBox) v.findViewById(R.id.show_child_subject_checkBox);
        }
    }



    public static String giveSubjectHexValue(double rated) {
        rated = (512*((rated-60)/40));
        int i = (int) rated;
        String part = "00";
        String complete = "";
        if (i <= 0) {
            i = 0;
        }
        if (i >= 512) {
            i = 512;
        }
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


}

