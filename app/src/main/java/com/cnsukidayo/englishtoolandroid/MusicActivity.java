package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
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
import com.cnsukidayo.englishtoolandroid.core.enums.MusicMode;
import com.cnsukidayo.englishtoolandroid.actitivesupport.music.ChangeMusicModelPopWindow;
import com.cnsukidayo.englishtoolandroid.actitivesupport.music.MusicRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MusicActivity extends AppCompatActivity {
    // 所有文件
    private List<File> allMusicList;
    // 播放模式
    private ChangeMusicModelPopWindow changeMusicModelPopWindow = new ChangeMusicModelPopWindow();
    // 返回上一页
    private Button back;
    // 进度条
    private Timer timer = new Timer();
    private final MediaPlayer mediaPlayer = new MediaPlayer();
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
        allMusicList = Arrays.asList(Objects.requireNonNull(baseFile.listFiles()));
        back = findViewById(R.id.back);
        musicText = findViewById(R.id.musicText);
        musicBar = findViewById(R.id.musicBar);
        randomMusicPlay = findViewById(R.id.randomMusicPlay);
        randomMusicSuspend = findViewById(R.id.randomMusicSuspend);
        randomMusicStop = findViewById(R.id.randomMusicStop);
        musicModeChange = findViewById(R.id.musicModeChange);
        // 适配器
        WrapRecyclerView musicRecyclerView = findViewById(R.id.musicRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(allMusicList, this, consumer);
        musicRecyclerView.setLayoutManager(linearLayoutManager);
        musicRecyclerView.setAdapter(this.musicRecyclerViewAdapter);
        randomMusicPlay.setOnClickListener(view -> playMusicStrategy());
        randomMusicStop.setOnClickListener(view -> mediaPlayer.stop());
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (mediaPlayer.getCurrentPosition() != 0) {
                playMusicStrategy();
            }
        });
        // 更改当前播放模式
        musicModeChange.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.change_music_mod_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, musicModeChange.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            changeMusicModelPopWindow.init(changeMusicModPop, changeModPopWindow);
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
                randomMusicSuspend.setBackgroundResource(R.drawable.mark_word_green);
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
        back.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(MainActivity.RESULT_OK, intent);
            finish();
        });
    }

    /**
     * 播放音乐的策略,可以采用策略设计模式
     */
    public void playMusicStrategy() {
        if (changeMusicModelPopWindow.getMusicMode() == MusicMode.COMPLETERANDOM || changeMusicModelPopWindow.getMusicMode() == MusicMode.SEQUENTIAL) {
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

    /**
     * 播放音乐的核心方法
     *
     * @param file 传入音乐文件的地址
     */
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
        pausePosition = 0;
        randomMusicSuspend.setBackgroundResource(R.drawable.check_flag_word_fillet);
        randomMusicSuspend.setText("暂停播放");
        musicRecyclerViewAdapter.changeBackGroundByID(findIDByFile(file));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timer = null;
        super.onDestroy();
    }

    private void randomPlay() {
        pausePosition = 0;
        pre = random(allMusicList.size() - 1, pre);
        playMusic(allMusicList.get(pre));
    }

    private Consumer<File> consumer = this::playMusic;

    private Random random = new Random();

    private int random(int max, int exclude) {
        int result;
        while (exclude == (result = random.nextInt(max))) ;
        return result;
    }

    // 根据File对象获取到ID
    private int findIDByFile(File file) {
        for (int i = 0; i < allMusicList.size(); i++) {
            // 这里可以直接地址等没有什么关系,反而效率高
            if (allMusicList.get(i) == file) {
                return i;
            }
        }
        return 0;
    }

}
