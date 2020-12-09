package com.touchy.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;

public class StageTwoActivity extends AppCompatActivity {
    private final int CODE_LENGTH = 15;

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
                MaterialTextView codePattern = findViewById(R.id.pattern);

                findViewById(R.id.stageTwoInstructionsText).setVisibility(View.VISIBLE);
                codePattern.setText(randomStringGenerator(CODE_LENGTH));
                codePattern.setVisibility(View.VISIBLE);
                findViewById(R.id.stageTwoView).setVisibility(View.VISIBLE);
                findViewById(R.id.stageTwoCloseButton).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private String randomStringGenerator(int codeLength) {
        Character[] arr = new Character[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        StringBuilder code = new StringBuilder();
        while (codeLength > 0) {
            code.append(arr[(int) (Math.random() * 10)]);
            codeLength--;
        }

        return code.toString();
    }

    public void navigateBack(View view) {
        Intent intent = new Intent(this, CalibrationSetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}