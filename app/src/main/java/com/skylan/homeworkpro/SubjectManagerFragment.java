package com.skylan.homeworkpro;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SubjectManagerFragment extends BaseFragment implements ActionMode.Callback {
    // communicate with the Activity that manages this fragment
    // http://developer.android.com/guide/components/fragments.html#CommunicatingWithActivity

    public static ArrayList<SubjectInfo> subjectList = new ArrayList<SubjectInfo>();
    public static FloatingActionButton fabCreateSubject;
    private AlertDialog.Builder build;
    private MultiSelector mMultiSelector = new MultiSelector();

    public ActionMode actionMode;
    public RecyclerView recList;
    public SubjectInfo subject;
    public SubjectInfo tempSI;
    public ArrayList<SubjectHolder> mHolders;
    private MenuItem mEditItem;
    private Menu mActionMenu;
    public SubjectAdapter sAdapter;
    public MainNavigationActivity mnActivity;
    public NewAdapter mnAdapter;
    public ArrayList<ArrayList<SubjectInfo>> mnChildItem;
    public DataManager dbHelper;

    // inspiration from:
    // https://github.com/bignerdranch/recyclerview-multiselect#modal-multiselection-with-long-click

    public ActionMode.Callback mEditOrDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            super.onDestroyActionMode(actionMode);
            /*for (int go = 0; go < mHolders.size(); go++) {
                mHolders.get(go).bindSubject(mHolders.get(go).sInfo);
            }*/
            deselectAll();
            sAdapter.notifyDataSetChanged();
            fabCreateSubject.show();
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
                    for (int i = 0; i < sAdapter.getItemCount(); i++) {
                        mMultiSelector.setSelected(i, 0, true);
                    }
                    return true;
                case R.id.action_share:
                    // Does not crash.
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getSharedSubjectText(mMultiSelector.getSelectedPositions()));
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);
                    deselectAll();
                    actionMode.finish();
                    sAdapter.notifyDataSetChanged();
                    return true;

                case R.id.action_archive:
                    List<Integer> positions = mMultiSelector.getSelectedPositions();
                    dbHelper = new DataManager(getActivity().getBaseContext());
                    for (int go = positions.size()-1; go >= 0; go--) {
                        tempSI = SubjectInfo.findById(SubjectInfo.class, subjectList.get(positions.get(go)).getId());
                        tempSI.subjectArchived = true;
                        tempSI.save();
                        subjectList.remove((int) positions.get(go));
                        mHolders.remove((int) positions.get(go));
                        mnChildItem.get(0).remove((int) positions.get(go));
                        dbHelper.setArchived(DataManager.ASSIGNMENT_TABLE, tempSI.subjectName, tempSI.subjectArchived);
                    }
                    deselectAll();
                    actionMode.finish();
                    return true;
                case R.id.action_edit:
                    final int position = mMultiSelector.getSelectedPositions().get(0);
                    String alreadyName = subjectList.get(position).subjectName;
                    int alreadyGrade = subjectList.get(position).subjectGrade;
                    build = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater1 = getActivity().getLayoutInflater();
                    View alertview = inflater1.inflate(R.layout.create_subject_dialog, null);

                    // Pass null as the parent view because its going in the dialog layout
                    build.setView(alertview);
                    final EditText inputSubjectName = (EditText) alertview.findViewById(R.id.dialog_edit_subject_card_name);
                    final EditText inputSubjectGrade = (EditText) alertview.findViewById(R.id.dialog_edit_subject_card_grade);
                    inputSubjectName.setText(alreadyName);
                    inputSubjectName.setEnabled(false);
                    inputSubjectName.setFocusable(false);
                    inputSubjectGrade.setText("" + alreadyGrade);

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
                            String enteredSubjectName = inputSubjectName.getText().toString();
                            int enteredSubjectGrade = Integer.parseInt("0" + inputSubjectGrade.getText().toString()); //was getting stupid error from null value going to int?
                            boolean enteredSubjectIsArchived = false;

                            if (subjectCanBeEntered(inputSubjectName, inputSubjectGrade, subjectList, true)) {
                                tempSI = SubjectInfo.findById(SubjectInfo.class,
                                        subjectList.get(position).getId());
                                tempSI.subjectName = enteredSubjectName;
                                tempSI.subjectGrade = enteredSubjectGrade;
                                tempSI.subjectArchived = enteredSubjectIsArchived;
                                tempSI.save();
                                subjectList.set(position, tempSI);
                                mnChildItem.get(0).set(position, tempSI);
                                dbHelper = new DataManager(getActivity().getBaseContext());
                                dbHelper.changeSubjectGrade(DataManager.ASSIGNMENT_TABLE,
                                        tempSI.subjectName, tempSI.subjectGrade);
                                //sa.notifyDataSetChanged();
                                recList.smoothScrollToPosition(position);
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

    public void deselectAll() {
        for (int go = mMultiSelector.getSelectedPositions().size()-1; go>=0; go--){
            mMultiSelector.setSelected(mMultiSelector
                    .getSelectedPositions().get(go), 0, false);
        }
    }

    private String getSharedSubjectText(List<Integer> positions) {
        String shareText = "";
        if (positions.equals(null)) {
            shareText = "";
        } else {
            for (int go = 0; go < positions.size(); go++) {
                shareText = shareText +
                            subjectList.get(go).subjectName + "\t" +
                            subjectList.get(go).subjectGrade + "\n";
            }
        }
        return shareText;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // I can use these arguments as string[] arrays to display whichever classes checked
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types and number of parameters
    public static SubjectManagerFragment newInstance(String param1, String param2) {
        SubjectManagerFragment fragment = new SubjectManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SubjectManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mnActivity = (MainNavigationActivity) activity;
            mnAdapter = mnActivity.myAdapter;
            mnChildItem = mnActivity.childItem;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainNavigationActivity... or something?");
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        deselectAll();
        sAdapter.notifyDataSetChanged();
    }


    //non graphical initialization
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setRetainInstance(true);

    }

    //graphical initialization
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View smFragmentView = inflater.inflate(R.layout.fragment_subject_manager, container, false);
        subjectList = getSubjectInfoArrayList();
        mHolders = new ArrayList<SubjectHolder>();
        recList = (RecyclerView) smFragmentView.findViewById(R.id.subject_card_list);
        recList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);
        sAdapter = new SubjectAdapter();
        recList.setAdapter(sAdapter);

        fabCreateSubject = (FloatingActionButton) smFragmentView.findViewById(R.id.fab_create_subject);
        fabCreateSubject.attachToRecyclerView(recList);
        fabCreateSubject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                build = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater1 = getActivity().getLayoutInflater();
                View alertview = inflater1.inflate(R.layout.create_subject_dialog, null);

                // Pass null as the parent view because its going in the dialog layout
                build.setView(alertview);
                final EditText inputSubjectName = (EditText) alertview.findViewById(R.id.dialog_edit_subject_card_name);
                final EditText inputSubjectGrade = (EditText) alertview.findViewById(R.id.dialog_edit_subject_card_grade);

                build.setTitle(R.string.subject_dialog_title_create);
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
                        String enteredSubjectName = inputSubjectName.getText().toString();
                        int enteredSubjectGrade = Integer.parseInt("0" + inputSubjectGrade.getText().toString()); //was getting stupid error from null value going to int?
                        boolean enteredSubjectIsArchived = false;

                        if (subjectCanBeEntered(inputSubjectName, inputSubjectGrade, subjectList, false)) {
                            SubjectInfo si = new SubjectInfo(enteredSubjectName, "Assignments", enteredSubjectGrade, enteredSubjectIsArchived, true);
                            si.save();
                            subjectList.add(si);
                            mnChildItem.get(0).add(si);
                            //sa.notifyDataSetChanged();
                            recList.smoothScrollToPosition(subjectList.size() - 1);
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
        // Inflate the layout for this fragment

        return smFragmentView;

    }

    public ArrayList<SubjectInfo> getSubjectInfoArrayList(){
        ArrayList<SubjectInfo> sial= new ArrayList<SubjectInfo>();
        List<SubjectInfo> sil = SubjectInfo.listAll(SubjectInfo.class);
        sial.addAll(sil);
        for (int go = sial.size()-1; go >= 0; go--) {
            //iterator moves backwards because go skips due to .remove() (shift down all indices)
            if (sial.get(go).subjectArchived || sial.get(go).itemHeaderTitle.equals("Archived")) {
                sial.remove(go);
            }
        }
        return sial;
    }

    public boolean subjectCanBeEntered (EditText inputName, EditText inputGrade, ArrayList<SubjectInfo> aList, boolean isEditMode) {
        boolean enterable = true;
        if ((inputName.getText().toString().equals("")) && (inputGrade.getText().toString().equals(""))) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Enter class name and grade.",
                    Toast.LENGTH_SHORT).show();
        } else if ((inputName.getText().toString().equals(""))) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Enter a class name.",
                    Toast.LENGTH_SHORT).show();
        } else if ((inputGrade.getText().toString().equals(""))) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Enter a grade.",
                    Toast.LENGTH_SHORT).show();
        }
        if (Integer.parseInt(inputGrade.getText().toString())>110) {
            enterable = false;
            Toast.makeText(
                    getActivity().getApplicationContext(), "Entered grade is above 110.",
                    Toast.LENGTH_SHORT).show();
        }
        if (!isEditMode) {
            for (int go = 0; go < aList.size(); go++) {
                if (inputName.getText().toString().equals(aList.get(go).subjectName)) {
                    enterable = false;
                    Toast.makeText(
                            getActivity().getApplicationContext(), "You've already saved a class with that name.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        return enterable;
    }

    private class SubjectHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        protected CardView vSubjectCard;
        protected TextView vSubjectName;
        protected TextView vSubjectGrade;
        protected RelativeLayout vSubjectLayout;
        private SubjectInfo sInfo;

        public SubjectHolder(View itemView) {
            super(itemView, mMultiSelector);

            //vSubjectCard =(CardView) itemView.findViewById(R.id.subject_card);
            vSubjectName = (TextView) itemView.findViewById(R.id.subject_card_name_textView);
            vSubjectGrade = (TextView) itemView.findViewById(R.id.subject_card_grade_textView);
            vSubjectLayout = (RelativeLayout) itemView.findViewById(R.id.subject_card_relative_layout);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);

        }
        @Override
        public void setSelectable(boolean isSelectable) {
            super.setSelectable(isSelectable);
            if (isSelectable) {
                vSubjectLayout.setBackgroundColor(Color.WHITE);
            }
        }
        @Override
        public void setActivated(boolean isActivated) {
            super.setActivated(isActivated);
        }

        public void bindSubject(SubjectInfo si) {
            sInfo = si;
            vSubjectName.setText(sInfo.subjectName);
            vSubjectGrade.setText(Integer.toString(si.subjectGrade));
            if (mMultiSelector.getSelectedPositions().size()==0) {
                vSubjectLayout.setBackgroundColor(
                        Color.parseColor(giveSubjectHexValue((double) si.subjectGrade)));
            }
        }

        @Override
        public void onClick(View v) {

            if (sInfo == null) {
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
            fabCreateSubject.hide();
            return true;
        }
    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {
        @Override
        public SubjectHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_card_layout, parent, false);
            return new SubjectHolder(view);
        }

        @Override
        public void onBindViewHolder(SubjectHolder holder, int pos) {
            SubjectInfo sInfo = subjectList.get(pos);
            holder.bindSubject(sInfo);
            mHolders.add(holder);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Now it works on my Galaxy S6 Active :D
                holder.setSelectionModeStateListAnimator(null);
                holder.setDefaultModeStateListAnimator(null);
            }
        }
        @Override
        public int getItemCount() {
            return subjectList.size();
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
