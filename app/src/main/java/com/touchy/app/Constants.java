package com.touchy.app;

import android.graphics.Color;

public class Constants {
    public enum TOUCHED_AREA  {
        NORTH_WEST,
        NORTH_EAST,
        CENTER,
        SOUTH_EAST,
        SOUTH_WEST
    }

    public static String CALIBRATION_LOG_FILENAME = "calibrationData";
    public static String STAGE_ONE_LOG_FILENAME = "stageOneData";
    public static String STAGE_TWO_LOG_FILENAME = "stageTwoData";

    public static int[] TARGET_COLORS = {
            Color.GREEN,
            Color.RED,
            Color.parseColor("#9900ff"),
            Color.CYAN,
            Color.BLACK,
            Color.parseColor("#00FF7F"),
            Color.BLUE,
            Color.parseColor("#7FFF00"),
    };

    public static String APP_BACKGROUND_COLOR = "#E4E0E0";
}
