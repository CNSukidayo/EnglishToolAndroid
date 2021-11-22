package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.actitivesupport.music.MusicRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ListingComprehension extends AppCompatActivity {

    // 所有文件
    private List<File> allListingList;
    // 适配器
    private MusicRecyclerViewAdapter musicRecyclerViewAdapter;

    // 进度条
    private Timer timer = new Timer();
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    // 当前正在播放的音乐
    private TextView musicText;
    // 暂停按钮
    private Button randomMusicSuspend;
    // 进度条
    private TextView musicBar;
    // 当前进度
    private int pausePosition;

    private Button run2s;
    private Button run5s;
    private Button run30s;
    private Button back2s;
    private Button back5s;
    private Button back30s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listing_comprehension);
        String basePath = GetPathUtils.getStoragePath(this, true);
        File baseFile = new File(basePath + File.separator + EnglishToolProperties.englishSourcePath + File.separator + EnglishToolProperties.newWestListingComprehension);
        allListingList = Arrays.asList(Objects.requireNonNull(baseFile.listFiles()));
        // 适配器
        WrapRecyclerView musicRecyclerView = findViewById(R.id.musicRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(allListingList, this, consumer);
        musicRecyclerView.setLayoutManager(linearLayoutManager);
        musicRecyclerView.addFooterView(LayoutInflater.from(this).inflate(R.layout.place_holder_view2, null));
        musicRecyclerView.setAdapter(this.musicRecyclerViewAdapter);

        musicText = findViewById(R.id.musicText);
        randomMusicSuspend = findViewById(R.id.randomMusicSuspend);
        musicBar = findViewById(R.id.musicBar);
        run2s = findViewById(R.id.run2s);
        run5s = findViewById(R.id.run5s);
        run30s = findViewById(R.id.run30s);
        back2s = findViewById(R.id.back2s);
        back5s = findViewById(R.id.back5s);
        back30s = findViewById(R.id.back30s);
        mediaPlayer.reset();
        timer.schedule(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
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
        // todo 模拟问题
        run2s.setOnClickListener(getChangePosition());
        run5s.setOnClickListener(getChangePosition());
        run30s.setOnClickListener(getChangePosition());
        back2s.setOnClickListener(getChangePosition());
        back5s.setOnClickListener(getChangePosition());
        back30s.setOnClickListener(getChangePosition());
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
        pausePosition = 0;
        randomMusicSuspend.setBackgroundResource(R.drawable.check_flag_word_fillet);
        randomMusicSuspend.setText("暂停播放");
        musicRecyclerViewAdapter.changeBackGroundByID(findIDByFile(file));
    }

    // 根据File对象获取到ID
    private int findIDByFile(File file) {
        for (int i = 0; i < allListingList.size(); i++) {
            // 这里可以直接地址等没有什么关系,反而效率高
            if (allListingList.get(i) == file) {
                return i;
            }
        }
        return 0;
    }


    private View.OnClickListener changePosition = null;

    // 改变当前播放的位置
    private View.OnClickListener getChangePosition() {
        if (changePosition == null) {
            changePosition = v -> {
                int result = mediaPlayer.getCurrentPosition();
                switch (v.getId()) {
                    case R.id.run2s:
                        result += 1000 * 2;
                        break;
                    case R.id.run5s:
                        result += 1000 * 5;
                        break;
                    case R.id.run30s:
                        result += 1000 * 30;
                        break;
                    case R.id.back2s:
                        result -= 1000 * 2;
                        break;
                    case R.id.back5s:
                        result -= 1000 * 5;
                        break;
                    case R.id.back30s:
                        result -= 1000 * 30;
                        break;
                }
                if (result < 0) {
                    result = 0;
                }
                mediaPlayer.seekTo(result);
                int duration = mediaPlayer.getDuration();
                String durationStr = String.format("%d : %d", (duration % (1000 * 60 * 60)) / (1000 * 60), ((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                int currentPosition = mediaPlayer.getCurrentPosition();
                String currentPositionSrt = String.format("%d : %d", (currentPosition % (1000 * 60 * 60)) / (1000 * 60), ((currentPosition % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
                musicBar.setText(currentPositionSrt + "/" + durationStr);
            };
        }
        return changePosition;
    }
}
