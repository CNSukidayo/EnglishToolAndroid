package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.Button;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

public class MarkWordButtonBackGroundChangeHandler {
    private final Button markButton;

    public MarkWordButtonBackGroundChangeHandler(Button markButton) {
        this.markButton = markButton;
    }

    public void changeButtonBackGround(WordMarkColor wordMarkColor) {
        String mark = "标记单词";
        String unMark = "解除标记";
        switch (wordMarkColor) {
            case RED:
                this.markButton.setBackgroundResource(R.drawable.mark_word_red);
                markButton.setText(unMark);
                break;
            case DEFAULT:
                markButton.setText(mark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_green);
                break;
            case BLUE:
                markButton.setText(unMark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_blue);
                break;
            case YELLOW:
                markButton.setText(unMark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_yellow);
                break;
            case VIOLET:
                markButton.setText(unMark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_violet);
                break;
            case ORANGE:
                markButton.setText(unMark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_orange);
                break;
            case PINK:
                markButton.setText(unMark);
                this.markButton.setBackgroundResource(R.drawable.mark_word_pink);
                break;
        }
    }

}
