package com.example.arturarzumanyan.taskmanager;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;

public class ResourceManager {
    private static ResourceManager resourceManager;
    private static ColorPalette colorPalette;
    private static Context context;

    public static void initResourceManager(Context context) {
        colorPalette = new ColorPalette(context);
        context = context;
    }

    public synchronized static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public SparseIntArray getColorPalette() {
        return colorPalette.getColorPalette();
    }
}
