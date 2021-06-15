package com.cnsukidayo.englishtoolandroid.actitivesupport.music;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.MusicMode;

public class ChangeMusicModelPopWindow {
    //
    private PopupWindow popupWindow;
    // 三种模式的组件选择,全随机;
    private TextView musicSequential;
    private TextView musicCompleteRandom;
    private TextView musicIncompleteRandom;
    // 当前的播放模式,常驻内存
    private MusicMode musicMode = MusicMode.COMPLETERANDOM;
    // 监听,常驻内存,避免重复创建
    private View.OnClickListener onClickListener;

    // 当前播放音乐的模式
    public ChangeMusicModelPopWindow() {
    }


    public void init(LinearLayout linearLayout, PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
        musicSequential = linearLayout.findViewById(R.id.musicSequential);
        musicCompleteRandom = linearLayout.findViewById(R.id.musicCompleteRandom);
        musicIncompleteRandom = linearLayout.findViewById(R.id.musicIncompleteRandom);
        if (musicMode == MusicMode.SEQUENTIAL) {
            musicSequential.setBackground(linearLayout.getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
            musicCompleteRandom.setBackground(null);
            musicIncompleteRandom.setBackground(null);
        } else if (musicMode == MusicMode.COMPLETERANDOM) {
            musicSequential.setBackground(null);
            musicCompleteRandom.setBackground(linearLayout.getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
            musicIncompleteRandom.setBackground(null);
        } else {
            musicSequential.setBackground(null);
            musicCompleteRandom.setBackground(null);
            musicIncompleteRandom.setBackground(linearLayout.getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
        }
        musicSequential.setOnClickListener(v12 -> popupWindow.dismiss());
        musicCompleteRandom.setOnClickListener(v1 -> popupWindow.dismiss());
        musicIncompleteRandom.setOnClickListener(v13 -> getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        if (this.onClickListener == null) {
            onClickListener = v -> {
                popupWindow.dismiss();
                switch (v.getId()) {
                    case R.id.musicSequential:
                        musicMode = MusicMode.SEQUENTIAL;
                        break;
                    case R.id.musicCompleteRandom:
                        musicMode = MusicMode.COMPLETERANDOM;
                        break;
                    case R.id.musicIncompleteRandom:
                        musicMode = MusicMode.INCOMPLETERANDOM;
                        break;
                }
            };
        }
        return onClickListener;
    }

    public MusicMode getMusicMode() {
        return musicMode;
    }
}
