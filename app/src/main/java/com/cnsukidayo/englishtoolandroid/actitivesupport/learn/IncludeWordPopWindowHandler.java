package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.entitys.WordInclude;
import com.cnsukidayo.englishtoolandroid.myview.WrapRecyclerView;

import java.util.function.Consumer;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class IncludeWordPopWindowHandler {
    // 上下文
    private Context context;
    private LayoutInflater layoutInflater;
    private RelativeLayout includeWordPopLayout;
    private WrapRecyclerView induceWordWrapRecyclerView;
    // 添加新分类按钮
    private TextView addNewGroup;
    // 分类管理器
    private IncludeWordManager includeWordManager;
    private Consumer<Word> consumer;
    public IncludeWordPopWindowHandler(Context context, RelativeLayout includeWordPopLayout, IncludeWordManager includeWordManager) {
        this.includeWordPopLayout = includeWordPopLayout;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.includeWordManager = includeWordManager;
        init();
        event();
    }

    private void init() {
        induceWordWrapRecyclerView = includeWordPopLayout.findViewById(R.id.induceWordWrapRecyclerView);
        addNewGroup = includeWordPopLayout.findViewById(R.id.addNewGroup);
        induceWordWrapRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        // 不停的变动
        // 添加一个白块用于消除上下差别
        induceWordWrapRecyclerView.addFooterView(layoutInflater.inflate(R.layout.place_holder_view, null));
        updateInduceWordWrapRecyclerView();
    }

    private void event() {
        // 添加新的分组
        addNewGroup.setOnClickListener(v -> {
            View addNewIncludeInputDialog = layoutInflater.inflate(R.layout.add_new_include_input_dialog, null);
            EditText newTitle = addNewIncludeInputDialog.findViewById(R.id.newTitle);
            CheckBox defaultTitle = addNewIncludeInputDialog.findViewById(R.id.defaultTitle);
            EditText newDescribe = addNewIncludeInputDialog.findViewById(R.id.newDescribe);
            CheckBox defaultDescribe = addNewIncludeInputDialog.findViewById(R.id.defaultDescribe);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(addNewIncludeInputDialog);
            builder.setCancelable(false);
            // 控制键盘回收
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            builder.setPositiveButton("确定", (dialog, which) -> {
                WordInclude wordInclude = WordInclude.getWordIncludeInstance();
                String title = newTitle.getText().toString();
                String describe = newDescribe.getText().toString();
                wordInclude.setTitle(title);
                wordInclude.setDescribe(describe);
                if (title.length() != 0 || !defaultTitle.isChecked()) {
                    wordInclude.setDefaultTitle(false);
                }
                if (describe.length() != 0 || !defaultDescribe.isChecked()) {
                    wordInclude.setDefaultDescribe(false);
                }
                includeWordManager.addNewInclude(wordInclude);
                updateInduceWordWrapRecyclerView();
                inputMethodManager.hideSoftInputFromWindow(newTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.hideSoftInputFromWindow(newDescribe.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            });
            builder.setNegativeButton("取消", (dialog, which) -> {
                inputMethodManager.hideSoftInputFromWindow(newTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.hideSoftInputFromWindow(newDescribe.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            });
            builder.show();
        });
    }

    private void updateInduceWordWrapRecyclerView() {
        IncludePopRecyclerViewAdapter adapter = new IncludePopRecyclerViewAdapter(context, includeWordManager, this::updateInduceWordWrapRecyclerView);
        adapter.setPlayConsumer(consumer);
        induceWordWrapRecyclerView.setAdapter(adapter);
    }

    public void setPlayConsumer(Consumer<Word> consumer) {
        this.consumer = consumer;
    }
}
