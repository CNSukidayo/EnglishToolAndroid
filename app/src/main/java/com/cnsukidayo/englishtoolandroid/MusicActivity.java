package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.music.MediaButtonReceiver;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity {
    // 所有文件
    private File[] files;
    // 进度条
    private Timer timer = new Timer();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // 当前正在播放的音乐
    private TextView musicText;
    // 进度条
    private TextView musicBar;
    // 随机播放按钮
    private Button randomMusicPlay;
    // 暂停按钮
    private Button randomMusicSuspend;
    // 当前进度
    private int pausePosition;
    // 停止按钮
    private Button randomMusicStop;
    //
    private int pre = -1;
    //
    private boolean firstClick = false;

    private AudioManager mAudioManager;
    private ComponentName mComponentName;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        String basePath = GetPathUtils.getStoragePath(this, true);
        File baseFile = new File(basePath + File.separator + EnglishToolProperties.musicSourcePath);
        files = baseFile.listFiles();
        musicText = findViewById(R.id.musicText);
        musicBar = findViewById(R.id.musicBar);
        randomMusicPlay = findViewById(R.id.randomMusicPlay);
        randomMusicSuspend = findViewById(R.id.randomMusicSuspend);
        randomMusicStop = findViewById(R.id.randomMusicStop);
        randomMusicPlay.setOnClickListener(view -> randomPlay());
        randomMusicStop.setOnClickListener(view -> mediaPlayer.stop());
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (mediaPlayer.getCurrentPosition() != 0) {
                randomPlay();
            }
        });
        mediaPlayer.reset();
        timer.schedule(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                firstClick = false;
                if (mediaPlayer.isPlaying()) {
                    int duration = mediaPlayer.getDuration();
                    String durationStr = String.format("%d : %d", (duration % (1000 * 60 * 60)) / (1000 * 60), ((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    String currentPositionSrt = String.format("%d : %d", (currentPosition % (1000 * 60 * 60)) / (1000 * 60), ((currentPosition % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                    musicBar.setText(currentPositionSrt + "/" + durationStr);
                }
            }
        }, 0, 800);
        randomMusicSuspend.setOnClickListener(view -> {
            if (pausePosition == 0) {
                pausePosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                randomMusicSuspend.setBackgroundResource(R.drawable.flag_word_fillet);
                randomMusicSuspend.setText("继续播放");
            } else {
                mediaPlayer.seekTo(pausePosition);
                mediaPlayer.start();
                pausePosition = 0;
                randomMusicSuspend.setBackgroundResource(R.drawable.check_flag_word_fillet);
                randomMusicSuspend.setText("暂停播放");
            }
        });
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
        CacheQueue.SINGLE.addWork("stopMusic", o -> {
            randomMusicSuspend.performClick();
            return null;
        });
        CacheQueue.SINGLE.addWork("randomPlay", o -> {
            randomPlay();
            return null;
        });
    }

    @Override
    protected void onResume() {
        mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 取消注册
        mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timer = null;
        super.onDestroy();
    }

    private void randomPlay() {
        pausePosition = 0;
        pre = random(files.length - 1, pre);
        musicText.setText(files[pre].getName());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(files[pre].getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Random random = new Random();

    private int random(int max, int exclude) {
        int result;
        while (exclude == (result = random.nextInt(max))) ;
        return result;
    }

}
