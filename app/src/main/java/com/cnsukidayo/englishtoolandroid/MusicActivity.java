package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.enums.MusicMode;
import com.cnsukidayo.englishtoolandroid.core.music.MediaButtonReceiver;
import com.cnsukidayo.englishtoolandroid.core.music.MusicRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MusicActivity extends AppCompatActivity {
    // 所有文件
    private File[] allMusicFiles;
    // 播放模式 默认全随机
    private MusicMode musicMode = MusicMode.COMPLETERANDOM;
    // 进度条
    private Timer timer = new Timer();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // 当前正在播放的音乐
    private TextView musicText;
    // 模式选择按钮
    private Button musicModeChange;
    // 适配器
    private MusicRecyclerViewAdapter musicRecyclerViewAdapter;
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
    // 播放计数器
    private int playCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        String basePath = GetPathUtils.getStoragePath(this, true);
        File baseFile = new File(basePath + File.separator + EnglishToolProperties.musicSourcePath);
        allMusicFiles = baseFile.listFiles();
        List<File> allMusicList = Arrays.asList(allMusicFiles);
        musicText = findViewById(R.id.musicText);
        musicBar = findViewById(R.id.musicBar);
        randomMusicPlay = findViewById(R.id.randomMusicPlay);
        randomMusicSuspend = findViewById(R.id.randomMusicSuspend);
        randomMusicStop = findViewById(R.id.randomMusicStop);
        musicModeChange = findViewById(R.id.musicModeChange);
        // 适配器
        WrapRecyclerView musicRecyclerView = findViewById(R.id.musicRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(allMusicFiles, this, consumer);
        musicRecyclerView.setLayoutManager(linearLayoutManager);
        musicRecyclerView.setAdapter(this.musicRecyclerViewAdapter);
        randomMusicPlay.setOnClickListener(view -> randomPlay());
        randomMusicStop.setOnClickListener(view -> mediaPlayer.stop());
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (mediaPlayer.getCurrentPosition() != 0) {
                if (musicMode == MusicMode.COMPLETERANDOM || musicMode == MusicMode.INCOMPLETERANDOM) {
                    playCount = 0;
                    randomPlay();
                } else {
                    if (playCount == allMusicList.size() - 1) {
                        playCount = 0;
                    }
                    if (playCount == 0) {
                        Collections.shuffle(allMusicList);
                    }
                    playMusic(allMusicList.get(playCount++));
                }
            }
        });
        //
        musicModeChange.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.change_music_mod_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, musicModeChange.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView musicSequential = changeMusicModPop.findViewById(R.id.musicSequential);
            TextView musicCompleteRandom = changeMusicModPop.findViewById(R.id.musicCompleteRandom);
            TextView musicIncompleteRandom = changeMusicModPop.findViewById(R.id.musicIncompleteRandom);
            // 可优化
            if (musicMode == MusicMode.SEQUENTIAL) {
                musicSequential.setBackground(getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
                musicCompleteRandom.setBackground(null);
                musicIncompleteRandom.setBackground(null);
            } else if (musicMode == MusicMode.COMPLETERANDOM) {
                musicSequential.setBackground(null);
                musicCompleteRandom.setBackground(getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
                musicIncompleteRandom.setBackground(null);
            } else {
                musicSequential.setBackground(null);
                musicCompleteRandom.setBackground(null);
                musicIncompleteRandom.setBackground(getResources().getDrawable(R.drawable.pre_view_english_mod_fillet));
            }
            musicSequential.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicMode = MusicMode.SEQUENTIAL;
                    changeModPopWindow.dismiss();
                }
            });
            musicCompleteRandom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicMode = MusicMode.COMPLETERANDOM;
                    changeModPopWindow.dismiss();
                }
            });
            musicIncompleteRandom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicMode = MusicMode.INCOMPLETERANDOM;
                    changeModPopWindow.dismiss();
                }
            });
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(musicModeChange);
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
        pre = random(allMusicFiles.length - 1, pre);
        playMusic(allMusicFiles[pre]);
    }

    private Consumer<File> consumer = this::playMusic;

    public void playMusic(File file) {
        musicText.setText(file.getName());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
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
