package com.touchy.app.Utils;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

public class ScreenHelper {
    public static void hideKeyboardOnCreate(Activity activityReference) {
        // hide keyboard on activity create
        activityReference.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void hideNavigationBar(Object objectReference) {
        if (objectReference == null) return;
        if (objectReference instanceof AlertDialog) {
            Objects.requireNonNull(((AlertDialog) objectReference).getWindow()).getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            ((Activity)objectReference).getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static void keepScreenAlwaysOn(Activity activityReference) {
        activityReference.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void makeScreenFullScreen(Activity activityReference) {
        activityReference.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void removeFocusFromView(Object objectReference) {
        if (objectReference == null) return;
        if (objectReference instanceof AlertDialog) {
            Objects.requireNonNull(((AlertDialog) objectReference).getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            ((Activity) objectReference).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    public static void returnFocusToView(Object objectReference) {
        if (objectReference == null) return;
        if (objectReference instanceof AlertDialog) {
            Objects.requireNonNull(((AlertDialog) objectReference).getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            ((Activity) objectReference).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    public static void setActivityInitialLayout(Activity activityReference) {
        // hide navigation
        ScreenHelper.hideNavigationBar(activityReference);
        // keep screen always on
        ScreenHelper.keepScreenAlwaysOn(activityReference);
        // make screen full screen
        ScreenHelper.makeScreenFullScreen(activityReference);
    }
}
