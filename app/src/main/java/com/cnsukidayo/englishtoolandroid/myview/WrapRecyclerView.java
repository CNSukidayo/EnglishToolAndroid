package com.cnsukidayo.englishtoolandroid.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WrapRecyclerView extends RecyclerView {
    public ArrayList<View> mHeaderViews = new ArrayList<>();
    public ArrayList<View> mFooterViews = new ArrayList<>();
    //添加Adapter
    public Adapter mAdapter;

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public void addHeaderView(View view) {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    public void addFooterView(View view) {
        mFooterViews.clear();
        mFooterViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.isEmpty() && mFooterViews.isEmpty()) {
            super.setAdapter(adapter);
        } else {
            adapter = new RecyclerWrapAdapter(mHeaderViews, mFooterViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }
}