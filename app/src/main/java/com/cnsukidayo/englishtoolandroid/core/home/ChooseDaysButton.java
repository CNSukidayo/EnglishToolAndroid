package com.cnsukidayo.englishtoolandroid.core.home;

import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.R;
import com.google.android.material.drawable.DrawableUtils;

/**
 * 选择第几天的符合组件
 */
public class ChooseDaysButton {

    private CheckBox checkBox;
    private TextView textView;
    private LinearLayout linearLayout;
    private boolean choseFlag = false;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        linearLayout.setOnClickListener(v -> {
            choseFlag = !choseFlag;
            if (choseFlag) {
                linearLayout.setBackgroundResource(R.color.colorChooseJsonButtonRed);
                checkBox.setChecked(true);
                checkBox.setBackgroundResource(R.color.colorChooseJsonCheckBoxBlue);
            } else {
                linearLayout.setBackgroundResource(R.color.colorNotChooseJsonButtonGreen);
                checkBox.setChecked(false);
            }
        });
    }

}
