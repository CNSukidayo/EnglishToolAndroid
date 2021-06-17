package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

import java.util.ArrayList;
import java.util.List;

public class IncludeWordMessageRecyclerViewAdapter extends RecyclerView.Adapter<IncludeWordMessageRecyclerViewAdapter.LinearViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Word word;
    private final List<PartOfSpeechEnum> partOfSpeechEnums = new ArrayList<>(3);

    /**
     * @param context 上下文
     * @param word    当前单词
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public IncludeWordMessageRecyclerViewAdapter(Context context, Word word) {
        this.layoutInflater = LayoutInflater.from(context);
        this.word = word;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.include_word_message_element, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        PartOfSpeechEnum partOfSpeechEnum = partOfSpeechEnums.get(position);
        holder.includeWordMessageElement.getPartOfSpeech().setText(partOfSpeechEnum.getName() + ".");
        holder.includeWordMessageElement.getChinese().setText(word.getAllChineseMap().get(partOfSpeechEnum));
        if (partOfSpeechEnum == PartOfSpeechEnum.PHTASESANDCOLLOCATION) {
            holder.includeWordMessageElement.getChinese().setSingleLine(false);
        }
    }

    @Override
    public int getItemCount() {
        for (PartOfSpeechEnum partOfSpeechEnum : PartOfSpeechEnum.values()) {
            String value = word.getAllChineseMap().get(partOfSpeechEnum);
            if (value != null && value.length() != 0 && !partOfSpeechEnums.contains(partOfSpeechEnum)) {
                partOfSpeechEnums.add(partOfSpeechEnum);
            }
        }
        return partOfSpeechEnums.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {

        private IncludeWordMessageElement includeWordMessageElement;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            includeWordMessageElement = new IncludeWordMessageElement();
            includeWordMessageElement.setPartOfSpeech(itemView.findViewById(R.id.partOfSpeech));
            includeWordMessageElement.setChinese(itemView.findViewById(R.id.chinese));
        }

    }

}
