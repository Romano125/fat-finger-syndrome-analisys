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
            Color.parseColor("#6A66A3"),
            Color.parseColor("#C04ABC"),
            Color.parseColor("#53A038"),
            Color.parseColor("#C8AD55"),
            Color.parseColor("#1D2F6F"),
            Color.parseColor("#EE7674"),
            Color.parseColor("#FF6663"),
            Color.parseColor("#211C24"),
            Color.parseColor("#5E2BFF"),
            Color.parseColor("#6A041D"),
            Color.parseColor("#F5B841"),
            Color.parseColor("#691213"),
            Color.parseColor("#D4B82B"),
    };

    public static int[] KEYBOARD_KEYS = {
            R.id.t9_key_0,
            R.id.t9_key_1,
            R.id.t9_key_2,
            R.id.t9_key_3,
            R.id.t9_key_4,
            R.id.t9_key_5,
            R.id.t9_key_6,
            R.id.t9_key_7,
            R.id.t9_key_8,
            R.id.t9_key_9
    };

    public static String APP_BACKGROUND_COLOR = "#E4E0E0";
}
