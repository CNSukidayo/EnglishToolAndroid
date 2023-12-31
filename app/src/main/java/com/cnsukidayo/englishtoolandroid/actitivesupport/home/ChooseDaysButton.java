package com.cnsukidayo.englishtoolandroid.actitivesupport.home;

import android.os.Build;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;

import java.io.File;
import java.util.List;

/**
 * 选择第几天的符合组件
 */
public class ChooseDaysButton {
    private CheckBox checkBox;
    private TextView textView;
    private LinearLayout linearLayout;
    private volatile boolean choseFlag = false;
    // 当前天所对应的Json路径
    private File thisDayJsonFile;

    /**
     * 改变当前选择日期按钮的状态
     *
     * @param status true则按钮选中,false则按钮不选中
     */
    public void changeChoseStatus(boolean status) {
        choseFlag = status;
        refuseChoseStatus();
    }

    /**
     * 刷新当前按钮的选中状态
     */
    public void refuseChoseStatus() {
        if (choseFlag) {
            linearLayout.setBackgroundResource(R.drawable.json_linear_layout_choose);
            checkBox.setChecked(true);
        } else {
            linearLayout.setBackgroundResource(R.drawable.json_linear_layout_not_choose);
            checkBox.setChecked(false);
        }
    }

    /**
     * 得到当前按钮所对应的所有单词,注意如果当前天数没有被选中,返回的将是一个空集(而不是null)
     *
     * @param list 添加到哪个集合
     * @return 返回的List保证不会空, 但有可能是空集.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getThisDayWordsWithChose(List<Word> list) {
        if (!choseFlag) {
            return;
        }
        getThisDayWords(list);
    }

    /**
     * 得到当前按钮所对应的所有单词
     *
     * @param list 添加到哪个集合
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getThisDayWords(List<Word> list) {
        ParseWordsUtils.parseJsonAndGetWordsWithList(list,thisDayJsonFile.getAbsoluteFile());
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
        linearLayout.setOnClickListener(view -> {
            choseFlag = !isChoseFlag();
            refuseChoseStatus();
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
