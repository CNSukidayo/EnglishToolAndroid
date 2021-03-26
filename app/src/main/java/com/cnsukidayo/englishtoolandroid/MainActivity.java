package com.cnsukidayo.englishtoolandroid;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.enums.WordCategory;
import com.cnsukidayo.englishtoolandroid.core.home.HomeListViewAdapter;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;
import com.cnsukidayo.englishtoolandroid.utils.SortUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.Assert;

import static com.cnsukidayo.englishtoolandroid.utils.RandomArrayUtils.randomEleList;

public class MainActivity extends AppCompatActivity {

    private HomeListViewAdapter homeListViewAdapter;
    private Button basic;
    // 听写模式
    private Button dictationModel;
    // 中译英模式
    private Button chineseEnglishTranslationModel;
    // 全选按钮
    private Button allChose;
    // 听音乐按钮
    private Button toMusic;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        final String basePath = GetPathUtils.getStoragePath(this, true);
        final File baseFile = new File(basePath + File.separator + EnglishToolProperties.englishSourcePath);

        // 创建适配器
        this.homeListViewAdapter = new HomeListViewAdapter(this, baseFile);
        ListView homeJsonListView = findViewById(R.id.homeJsonListView);
        homeJsonListView.setAdapter(this.homeListViewAdapter);
        homeJsonListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.start_table_layout, null));

        // 基础词汇按钮
        basic = findViewById(R.id.basic);
        basic.setOnClickListener(view -> {
            File[] allBasicFile = new File(baseFile, File.separator + "basic").listFiles();
            SortUtils.sortWordWithName(allBasicFile);
            CacheQueue.SINGLE.doWork("allWords", () -> {
                String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "basic.json";
                Map<String, Word> parseWords = ParseWordsUtils.parseJsonAndGetWordsWithMap(new File(absolutePath));

                List<Word> allWords = new ArrayList<>(allBasicFile.length);
                for (File file : allBasicFile) {
                    Word word = new Word();
                    word.setAudioPath(file.getAbsolutePath());
                    word.setEnglish(file.getName().substring(0, file.getName().indexOf('.')).replaceAll("[0-9]", ""));
                    word.setDays(-1);
                    word.setCategory(WordCategory.BASIS);
                    word.setAllChineseMap(parseWords.getOrDefault(word.getEnglish(), new Word()).getAllChineseMap());
                    allWords.add(word);
                }
                return allWords;
            });

            Intent intent = new Intent(MainActivity.this, LearnPage.class);
            Bundle bundle = new Bundle();
            bundle.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name());
            bundle.putString("basePath", basePath);
            bundle.putBoolean("flag", true);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        });
        // 听写模式
        dictationModel = findViewById(R.id.dictationModel);
        dictationModel.setOnClickListener(getStartOnClickListener());

        // 中译英模式
        chineseEnglishTranslationModel = findViewById(R.id.chineseEnglishTranslationModel);
        chineseEnglishTranslationModel.setOnClickListener(getStartOnClickListener());
        // 全选按钮
        allChose = findViewById(R.id.allChose);
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
                homeListViewAdapter.changeChoseStatus(flag);

            }
        });
        // 听音乐按钮
        toMusic = findViewById(R.id.toMusic);
        toMusic.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private View.OnClickListener startOnClickListener;
    private boolean flag = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private View.OnClickListener getStartOnClickListener() {
        if (startOnClickListener == null) {
            startOnClickListener = view -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CacheQueue.SINGLE.doWork("allWords", () -> {
                        List<Word> allWords = homeListViewAdapter.getAllCheckWords();
                        if (!flag) {
                            flag = true;
                            return randomEleList(allWords, allWords.size());
                        } else {
                            Collections.shuffle(allWords);
                            return allWords;
                        }
                    });
                }
                try {
                    Assert.isTrue(homeListViewAdapter.assertOneDay(), () -> new RuntimeException("用户没有选择任何一天!"));
                } catch (RuntimeException e) {
                    Toast.makeText(getApplicationContext(), "点击按钮,请至少选择一天!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                // 打乱之后的集合
                Intent intent = new Intent(MainActivity.this, LearnPage.class);
                Bundle bundle = new Bundle();
                switch (view.getId()) {
                    case R.id.dictationModel:
                        bundle.putString(StartMod.class.getName(), StartMod.DICTATION.name());
                        break;
                    case R.id.chineseEnglishTranslationModel:
                        bundle.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name());
                        break;
                }
                bundle.putBoolean("flag", true);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            };
        }
        return startOnClickListener;
    }


}
