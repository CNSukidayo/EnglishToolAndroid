package com.cnsukidayo.englishtoolandroid;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.home.HomeListViewAdapter;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;

public class MainActivity extends AppCompatActivity {

    private HomeListViewAdapter homeListViewAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        String basePath = GetPathUtils.getStoragePath(this, true);
        File baseFile = new File(basePath + File.separator + EnglishToolProperties.sourcePath);

        // 创建适配器
        this.homeListViewAdapter = new HomeListViewAdapter(this, baseFile);
        ListView homeJsonListView = findViewById(R.id.homeJsonListView);
        homeJsonListView.setAdapter(this.homeListViewAdapter);
        homeJsonListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.start_table_layout, null));

        //

        // 中译英模式
        Button chineseEnglishTranslationModel = findViewById(R.id.chineseEnglishTranslationModel);
        chineseEnglishTranslationModel.setOnClickListener(v -> {
            List<Word> allWords = homeListViewAdapter.getAllCheckWords();
            // 打乱之后的集合
            ArrayList<Word> randomList = randomEleList(allWords, allWords.size());
            Intent intent = new Intent(MainActivity.this, LearnPage.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("randomList", randomList);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        // 全选按钮
        Button allChose = findViewById(R.id.allChose);
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

    }

    private <T> ArrayList<T> randomEleList(List<T> source, int count) {
        final int[] randomList = ArrayUtil.sub(RandomUtil.randomInts(source.size()), 0, count);
        ArrayList<T> result = new ArrayList<>();
        for (int e : randomList) {
            result.add(source.get(e));
        }
        return result;
    }


}
