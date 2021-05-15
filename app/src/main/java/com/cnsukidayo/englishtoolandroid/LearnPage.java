package com.cnsukidayo.englishtoolandroid;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.learn.LearnPageRecyclerView;
import com.cnsukidayo.englishtoolandroid.core.learn.SearchPoPRecyclerViewAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnPage extends AppCompatActivity {

    private List<Word> allWorlds;
    private List<Word> tempAllWorlds;
    private int current = 0;
    // 内存中寄存当前用户输入的所有单词
    private Map<Integer, Word> playerWriteWordsCache;
    // 当前是标记模式的标记
    private boolean signFlag = false;
    // 当前是否是标记单词混乱模式
    private boolean chaosSignFlag = false;
    // 当前是否是随机区域模式
    private boolean rangeWordFlag = false;
    // 当前的模式状态码
    private StartMod startMod;
    // RecyclerView的Adapter
    private LearnPageRecyclerView learnPageRecyclerView;

    // 返回上一级按钮
    private Button backButton;
    // 保存按钮
    private Button save;
    // 保存现场按钮
    private Button saveScene;
    // 混乱单词按钮
    private Button chaosWord;
    // 强制显示中/英文
    private CheckBox enforceChineseView;
    // 区域随机
    private Button rangeRandom;
    // activityLearnPage外部的Layout
    private LinearLayout learnPage;
    // topBarLayout
    private LinearLayout topBarLinearLayout;
    // 结果回显TableRow
    private TableRow resultTableRow;
    // 检查答案的TableLayout
    private TableLayout checkAnswersTableLayout;
    // 控制单词播放的controllerTableLayout
    private TableRow controllerTableRow;
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
    // 清除英文输入框中的内容
    private Button clearEnglishInput;
    // 搜索弹出的PopWindow
    private PopupWindow searchPopupWindow;
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
    // 临时
    private int status;
    // baseFile
    private File baseFile;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_learn_page);
        RecyclerView chineseInputRecyclerView = findViewById(R.id.chineseInputRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        chineseInputRecyclerView.setLayoutManager(linearLayoutManager);
        learnPageRecyclerView = new LearnPageRecyclerView(this);
        chineseInputRecyclerView.setAdapter(learnPageRecyclerView);

        this.allWorlds = CacheQueue.SINGLE.get("allWords");
        startMod = StartMod.valueOf(getIntent().getExtras().getString(StartMod.class.getName()));
        this.status = getIntent().getExtras().getInt("status");
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
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);
        stopButton = findViewById(R.id.stopButton);
        markThisButton = findViewById(R.id.markThisButton);
        rangeRandom = findViewById(R.id.rangeRandom);
        achievementTextView = findViewById(R.id.achievementTextView);
        startMarkModeButton = findViewById(R.id.startMarkModeButton);
        checkAnswersButton = findViewById(R.id.checkAnswersButton);
        backButton = findViewById(R.id.back);
        save = findViewById(R.id.save);
