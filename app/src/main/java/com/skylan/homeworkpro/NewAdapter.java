package com.skylan.homeworkpro;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;


@SuppressWarnings("unchecked")
public class NewAdapter extends BaseExpandableListAdapter {
	// keep in mind that an adapter is useful for turning your raw object into a view!!
	// I'm trying a new approach and just passing in SubjectInfo projects, which makes more sense
	// for what an adapter is meant to do.
    //
    // That worked out well ^^

	public ArrayList<String> groupItem;
	public ArrayList<SubjectInfo> tempChild;
	public ArrayList<ArrayList<SubjectInfo>> childItemList =
			new ArrayList<ArrayList<SubjectInfo>>();
	//childItemList is an ArrayList of ArrayLists!!
	public LayoutInflater minflater;
	public Activity activity;
	private final Context context;

	public NewAdapter(Context context,ArrayList<String> grList,
					  ArrayList<ArrayList<SubjectInfo>> childItemList) {
		this.context = context;
		groupItem = grList;
		this.childItemList = childItemList;
		//and each child is an ArrayList
	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.minflater = mInflater;
		activity = act;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childItemList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

    @Override
    public View getChildView(int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent)
    {
		//child is a list! so is tempChild... etc
        tempChild = (ArrayList<SubjectInfo>) childItemList.get(groupPosition);
        TextView text = null;
		CheckBox cBox = null;

		if (convertView == null)
        {   LayoutInflater layoutInflater = (LayoutInflater)context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_submenu_item,parent,false);
        }
        text = (TextView) convertView.findViewById(R.id.submenu_textView);
		cBox = (CheckBox) convertView.findViewById(R.id.show_child_subject_checkBox);
        text.setText(tempChild.get(childPosition).subjectName);
		cBox.setChecked(tempChild.get(childPosition).subjectChecked);
		cBox.setTag(R.string.group_position_key, groupPosition);
		cBox.setTag(R.string.si_array_index_key, tempChild.get(childPosition).getId());
		cBox.setTag(R.string.child_position_key, childPosition);
		//to give the index for ArrayList and SugarRecord DB update data
		//cBox.setOnCheckedChangeListener(myCheckChangedListener);
        convertView.setTag(tempChild.get(childPosition));
        return convertView;
    }


	@Override
	public int getChildrenCount(int groupPosition) {
		return childItemList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return childItemList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater layoutInflater =
					(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_group_item,parent,false);
        }
        ((TextView) convertView).setText(groupItem.get(groupPosition));
		//Header Title
        convertView.setTag(groupItem.get(groupPosition));
        return convertView;
    }

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true; //was false, and was why toast wasn't presenting. Now that True, it does.
	}

}
