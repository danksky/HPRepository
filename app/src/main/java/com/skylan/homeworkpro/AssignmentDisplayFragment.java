package com.skylan.homeworkpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AssignmentDisplayFragment extends BaseFragment implements ActionMode.Callback {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "parameterAssignmentList";
    public static final String ARG_PARAM2 = "parameterDisplayArchivedMode";
    public DataManager dbHelper;
    public AssignmentManager assManager;
    //private OnFragmentInteractionListener mListener;
    public OnSubjectCheckedListener mCallBack;

    public static ArrayList<AssignmentInfo> assignmentList;
    public ArrayList<SubjectInfo> subjectList;
    public static ArrayList subjectStringList;
    public AssignmentAdapter assAdapter;
    public static FloatingActionButton fabCreateAssignment;
    public MainNavigationActivity mnActivity;
    public NewAdapter mnAdapter;
    public ArrayList<ArrayList<SubjectInfo>> mnChildItem;
    private MenuItem mEditItem;
    private Menu mActionMenu;
    public RecyclerView recList;
    private AlertDialog.Builder build;
    private AlertDialog.Builder buildCal;
    private AlertDialog.Builder buildSubject;
    private MultiSelector mMultiSelector = new MultiSelector();
    public ActionMode actionMode;
    private AssignmentInfo tempAI;
    private int position;
    public static boolean displayArchivedMode;

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

    public ActionMode.Callback mEditOrDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            super.onDestroyActionMode(actionMode);
            /*for (int go = 0; go < mHolders.size(); go++) {
                mHolders.get(go).bindSubject(mHolders.get(go).sInfo);
            }*/
            deselectAll();
            assAdapter.notifyDataSetChanged();
            fabCreateAssignment.show();

            /*AssignmentAlertDialog dog = new AssignmentAlertDialog(getActivity(), , assignmentList.get(0));
            dog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });*/
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.menu_subject_manager, menu);
            mEditItem = actionMode.getMenu().findItem(R.id.action_edit);
            mActionMenu = actionMode.getMenu();
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_select_all:
                    for (int i = 0; i < assAdapter.getItemCount(); i++) {
                        mMultiSelector.setSelected(i, 0, true); }
                    return true;
                case R.id.action_share:
                    // Crashes.
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getSharedAssignmentText(mMultiSelector.getSelectedPositions()));
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);
                    deselectAll();
                    actionMode.finish();
                    assAdapter.notifyDataSetChanged();
                    return true;

                case R.id.action_archive:
                    List<Integer> positions = mMultiSelector.getSelectedPositions();
                    dbHelper = new DataManager(getActivity().getBaseContext());
                    for (int go = positions.size()-1; go >= 0; go--) {
                        position = positions.get(go);
                        tempAI = assignmentList.get(positions.get(go));
                        dbHelper.setArchived(DataManager.ASSIGNMENT_TABLE,
                                tempAI.assignmentTitle,
                                tempAI.assignmentClassSubject,
                                true);
                        assignmentList.remove(position);
                    }
                    assAdapter.notifyDataSetChanged();
                    deselectAll();
                    actionMode.finish();
                    return true;
                case R.id.action_edit:

                    final int position = mMultiSelector.getSelectedPositions().get(0);
                    tempAI = assignmentList.get(position);
                    String alreadyName = tempAI.assignmentTitle;
                    build = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater1 = getActivity().getLayoutInflater();
                    View alertview = inflater1.inflate(R.layout.dialog_create_assignment, null);

                    // Pass null as the parent view because its going in the dialog layout
                    build.setView(alertview);
                    final EditText inputAssignmentName = (EditText) alertview.findViewById(R.id.dialog_edit_assignment_card_name);
                    final Spinner inputSubject = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_subject_spinner);
                    final Spinner inputType = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_type_spinner);
                    final SeekBar inputDifficulty = (SeekBar) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_seekbar);
                    final TextView inputDate = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_date_textView);
                    final TextView showDifficulty = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_show_textView);

                    final SimpleDateFormat df = new SimpleDateFormat("EEEE, LLLL dd");

                    inputAssignmentName.setText(tempAI.assignmentTitle);
                    showDifficulty.setText(tempAI.assignmentDifficulty+"");
                    inputDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            showDifficulty.setText(seekBar.getProgress()+"");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            InputMethodManager imm = (InputMethodManager)
                                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    inputDate.setText(tempAI.assignmentDueDateText);
                    inputDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InputMethodManager imm = (InputMethodManager)
                                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
                            buildCal = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflaterCal = getActivity().getLayoutInflater();
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
                                    deselectAll();
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = buildCal.create();
                            alert.show();
                        }
                    });
                    final ArrayAdapter<CharSequence> subjectAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.new_scroller, subjectStringList);
                    ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                            getActivity(), R.array.homework_array, R.layout.new_scroller);
                    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    inputSubject.setEnabled(false);
                    inputSubject.setClickable(false);
                    inputSubject.setFocusable(false);
                    inputSubject.setFocusableInTouchMode(false);inputType.setAdapter(typeAdapter);
                    inputSubject.setAdapter(subjectAdapter);
                    inputSubject.setSelection(subjectStringList.indexOf(tempAI.assignmentClassSubject));
                    inputType.setSelection(tempAI.assignmentType);

                    build.setTitle(R.string.edit_subject_dialog_title);
                    build.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            deselectAll();
                            recList.getAdapter().notifyDataSetChanged();
                        }
                    });
                    build.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            assID = getUniqueID(assignmentList);
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
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    public interface OnSubjectCheckedListener{
        public void onSubjectCheck(SubjectInfo si, int groupPos, int childPos);
    }

    public int getUniqueID(ArrayList<AssignmentInfo> aList) {
        int maxID = 0;
        for (int go = 0; go < aList.size()-1; go++) {
            if (aList.get(go).assignmentID > maxID) {
                maxID = aList.get(go).assignmentID;
            }
        }
        return (maxID+1);
    }

    public void deselectAll() {
        for (int go = mMultiSelector.getSelectedPositions().size()-1; go>=0; go--){
            mMultiSelector.setSelected(mMultiSelector
                    .getSelectedPositions().get(go), 0, false);
        }
    }

    private String getSharedAssignmentText(List<Integer> positions) {
        String shareText;
        if (positions.equals(null)) {
            shareText = "";
        } else {
            shareText = "Assignment and days left:\n";
            for (int go = 0; go < positions.size(); go++) {
                shareText = shareText +
                        assignmentList.get(go).assignmentTitle + " - \t" +
                        assignmentList.get(go).assignmentDaysUntilDue + "\n";
            }
        }
        return shareText;
    }

    public static AssignmentDisplayFragment newInstance(ArrayList<SubjectInfo> subjList, boolean displayArchived) {
        AssignmentDisplayFragment amFragment = new AssignmentDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, subjList);
        args.putBoolean(ARG_PARAM2, displayArchived);
        amFragment.setArguments(args);
        return amFragment;
    }

    public AssignmentDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (OnSubjectCheckedListener) activity;
            mnActivity = (MainNavigationActivity) activity;
            mnAdapter = mnActivity.myAdapter;
            mnChildItem = mnActivity.childItem;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainNavigationActivity... or something?");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subjectList = (ArrayList<SubjectInfo>) getArguments().getSerializable(ARG_PARAM1);
            displayArchivedMode = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new DataManager(this.getActivity().getBaseContext());
        assManager = new AssignmentManager(this.getActivity().getBaseContext());
        assignmentList = assManager.getAppropriateAssignmentList(displayArchivedMode);
        // Because subjectList should be initialized through onCreate
        // subjectList = dbHelper.getSubjectInfoArrayList();
        subjectStringList = dbHelper.getSubjectInfoStringArrayList();
        assAdapter = new AssignmentAdapter();


        //assignmentList.add(DataManager.assignmentDummy);

        View amFragmentView = inflater.inflate(R.layout.fragment_assignment_manager, container, false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList = (RecyclerView) amFragmentView.findViewById(R.id.assignment_card_list);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(llm);
        //final AssignmentAdapter aa = new AssignmentAdapter(assignmentList, dbHelper);
        recList.setAdapter(assAdapter);

        fabCreateAssignment = (FloatingActionButton) amFragmentView.findViewById(R.id.fab_create_assignment);
        fabCreateAssignment.attachToRecyclerView(recList);
        fabCreateAssignment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                build = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater1 = getActivity().getLayoutInflater();
                View alertview = inflater1.inflate(R.layout.dialog_create_assignment, null);

                // Pass null as the parent view because its going in the dialog layout
                build.setView(alertview);
                final EditText inputAssignmentName = (EditText) alertview.findViewById(R.id.dialog_edit_assignment_card_name);
                final Spinner inputSubject = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_subject_spinner);
                final Spinner inputType = (Spinner) alertview.findViewById(R.id.dialog_edit_assignment_type_spinner);
                final SeekBar inputDifficulty = (SeekBar) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_seekbar);
                final TextView inputDate = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_date_textView);
                final TextView showDifficulty = (TextView) alertview.findViewById(R.id.dialog_edit_assignment_difficulty_show_textView);

                final SimpleDateFormat df = new SimpleDateFormat("EEEE, LLLL dd");

                showDifficulty.setText(inputDifficulty.getProgress()+"");
                inputDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        showDifficulty.setText(seekBar.getProgress() + "");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        InputMethodManager imm = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                inputDate.setText(df.format(Calendar.getInstance().getTime()));
                inputDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
                        buildCal = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflaterCal = getActivity().getLayoutInflater();
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
                                deselectAll();
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = buildCal.create();
                        alert.show();
                    }
                });
                if (!subjectStringList.contains("Subject?")){
                    subjectStringList.add(0, "Subject?");
                }
                if (!subjectStringList.contains("Create +")) {
                    subjectStringList.add("Create +");
                }
                final ArrayAdapter<CharSequence> subjectAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.new_scroller, subjectStringList);
                ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                        getActivity(), R.array.homework_array, R.layout.new_scroller);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inputType.setAdapter(typeAdapter);
                inputSubject.setAdapter(subjectAdapter);

                inputSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                InputMethodManager imm = (InputMethodManager)
                                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(inputAssignmentName.getWindowToken(), 0);
                                if (subjectStringList.size()-1 == i) {
                                    final SubjectManagerFragment subjectManagerFragment = new SubjectManagerFragment();
                                    buildSubject = new AlertDialog.Builder(getActivity());
                                    LayoutInflater inflaterSubject = getActivity().getLayoutInflater();
                                    View alertviewSubject = inflaterSubject.inflate(R.layout.create_subject_dialog, null);

                                    // Pass null as the parent view because its going in the dialog layout
                                    buildSubject.setView(alertviewSubject);
                                    final EditText inputSubjectName = (EditText) alertviewSubject.findViewById(R.id.dialog_edit_subject_card_name);
                                    final EditText inputSubjectGrade = (EditText) alertviewSubject.findViewById(R.id.dialog_edit_subject_card_grade);

                                    buildSubject.setTitle(R.string.subject_dialog_title_create);
                                    buildSubject.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            String enteredSubjectName = inputSubjectName.getText().toString();
                                            int enteredSubjectGrade = Integer.parseInt("0" + inputSubjectGrade.getText().toString()); //was getting stupid error from null value going to int?
                                            boolean enteredSubjectIsArchived = false;

                                            if (subjectManagerFragment.subjectCanBeEntered(inputSubjectName, inputSubjectGrade, subjectList, false)) {
                                                SubjectInfo si = new SubjectInfo(enteredSubjectName, "Assignments", enteredSubjectGrade, enteredSubjectIsArchived, true);
                                                si.save();
                                                mnChildItem.get(0).add(si);
                                                subjectStringList.add(subjectStringList.size() - 1, enteredSubjectName);
                                                subjectAdapter.notifyDataSetChanged();
                                                mnAdapter.notifyDataSetChanged();
                                                inputSubject.setSelection(subjectStringList.indexOf(enteredSubjectName), true);
                                                Toast.makeText(getActivity(), "Subject created", Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.cancel();
                                        }
                                    });
                                    buildSubject.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            inputSubject.setSelection(0);
                                            dialog.cancel();
                                        }
                                    });
                                    buildSubject.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            if (inputSubjectName.getText().toString().equals("")) {
                                                inputSubject.setSelection(0);
                                            }
                                        }
                                    });
                                    AlertDialog alert = buildSubject.create();
                                    alert.show();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // Nothing for now.
                            }
                                                       }
                );


                build.setTitle(R.string.assignment_dialog_title_create);
                build.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        deselectAll();
                        recList.getAdapter().notifyDataSetChanged();
                    }
                });
                build.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        assID = getUniqueID(assignmentList);
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
                            assManager.newAssignment(assignmentList, new AssignmentInfo(assID,
                                    assTitle, assGrade, assSubject, assDays, assDifficulty, assType, assDate,
                                    assDueDateText, assDueDateMSec, assRating,
                                    AssignmentInputActivity.booleanToInt(false),
                                    AssignmentInputActivity.booleanToInt(false),
                                    AssignmentInputActivity.booleanToInt(false)));
                            assAdapter.notifyDataSetChanged();
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
            }
        });
        return amFragmentView;
    }

    public void onButtonPressed(Uri uri) {
       
    }

    public void reAddAssignments (String checkName) {
        //Once checkboxes have been activated, these are the protocols to restore assignments to screen

        ArrayList<AssignmentInfo> rawAndUnfilteredList = dbHelper.getAssignmentInfoArrayList();
        // Archived assignment protocol
        if (displayArchivedMode) {
            if (checkName.equals("Archived")) {
                for (int go = 0; go < rawAndUnfilteredList.size(); go++) {
                    if (rawAndUnfilteredList.get(go).assignmentArchived) {
                        assignmentList.add(rawAndUnfilteredList.get(go));
                    }
                }
            }
            if (checkName.equals("Completed")) {
                for (int go = 0; go < rawAndUnfilteredList.size(); go++) {
                    if (rawAndUnfilteredList.get(go).assignmentCompleted) {
                        assignmentList.add(rawAndUnfilteredList.get(go));
                    }
                }
            }
            if (checkName.equals("Overdue")) {
                for (int go = 0; go < rawAndUnfilteredList.size(); go++) {
                    if (rawAndUnfilteredList.get(go).assignmentOverdue) {
                        assignmentList.add(rawAndUnfilteredList.get(go));
                    }
                }
            }
        }

        // Active assignment protocol
        else {
            for (int go = 0; go < rawAndUnfilteredList.size(); go++) {
                if (rawAndUnfilteredList.get(go).assignmentClassSubject.equals(checkName) && !rawAndUnfilteredList.get(go).assignmentArchived) {
                    assignmentList.add(rawAndUnfilteredList.get(go));
                }
            }
        }
        if (!assAdapter.equals(null)) {
            assAdapter.notifyDataSetChanged();
        }
    }

    public boolean assignmentCanBeEntered (EditText inputName, Spinner subjectSpinner, Spinner typeSpinner) {
        boolean enterable = true;
        if ((inputName.getText().toString().equals(""))) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Enter a class name.",
                    Toast.LENGTH_SHORT).show();
        } else if (subjectSpinner.getSelectedItemId()==0) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Set a subject.",
                    Toast.LENGTH_SHORT).show();
        } else if (typeSpinner.getSelectedItemId()==0) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Set a type.",
                    Toast.LENGTH_SHORT).show();
        }
        return enterable;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    private class AssignmentHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        protected CardView vAssignmentCard;
        protected TextView vAssignmentName;
        protected TextView vAssignmentSubject;
        protected TextView vAssignmentType;
        protected TextView vAssignmentDueDate;
        protected RelativeLayout vAssignmentLayout;
        private AssignmentInfo aInfo;

        public AssignmentHolder(View itemView) {
            super(itemView, mMultiSelector);

            //vAssignmentCard =(CardView) itemView.findViewById(R.id.subject_card);
            vAssignmentName = (TextView) itemView.findViewById(R.id.assignment_card_name_textView);
            vAssignmentSubject = (TextView) itemView.findViewById(R.id.assignment_card_subject_textView);
            vAssignmentType = (TextView) itemView.findViewById(R.id.assignment_card_type_textView);
            vAssignmentDueDate = (TextView) itemView.findViewById(R.id.assignment_card_due_textView);
            vAssignmentLayout = (RelativeLayout) itemView.findViewById(R.id.assignment_card_relative_layout);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);

        }
        @Override
        public void setSelectable(boolean isSelectable) {
            super.setSelectable(isSelectable);
            if (isSelectable) {
                vAssignmentLayout.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public void setActivated(boolean isActivated) {
            super.setActivated(isActivated);
        }

        public void bindAssignment(AssignmentInfo ai) {
            aInfo = ai;
            vAssignmentName.setText(aInfo.assignmentTitle);
            vAssignmentSubject.setText(aInfo.assignmentClassSubject);
            vAssignmentType.setText(typeToText(aInfo.assignmentType));
            vAssignmentDueDate.setText(aInfo.assignmentDueDateText);
            /*if (mMultiSelector.getSelectedPositions().size()==0) {
                vAssignmentLayout.setBackgroundColor(
                        Color.parseColor(giveAssignmentHexValue((double) si.subjectGrade)));
            }*/
        }

        @Override
        public void onClick(View v) {

            if (aInfo == null) {
                return;
            }
            if (!mMultiSelector.tapSelection(this)) {
                // This condition is the same as, if not in ActionMode, handle the click normally:
            }
            if (mMultiSelector.tapSelection(this)) {
                mMultiSelector.tapSelection(this);

                switch (mMultiSelector.getSelectedPositions().size()) {
                    case 0:
                        actionMode.finish();
                        break;
                    case 1:
                        mEditItem.setVisible(true);
                        break;
                    case 2:
                        mEditItem.setVisible(false);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            actionMode = getActionBar().startActionMode(mEditOrDeleteMode);
            mMultiSelector.setSelectable(true);
            mMultiSelector.setSelected(this, true);
            fabCreateAssignment.hide();
            return true;
        }

        public String typeToText(int type) {
            switch (type) {
                case 1:
                    return "Exam";
                case 2:
                    return "Essay";
                case 3:
                    return "Test";
                case 4:
                    return "Project";
                case 5:
                    return "Quiz";
                case 6:
                    return "Homework";
                case 7:
                    return "Reading";
                default:
                    return "Unknown type";
            }

        }
    }

    private class AssignmentAdapter extends RecyclerView.Adapter<AssignmentHolder> {
        @Override
        public AssignmentHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.assignment_card_layout, parent, false);
            return new AssignmentHolder(view);
        }

        @Override
        public void onBindViewHolder(AssignmentHolder holder, int pos) {
            AssignmentInfo aInfo = assignmentList.get(pos);
            holder.bindAssignment(aInfo);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Now it works on my Galaxy S6 Active :D
                holder.setSelectionModeStateListAnimator(null);
                holder.setDefaultModeStateListAnimator(null);
            }
        }
        @Override
        public int getItemCount() {
            return assignmentList.size();
        }

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    */
}
