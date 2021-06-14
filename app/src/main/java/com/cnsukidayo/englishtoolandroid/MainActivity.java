package com.cnsukidayo.englishtoolandroid;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.cache.CacheQueue;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.StartMod;
import com.cnsukidayo.englishtoolandroid.core.home.HomeRecyclerViewAdapter;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;
import com.cnsukidayo.englishtoolandroid.utils.ParseWordsUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

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
    // 听音乐按钮
    private Button toMusic;

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
            // todo 问题是现在不能保存用户的单词的英文输入
            CacheQueue.SINGLE.doWork("allWords", () -> {
                String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "scene.json";
                List<Word> allWords = ParseWordsUtils.parseJsonAndGetWordsWithList(new File(absolutePath));
                return allWords;
            });
            Intent intent = new Intent(MainActivity.this, LearnPage.class);
            Bundle bundle = new Bundle();
            bundle.putString(StartMod.class.getName(), StartMod.DICTATION.name());
            bundle.putInt("status", 2);
            bundle.putString("baseFilePath", baseFile.getAbsolutePath());
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
        });
        // 搜搜单词
        searchWord = startTableLayout.findViewById(R.id.searchWord);
        searchWord.setOnClickListener(v -> {
            // todo 这和地方写的有问题
            CacheQueue.SINGLE.doWork("allWords", () -> homeRecyclerViewAdapter.getAllWords());
            Intent intent = new Intent(MainActivity.this, LearnPage.class);
            Bundle bundle = new Bundle();
            bundle.putString(StartMod.class.getName(), StartMod.CHINESEENGLISHTRANSLATE.name());
            bundle.putInt("status", 1);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        });
        // 听写模式
        dictationModel = startTableLayout.findViewById(R.id.dictationModel);
        dictationModel.setOnClickListener(getStartOnClickListener());

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
        // 听音乐按钮
        toMusic = startTableLayout.findViewById(R.id.toMusic);
        toMusic.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivityForResult(intent, 3);
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
                bundle.putInt("status", 2);
                bundle.putString("baseFilePath", baseFile.getAbsolutePath());
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            };
        }
        return startOnClickListener;
    }


}
