package com.cnsukidayo.englishtoolandroid.core.learn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

import java.util.ArrayList;
import java.util.List;

public class LearnPageRecyclerView extends RecyclerView.Adapter<LearnPageRecyclerView.LinearViewHolder> {

    private Context context;
    private final List<ChineseInput> allChineseInput;

    public LearnPageRecyclerView(Context context) {
        this.context = context;
        allChineseInput = new ArrayList<>(getItemCount());
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_learn_page_chinese_input, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.chineseInput.setPartOfSpeech(PartOfSpeechEnum.values()[position]);
        holder.chineseInput.getPartOfSpeechTextView().setText(holder.chineseInput.getPartOfSpeech().getName().toUpperCase() + ".");
    }

    @Override
    public int getItemCount() {
        return PartOfSpeechEnum.values().length;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {

        private ChineseInput chineseInput;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            chineseInput = new ChineseInput();
            chineseInput.setPartOfSpeechTextView(itemView.findViewById(R.id.partOfSpeechTextView));
            chineseInput.setChineseInputText(itemView.findViewById(R.id.chineseInputText));
            chineseInput.setChineseAnswerTextView(itemView.findViewById(R.id.chineseAnswerTextView));
            allChineseInput.add(chineseInput);
        }

    }

    /**
     * 传入单词对象,设置答案框的内容为单词内容.该方法主要是检查功能
     * 如果传入的对象是null 代表清除中文答案框中的所有内容
     *
     * @param word 传入单词对象
     */
    public void setAnswerLabelTextFromWord(Word word) {
        for (ChineseInput chineseInput : this.allChineseInput) {
            // 这里稍微有点问题
            String text = "";
            if (word != null) {
                text = word.getAllChineseMap().get(chineseInput.getPartOfSpeech().name());
            }
            if (text.contains("\n")) {
                text = "<html><body>" + text.replaceAll("\n", "<br>") + "<body><html>";
            }
            chineseInput.getChineseAnswerTextView().setText(text);
        }
    }

}
