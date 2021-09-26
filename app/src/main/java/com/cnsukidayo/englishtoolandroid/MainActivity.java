package com.cnsukidayo.englishtoolandroid;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.actitivesupport.home.HomeRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;

import static com.cnsukidayo.englishtoolandroid.utils.RandomArrayUtils.randomEleList;

public class MainActivity extends AppCompatActivity {

    private HomeRecyclerViewAdapter homeRecyclerViewAdapter;
    // 还原现场
    private LinearLayout restoreTheScene;
    // 搜搜单词
    private Button searchWord;
    // 听写模式
    private Button dictationModel;
    // 中译英模式
    private Button chineseEnglishTranslationModel;
    // 全选按钮
    private Button allChose;
    // 顺序背诵按钮
    private Button orderRecite;
    // 介词短语背诵按钮
    private Button PREPPhraseRecite;
    // 计时模式
    private Button timeRecord;
    // 联想模式
    private Button imaginationMode;
    // 听音乐按钮
    private Button toMusic;
    // 控制电脑按钮
    private Button controlComputer;

    private File baseFile;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        final String basePath = GetPathUtils.getStoragePath(this, true);
        baseFile = new File(basePath + File.separator + EnglishToolProperties.englishSourcePath);

        WrapRecyclerView homeJsonRecyclerView = findViewById(R.id.homeJsonRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(baseFile, this);
        homeJsonRecyclerView.setLayoutManager(linearLayoutManager);
        View startTableLayout = this.getLayoutInflater().inflate(R.layout.start_table_layout, null);
        homeJsonRecyclerView.addFooterView(startTableLayout);
        homeJsonRecyclerView.setAdapter(this.homeRecyclerViewAdapter);

        // 还原现场
        restoreTheScene = startTableLayout.findViewById(R.id.restoreTheScene);
        restoreTheScene.setOnClickListener(v -> {
            CacheQueue.SINGLE.doWork("allWords", () -> {
                String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + "scene.json";
                List<Word> allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(new File(absolutePath));
                return allWords;
            });
            startActivityForResult(getIntent(LearnPage.class, bundle1 -> () -> bundle1.putString(StartMod.class.getName(), StartMod.DICTATION.name())), 2);
        });
        // 搜搜单词
        searchWord = startTableLayout.findViewById(R.id.searchWord);
        searchWord.setOnClickListener(v -> startActivityForResult(getIntent(SearchWord.class, null), 1));
        // 听写模式
        dictationModel = startTableLayout.findViewById(R.id.dictationModel);
        dictationModel.setOnClickListener(getStartOnClickListener());
        // 顺序背诵
        orderRecite = startTableLayout.findViewById(R.id.orderRecite);
        orderRecite.setOnClickListener(v -> {
            CacheQueue.SINGLE.doWork("allWords", () -> {
                File englishJsonFile = new File(baseFile, EnglishToolProperties.json);
                // 不包含supplement.json
                File[] files = englishJsonFile.listFiles((dir, name) -> !"supplement.json".equals(name));
                List<Word> allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(Objects.requireNonNull(files));
                allWords.sort((o1, o2) -> {
                    char[] chars1 = o1.getEnglish().toLowerCase().toCharArray();
                    char[] chars2 = o2.getEnglish().toLowerCase().toCharArray();
                    for (int i = 0; i < Math.min(chars1.length, chars2.length); i++) {
                        int result = chars1[i] - chars2[i];
                        if (result != 0) {
                            return result;
                        }
                    }
                    return chars1.length - chars2.length;
                });
                return allWords;
            });
            startActivityForResult(getIntent(LearnPage.class, bundle1 -> () -> bundle1.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name())), 2);
        });
        // 所有介词短语
        PREPPhraseRecite = startTableLayout.findViewById(R.id.PREPPhraseRecite);
        PREPPhraseRecite.setOnClickListener(v -> {
            CacheQueue.SINGLE.doWork("allWords", () -> {
                File englishJsonFile = new File(baseFile, EnglishToolProperties.json);
                List<Word> allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(Objects.requireNonNull(englishJsonFile.listFiles()));
                List<Word> collect = allWords.stream().filter(word -> word.getPREPPhrase() != null && !word.getPREPPhrase().isEmpty()).collect(Collectors.toList());
                Collections.shuffle(collect);
                return collect;
            });
            startActivityForResult(getIntent(LearnPage.class, bundle1 -> () -> bundle1.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name())), 2);
        });
        // 中译英模式
        chineseEnglishTranslationModel = startTableLayout.findViewById(R.id.chineseEnglishTranslationModel);
        chineseEnglishTranslationModel.setOnClickListener(getStartOnClickListener());
        // 全选按钮
        allChose = startTableLayout.findViewById(R.id.allChose);
        allChose.setOnClickListener(new View.OnClickListener() {
            // true显示全不选 false显示全选
            private boolean flag = false;

            @Override
            public void onClick(View v) {

                flag = !flag;
                if (flag) {
                    allChose.setText("全不选");
                } else {
                    allChose.setText("全选");
                }
                homeRecyclerViewAdapter.changeChoseStatus(flag);

            }
        });
        // 计时模式
        timeRecord = startTableLayout.findViewById(R.id.timeRecord);
        timeRecord.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TimeRecordActivity.class);
            startActivityForResult(intent, 5);
        });
        // 联想模式
        imaginationMode = startTableLayout.findViewById(R.id.imaginationMode);
        imaginationMode.setOnClickListener(v -> startActivityForResult(getIntent(ImaginationMode.class, null), 6));
        // 听音乐按钮
        toMusic = startTableLayout.findViewById(R.id.toMusic);
        toMusic.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivityForResult(intent, 3);
        });
        // 控制电脑按钮
        controlComputer = startTableLayout.findViewById(R.id.controlComputer);
        controlComputer.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ControllerComputerActivity.class);
            startActivityForResult(intent, 4);
        });
    }

    private View.OnClickListener startOnClickListener;
    private boolean noFirstRandom = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getStartOnClickListener() {
        if (startOnClickListener == null) {
            startOnClickListener = view -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CacheQueue.SINGLE.doWork("allWords", () -> {
                        List<Word> allWords = homeRecyclerViewAdapter.getAllCheckWords();
                        if (!noFirstRandom) {
                            noFirstRandom = true;
                            return randomEleList(allWords, allWords.size());
                        } else {
                            Collections.shuffle(allWords);
                            return allWords;
                        }
                    });
                }
                try {
                    Assert.isTrue(homeRecyclerViewAdapter.assertOneDay(), () -> new RuntimeException("用户没有选择任何一天!"));
                } catch (RuntimeException e) {
                    Toast.makeText(getApplicationContext(), "点击按钮,请至少选择一天!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                // 打乱之后的集合
                startActivityForResult(getIntent(LearnPage.class, bundle -> () -> {
                    switch (view.getId()) {
                        case R.id.dictationModel:
                            bundle.putString(StartMod.class.getName(), StartMod.DICTATION.name());
                            break;
                        case R.id.chineseEnglishTranslationModel:
                            bundle.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name());
                            break;
                    }
                }), 2);
            };
        }
        return startOnClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Intent getIntent(Class cls, Function<Bundle, Runnable> function) {
        Intent intent = new Intent(MainActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putString("baseFilePath", baseFile.getAbsolutePath());
        if (function != null) {
            function.apply(bundle).run();
        }
        intent.putExtras(bundle);
        return intent;
    }

}
