package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;

public class IncludeWordElement {
    private int order;
    // 展开按钮
    private ImageButton open;
    // 是否处于展开的状态
    private boolean isOpen;
    // 标题
    private TextView includeTitle;
    // 描述信息
    private TextView includeDescribe;
    // 添加按钮
    private ImageButton add;
    // 三个左滑按钮
    private LinearLayout leftSlideLinearLayout;
    //
    private ImageButton edit;
    private ImageButton moveDown;
    private ImageButton moveUp;
    private ImageButton delete;
    // 是否处于左滑状态中
    private boolean isLeftSlide = false;
    // 展开的RecyclerView
    private RecyclerView controlWordWrapRecyclerView;

    public IncludeWordElement() {
    }

    public ImageButton getOpen() {
        return open;
    }

    public void setOpen(ImageButton open) {
        this.open = open;
    }


    public TextView getIncludeTitle() {
        return includeTitle;
    }

    public void setIncludeTitle(TextView includeTitle) {
        this.includeTitle = includeTitle;
    }

    public TextView getIncludeDescribe() {
        return includeDescribe;
    }

    public void setIncludeDescribe(TextView includeDescribe) {
        this.includeDescribe = includeDescribe;
    }

    public ImageButton getAdd() {
        return add;
    }

    public void setAdd(ImageButton add) {
        this.add = add;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    public LinearLayout getLeftSlideLinearLayout() {
        return leftSlideLinearLayout;
    }

    public void setLeftSlideLinearLayout(LinearLayout leftSlideLinearLayout) {
        this.leftSlideLinearLayout = leftSlideLinearLayout;
        this.moveUp = this.leftSlideLinearLayout.findViewById(R.id.moveUp);
        this.moveDown = this.leftSlideLinearLayout.findViewById(R.id.moveDown);
        this.delete = this.leftSlideLinearLayout.findViewById(R.id.delete);
    }

    public boolean isLeftSlide() {
        return isLeftSlide;
    }

    public void setLeftSlide(boolean leftSlide) {
        isLeftSlide = leftSlide;
    }

    public ImageButton getMoveDown() {
        return moveDown;
    }

    public ImageButton getMoveUp() {
        return moveUp;
    }

    public ImageButton getDelete() {
        return delete;
    }

    public RecyclerView getControlWordWrapRecyclerView() {
        return controlWordWrapRecyclerView;
    }

    public void setControlWordWrapRecyclerView(RecyclerView controlWordWrapRecyclerView) {
        this.controlWordWrapRecyclerView = controlWordWrapRecyclerView;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public ImageButton getEdit() {
        return edit;
    }

    public void setEdit(ImageButton edit) {
        this.edit = edit;
    }
}
