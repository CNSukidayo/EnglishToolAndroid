package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.Button;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

public class MarkModeButtonBackGroundChangeHandler {
    private final Button markModeButton;

    public MarkModeButtonBackGroundChangeHandler(Button markModeButton) {
        this.markModeButton = markModeButton;
    }

    public void changeButtonBackGround(WordMarkColor wordMarkColor) {
        String mark = "开启标记模式";
        String unMark = "关闭标记模式";
        switch (wordMarkColor) {
            case RED:
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.android_holo_red_light));
                markModeButton.setText(unMark);
                break;
            case BLACK:
                markModeButton.setText(mark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.colorBlack));
                break;
            case BLUE:
                markModeButton.setText(unMark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.android_holo_blue_light));
                break;
            case YELLOW:
                markModeButton.setText(unMark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.colorYellow));
                break;
            case VIOLET:
                markModeButton.setText(unMark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.colorViolet));
                break;
            case ORANGE:
                markModeButton.setText(unMark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.android_holo_orange_light));
                break;
            case PINK:
                markModeButton.setText(unMark);
                this.markModeButton.setTextColor(markModeButton.getResources().getColor(R.color.colorPink));
                break;
        }
    }

}
