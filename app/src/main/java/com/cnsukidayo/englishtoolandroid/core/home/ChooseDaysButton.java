package com.cnsukidayo.englishtoolandroid.core.home;

import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.R;

import java.io.File;

/**
 * 选择第几天的符合组件
 */
public class ChooseDaysButton {

    private CheckBox checkBox;
    private TextView textView;
    private LinearLayout linearLayout;
    private boolean choseFlag = false;
    // 当前天所对应的Json路径
    private File thisDayJsonFile;
    /**
     * 改变当前选择日期按钮的状态
     *
     * @param status true则按钮选中,false则按钮不选中
     */
    public void changeChoseStatus(boolean status) {
        choseFlag = status;
        if (choseFlag) {
            linearLayout.setBackgroundResource(R.drawable.json_linear_layout_choose);
            checkBox.setChecked(true);
        } else {
            linearLayout.setBackgroundResource(R.drawable.json_linear_layout_not_choose);
            checkBox.setChecked(false);
        }
    }

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
            changeChoseStatus(!choseFlag);
        });
    }

    public void setThisDayJsonFile(File thisDayJsonFile) {
        this.thisDayJsonFile = thisDayJsonFile;
    }

    public File getThisDayJsonFile() {
        return thisDayJsonFile;
    }

    public boolean isChoseFlag() {
        return choseFlag;
    }
}
