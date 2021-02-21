package com.cnsukidayo.englishtoolandroid.core.learn;

import android.widget.EditText;
import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

public class ChineseInput {
    // 当前是什么词性
    private PartOfSpeechEnum partOfSpeech;
    // 这个基本上用不到
    private TextView partOfSpeechTextView;
    private EditText chineseInputText;
    private TextView chineseAnswerTextView;

    public ChineseInput() {
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

    public EditText getChineseInputText() {
        return chineseInputText;
    }

    public void setChineseInputText(EditText chineseInputText) {
        this.chineseInputText = chineseInputText;
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
