package com.skylan.homeworkpro;

/**
 * Created by danielkawalsky on 2/28/15.
 */
import java.util.ArrayList;

//import homeworkprioritizer.myfirstapp.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AssignmentArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] titleValues;
    private final String[] infoValues;
    private final String[] subjectValues;
    private final String[] assnValues;

    public AssignmentArrayAdapter(Context context, String[] titleValues,
                                  String[] infoValues, String[] assnValues, String[] subjectValues) {
        super(context, R.layout.new_array, titleValues);
        this.context = context;
        this.titleValues = titleValues;
        this.infoValues = infoValues;
        this.subjectValues = subjectValues;
        this.assnValues = assnValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.new_array, parent, false);
        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.list_item_box);
        if (position == 0){
            relativeLayout.setBackgroundResource(R.drawable.first_item);
        }
        TextView itemTitle = (TextView) rowView
                .findViewById(R.id.list_item_title);
        TextView itemInfo = (TextView) rowView
                .findViewById(R.id.list_item_info);
        TextView itemNum = (TextView) rowView
                .findViewById(R.id.list_item_num);
        TextView itemSubject = (TextView) rowView
                .findViewById(R.id.list_item_subject);
        TextView itemValues = (TextView) rowView
                .findViewById(R.id.list_item_values);
        itemTitle.setText(titleValues[position]);
        itemInfo.setText(infoValues[position]);
        itemNum.setText(""+(position+1));
        itemSubject.setText(subjectValues[position]);
        itemValues.setText(assnValues[position]);
        return rowView;
    }

}
