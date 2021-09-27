package com.cnsukidayo.englishtoolandroid;

import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.LearnPageRecyclerView;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.Imagination;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ImaginationMode extends AppCompatActivity {

    // 基础路径
    private String baseFilePath;
    private File baseFile;
    // RecyclerView的Adapter
    private LearnPageRecyclerView learnPageRecyclerView;

    // 单词音频播放器,有写好的类
    private AsyncPlayer asyncPlayer = new AsyncPlayer("单词音频播放器");
    private AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();

    private Gson gson = new Gson();
    private Imagination imagination;
    // 检查+播放按钮
    private Button checkButton;
    // 播放按钮
    private Button playButton;
    // 输入框
    private EditText input;
    // 当前单词的引用
    private Word current;
    private Word current2;
    // 清除输入框
    private Button clearEnglishInput;
    // 正确库按钮
    private Button rightButton;
    // 错误库按钮
    private Button errorButton;
    // 随机一个
    private Button randomButton;
    // 顺序背诵的某个库
    private List<Word> oneList = null;
    // 索引
    private int index = 0;
    // 上一个按钮
    private Button preButton;
    // 下一个按钮
    private Button nextButton;
    // 只有正确
    private Button onlyRight;
    // 只有错误
    private Button onlyError;
    // 只有被动
    private Button onlyPassive;
    // 进度
    private TextView progressView;
    // 保存进度
    private Button saveProgress;
    // 四个库的size
    private TextView allPool;
    private TextView rightPool;
    private TextView errorPool;
    private TextView passivePool;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imagination_mode);
        baseFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("baseFilePath");
        baseFile = new File(baseFilePath);
        WrapRecyclerView chineseInputRecyclerView = findViewById(R.id.chineseDisplayRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        chineseInputRecyclerView.setLayoutManager(linearLayoutManager);
        LinearLayout supplement = (LinearLayout) this.getLayoutInflater().inflate(R.layout.supplement_learn_view, null);
        learnPageRecyclerView = new LearnPageRecyclerView(this, supplement);
        chineseInputRecyclerView.addFooterView(supplement);
        chineseInputRecyclerView.setAdapter(learnPageRecyclerView);

        playButton = findViewById(R.id.playButton);
        checkButton = findViewById(R.id.checkButton);
        input = findViewById(R.id.input);
        clearEnglishInput = findViewById(R.id.clearEnglishInput);
        allPool = findViewById(R.id.allPool);
        rightPool = findViewById(R.id.rightPool);
        errorPool = findViewById(R.id.errorPool);
        passivePool = findViewById(R.id.passivePool);
        rightButton = findViewById(R.id.rightButton);
        errorButton = findViewById(R.id.errorButton);
        randomButton = findViewById(R.id.randomButton);
        preButton = findViewById(R.id.preButton);
        nextButton = findViewById(R.id.nextButton);
        onlyRight = findViewById(R.id.onlyRight);
        onlyError = findViewById(R.id.onlyError);
        onlyPassive = findViewById(R.id.onlyPassive);
        progressView = findViewById(R.id.progressView);
        saveProgress = findViewById(R.id.saveProgress);
        imagination = getImagination();
        // 查看结果
        checkButton.setOnClickListener(v -> {
            if (current2 != null) {
                learnPageRecyclerView.setAnswerLabelTextFromWord(current2);
                playMedia(current2);
                current2 = null;
                return;
            }
            if (current != null) {
                Toast.makeText(getApplicationContext(), "当前单词还没有添加到库中", Toast.LENGTH_LONG).show();
                return;
            }
            String inputText = input.getText().toString();
            Word word = imagination.getAllWorld().get(inputText);
            updatePool();
            if (word != null) {
                current = word;
                learnPageRecyclerView.setAnswerLabelTextFromWord(word);
                playMedia(word);
            } else {
                Toast.makeText(getApplicationContext(), "没有匹配到单词", Toast.LENGTH_LONG).show();
            }
        });
        // 清除输入框
        clearEnglishInput.setOnClickListener(v -> {
            input.setText("");
        });
        // 正确库
        rightButton.setOnClickListener(v -> {
            if (current != null) {
                Word remove = imagination.getAllWorld().remove(current.getEnglish());
                if (remove != null) {
                    imagination.getActive().put(current.getEnglish(), current);
                    imagination.getActiveList().add(current);
                    current = null;
                    updatePool();
                }
            }
        });
        // 错误库
        errorButton.setOnClickListener(v -> {
            if (current != null) {
                Word remove = imagination.getAllWorld().remove(current.getEnglish());
                if (remove != null) {
                    imagination.getErr().put(current.getEnglish(), current);
                    imagination.getErrList().add(current);
                    current = null;
                    updatePool();
                }
            }
        });
        // 只有随机出来的单词是直接加到被动库里的
        randomButton.setOnClickListener(v -> {
            current2 = imagination.getAllWorld().values().iterator().next();
            imagination.getAllWorld().remove(current2.getEnglish());
            imagination.getPassive().put(current2.getEnglish(), current2);
            imagination.getPassiveList().add(current2);
            learnPageRecyclerView.setAnswerLabelTextFromWord(null);
            input.setText(current2.getEnglish());
            updatePool();
        });
        // 上一个按钮
        preButton.setOnClickListener(v -> {
            if (oneList != null) {
                if (index == 0) {
                    index = oneList.size() - 1;
                } else {
                    index--;
                }
                progressView.setText(index + 1 + "/" + oneList.size());
                Word word = oneList.get(index);
                learnPageRecyclerView.setAnswerLabelTextFromWord(word);
                input.setText(word.getEnglish());
                playMedia(word);
            }
        });
        // 下一个按钮
        nextButton.setOnClickListener(v -> {
            if (oneList != null) {
                if (index == oneList.size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
                progressView.setText(index + 1 + "/" + oneList.size());
                Word word = oneList.get(index);
                learnPageRecyclerView.setAnswerLabelTextFromWord(word);
                input.setText(word.getEnglish());
                playMedia(word);
            }
        });
        onlyRight.setOnClickListener(v -> {
            index = 0;
            oneList = imagination.getActiveList();
        });
        onlyError.setOnClickListener(v -> {
            index = 0;
            oneList = imagination.getErrList();
        });
        onlyPassive.setOnClickListener(v -> {
            index = 0;
            oneList = imagination.getPassiveList();
        });
        saveProgress.setOnClickListener(v -> {
            String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.imagination;
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                String result = gson.toJson(imagination);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        // 播放音频
        playButton.setOnClickListener(v -> {
            if (current != null) {
                playMedia(current);
            }
            if (current2 != null) {
                playMedia(current2);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Imagination getImagination() {
        Imagination result;
        String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.imagination;
        File imaginationFile = new File(absolutePath);
        if (imaginationFile.exists()) {
            try {
                return gson.fromJson(new FileReader(imaginationFile), Imagination.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        result = new Imagination();
        result.setErr(new HashMap<>());
        result.setPassive(new HashMap<>());
        result.setActive(new HashMap<>());
        result.setAllWorld(new HashMap<>());
        result.setActiveList(new ArrayList<>());
        result.setPassiveList(new ArrayList<>());
        result.setErrList(new ArrayList<>());
        File englishJsonFile = new File(baseFilePath, EnglishToolProperties.json);
        // 不包含supplement.json
        File[] files = englishJsonFile.listFiles((dir, name) -> !"supplement.json".equals(name));
        List<Word> allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(Objects.requireNonNull(files));
        // 打乱
        Collections.shuffle(allWords);
        allWords.forEach(word -> result.getAllWorld().put(word.getEnglish(), word));
        return result;
    }

    private void playMedia(Word word) {
        asyncPlayer.play(getApplicationContext(), word.getAudioUri(baseFile.getAbsolutePath()), false, audioAttributes);
    }

    /**
     * 更新当前的库
     */
    private void updatePool() {
        allPool.setText("总库剩余:" + imagination.getAllWorld().size());
        rightPool.setText("正确库数量:" + imagination.getActive().size());
        errorPool.setText("错题库数量:" + imagination.getErr().size());
        passivePool.setText("被动库数量:" + imagination.getPassive().size());
    }

}
