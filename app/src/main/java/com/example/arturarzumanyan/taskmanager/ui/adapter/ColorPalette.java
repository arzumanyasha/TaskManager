package com.example.arturarzumanyan.taskmanager.ui.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.R;

public class ColorPalette {
    private Context mContext;

    public ColorPalette(Context mContext) {
        this.mContext = mContext;
    }

    public SparseIntArray getColorPalette() {
        SparseIntArray colorPalette = new SparseIntArray();
        colorPalette.put(1, ResourcesCompat.getColor(mContext.getResources(), R.color._1, null));
        colorPalette.put(2, ResourcesCompat.getColor(mContext.getResources(), R.color._2, null));
        colorPalette.put(3, ResourcesCompat.getColor(mContext.getResources(), R.color._3, null));
        colorPalette.put(4, ResourcesCompat.getColor(mContext.getResources(), R.color._4, null));
        colorPalette.put(5, ResourcesCompat.getColor(mContext.getResources(), R.color._5, null));
        colorPalette.put(6, ResourcesCompat.getColor(mContext.getResources(), R.color._6, null));
        colorPalette.put(7, ResourcesCompat.getColor(mContext.getResources(), R.color._7, null));
        colorPalette.put(8, ResourcesCompat.getColor(mContext.getResources(), R.color._8, null));
        colorPalette.put(9, ResourcesCompat.getColor(mContext.getResources(), R.color._9, null));
        colorPalette.put(10, ResourcesCompat.getColor(mContext.getResources(), R.color._10, null));
        colorPalette.put(11, ResourcesCompat.getColor(mContext.getResources(), R.color._11, null));
        return colorPalette;
    }

}
