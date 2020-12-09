package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.touchy.app.Activities.MainMenuActivity;
import com.touchy.app.Models.Target;
import com.touchy.app.R;

public class StageTwoView extends FrameLayout implements View.OnClickListener {
    private TextInputEditText mPasswordField;
    private MaterialTextView materialTextView;
    private long startTime = System.currentTimeMillis();

    public StageTwoView(Context context) {
        super(context);
        init();
    }

    public StageTwoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StageTwoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_stage_two, this);
        initViews();
    }

    private void initViews() {
        mPasswordField = $(R.id.password_field);
        materialTextView = $(R.id.pattern);

        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_clear).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            mPasswordField.append(((TextView) v).getText());

            float density = getResources().getDisplayMetrics().density;
            int radius = (int)(v.getWidth() / density);

            int [] location = new int[2];
            v.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];

            Target target = new Target(x, y, radius);

            System.out.println(radius);
            System.out.println(x);
            System.out.println(y);

            checkIfPhraseIsCorrect();

            return;
        }

        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
                mPasswordField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
                Editable editable = mPasswordField.getText();
                int charCount = editable.length();
                if (charCount > 0) {
                    editable.delete(charCount - 1, charCount);
                }
            }
            break;
        }
    }

    private void checkIfPhraseIsCorrect() {
        if (materialTextView.getText().equals(getInputText())) {
            System.out.println("Total time: " + ((System.currentTimeMillis() - startTime) / 1000));

            Intent intent = new Intent(getContext(), MainMenuActivity.class);
            getContext().startActivity(intent);
        }
    }

    public String getInputText() {
        return mPasswordField.getText().toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}
