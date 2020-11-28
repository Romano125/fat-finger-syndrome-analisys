package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.textview.MaterialTextView;
import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;
import com.touchy.app.Views.StageTwoView;

public class StageTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_two);

        ScreenHelper.setActivityInitialLayout(this);

        MaterialTextView stageTwoInfo = findViewById(R.id.stageTwoInfo);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.translation);
        stageTwoInfo.startAnimation(logoAnimation);

        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StageTwoView stageTwoView = findViewById(R.id.stageTwoView);
                stageTwoView.setVisibility(View.VISIBLE);
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