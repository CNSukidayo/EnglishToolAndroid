package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

public class ChineseDisplay {
    // 当前是什么词性
    private PartOfSpeechEnum partOfSpeech;
    // 这个基本上用不到
    private TextView partOfSpeechTextView;
    private TextView chineseAnswerTextView;

    public ChineseDisplay() {
    }

    public PartOfSpeechEnum getPartOfSpeech() {
        return partOfSpeech;
    }

    public TextView getPartOfSpeechTextView() {
        return partOfSpeechTextView;
    }

    public void setPartOfSpeechTextView(TextView partOfSpeechTextView) {
        this.partOfSpeechTextView = partOfSpeechTextView;
    }

    public TextView getChineseAnswerTextView() {
        return chineseAnswerTextView;
    }

    public void setChineseAnswerTextView(TextView chineseAnswerTextView) {
        this.chineseAnswerTextView = chineseAnswerTextView;
    }

    public void setPartOfSpeech(PartOfSpeechEnum partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}
