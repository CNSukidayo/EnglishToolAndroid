package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.entitys.WordInclude;

import java.util.function.Consumer;

public class ControlWordRecyclerViewAdapter extends RecyclerView.Adapter<ControlWordRecyclerViewAdapter.LinearViewHolder> {

    private final LayoutInflater layoutInflater;
    private final WordInclude wordInclude;
    private final Runnable refreshList;
    private Context context;
    private Consumer<Word> consumer;
    /**
     * @param context     上下文
     * @param wordInclude 单词管理器
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ControlWordRecyclerViewAdapter(Context context, WordInclude wordInclude, Runnable refreshList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.wordInclude = wordInclude;
        this.refreshList = refreshList;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.include_word_control_element, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        Word word = wordInclude.getWordByOrder(position).getWord();
        holder.includeWordControlElement.getEnglishOriginal().setText(word.getEnglish());
        // 删除某个单词
        holder.includeWordControlElement.getDelete().setOnClickListener(v -> {
            wordInclude.removeIncludeByOrder(position);
            // 刷新列表
            refreshList.run();
        });
        // 上移
        holder.includeWordControlElement.getMoveUp().setOnClickListener(v -> {
            wordInclude.moveUp(position);
            // 刷新列表
            refreshList.run();
        });
        // 下移
        holder.includeWordControlElement.getMoveDown().setOnClickListener(v -> {
            wordInclude.moveDown(position);
            // 刷新列表
            refreshList.run();
        });
        // 单词信息的RecyclerView
        holder.includeWordControlElement.getRecyclerView().setLayoutManager(new LinearLayoutManager(context));
        holder.includeWordControlElement.getRecyclerView().setAdapter(new IncludeWordMessageRecyclerViewAdapter(context, word));
        holder.includeWordControlElement.getEnglishOriginal().setOnClickListener(v -> consumer.accept(word));
    }


    @Override
    public int getItemCount() {
        return wordInclude.getWordCount();
    }

    public void setPlayConsumer(Consumer<Word> consumer) {
        this.consumer = consumer;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private IncludeWordControlElement includeWordControlElement;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            includeWordControlElement = new IncludeWordControlElement();
            includeWordControlElement.setLinearLayout(itemView.findViewById(R.id.playEventRelativeLayout));
            includeWordControlElement.setRecyclerView(itemView.findViewById(R.id.messageDetail));
            includeWordControlElement.setEnglishOriginal(itemView.findViewById(R.id.englishOriginal));
            includeWordControlElement.setMoveUp(itemView.findViewById(R.id.moveUp));
            includeWordControlElement.setMoveDown(itemView.findViewById(R.id.moveDown));
            includeWordControlElement.setDelete(itemView.findViewById(R.id.delete));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
