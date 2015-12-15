package com.skylan.homeworkpro;

import com.orm.SugarRecord;

/**
 * Created by danielkawalsky on 7/11/15.
 */
public class CheckInfo extends SugarRecord{

    public String itemTitle;
    public String itemHeaderTitle;
    public boolean itemChecked;

    public CheckInfo() {}

    public CheckInfo (String itemTitle, String itemHeaderTitle, boolean itemChecked){
        this.itemTitle = itemTitle;
        this.itemHeaderTitle = itemHeaderTitle;
        this.itemChecked = itemChecked;
    }
}
