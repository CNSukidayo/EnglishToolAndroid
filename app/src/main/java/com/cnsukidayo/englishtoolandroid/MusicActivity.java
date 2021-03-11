package com.cnsukidayo.englishtoolandroid;

import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;

import cn.hutool.core.util.RandomUtil;

public class MusicActivity extends AppCompatActivity {
    // 所有文件
    private Uri[] uris;
    private AsyncPlayer asyncPlayer = new AsyncPlayer("单词音频播放器");
    private AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();
    // 当前正在播放的音乐
    private TextView musicText;
    // 随机播放按钮
    private Button randomMusicPlay;
    // 暂停按钮
    private Button randomMusicStop;

    private int pre = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        String basePath = GetPathUtils.getStoragePath(this, true);
        File baseFile = new File(basePath + File.separator + EnglishToolProperties.musicSourcePath);
        File[] files = baseFile.listFiles();
        uris = new Uri[files.length];
        for (int i = 0; i < files.length; i++) {
            uris[i] = Uri.fromFile(files[i]);
        }
        musicText = findViewById(R.id.musicText);
        randomMusicPlay = findViewById(R.id.randomMusicPlay);
        randomMusicStop = findViewById(R.id.randomMusicStop);
        randomMusicPlay.setOnClickListener(v -> {
            pre = random(0, files.length - 1, pre);
            musicText.setText("当前正在播放:" + files[pre].getName());
            asyncPlayer.play(getApplicationContext(), uris[pre], false, audioAttributes);
        });
        randomMusicStop.setOnClickListener(v -> asyncPlayer.stop());
    }

    private int random(int min, int max, int exclude) {
        int result;
        while (exclude == (result = RandomUtil.randomInt(min, max))) ;
        return result;
    }

}
