package com.skylan.homeworkpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by danielkawalsky on 12/14/15.
 */

public class AssignmentAlertDialog extends DialogFragment {

    private AlertDialog.Builder build;
    private AlertDialog.Builder buildCal;
    private AlertDialog.Builder buildSubject;
    private View alertview;

    // All the specs of a new AssignmentInfo
    public int assID;
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

    public AssignmentAlertDialog(final Context context, final AssignmentManager assManager, AssignmentInfo assInfo, boolean isEditMode) {
        super(context);
        this.setContentView(R.layout.dialog_create_assignment);

        if (isEditMode) {
            this.setTitle(R.string.edit_assignment_dialog_title);
        } else {
            this.setTitle(R.string.assignment_dialog_title_create);
        }

        final EditText inputAssignmentName = (EditText) alertview.findViewById(R.id.dialog_edit_assignment_card_name);
        final Spinner inputSubject = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_subject_spinner);
        final Spinner inputType = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_type_spinner);
        final SeekBar inputDifficulty = (SeekBar) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_seekbar);
        final TextView inputDate = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_date_textView);
        final TextView showDifficulty = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_show_textView);

        final SimpleDateFormat df = new SimpleDateFormat("EEEE, LLLL dd");

        inputAssignmentName.setText(assInfo.assignmentTitle);
        showDifficulty.setText(assInfo.assignmentDifficulty+"");
        inputDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                showDifficulty.setText(seekBar.getProgress()+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        inputDate.setText(assInfo.assignmentDueDateText);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
                buildCal = new AlertDialog.Builder(context);
                LayoutInflater inflaterCal = getLayoutInflater();
                View alertviewCal = inflaterCal.inflate(R.layout.calendar_view, null);
                final CalendarView calView = (CalendarView) alertviewCal.findViewById(R.id.calendarView);
                final Calendar cal = Calendar.getInstance();
                calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        assDueDateText = df.format(calView.getDate());
                    }
                });
                buildCal.setView(alertviewCal);
                buildCal.setTitle("Set due date");
                buildCal.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        inputDate.setText(assDueDateText);
                        int dateNow = (int) ( cal.getTimeInMillis() / 1000 / 60 / 60 / 24);
                        int dateThen = (int) ( calView.getDate() / 1000 / 60 / 60 / 24 );
                        assDueDateMSec = calView.getDate();
                        assDays = dateThen - dateNow;
                        dialog.cancel();
                    }
                });
                buildCal.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = buildCal.create();
                alert.show();
            }
        });
        final ArrayAdapter<CharSequence> subjectAdapter = new ArrayAdapter<CharSequence>(context,
                R.layout.new_scroller, AssignmentDisplayFragment.subjectStringList);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                context, R.array.homework_array, R.layout.new_scroller);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputSubject.setEnabled(false);
        inputSubject.setClickable(false);
        inputSubject.setFocusable(false);
        inputSubject.setFocusableInTouchMode(false);inputType.setAdapter(typeAdapter);
        inputSubject.setAdapter(subjectAdapter);
        inputSubject.setSelection(AssignmentDisplayFragment.subjectStringList.indexOf(assInfo.assignmentClassSubject));
        inputType.setSelection(assInfo.assignmentType);

        this.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                adFragment.deselectAll();
                adFragment.recList.getAdapter().notifyDataSetChanged();
            }
        });

        
        build.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                assID = assManager.getUniqueID();
                assTitle = inputAssignmentName.getText().toString();
                assSubject = inputSubject.getSelectedItem().toString();
                assDifficulty = inputDifficulty.getProgress();
                assType = inputType.getSelectedItemPosition();
                assRating = AssignmentInputActivity
                        .giveMeARating(assGrade, assDays, assDifficulty, assType);

                if (assignmentCanBeEntered(inputAssignmentName, inputSubject, inputType)) {
                    assGrade = SubjectInfo.findById(SubjectInfo.class,
                            SubjectInfo.listAll(SubjectInfo.class)
                                    .get(inputSubject
                                            .getSelectedItemPosition() - 1)
                                    .getId()).subjectGrade;
                    dbHelper.addData(DataManager.ASSIGNMENT_TABLE,
                            assTitle,
                            assGrade,
                            assSubject,
                            assDays,
                            assDifficulty,
                            assType,
                            assDate,
                            assDueDateText,
                            assDueDateMSec,
                            assRating,
                            AssignmentInputActivity.booleanToInt(false),
                            AssignmentInputActivity.booleanToInt(false),
                            AssignmentInputActivity.booleanToInt(false));
                    //sa.notifyDataSetChanged();
                    assAdapter.notifyDataSetChanged();
                    assignmentList.add(
                            new AssignmentInfo(assID,
                                    assTitle, assGrade, assSubject, assDays, assDifficulty, assType, assDate,
                                    assDueDateText, assDueDateMSec, assRating,
                                    AssignmentInputActivity.booleanToInt(false),
                                    AssignmentInputActivity.booleanToInt(false),
                                    AssignmentInputActivity.booleanToInt(false)));
                    assignmentList.remove(position);
                    dbHelper.removeData(DataManager.ASSIGNMENT_TABLE, tempAI.assignmentTitle,
                            tempAI.assignmentClassGrade, tempAI.assignmentDaysUntilDue,
                            tempAI.assignmentClassSubject, tempAI.assignmentDifficulty,
                            tempAI.assignmentType, tempAI.assignmentDate,
                            tempAI.assignmentDueDateText, tempAI.assignmentDueDateMSec,
                            tempAI.assignmentUrgencyRating, tempAI.assignmentArchived,
                            tempAI.assignmentCompleted, tempAI.assignmentOverdue,
                            tempAI.assignmentID);
                    recList.smoothScrollToPosition(assignmentList.size() - 1);
                }
                deselectAll();
                dialog.cancel();
            }
        });
        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deselectAll();
                dialog.cancel();
            }
        });
        AlertDialog alert = build.create();
        alert.show();
        deselectAll();
        actionMode.finish();
    }
}
