package com.example.misho.login;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by Misho on 9/7/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    public SpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
     //  int count = super.getCount();
        return super.getCount();
        //return count>0 ? count-1 : count ;
    }
}
