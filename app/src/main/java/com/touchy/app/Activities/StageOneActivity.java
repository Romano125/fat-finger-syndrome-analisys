package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.textview.MaterialTextView;
import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;
import com.touchy.app.Views.StageOneView;

public class StageOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_one);

        ScreenHelper.setActivityInitialLayout(this);

        MaterialTextView stageOneInfo = findViewById(R.id.stageOneInfo);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.activity_splash_translation);
        stageOneInfo.startAnimation(logoAnimation);

        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StageOneView stageOneView = findViewById(R.id.stageOneView);
                stageOneView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}