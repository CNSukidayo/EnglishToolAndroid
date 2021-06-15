package com.cnsukidayo.englishtoolandroid.actitivesupport.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;

public class MediaButtonReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        int keyAction = keyEvent.getAction();

        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)
                && (KeyEvent.ACTION_DOWN == keyAction)) {
            // 获得按键字节码
            int keyCode = keyEvent.getKeyCode();

            // 获得事件的时间
            final long nowTime = keyEvent.getEventTime();
            Long preClickTime = (Long) CacheQueue.SINGLE.accept("preClickTime", null);
            if (preClickTime == null) {
                preClickTime = 0L;
            }
            Log.d("时差", "" + (nowTime - preClickTime));
            if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
                if (nowTime - preClickTime <= 500) {
                    CacheQueue.SINGLE.accept("randomPlay", null);
                } else {
                    CacheQueue.SINGLE.accept("stopMusic", null);
                }
            }
            CacheQueue.SINGLE.addWork("preClickTime", o -> nowTime);
        }
    }
}