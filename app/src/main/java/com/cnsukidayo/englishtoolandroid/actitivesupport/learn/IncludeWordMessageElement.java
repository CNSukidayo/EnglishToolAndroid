package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.TextView;

public class IncludeWordMessageElement {
    // 词性
    private TextView partOfSpeech;
    // 中文
    private TextView chinese;

    public TextView getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(TextView partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public TextView getChinese() {
        return chinese;
    }

    public void setChinese(TextView chinese) {
        this.chinese = chinese;
    }
}
