package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

public class MarkWordPopWindow {
    //
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private View RED;
    private View GREEN;
    private View BLUE;
    private View YELLOW;
    private View VIOLET;
    private View ORANGE;
    private View PINK;
    // 对当前单词的引用
    private Word word;
    // 事件
    private View.OnClickListener onClickListener;
    // 标记按钮
    private MarkWordButtonBackGroundChangeHandler markWordButtonBackGroundChangeHandler;

    /**
     * 四个参数必须严格来
     *
     * @param linearLayout                          用于获取弹出的那个layout
     * @param popupWindow                           弹出的POPWindow对象
     * @param word                                  当前单词
     * @param markWordButtonBackGroundChangeHandler 改变标记按钮背景的处理器
     */
    public MarkWordPopWindow(LinearLayout linearLayout, PopupWindow popupWindow, Word word, MarkWordButtonBackGroundChangeHandler markWordButtonBackGroundChangeHandler) {
        this.popupWindow = popupWindow;
        this.linearLayout = linearLayout;
        this.word = word;
        this.markWordButtonBackGroundChangeHandler = markWordButtonBackGroundChangeHandler;
        init();
        event();
    }


    private void init() {
        RED = linearLayout.findViewById(R.id.markRED);
        GREEN = linearLayout.findViewById(R.id.markGREEN);
        BLUE = linearLayout.findViewById(R.id.markBLUE);
        YELLOW = linearLayout.findViewById(R.id.markYELLOW);
        VIOLET = linearLayout.findViewById(R.id.markVIOLET);
        ORANGE = linearLayout.findViewById(R.id.markORANGE);
        PINK = linearLayout.findViewById(R.id.markPINK);
    }

    private void event() {
        RED.setOnClickListener(getOnClickListener());
        GREEN.setOnClickListener(getOnClickListener());
        BLUE.setOnClickListener(getOnClickListener());
        YELLOW.setOnClickListener(getOnClickListener());
        VIOLET.setOnClickListener(getOnClickListener());
        ORANGE.setOnClickListener(getOnClickListener());
        PINK.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        if (onClickListener == null) {
            onClickListener = v -> {
                popupWindow.dismiss();
                switch (v.getId()) {
                    case R.id.markRED:
                        this.word.setWordMarkColor(WordMarkColor.RED);
                        break;
                    case R.id.markGREEN:
                        this.word.setWordMarkColor(WordMarkColor.DEFAULT);
                        break;
                    case R.id.markBLUE:
                        this.word.setWordMarkColor(WordMarkColor.BLUE);
                        break;
                    case R.id.markYELLOW:
                        this.word.setWordMarkColor(WordMarkColor.YELLOW);
                        break;
                    case R.id.markVIOLET:
                        this.word.setWordMarkColor(WordMarkColor.VIOLET);
                        break;
                    case R.id.markORANGE:
                        this.word.setWordMarkColor(WordMarkColor.ORANGE);
                        break;
                    case R.id.markPINK:
                        this.word.setWordMarkColor(WordMarkColor.PINK);
                        break;
                }
                markWordButtonBackGroundChangeHandler.changeButtonBackGround(word.getWordMarkColor());
            };
        }
        return onClickListener;
    }

}
