package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.ChangePlayModePopWindow;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.ChaosWordPopWindow;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.IncludeWordManager;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.IncludeWordPopWindowHandler;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.LearnPageRecyclerView;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkModeButtonBackGroundChangeHandler;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkModePopWindow;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkWordButtonBackGroundChangeHandler;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkWordPopWindow;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
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
import java.util.Map;
import java.util.function.Consumer;

public class LearnPage extends AppCompatActivity {

    private List<Word> allWorlds;
    private List<Word> tempAllWorlds;
    private int current = 0;
    // 内存中寄存当前用户输入的所有单词
    private Map<String, Word> playerWriteWordsCache;
    // 当前是否是标记单词混乱模式
    // 当前是否是随机区域模式
    private boolean rangeWordFlag = false;
    // 当前的模式状态码
    private StartMod startMod;
    // RecyclerView的Adapter
    private LearnPageRecyclerView learnPageRecyclerView;

    // 返回上一级按钮
    private ImageButton backButton;
    // 保存现场按钮
    private Button saveScene;
    // 混乱单词按钮
    private Button chaosWord;
    // 强制显示中/英文
    private Button changeMod;
    // 预览英文标记
    private boolean preViewEnglishFlag;
    // 是否是第一次查看答案
    private boolean isFirstCheckAnswer;
    // 区域随机
    private Button rangeRandom;
    // 单词归纳
    private Button induceWord;
    // 整体的Toolbar,主要就是用来显示那个PopWindow用的,这个PopWindow应该常驻内存,否则每次创建太浪费资源
    private LinearLayout topBar;
    // 显示功能的Toolbar
    private HorizontalScrollView topBarHorizontalScrollView;
    // 结果回显TextView
    private TextView englishAnswerTextView;
    // 显示正确或错误的结果TextView
    private TextView achievementTextView;
    // 进度
    private TextView progressTextView;
    // 英文输入框
    private EditText input;
    // 清除英文输入框中的内容
    private Button clearEnglishInput;
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
    // baseFile
    private File baseFile;
    // 改变单词背景色的处理器
    private MarkWordButtonBackGroundChangeHandler markWordButtonBackGroundChangeHandler;
    // 改变标记模式背景色的处理器
    private MarkModeButtonBackGroundChangeHandler markModeButtonBackGroundChangeHandler;
    // 用单词颜色来作为当前标记模式的,默认是不开启标记模式
    private WordMarkColor nowMarkMode = WordMarkColor.DEFAULT;
    // 用单词颜色作为当前混沌模式的标记,默认是不开启的
    private WordMarkColor nowChaosMode = WordMarkColor.DEFAULT;
    // 单词归纳PopWindow,常驻内存
    private PopupWindow induceWordPopWindow;
    private RelativeLayout includeWordPopLayout;
    // 处理分类单词的处理器
    private IncludeWordPopWindowHandler includeWordPopWindowHandler;
    // 分类管理器
    private IncludeWordManager includeWordManager;
    // 序列化和反序列化工具
    private Gson gson = new Gson();

