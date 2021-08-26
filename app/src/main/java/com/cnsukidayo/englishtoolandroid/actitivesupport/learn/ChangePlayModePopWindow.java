package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;

import java.util.function.Consumer;

public class ChangePlayModePopWindow {
    //
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private View dictation;
    private View englishChineseTranslate;
    private View chineseEnglishTranslate;
    private View onlyRecite;
    private TextView preViewEnglish;
    // 事件
    private View.OnClickListener onClickListener;
    private Consumer<StartMod> startModConsumer;
    private Consumer<Boolean> preViewEnglishFlagConsumer;
    // 是否是预览模式
    private boolean preViewEnglishFlag;

    /**
     * 四个参数必须严格来
     *
     * @param linearLayout 用于获取弹出的那个layout
     * @param popupWindow  弹出的POPWindow对象
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ChangePlayModePopWindow(LinearLayout linearLayout, PopupWindow popupWindow, Consumer<StartMod> startModConsumer, Consumer<Boolean> preViewEnglishFlagConsumer) {
        this.popupWindow = popupWindow;
        this.linearLayout = linearLayout;
        this.startModConsumer = startModConsumer;
        this.preViewEnglishFlagConsumer = preViewEnglishFlagConsumer;
        init();
        event();
    }


    public void changeIsPreEnglish(boolean flag) {
        preViewEnglishFlag = flag;
        if (flag) {
            preViewEnglish.setBackgroundResource(R.drawable.pre_view_english_mod_fillet);
            preViewEnglish.setText("预览英文");
        } else {
            preViewEnglish.setBackgroundResource(R.drawable.dispre_view_english_mod_fillet);
            preViewEnglish.setText("不预览英文");
        }
    }

    private void init() {
        dictation = linearLayout.findViewById(R.id.dictation);
        englishChineseTranslate = linearLayout.findViewById(R.id.englishChineseTranslate);
        chineseEnglishTranslate = linearLayout.findViewById(R.id.chineseEnglishTranslate);
        onlyRecite = linearLayout.findViewById(R.id.onlyRecite);
        preViewEnglish = linearLayout.findViewById(R.id.preViewEnglish);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void event() {
        dictation.setOnClickListener(getOnClickListener());
        englishChineseTranslate.setOnClickListener(getOnClickListener());
        chineseEnglishTranslate.setOnClickListener(getOnClickListener());
        onlyRecite.setOnClickListener(getOnClickListener());
        preViewEnglish.setOnClickListener(v -> {
            changeIsPreEnglish(!preViewEnglishFlag);
            preViewEnglishFlagConsumer.accept(preViewEnglishFlag);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getOnClickListener() {
        if (onClickListener == null) {
            onClickListener = v -> {
                popupWindow.dismiss();
                switch (v.getId()) {
                    case R.id.dictation:
                        startModConsumer.accept(StartMod.DICTATION);
                        break;
                    case R.id.chineseEnglishTranslate:
                        startModConsumer.accept(StartMod.CHINESEENGLISHTRANSLATE);
                        break;
                    case R.id.englishChineseTranslate:
                        startModConsumer.accept(StartMod.ENGLISHCHINESETRANSLATE);
                        break;
                    case R.id.onlyRecite:
                        startModConsumer.accept(StartMod.ONLYRECITE);
                        break;
                }
            };
        }
        return onClickListener;
    }

}
