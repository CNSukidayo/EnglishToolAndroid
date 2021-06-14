package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.learn.LearnPageRecyclerView;
import com.cnsukidayo.englishtoolandroid.core.learn.SearchPoPRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchWord extends AppCompatActivity {
    // 基础路径
    private String baseFilePath;
    // RecyclerView的Adapter
    private LearnPageRecyclerView learnPageRecyclerView;
    // 所有单词List
    private List<Word> allWords;
    // 单词输入框
    private TextView input;
    // 搜索弹出的PopWindow
    private PopupWindow searchPopupWindow;
    private AsyncPlayer asyncPlayer = new AsyncPlayer("单词音频播放器");
    private AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();
    // 结果回显TextView
    private TextView englishAnswerTextView;
    // 播放
    private Button playButton;
    // 暂停
    private Button stopButton;
    // 清除
    private Button clearEnglishInput;

    // 上一个播放的单词
    private Word prePlayWord;
    // 第几天
    private TextView day;

    @SuppressLint("ShowToast")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seach_word);
        baseFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("baseFilePath");
        input = findViewById(R.id.input);
        englishAnswerTextView = findViewById(R.id.result);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        clearEnglishInput = findViewById(R.id.clearEnglishInput);
        day = findViewById(R.id.day);
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

        File englishJsonFile = new File(baseFilePath, EnglishToolProperties.json);
        allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(Objects.requireNonNull(englishJsonFile.listFiles()));
        ((TextView) findViewById(R.id.progressTextView)).setText("总数:" + allWords.size());
        // 设置监听事件
        setInputChangeEvent();
        playButton.setOnClickListener(v -> {
            if (prePlayWord != null) {
                asyncPlayer.play(getApplicationContext(), prePlayWord.getAudioUri(baseFilePath), false, audioAttributes);
            }
        });
        stopButton.setOnClickListener(v -> asyncPlayer.stop());
        clearEnglishInput.setOnClickListener(v -> input.setText(""));
        englishAnswerTextView.setOnLongClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(englishAnswerTextView.getText());
            Toast.makeText(this, "以复制到剪切板", Toast.LENGTH_SHORT);
            return true;
        });
    }

    // 无动画
    private boolean enableAnimation = true;
    // 缓存搜索
    private Map<Character, List<Word>> searchMapCache;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setInputChangeEvent() {
        searchMapCache = new HashMap<Character, List<Word>>(allWords.size()) {{
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
        TextWatcher watcher = new TextWatcher() {

            {
                for (Word word : allWords) {
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().toLowerCase();
                if (searchText.isEmpty()) {
                    destroyPoPWindow();
                    return;
                }
                showPopWindow(searchText, false);
            }

        };
        input.addTextChangedListener(watcher);
        input.setOnClickListener(v -> {
            // 这个需要自已来控制是否显示出来
            if (input.getText().toString() != null && searchPopupWindow != null && !searchPopupWindow.isShowing()) {
                showPopWindow(input.getText().toString(), false);
            } else if (searchPopupWindow != null) {
                searchPopupWindow.dismiss();
            }
        });
    }

    /**
     * 展示搜索出的单词
     *
     * @param searchText 搜索的文本
     * @param isBF       是否暴风搜索
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showPopWindow(String searchText, boolean isBF) {
        // 得到匹配当前当前首字母的List
        // todo 第几天
        List<Word> matchListWord;
        if (!isBF) {
            matchListWord = searchMapCache.get(searchText.charAt(0));
        } else {
            matchListWord = allWords;
        }
        if (matchListWord == null) {
            destroyPoPWindow();
            return;
        }
        // 匹配对应的单词
        List<Word> temp = new ArrayList<>(10);
        for (Word word : matchListWord) {
            if (word.getEnglish().contains(searchText)) temp.add(word);
        }

        // 通过RelativeLayout+RecyclerView显示出来
        RelativeLayout layoutSearchPop = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_search_pop, null);
        WrapRecyclerView searchPopRecyclerView = layoutSearchPop.findViewById(R.id.searchPopRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchWord.this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        searchPopRecyclerView.setLayoutManager(linearLayoutManager);
        @SuppressLint("SetTextI18n") SearchPoPRecyclerViewAdapter searchPoPRecyclerViewAdapter = new SearchPoPRecyclerViewAdapter(
                SearchWord.this, temp,
                word -> {
                    day.setText(Integer.toString(word.getDays()));
                    prePlayWord = word;
                    destroyPoPWindow();
                    learnPageRecyclerView.setAnswerLabelTextFromWord(word);
                    asyncPlayer.play(getApplicationContext(), word.getAudioUri(baseFilePath), false, audioAttributes);
                    englishAnswerTextView.setText(word.getEnglish());
                });
        // 如果不是暴风搜索就添加展开的按钮,让用户可以展开搜搜
        if (!isBF) {
            View openSearchLayOut = this.getLayoutInflater().inflate(R.layout.activity_search_pop_open, null);
            openSearchLayOut.setOnClickListener(v -> showPopWindow(searchText, true));
            TextView openSearch = openSearchLayOut.findViewById(R.id.openSearch);
            openSearch.setOnClickListener(v -> {
                openSearch.setText("正在搜索...");
                showPopWindow(searchText, true);
            });
            searchPopRecyclerView.addFooterView(openSearchLayOut);
        }
        searchPopRecyclerView.setAdapter(searchPoPRecyclerViewAdapter);

        searchPopupWindow = new PopupWindow(layoutSearchPop, input.getWidth(), 400);
        searchPopupWindow.setOutsideTouchable(true);
        if (!enableAnimation) {
            searchPopupWindow.setAnimationStyle(R.style.showPopupAnimation);
        }
        searchPopupWindow.showAsDropDown(input);
        enableAnimation = false;

    }

    // 销毁弹出的PopWindow
    private void destroyPoPWindow() {
        if (searchPopupWindow != null) {
            searchPopupWindow.setAnimationStyle(-1);
            searchPopupWindow.dismiss();
            enableAnimation = true;
        }
    }

}
