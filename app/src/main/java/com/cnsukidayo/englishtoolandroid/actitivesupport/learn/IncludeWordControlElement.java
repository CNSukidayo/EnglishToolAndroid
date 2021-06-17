package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class IncludeWordControlElement {
    // 整个元素,可能要添加点击播放功能
    private RelativeLayout relativeLayout;
    // 原始英文
    private TextView englishOriginal;
    //
    private ImageButton moveDown;
    private ImageButton moveUp;
    private ImageButton delete;
    // 中文展示的RecyclerView
    private RecyclerView recyclerView;

    public RelativeLayout getLinearLayout() {
        return relativeLayout;
    }

    public void setLinearLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public TextView getEnglishOriginal() {
        return englishOriginal;
    }

    public void setEnglishOriginal(TextView englishOriginal) {
        this.englishOriginal = englishOriginal;
    }

    public ImageButton getMoveDown() {
        return moveDown;
    }

    public void setMoveDown(ImageButton moveDown) {
        this.moveDown = moveDown;
    }

    public ImageButton getMoveUp() {
        return moveUp;
    }

    public void setMoveUp(ImageButton moveUp) {
        this.moveUp = moveUp;
    }

    public ImageButton getDelete() {
        return delete;
    }

    public void setDelete(ImageButton delete) {
        this.delete = delete;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
