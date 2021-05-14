package com.cnsukidayo.englishtoolandroid.core.enums;

import android.widget.TextView;

/**
 * 开始的模式枚举
 */
public enum StartMod {
    DICTATION {
        @Override
        public void englishAnswerValueHandle(String englishWorld, TextView textView) {
            textView.setText("");
        }

        @Override
        public boolean isViewChinese() {
            return false;
        }
    },
    CHINESEENGLISHTRANSLATE {
        @Override
        public void englishAnswerValueHandle(String englishWorld, TextView textView) {
            textView.setText(englishWorld);
        }

        @Override
        public boolean isViewChinese() {
            return true;
        }
    };

    /**
     * 改变TextView中的内容,改变的内容为englishWorld.根据当前的模式.
     *
     * @param englishWorld 目标英文单词
     * @param textView 显示英文单词的文本框
     */
    public abstract void englishAnswerValueHandle(String englishWorld, TextView textView);
    public abstract boolean isViewChinese();

}