//        canScrollContainerCheckBox = findViewById(R.id.canScrollContainer);
        resultTableRow = findViewById(R.id.resultTableRow);
        learnPage = findViewById(R.id.learnPage);
        topBarLinearLayout = findViewById(R.id.topBar);
        checkAnswersTableLayout = findViewById(R.id.checkAnswersTableLayout);
        controllerTableRow = findViewById(R.id.controllerTableRow);
        saveScene = findViewById(R.id.saveScene);
        chaosWord = findViewById(R.id.chaosWord);
        enforceChineseView = findViewById(R.id.enforceChineseView);
        // 动态删除组件
        removeViewByStatus();

        backButton.setOnClickListener(v -> {
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
        });
        save.setOnClickListener(v -> {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "basic.json";
            playerWriteWordsCache.put(current, learnPageRecyclerView.getWordFromInPutText());
            for (int i = 0; i < allWorlds.size(); i++) {
                Word tempWord = playerWriteWordsCache.get(i);
                if (tempWord == null || tempWord.noChinese()) {
                    continue;
                }
                Word word = allWorlds.get(i);
                word.setAllChineseMap(tempWord.getAllChineseMap());
            }
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                Gson gson = new Gson();
                String result = gson.toJson(allWorlds);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
//        canScrollContainerCheckBox.setOnClickListener(v -> {
//            learnPageRecyclerView.setCanScrollContainer(canScrollContainerCheckBox.isChecked());
//            onResume();
//        });
        chaosWord.setOnClickListener(v -> {
            chaosSignFlag = !chaosSignFlag;
            if (chaosSignFlag) {
                chaosWord.setTextColor(getResources().getColor(R.color.colorFalseColor));
                chaosWord.setText("关闭混沌模式");
                current = 0;
                tempAllWorlds = allWorlds;
                allWorlds = new ArrayList<>();
                for (Word tempAllWorld : tempAllWorlds) {
                    if (tempAllWorld.isFlag()) {
                        allWorlds.add(tempAllWorld);
                    }
                }
                Collections.shuffle(allWorlds);
                rangeRandom.setEnabled(false);
                rangeRandom.setTextColor(getResources().getColor(R.color.colorNotChooseJsonCheckBoxBlue));
            } else {
                chaosWord.setTextColor(ColorStateList.valueOf(0xFF000000));
                chaosWord.setText("开启混沌模式");
                allWorlds = tempAllWorlds;
                rangeRandom.setEnabled(true);
                rangeRandom.setTextColor(getResources().getColor(R.color.colorBlack));
            }

        });

        enforceChineseView.setOnClickListener(v -> {
            if (startMod == StartMod.CHINESEENGLISHTRANSLATE) {
                startMod = StartMod.DICTATION;
            } else if (startMod == StartMod.DICTATION) {
                startMod = StartMod.CHINESEENGLISHTRANSLATE;
            }
            enforceChineseView.setChecked(startMod.isViewChinese());
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
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(0 < min && max <= allWorlds.size() && min < max)) {
                        Toast.makeText(getApplicationContext(), "输入错误,请输入1~" + allWorlds.size() + "之间的值", Toast.LENGTH_LONG).show();
                    }
                    current = 0;
                    tempAllWorlds = allWorlds;
                    allWorlds = new ArrayList<>(max - min + 1);
                    for (int i = min; i <= max; i++) {
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
            // 设置所有中文
            learnPageRecyclerView.setAnswerLabelTextFromWord(allWorlds.get(current));
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
        // 保存现场
        saveScene.setOnClickListener(v -> {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "scene.json";
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                Gson gson = new Gson();
                String result = gson.toJson(allWorlds);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        // 单词搜索的事件监听
        if (status == 1) {
            input.addTextChangedListener(new TextWatcher() {
                private Map<Character, List<Word>> searchMapCache = new HashMap<Character, List<Word>>(allWorlds.size()) {{
                    put('a', new ArrayList<>());
                    put('b', new ArrayList<>());
                    put('c', new ArrayList<>());
                    put('d', new ArrayList<>());
                    put('e', new ArrayList<>());
                    put('f', new ArrayList<>());
                    put('g', new ArrayList<>());
                    put('h', new ArrayList<>());
                    put('i', new ArrayList<>());
                    put('j', new ArrayList<>());
                    put('k', new ArrayList<>());
                    put('l', new ArrayList<>());
                    put('m', new ArrayList<>());
                    put('n', new ArrayList<>());
                    put('o', new ArrayList<>());
                    put('p', new ArrayList<>());
                    put('q', new ArrayList<>());
                    put('r', new ArrayList<>());
                    put('s', new ArrayList<>());
                    put('t', new ArrayList<>());
                    put('u', new ArrayList<>());
                    put('v', new ArrayList<>());
                    put('w', new ArrayList<>());
                    put('x', new ArrayList<>());
                    put('y', new ArrayList<>());
                    put('z', new ArrayList<>());
                }};

                {
                    for (Word word : allWorlds) {
                        // 获取到第一个字符
                        char firstChar = word.getEnglish().charAt(0);
                        // 拿到该字符所对应的List,并将当前单词添加进去
                        searchMapCache.get(Character.toLowerCase(firstChar)).add(word);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                private boolean enableAnimation = true;

                @Override
                public void afterTextChanged(Editable editable) {
                    String searchText = editable.toString().toLowerCase();
                    if (searchText.isEmpty()) {
                        destroyPoPWindow();
                        return;
                    }

                    // 得到匹配当前当前首字母的List
                    List<Word> matchFirstCharListWord = searchMapCache.get(searchText.charAt(0));
                    if (matchFirstCharListWord == null) {
                        destroyPoPWindow();
                        return;
                    }
                    // 匹配对应的单词
                    List<Word> temp = new ArrayList<>(10);
                    for (Word word : matchFirstCharListWord) {
                        if (word.getEnglish().contains(searchText)) temp.add(word);
                    }
                    if (temp.size() == 0) {
                        destroyPoPWindow();
                        return;
                    }
                    // 通过RelativeLayout+RecyclerView显示出来
                    RelativeLayout layoutSearchPop = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_search_pop, null);
                    RecyclerView searchPopRecyclerView = layoutSearchPop.findViewById(R.id.searchPopRecyclerView);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LearnPage.this) {
                        @Override
                        public boolean canScrollHorizontally() {
                            return false;
                        }
                    };
                    searchPopRecyclerView.setLayoutManager(linearLayoutManager);
                    SearchPoPRecyclerViewAdapter searchPoPRecyclerViewAdapter = new SearchPoPRecyclerViewAdapter(
                            LearnPage.this, temp,
                            word -> {
                                destroyPoPWindow();
                                learnPageRecyclerView.setAnswerLabelTextFromWord(word);
                                asyncPlayer.play(getApplicationContext(), word.getAudioUri(), false, audioAttributes);
                                toPlay = word;
                                englishAnswerTextView.setText(word.getEnglish());
                            });
                    searchPopRecyclerView.setAdapter(searchPoPRecyclerViewAdapter);

                    searchPopupWindow = new PopupWindow(layoutSearchPop, input.getWidth(), 400);
                    searchPopupWindow.setOutsideTouchable(true);
                    if (!enableAnimation) {
                        searchPopupWindow.setAnimationStyle(R.style.showPopupAnimation);
                    }
                    searchPopupWindow.showAsDropDown(input);
                    enableAnimation = false;

                }

                private void destroyPoPWindow() {
                    if (searchPopupWindow != null) {
                        searchPopupWindow.setAnimationStyle(-1);
                        searchPopupWindow.dismiss();
                        enableAnimation = true;
                    }
                }

            });
        }
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
                // 缓存单词PE特有,先缓存当前写好的单词再切换 但是在混乱模式时不可用
                if (!chaosSignFlag) {
                    playerWriteWordsCache.put(current, learnPageRecyclerView.getWordFromInPutText());
                }
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
                if (!chaosSignFlag) {
                    learnPageRecyclerView.setInPutTextFromWord(playerWriteWordsCache.get(current));
                }
                checkSign();
                progressTextView.setText((current + 1) + "/" + allWorlds.size());
                startMod.englishAnswerValueHandle(allWorlds.get(current).getEnglish(), englishAnswerTextView);
                achievementTextView.setText("");
                learnPageRecyclerView.setAnswerLabelTextFromWord(null);
                if (status == 0) {
                    checkAnswersButton.performClick();
                }
            };
        }
        return playClickListener;
    }

    private Word toPlay;

    // 根据当前的状态码删除组件
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void removeViewByStatus() {
        enforceChineseView.setChecked(startMod.isViewChinese());
        playButton.setOnClickListener(getPlayClickListener());
        switch (status) {
            case 2:
                topBarLinearLayout.removeView(save);
                break;
            case 1:
                playButton.setOnClickListener(v -> {
                    if (toPlay != null) {
                        asyncPlayer.play(getApplicationContext(), toPlay.getAudioUri(), false, audioAttributes);
                    }
                });
                progressTextView.setText("总数:" + allWorlds.size());
                controllerTableRow.removeView(findViewById(R.id.preButton));
                controllerTableRow.removeView(findViewById(R.id.nextButton));
                learnPage.removeView(checkAnswersTableLayout);
                topBarLinearLayout.removeView(findViewById(R.id.save));
                input.setHint("搜索");
                break;
        }
    }

    // 寻找下一个标记的单词
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

    // 检查当前单词是否被标记
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
