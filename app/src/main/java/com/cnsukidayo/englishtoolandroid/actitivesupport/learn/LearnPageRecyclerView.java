package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

import java.util.ArrayList;
import java.util.List;

public class LearnPageRecyclerView extends RecyclerView.Adapter<LearnPageRecyclerView.LinearViewHolder> {

    private Context context;
    private final List<ChineseInput> allChineseInput;
    private LayoutInflater layoutInflater;

    public LearnPageRecyclerView(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        allChineseInput = new ArrayList<>(getItemCount());
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.activity_learn_page_chinese_input, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.chineseInput.setPartOfSpeech(PartOfSpeechEnum.values()[position]);
        holder.chineseInput.getPartOfSpeechTextView().setText(holder.chineseInput.getPartOfSpeech().getName().toUpperCase() + ".");
        if (PartOfSpeechEnum.values()[position] == PartOfSpeechEnum.PHTASESANDCOLLOCATION) {
            holder.chineseInput.getChineseInputText().setSingleLine(false);
            holder.chineseInput.getChineseAnswerTextView().setSingleLine(false);
        }
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 传入单词对象,设置答案框的内容为单词内容.该方法主要是检查功能
     * 如果传入的对象是null 代表清除中文答案框中的所有内容
     *
     * @param word 传入单词对象
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAnswerLabelTextFromWord(Word word) {
        for (ChineseInput chineseInput : this.allChineseInput) {
            chineseInput.getChineseAnswerTextView().setText(word == null ? "" : word.getAllChineseMap().getOrDefault(chineseInput.getPartOfSpeech().name(), ""));
        }
    }

    /**
     * 将当前用户输入的单词中文保存到Word对象上,该方法主要是记忆功能.保存到Word实例上的时候并不会实例化类对于的属性,只有一个枚举Map.<br>
     * 不会造成高额的资源消耗.
     *
     * @return 返回Word对象, 和正常的Word对象不同, 该对象缺斤短两, 只保留枚举Map.从而降低资源浪费
     */
    // 该方法和PC端不同
    public Word getWordFromInPutText() {
        Word word = new Word();
        for (ChineseInput chineseInput : this.allChineseInput) {
            word.getAllChineseMap().put(chineseInput.getPartOfSpeech(), chineseInput.getChineseInputText().getText().toString());
        }
        EditText input = ((AppCompatActivity) context).findViewById(R.id.input);
        word.setEnglish(input.getText().toString());
        return word;
    }

    /**
     * 传入单词对象,设置中文输入框中的内容为单词内容.该方法主要是记忆功能
     *
     * @param word 传入单词对象 如果传入值为null则将中文输入框中的内容设置为什么都没有
     */
    public void setInPutTextFromWord(Word word) {
        for (ChineseInput chineseInput : this.allChineseInput) {
            chineseInput.getChineseInputText().setText(word == null ? "" : word.getAllChineseMap().get(chineseInput.getPartOfSpeech()));
        }
        EditText input = ((AppCompatActivity) context).findViewById(R.id.input);
        input.setText(word == null ? "" : word.getEnglish());
    }

    public void setCanScrollContainer(boolean flag) {
        for (ChineseInput chineseInput : allChineseInput) {
            chineseInput.getChineseInputText().setHorizontalScrollBarEnabled(flag);
        }
    }

}
