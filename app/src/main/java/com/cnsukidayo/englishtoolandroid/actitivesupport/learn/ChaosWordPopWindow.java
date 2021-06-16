package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

import java.util.function.Consumer;

public class ChaosWordPopWindow {
    //
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private View RED;
    private View unChaos;
    private View BLUE;
    private View YELLOW;
    private View VIOLET;
    private View ORANGE;
    private View PINK;
    // 事件
    private View.OnClickListener onClickListener;
    private Consumer<WordMarkColor> chaneChaosMode;
    //
    private Button chaosButton;

    /**
     * 四个参数必须严格来
     *
     * @param linearLayout 用于获取弹出的那个layout
     * @param popupWindow  弹出的POPWindow对象
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ChaosWordPopWindow(LinearLayout linearLayout, PopupWindow popupWindow, Button chaosButton, Consumer<WordMarkColor> chaneChaosMode) {
        this.popupWindow = popupWindow;
        this.linearLayout = linearLayout;
        this.chaosButton = chaosButton;
        this.chaneChaosMode = chaneChaosMode;
        init();
        event();
    }


    private void init() {
        RED = linearLayout.findViewById(R.id.markRED);
        unChaos = linearLayout.findViewById(R.id.unChaos);
        BLUE = linearLayout.findViewById(R.id.markBLUE);
        YELLOW = linearLayout.findViewById(R.id.markYELLOW);
        VIOLET = linearLayout.findViewById(R.id.markVIOLET);
        ORANGE = linearLayout.findViewById(R.id.markORANGE);
        PINK = linearLayout.findViewById(R.id.markPINK);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void event() {
        RED.setOnClickListener(getOnClickListener());
        unChaos.setOnClickListener(getOnClickListener());
        BLUE.setOnClickListener(getOnClickListener());
        YELLOW.setOnClickListener(getOnClickListener());
        VIOLET.setOnClickListener(getOnClickListener());
        ORANGE.setOnClickListener(getOnClickListener());
        PINK.setOnClickListener(getOnClickListener());
    }

    private final String mark = "开启混沌模式";
    private final String unMark = "关闭混沌模式";

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getOnClickListener() {
        if (onClickListener == null) {
            onClickListener = v -> {
                popupWindow.dismiss();
                WordMarkColor wordMarkColor = WordMarkColor.DEFAULT;
                switch (v.getId()) {
                    case R.id.markRED:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.android_holo_red_light));
                        wordMarkColor = WordMarkColor.RED;
                        break;
                    case R.id.unChaos:
                        chaosButton.setText(mark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.colorBlack));
                        break;
                    case R.id.markBLUE:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.android_holo_blue_light));
                        wordMarkColor = WordMarkColor.BLUE;
                        break;
                    case R.id.markYELLOW:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.colorYellow));
                        wordMarkColor = WordMarkColor.YELLOW;
                        break;
                    case R.id.markVIOLET:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.colorViolet));
                        wordMarkColor = WordMarkColor.VIOLET;
                        break;
                    case R.id.markORANGE:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.android_holo_orange_light));
                        wordMarkColor = WordMarkColor.ORANGE;
                        break;
                    case R.id.markPINK:
                        chaosButton.setText(unMark);
                        this.chaosButton.setTextColor(chaosButton.getResources().getColor(R.color.colorPink));
                        wordMarkColor = WordMarkColor.PINK;
                        break;
                }
                chaneChaosMode.accept(wordMarkColor);
            };
        }
        return onClickListener;
    }

}
