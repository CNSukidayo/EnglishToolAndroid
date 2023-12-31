package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

import java.util.function.Consumer;

public class MarkModePopWindow {
    //
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private View RED;
    private View unMark;
    private View BLUE;
    private View YELLOW;
    private View VIOLET;
    private View ORANGE;
    private View PINK;
    // 对当前单词的引用
    private Consumer<WordMarkColor> wordMarkColorSupplier;
    // 事件
    private View.OnClickListener onClickListener;
    // 标记按钮
    private MarkModeButtonBackGroundChangeHandler markModeButtonBackGroundChangeHandler;

    /**
     * 四个参数必须严格来
     *
     * @param linearLayout                          用于获取弹出的那个layout
     * @param popupWindow                           弹出的POPWindow对象
     * @param wordMarkColorSupplier                 当颜色按钮被点击时修改LearnPage里WordMarkMode的标记色,通过lambda表达式来处理
     * @param markModeButtonBackGroundChangeHandler 改变标记模式按钮文字颜色的处理器
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public MarkModePopWindow(LinearLayout linearLayout, PopupWindow popupWindow, Consumer<WordMarkColor> wordMarkColorSupplier, MarkModeButtonBackGroundChangeHandler markModeButtonBackGroundChangeHandler) {
        this.popupWindow = popupWindow;
        this.linearLayout = linearLayout;
        this.wordMarkColorSupplier = wordMarkColorSupplier;
        this.markModeButtonBackGroundChangeHandler = markModeButtonBackGroundChangeHandler;
        init();
        event();
    }


    private void init() {
        RED = linearLayout.findViewById(R.id.markRED);
        unMark = linearLayout.findViewById(R.id.unMark);
        BLUE = linearLayout.findViewById(R.id.markBLUE);
        YELLOW = linearLayout.findViewById(R.id.markYELLOW);
        VIOLET = linearLayout.findViewById(R.id.markVIOLET);
        ORANGE = linearLayout.findViewById(R.id.markORANGE);
        PINK = linearLayout.findViewById(R.id.markPINK);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void event() {
        RED.setOnClickListener(getOnClickListener());
        unMark.setOnClickListener(getOnClickListener());
        BLUE.setOnClickListener(getOnClickListener());
        YELLOW.setOnClickListener(getOnClickListener());
        VIOLET.setOnClickListener(getOnClickListener());
        ORANGE.setOnClickListener(getOnClickListener());
        PINK.setOnClickListener(getOnClickListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getOnClickListener() {
        if (onClickListener == null) {
            onClickListener = v -> {
                popupWindow.dismiss();
                WordMarkColor wordMarkColor;
                switch (v.getId()) {
                    case R.id.markRED:
                        wordMarkColor = WordMarkColor.RED;
                        break;
                    case R.id.unMark:
                        wordMarkColor = WordMarkColor.DEFAULT;
                        break;
                    case R.id.markBLUE:
                        wordMarkColor = WordMarkColor.BLUE;
                        break;
                    case R.id.markYELLOW:
                        wordMarkColor = WordMarkColor.YELLOW;
                        break;
                    case R.id.markVIOLET:
                        wordMarkColor = WordMarkColor.VIOLET;
                        break;
                    case R.id.markORANGE:
                        wordMarkColor = WordMarkColor.ORANGE;
                        break;
                    case R.id.markPINK:
                        wordMarkColor = WordMarkColor.PINK;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + v.getId());
                }
                wordMarkColorSupplier.accept(wordMarkColor);
                markModeButtonBackGroundChangeHandler.changeButtonBackGround(wordMarkColor);
            };
        }
        return onClickListener;
    }

}
