package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

public class LeftSlideRelativeLayout extends RelativeLayout {
    private int mLastX;
    // 左滑的回调
    private Consumer<Boolean> leftSlideRunnable;

    public LeftSlideRelativeLayout(Context context) {
        super(context);
    }

    public LeftSlideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftSlideRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftSlideRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLeftSlideRunnable(Consumer<Boolean> leftSlideRunnable) {
        this.leftSlideRunnable = leftSlideRunnable;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - mLastX;
                // 往左划
                if (offsetX <= -80) {
                    leftSlideRunnable.accept(true);
                }
                // 往右划
                if (offsetX >= 80) {
                    leftSlideRunnable.accept(false);
                }
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }
}
