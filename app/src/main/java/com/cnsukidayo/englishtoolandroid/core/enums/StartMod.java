package com.cnsukidayo.englishtoolandroid.core.enums;

import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.LearnPageRecyclerView;

/**
 * 开始的模式枚举
 */
public enum StartMod {
    DICTATION {
        @Override
        public void englishAnswerValueHandle(Word trueWord, TextView textView, LearnPageRecyclerView learnPageRecyclerView) {
            textView.setText("");
        }
    },
    CHINESEENGLISHTRANSLATE {
        @Override
        public void englishAnswerValueHandle(Word trueWord, TextView textView, LearnPageRecyclerView learnPageRecyclerView) {
            textView.setText(trueWord.getEnglish());
        }
    },
    ENGLISHCHINESETRANSLATE {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void englishAnswerValueHandle(Word trueWord, TextView textView, LearnPageRecyclerView learnPageRecyclerView) {
            textView.setText("");
            learnPageRecyclerView.setAnswerLabelTextFromWord(trueWord);
        }
    };


    /**
     * 改变TextView中的内容,改变的内容为englishWorld.根据当前的模式.
     *
     * @param trueWord              正确的目标单词
     * @param textView              显示英文单词的文本框
     * @param learnPageRecyclerView 调用里面的一些方法
     */
    public abstract void englishAnswerValueHandle(Word trueWord, TextView textView, LearnPageRecyclerView learnPageRecyclerView);


}
