package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.entitys.WordInclude;

import java.util.function.Consumer;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class IncludePopRecyclerViewAdapter extends RecyclerView.Adapter<IncludePopRecyclerViewAdapter.LinearViewHolder> {

    private final LayoutInflater layoutInflater;
    // 分类管理器
    private final IncludeWordManager includeWordManager;
    // 刷新列表,丢到外部处理
    private final Runnable refreshList;
    private final Context context;
    private Consumer<Word> consumer;
    private Runnable saveIncludeRunnable;
    /**
     * @param context            上下文用于获取各种组件
     * @param includeWordManager 单词管理对象
     * @param refreshList        刷新列表方法
     */
    public IncludePopRecyclerViewAdapter(Context context, IncludeWordManager includeWordManager, Runnable refreshList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.includeWordManager = includeWordManager;
        this.refreshList = refreshList;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.include_word_element, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        // 根据顺序索引得到对应的分类对象
        WordInclude wordInclude = includeWordManager.getWordIncludeByOrder(position);
        wordInclude.setOrder(position);
        holder.inCludeWordElement.getIncludeTitle().setText(wordInclude.getTitle());
        holder.inCludeWordElement.getIncludeDescribe().setText(wordInclude.getDescribe());
        holder.inCludeWordElement.setLeftSlide(wordInclude.isLeftSlide());
        holder.inCludeWordElement.setOpen(wordInclude.isOpen());
        holder.inCludeWordElement.setOrder(position);
        // 保留当前是否正在左滑,显示到刷新之后
        if (wordInclude.isLeftSlide()) {
            holder.inCludeWordElement.getLeftSlideLinearLayout().setVisibility(View.VISIBLE);
            holder.inCludeWordElement.getAdd().setVisibility(View.GONE);
        } else {
            holder.inCludeWordElement.getLeftSlideLinearLayout().setVisibility(View.GONE);
            holder.inCludeWordElement.getAdd().setVisibility(View.VISIBLE);
        }
        // 保留当前是否正在展开,显示到刷新之后
        if (holder.inCludeWordElement.isOpen()) {
            open(holder);
        } else {
            fold(holder);
        }
        // (添加事件)将单词添加到当前分类
        holder.inCludeWordElement.getAdd().setOnClickListener(v -> {
            wordInclude.addWord(includeWordManager.getToAddWordTag());
            holder.inCludeWordElement.getIncludeTitle().setText(wordInclude.getTitle());
            holder.inCludeWordElement.getIncludeDescribe().setText(wordInclude.getDescribe());
            saveIncludeRunnable.run();
            if (holder.inCludeWordElement.isOpen()) {
                open(holder);
            }
        });
        // 左滑事件
        holder.leftSlideRelativeLayout.setLeftSlideRunnable((aBoolean) -> {
            // true为左滑false为右滑动
            if (aBoolean && !holder.inCludeWordElement.isLeftSlide()) {
                holder.inCludeWordElement.setLeftSlide(true);
                wordInclude.setLeftSlide(true);
                holder.inCludeWordElement.getLeftSlideLinearLayout().setVisibility(View.VISIBLE);
                holder.inCludeWordElement.getAdd().setVisibility(View.GONE);
            } else if (!aBoolean && holder.inCludeWordElement.isLeftSlide()) {
                holder.inCludeWordElement.setLeftSlide(false);
                wordInclude.setLeftSlide(false);
                holder.inCludeWordElement.getLeftSlideLinearLayout().setVisibility(View.GONE);
                holder.inCludeWordElement.getAdd().setVisibility(View.VISIBLE);
            }
        });
        // 删除当前分类
        holder.inCludeWordElement.getDelete().setOnClickListener(v -> {
            includeWordManager.removeIncludeByOrder(wordInclude.getOrder());
            // 刷新列表
            refreshList.run();
        });
        // 上移当前分类
        holder.inCludeWordElement.getMoveUp().setOnClickListener(v -> {
            if (includeWordManager.moveUp(wordInclude.getOrder())) {
                // 刷新列表
                refreshList.run();
            }
        });
        // 下移当前分类
        holder.inCludeWordElement.getMoveDown().setOnClickListener(v -> {
            if (includeWordManager.moveDown(wordInclude.getOrder())) {
                // 刷新列表
                refreshList.run();
            }
        });
        // 展开单词
        holder.inCludeWordElement.getOpen().setOnClickListener(v -> {
            holder.inCludeWordElement.setOpen(!holder.inCludeWordElement.isOpen());
            wordInclude.setOpen(holder.inCludeWordElement.isOpen());
            if (wordInclude.getWordCount() == 0) {
                refreshOpenButton(holder);
                return;
            }
            if (!holder.inCludeWordElement.isOpen()) {
                fold(holder);
            } else {
                open(holder);
            }
        });
        // 修改标题
        holder.inCludeWordElement.getEdit().setOnClickListener(v -> {
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
                wordInclude.refreshTitleAndDescribe();
                holder.inCludeWordElement.getIncludeTitle().setText(wordInclude.getTitle());
                holder.inCludeWordElement.getIncludeDescribe().setText(wordInclude.getDescribe());
                inputMethodManager.hideSoftInputFromWindow(newTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.hideSoftInputFromWindow(newDescribe.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                // 很特殊的一项
                saveIncludeRunnable.run();
            });
            builder.setNegativeButton("取消", (dialog, which) -> {
                inputMethodManager.hideSoftInputFromWindow(newTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.hideSoftInputFromWindow(newDescribe.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return includeWordManager.getIncludeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 根据LinearViewHolder展开单词
     *
     * @param holder 被展开的对象
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void open(LinearViewHolder holder) {
        refreshOpenButton(holder);
        holder.inCludeWordElement.getControlWordWrapRecyclerView().setLayoutManager(new LinearLayoutManager(context));
        refresh(holder);
    }

    /**
     * 根据LinearViewHolder收缩单词
     *
     * @param holder 被展开的对象
     */
    private void fold(LinearViewHolder holder) {
        refreshOpenButton(holder);
        holder.inCludeWordElement.getControlWordWrapRecyclerView().setAdapter(null);
    }

    // 刷新Open按钮的方位
    private void refreshOpenButton(LinearViewHolder holder) {
        if (holder.inCludeWordElement.isOpen()) {
            holder.inCludeWordElement.getOpen().setRotation(0);
        } else {
            holder.inCludeWordElement.getOpen().setRotation(-90);
        }
    }

    // 刷新子列表
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refresh(LinearViewHolder holder) {
        WordInclude wordInclude = includeWordManager.getWordIncludeByOrder(holder.inCludeWordElement.getOrder());
        ControlWordRecyclerViewAdapter adapter = new ControlWordRecyclerViewAdapter(context, wordInclude, () -> {
            wordInclude.refreshTitleAndDescribe();
            holder.inCludeWordElement.getIncludeTitle().setText(wordInclude.getTitle());
            holder.inCludeWordElement.getIncludeDescribe().setText(wordInclude.getDescribe());
            saveIncludeRunnable.run();
            refresh(holder);
        });
        adapter.setPlayConsumer(consumer);
        holder.inCludeWordElement.getControlWordWrapRecyclerView().setAdapter(adapter);
    }

    public void setPlayConsumer(Consumer<Word> consumer) {
        this.consumer = consumer;
    }

    public void setSaveIncludeRunnable(Runnable saveIncludeRunnable) {
        this.saveIncludeRunnable = saveIncludeRunnable;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {


        private IncludeWordElement inCludeWordElement;
        private LeftSlideRelativeLayout leftSlideRelativeLayout;

        LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            inCludeWordElement = new IncludeWordElement();
            inCludeWordElement.setOpen(itemView.findViewById(R.id.open));
            inCludeWordElement.setEdit(itemView.findViewById(R.id.edit));
            inCludeWordElement.setIncludeTitle(itemView.findViewById(R.id.includeTitle));
            inCludeWordElement.setIncludeDescribe(itemView.findViewById(R.id.includeDescribe));
            inCludeWordElement.setAdd(itemView.findViewById(R.id.add));
            inCludeWordElement.setControlWordWrapRecyclerView(itemView.findViewById(R.id.controlWordWrapRecyclerView));
            // 一开始先删除左滑的所有按钮
            LinearLayout leftSlide = itemView.findViewById(R.id.leftSlide);
            inCludeWordElement.setLeftSlideLinearLayout(leftSlide);
            leftSlideRelativeLayout = (LeftSlideRelativeLayout) itemView;
        }

    }

}
