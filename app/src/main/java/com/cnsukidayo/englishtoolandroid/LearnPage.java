package com.cnsukidayo.englishtoolandroid;

import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.learn.LearnPageRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnPage extends AppCompatActivity {

    private List<Word> allWorlds;
    private int current = 0;
    // 内存中寄存当前用户输入的所有单词
    private Map<Integer, Word> playerWriteWordsCache;
    // 当前是标记模式的标记
    private boolean signFlag = false;

    // 当前的模式状态码
    private StartMod startMod;
    // RecyclerView的Adapter
    private LearnPageRecyclerView learnPageRecyclerView;

    // 返回上一级按钮
    private Button backButton;
    // 是否允许可变宽度
    private CheckBox canScrollContainerCheckBox;
    // 结果回显TextView
    private TextView englishAnswerTextView;
    // 显示正确或错误的结果TextView
    private TextView achievementTextView;
    // 进度
    private TextView progressTextView;
    // 英文输入框
    private EditText input;
    // 上一个按钮
    private Button preButton;
    // 播放按钮
    private Button playButton;
    // 下一个按钮
    private Button nextButton;
    // 暂停按钮
    private Button stopButton;
    // 检查答案按钮
    private Button checkAnswersButton;
    // 标记单词按钮
    private Button markThisButton;
    // 开启标记模式按钮
    private Button startMarkModeButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_learn_page);
        RecyclerView chineseInputRecyclerView = findViewById(R.id.chineseInputRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chineseInputRecyclerView.setLayoutManager(linearLayoutManager);
        learnPageRecyclerView = new LearnPageRecyclerView(this);
        chineseInputRecyclerView.setAdapter(learnPageRecyclerView);

        this.allWorlds = CacheQueue.SINGLE.get("allWords");
        startMod = StartMod.valueOf(getIntent().getExtras().getString(StartMod.class.getName()));
        playerWriteWordsCache = new HashMap<>(100);
        current = 0;

        checkAnswersButton = findViewById(R.id.checkAnswersButton);
        achievementTextView = findViewById(R.id.achievementTextView);
        progressTextView = findViewById(R.id.progressTextView);
        englishAnswerTextView = findViewById(R.id.result);
        input = findViewById(R.id.input);
        preButton = findViewById(R.id.preButton);
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);
        stopButton = findViewById(R.id.stopButton);
        markThisButton = findViewById(R.id.markThisButton);
        startMarkModeButton = findViewById(R.id.startMarkModeButton);
        backButton = findViewById(R.id.back);
        canScrollContainerCheckBox = findViewById(R.id.canScrollContainer);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(MainActivity.RESULT_OK,intent);
            finish();
        });
        canScrollContainerCheckBox.setOnClickListener(v -> {
            learnPageRecyclerView.setCanScrollContainer(canScrollContainerCheckBox.isChecked());
            onResume();
        });
        stopButton.setOnClickListener(v -> asyncPlayer.stop());

        // 检查结果按钮事件
        checkAnswersButton.setOnClickListener(v -> {
            String answerText = allWorlds.get(current).getEnglish();
            // 设置英文
            englishAnswerTextView.setText(answerText);
            // 比对英文是否正确
            if (input.getText().toString().equals(answerText)) {
                achievementTextView.setText("正确");
                achievementTextView.setTextColor(getResources().getColor(R.color.colorTrueColor));
            } else {
                achievementTextView.setText("错误");
                achievementTextView.setTextColor(getResources().getColor(R.color.colorFalseColor));
            }
            // 设置所有中文
            learnPageRecyclerView.setAnswerLabelTextFromWord(allWorlds.get(current));
        });

        preButton.setOnClickListener(getPlayClickListener());
        playButton.setOnClickListener(getPlayClickListener());
        nextButton.setOnClickListener(getPlayClickListener());

        markThisButton.setOnClickListener(v -> {
            allWorlds.get(current).negationFlag();
            checkSign();
        });

        startMarkModeButton.setOnClickListener(v -> {
            signFlag = !signFlag;
            if (signFlag) {
                startMarkModeButton.setText("关闭标记模式");
                startMarkModeButton.setTextColor(getResources().getColor(R.color.colorStartFlagMode));
            } else {
                startMarkModeButton.setText("开启标记模式");
                startMarkModeButton.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

    }

    private View.OnClickListener playClickListener = null;
    // 单词音频播放器,有写好的类
    private AsyncPlayer asyncPlayer = new AsyncPlayer("单词音频播放器");
    private AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getPlayClickListener() {
        if (playClickListener == null) {
            playClickListener = v -> {
                // 缓存单词PE特有,先缓存当前写好的单词再切换
                playerWriteWordsCache.put(current, learnPageRecyclerView.getWordFromInPutText());
                switch (v.getId()) {
                    case R.id.preButton:
                        if (current == 0) {
                            current = allWorlds.size() - 1;
                        } else {
                            current--;
                        }

                        if (signFlag) {
                            current = find(-1);
                        }
                        break;
                    case R.id.nextButton:
                        if (current == allWorlds.size() - 1) {
                            current = 0;
                        } else {
                            current++;
                        }
                        if (signFlag) {
                            current = find(1);
                        }
                        break;
                }
                // 不管怎样最终都要播放音效
                asyncPlayer.play(getApplicationContext(), allWorlds.get(current).getAudioUri(), false, audioAttributes);
                // 加载新位置的单词
                learnPageRecyclerView.setInPutTextFromWord(playerWriteWordsCache.get(current));
                checkSign();
                progressTextView.setText((current + 1) + "/" + allWorlds.size());
                startMod.englishAnswerValueHandle(allWorlds.get(current).getEnglish(), englishAnswerTextView);
                achievementTextView.setText("");
                learnPageRecyclerView.setAnswerLabelTextFromWord(null);
            };
        }
        return playClickListener;
    }

    private int find(int sign) {
        for (int i = current; i < allWorlds.size() && i > -1; i += sign) {
            if (allWorlds.get(i).isFlag()) {
                return i;
            }
        }
        for (int i = sign == 1 ? 0 : allWorlds.size() - 1; i < allWorlds.size() && i > -1; i += sign) {
            if (allWorlds.get(i).isFlag()) {
                return i;
            }
        }
        return current;
    }

    private void checkSign() {
        if (allWorlds.get(current).isFlag()) {
            markThisButton.setText("解除标记");
            markThisButton.setBackgroundResource(R.drawable.check_flag_word_fillet);
        } else {
            markThisButton.setText("标记单词");
            markThisButton.setBackgroundResource(R.drawable.flag_word_fillet);
        }
    }


}