    @SuppressLint("ShowToast")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_learn_page);
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

        this.allWorlds = CacheQueue.SINGLE.get("allWords");
        startMod = StartMod.valueOf(getIntent().getExtras().getString(StartMod.class.getName()));
        String baseFilePath = getIntent().getExtras().getString("baseFilePath");
        if (baseFilePath != null) {
            this.baseFile = new File(baseFilePath);
        }
        playerWriteWordsCache = new HashMap<>(100);
        current = 0;

        progressTextView = findViewById(R.id.progressTextView);
        englishAnswerTextView = findViewById(R.id.result);
        input = findViewById(R.id.input);
        clearEnglishInput = findViewById(R.id.clearEnglishInput);
        preButton = findViewById(R.id.preButton);
        playButton = findViewById(R.id.checkButton);
        nextButton = findViewById(R.id.nextButton);
        stopButton = findViewById(R.id.stopButton);
        markThisButton = findViewById(R.id.markThisButton);
        rangeRandom = findViewById(R.id.rangeRandom);
        achievementTextView = findViewById(R.id.achievementTextView);
        startMarkModeButton = findViewById(R.id.startMarkModeButton);
        checkAnswersButton = findViewById(R.id.checkAnswersButton);
        backButton = findViewById(R.id.back);
        saveScene = findViewById(R.id.saveScene);
        chaosWord = findViewById(R.id.chaosWord);
        changeMod = findViewById(R.id.changeMod);
        induceWord = findViewById(R.id.induceWord);
        topBar = findViewById(R.id.topBar);

        topBarHorizontalScrollView = findViewById(R.id.topBarHorizontalScrollView);
        markWordButtonBackGroundChangeHandler = new MarkWordButtonBackGroundChangeHandler(markThisButton);
        markModeButtonBackGroundChangeHandler = new MarkModeButtonBackGroundChangeHandler(startMarkModeButton);
        // 初始化单词归类
        includeWordPopLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.include_word_pop, null);
        // 通过Json反序列化获取
        includeWordManager = getIncludeWordManager();
        // 交给处理器去处理
        includeWordPopWindowHandler = new IncludeWordPopWindowHandler(this, includeWordPopLayout, includeWordManager);
        includeWordPopWindowHandler.setPlayConsumer(this::playMedia);
        includeWordPopWindowHandler.setSaveIncludeRunnable(() -> {
            String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.include;
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                String result = gson.toJson(includeWordManager);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        induceWordPopWindow = new PopupWindow(includeWordPopLayout);
        // 返回
        backButton.setOnClickListener(v -> {
            if (topBarHorizontalScrollView.getVisibility() == View.INVISIBLE) {
                topBarHorizontalScrollView.setVisibility(View.VISIBLE);
                induceWordPopWindow.dismiss();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LearnPage.this);
                builder.setMessage("确认返回主页?");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", (dialog, which) -> {
                    Intent intent = new Intent();
                    setResult(MainActivity.RESULT_OK, intent);
                    finish();
                });
                builder.setNegativeButton("取消", (dialog, which) -> {

                });
                builder.show();
            }
        });
        // 混沌模式
        chaosWord.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.chaos_mode_color_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, chaosWord.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            ChaosWordPopWindow markWordPopWindow = new ChaosWordPopWindow(changeMusicModPop, changeModPopWindow, chaosWord, new Consumer<WordMarkColor>() {
                @Override
                public void accept(WordMarkColor wordMarkColor) {
                    nowChaosMode = wordMarkColor;
                    if (nowChaosMode != WordMarkColor.DEFAULT) {
                        tempAllWorlds = allWorlds;
                        allWorlds = new ArrayList<>();
                        for (Word tempAllWorld : tempAllWorlds) {
                            if (tempAllWorld.getWordMarkColor() == nowChaosMode) {
                                allWorlds.add(tempAllWorld);
                            }
                        }
                        if (allWorlds.size() == 0) {
                            nowChaosMode = WordMarkColor.DEFAULT;
                            allWorlds = tempAllWorlds;
                            chaosWord.setTextColor(chaosWord.getResources().getColor(R.color.colorBlack));
                            Toast.makeText(getApplicationContext(), "没有该种类的单词", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        current = 0;
                        Collections.shuffle(allWorlds);
                        rangeRandom.setEnabled(false);
                        rangeRandom.setTextColor(getResources().getColor(R.color.colorNotChooseJsonCheckBoxBlue));
                    } else {
                        allWorlds = tempAllWorlds;
                        rangeRandom.setEnabled(true);
                        rangeRandom.setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                }
            });
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(chaosWord);
        });
        rangeRandom.setOnClickListener(v -> {
            rangeWordFlag = !rangeWordFlag;
            if (rangeWordFlag) {
                // 得到玩家输入的View
                View rangeRandomWordInputView = getLayoutInflater().inflate(R.layout.range_random_word_input_dialog, null);
                EditText minRange = rangeRandomWordInputView.findViewById(R.id.minRange);
                EditText maxRange = rangeRandomWordInputView.findViewById(R.id.maxRange);
                minRange.setInputType(InputType.TYPE_CLASS_DATETIME);
                maxRange.setInputType(InputType.TYPE_CLASS_DATETIME);
                AlertDialog.Builder builder = new AlertDialog.Builder(LearnPage.this);
                builder.setTitle("区间随机:");
                builder.setMessage("选择要单独随机的区间:[1," + allWorlds.size() + "].注意这里是闭区间");
                builder.setView(rangeRandomWordInputView);
                builder.setCancelable(false);
                builder.setPositiveButton("确定", (dialog, which) -> {
                    int min, max = 0;
                    try {
                        min = Integer.parseInt(minRange.getText().toString());
                        max = Integer.parseInt(maxRange.getText().toString());
                    } catch (NumberFormatException e) {
                        rangeWordFlag = false;
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(0 < min && max <= allWorlds.size() && min < max)) {
                        Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                        rangeWordFlag = false;
                    }
                    current = 0;
                    tempAllWorlds = allWorlds;
                    allWorlds = new ArrayList<>(max - min + 1);
                    for (int i = min - 1; i < max; i++) {
                        allWorlds.add(tempAllWorlds.get(i));
                    }
                    Collections.shuffle(allWorlds);
                    chaosWord.setEnabled(false);
                    chaosWord.setTextColor(getResources().getColor(R.color.colorNotChooseJsonCheckBoxBlue));
                    rangeRandom.setText("还原");
                    rangeRandom.setTextColor(getResources().getColor(R.color.colorLightBlue));
                    playButton.performClick();
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                    rangeWordFlag = false;
                });
                builder.show();
            } else {
                rangeRandom.setTextColor(ColorStateList.valueOf(0xFF000000));
                rangeRandom.setText("区域随机");
                allWorlds = tempAllWorlds;
                chaosWord.setEnabled(true);
                chaosWord.setTextColor(getResources().getColor(R.color.colorBlack));
            }
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
            // 设置所有中文(除非当前是预览英文模式)
            if (!preViewEnglishFlag || isFirstCheckAnswer) {
                learnPageRecyclerView.setAnswerLabelTextFromWord(allWorlds.get(current));
            }
            if (startMod == StartMod.ENGLISHCHINESETRANSLATE) {
                playMedia(allWorlds.get(current));
            }
            isFirstCheckAnswer = true;
        });

        // 点击跳转的按钮
        progressTextView.setOnClickListener(v -> {
            final EditText editText = new EditText(LearnPage.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(LearnPage.this);
            builder.setTitle("跳转到:");
            builder.setMessage("输入要跳转到第几个单词,输入值的区间:[1," + allWorlds.size() + "]");
            builder.setView(editText);
            builder.setCancelable(false);
            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
            builder.setPositiveButton("确定", (dialog, which) -> {
                String value = editText.getText().toString();
                int i;
                try {
                    i = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                    return;
                }
                if (0 < i && i <= allWorlds.size()) {
                    current = i - 1;
                    playButton.performClick();
                } else {
                    Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("取消", (dialog, which) -> {

            });
            builder.show();
        });
        // 清除输入框按钮
        clearEnglishInput.setOnClickListener(v -> input.setText(""));

        preButton.setOnClickListener(getPlayClickListener());
        nextButton.setOnClickListener(getPlayClickListener());
        playButton.setOnClickListener(getPlayClickListener());

        // 更改当前模式
        changeMod.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.change_mod_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, changeMod.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            ChangePlayModePopWindow markWordPopWindow = new ChangePlayModePopWindow(changeMusicModPop, changeModPopWindow,
                    startMod1 -> startMod = startMod1, aBoolean -> preViewEnglishFlag = aBoolean);
            markWordPopWindow.changeIsPreEnglish(preViewEnglishFlag);
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(changeMod);
        });

        // 解除标记
        markThisButton.setOnLongClickListener(v -> {
            markWordButtonBackGroundChangeHandler.changeButtonBackGround(WordMarkColor.DEFAULT);
            return true;
        });
        // 标记单词
        markThisButton.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.mark_word_color_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, markThisButton.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            MarkWordPopWindow markWordPopWindow = new MarkWordPopWindow(changeMusicModPop, changeModPopWindow, allWorlds.get(current), markWordButtonBackGroundChangeHandler);
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(markThisButton);
        });
        // 解除标记模式
        startMarkModeButton.setOnLongClickListener(v -> {
            markModeButtonBackGroundChangeHandler.changeButtonBackGround(WordMarkColor.DEFAULT);
            return true;
        });
        // 开启标记模式
        startMarkModeButton.setOnClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.mark_mode_color_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, startMarkModeButton.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            MarkModePopWindow markWordPopWindow = new MarkModePopWindow(changeMusicModPop, changeModPopWindow, wordMarkColor -> nowMarkMode = wordMarkColor, markModeButtonBackGroundChangeHandler);
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(startMarkModeButton);
        });
        // 保存现场
        saveScene.setOnClickListener(v -> {
            String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + "scene.json";
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                String result = gson.toJson(allWorlds);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        // 单词归纳功能
        induceWord.setOnClickListener(v -> {
            // 现在采用延迟加载
            includeWordPopWindowHandler.init();
            includeWordManager.setToAddWordWordTag(allWorlds.get(current));
            topBarHorizontalScrollView.setVisibility(View.INVISIBLE);
            induceWordPopWindow.setWidth(topBar.getWidth());
            induceWordPopWindow.setHeight(getWindowManager().getDefaultDisplay().getHeight() - topBar.getHeight());
            induceWordPopWindow.showAsDropDown(topBar);
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && topBarHorizontalScrollView.getVisibility() == View.INVISIBLE) {
            topBarHorizontalScrollView.setVisibility(View.VISIBLE);
            induceWordPopWindow.dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(MainActivity.RESULT_OK, intent);
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    private IncludeWordManager getIncludeWordManager() {
        String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.include;
        File includeFile = new File(absolutePath);
        IncludeWordManager result = null;
        if (includeFile.exists()) {
            try {
                result = gson.fromJson(new FileReader(includeFile), IncludeWordManager.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return result;
        }
        result = new IncludeWordManager();
        result.setAllWordInclude(new ArrayList<>());
        return result;
    }

    private void playMedia(Word word) {
        asyncPlayer.play(getApplicationContext(), word.getAudioUri(baseFile.getAbsolutePath()), false, audioAttributes);
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
                boolean changeIndex = false;
                switch (v.getId()) {
                    case R.id.preButton:
                        isFirstCheckAnswer = false;
                        changeIndex = true;
                        if (current == 0) {
                            current = allWorlds.size() - 1;
                        } else {
                            current--;
                        }
                        if (nowMarkMode != WordMarkColor.DEFAULT) {
                            current = find(-1, nowMarkMode);
                        }
                        break;
                    case R.id.nextButton:
                        isFirstCheckAnswer = false;
                        changeIndex = true;
                        if (current == allWorlds.size() - 1) {
                            current = 0;
                        } else {
                            current++;
                        }
                        if (nowMarkMode != WordMarkColor.DEFAULT) {
                            current = find(1, nowMarkMode);
                        }
                        break;
                }
                // 不管怎样最终都要播放音效
                if (startMod != StartMod.ENGLISHCHINESETRANSLATE && !changeIndex) {
                    playMedia(allWorlds.get(current));
                }
                markWordButtonBackGroundChangeHandler.changeButtonBackGround(allWorlds.get(current).getWordMarkColor());
                progressTextView.setText((current + 1) + "/" + allWorlds.size());
                // 可以改进
                startMod.englishAnswerValueHandle(allWorlds.get(current), englishAnswerTextView, learnPageRecyclerView);
                achievementTextView.setText("");
                if (startMod == StartMod.DICTATION || startMod == StartMod.CHINESEENGLISHTRANSLATE) {
                    learnPageRecyclerView.setAnswerLabelTextFromWord(null);
                }
            };
        }
        return playClickListener;
    }

    /**
     * 寻找下一个标记单词
     *
     * @param sign          -1代表先向前找,1代表先向后找
     * @param wordMarkColor 传入当前找哪个颜色的单词
     * @return 返回单词索引
     */
    private int find(int sign, WordMarkColor wordMarkColor) {
        for (int i = current; i < allWorlds.size() && i > -1; i += sign) {
            if (allWorlds.get(i).getWordMarkColor() == wordMarkColor) {
                return i;
            }
        }
        for (int i = sign == 1 ? 0 : allWorlds.size() - 1; i < allWorlds.size() && i > -1; i += sign) {
            if (allWorlds.get(i).getWordMarkColor() == wordMarkColor) {
                return i;
            }
        }
        return current;
    }

}
