package com.touchy.app.Utils;

import android.view.Display;

import java.util.Locale;

public class Common {
    public static String getScreenResolution(Display display) {
        int height = display.getWidth();
        int width = display.getHeight();

        return String.format(Locale.ENGLISH,"%d x %d", width, height);
    }
}
