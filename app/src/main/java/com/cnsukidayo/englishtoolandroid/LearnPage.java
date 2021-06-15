package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.LearnPageRecyclerView;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkModeButtonBackGroundChangeHandler;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkModePopWindow;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkWordButtonBackGroundChangeHandler;
import com.cnsukidayo.englishtoolandroid.actitivesupport.learn.MarkWordPopWindow;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;
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
import java.util.function.Consumer;

public class LearnPage extends AppCompatActivity {

    private List<Word> allWorlds;
    private List<Word> tempAllWorlds;
    private int current = 0;
    // 内存中寄存当前用户输入的所有单词
    private Map<Integer, Word> playerWriteWordsCache;
    // 当前是否是标记单词混乱模式
    private boolean chaosSignFlag = false;
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
    // 临时
    private int status;
    // baseFile
    private File baseFile;
    // 改变单词背景色的处理器
    private MarkWordButtonBackGroundChangeHandler markWordButtonBackGroundChangeHandler;
    // 改变标记模式背景色的处理器
    private MarkModeButtonBackGroundChangeHandler markModeButtonBackGroundChangeHandler;
    // 用单词颜色来作为当前标记模式的,默认是不开启标记模式
    private WordMarkColor nowMarkMode = WordMarkColor.BLACK;

    @SuppressLint("ShowToast")
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
        saveScene = findViewById(R.id.saveScene);
        chaosWord = findViewById(R.id.chaosWord);
        changeMod = findViewById(R.id.changeMod);
        markWordButtonBackGroundChangeHandler = new MarkWordButtonBackGroundChangeHandler(markThisButton);
        markModeButtonBackGroundChangeHandler = new MarkModeButtonBackGroundChangeHandler(startMarkModeButton);
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
        chaosWord.setOnClickListener(v -> {
            chaosSignFlag = !chaosSignFlag;
            if (chaosSignFlag) {
                chaosWord.setTextColor(getResources().getColor(R.color.colorFalseColor));
                chaosWord.setText("关闭混沌模式");
                current = 0;
                tempAllWorlds = allWorlds;
                allWorlds = new ArrayList<>();
                for (Word tempAllWorld : tempAllWorlds) {
                    if (tempAllWorld.getWordMarkColor() == WordMarkColor.RED) {
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

        changeMod.setOnClickListener(v -> {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.change_mod_pop, null);
            TextView preViewEnglish = linearLayout.findViewById(R.id.preViewEnglish);
            if (preViewEnglishFlag) {
                preViewEnglish.setBackgroundResource(R.drawable.pre_view_english_mod_fillet);
                preViewEnglish.setText("预览英文");
            } else {
                preViewEnglish.setBackgroundResource(R.drawable.dispre_view_english_mod_fillet);
                preViewEnglish.setText("不预览英文");
            }
            PopupWindow changeModPopWindow = new PopupWindow(linearLayout, changeMod.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.findViewById(R.id.dictation).setOnClickListener(v1 -> {
                startMod = StartMod.DICTATION;
                changeModPopWindow.dismiss();
            });
            linearLayout.findViewById(R.id.englishChineseTranslate).setOnClickListener(v1 -> {
                startMod = StartMod.ENGLISHCHINESETRANSLATE;
                changeModPopWindow.dismiss();
            });
            linearLayout.findViewById(R.id.chineseEnglishTranslate).setOnClickListener(v1 -> {
                startMod = StartMod.CHINESEENGLISHTRANSLATE;
                changeModPopWindow.dismiss();
            });
            preViewEnglish.setOnClickListener(v12 -> {
                preViewEnglishFlag = !preViewEnglishFlag;
                if (preViewEnglishFlag) {
                    preViewEnglish.setBackgroundResource(R.drawable.pre_view_english_mod_fillet);
                    preViewEnglish.setText("预览英文");
                } else {
                    preViewEnglish.setBackgroundResource(R.drawable.dispre_view_english_mod_fillet);
                    preViewEnglish.setText("不预览英文");
                }
            });
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.showAsDropDown(changeMod);
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
                asyncPlayer.play(getApplicationContext(), allWorlds.get(current).getAudioUri(), false, audioAttributes);
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
        // 解除标记
        markThisButton.setOnClickListener(v -> markWordButtonBackGroundChangeHandler.changeButtonBackGround(WordMarkColor.GREEN));
        // 标记单词
        markThisButton.setOnLongClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.mark_word_color_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, markThisButton.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            MarkWordPopWindow markWordPopWindow = new MarkWordPopWindow(changeMusicModPop, changeModPopWindow, allWorlds.get(current), markWordButtonBackGroundChangeHandler);
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(markThisButton);
            return true;
        });
        // 解除标记模式
        startMarkModeButton.setOnClickListener(v -> markModeButtonBackGroundChangeHandler.changeButtonBackGround(WordMarkColor.GREEN));
        // 开启标记模式
        startMarkModeButton.setOnLongClickListener(v -> {
            LinearLayout changeMusicModPop = (LinearLayout) getLayoutInflater().inflate(R.layout.mark_word_color_pop, null);
            PopupWindow changeModPopWindow = new PopupWindow(changeMusicModPop, startMarkModeButton.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            MarkModePopWindow markWordPopWindow = new MarkModePopWindow(changeMusicModPop, changeModPopWindow, new Consumer<WordMarkColor>() {
                @Override
                public void accept(WordMarkColor wordMarkColor) {
                    nowMarkMode = wordMarkColor;
                }
            }, markModeButtonBackGroundChangeHandler);
            changeModPopWindow.setOutsideTouchable(true);
            changeModPopWindow.setFocusable(true);
            changeModPopWindow.showAsDropDown(markThisButton);
            return true;
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
        englishAnswerTextView.setOnClickListener(v -> {
            Word word = allWorlds.get(current);
            Toast toast = Toast.makeText(getApplicationContext(), "第" + word.getDays() + "天", Toast.LENGTH_SHORT);
            toast.show();
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
                // 缓存单词PE特有,先缓存当前写好的单词再切换 但是在混乱模式时不可用
                if (!chaosSignFlag) {
                    playerWriteWordsCache.put(current, learnPageRecyclerView.getWordFromInPutText());
                }
                switch (v.getId()) {
                    case R.id.preButton:
                        isFirstCheckAnswer = false;
                        if (current == 0) {
                            current = allWorlds.size() - 1;
                        } else {
                            current--;
                        }
                        // todo 这里先写死了
                        find(-1, WordMarkColor.RED);
                        break;
                    case R.id.nextButton:
                        isFirstCheckAnswer = false;
                        if (current == allWorlds.size() - 1) {
                            current = 0;
                        } else {
                            current++;
                        }
                        find(1, WordMarkColor.RED);
                        break;
                }
                // 不管怎样最终都要播放音效
                if (startMod != StartMod.ENGLISHCHINESETRANSLATE) {
                    asyncPlayer.play(getApplicationContext(), allWorlds.get(current).getAudioUri(), false, audioAttributes);
                }
                // 加载新位置的单词
                if (!chaosSignFlag) {
                    learnPageRecyclerView.setInPutTextFromWord(playerWriteWordsCache.get(current));
                }
                markWordButtonBackGroundChangeHandler.changeButtonBackGround(allWorlds.get(current).getWordMarkColor());
                progressTextView.setText((current + 1) + "/" + allWorlds.size());
                // 可以改进
                startMod.englishAnswerValueHandle(allWorlds.get(current), englishAnswerTextView, learnPageRecyclerView);
                achievementTextView.setText("");
                if (startMod != StartMod.ENGLISHCHINESETRANSLATE) {
                    learnPageRecyclerView.setAnswerLabelTextFromWord(null);
                }
                if (status == 0) {
                    checkAnswersButton.performClick();
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
