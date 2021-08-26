package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

import java.util.ArrayList;
import java.util.List;

public class LearnPageRecyclerView extends RecyclerView.Adapter<LearnPageRecyclerView.LinearViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<ChineseDisplay> chineseDisplays;
    private TextView PREPPhrase;
    private TextView discriminateMessage;
    private TextView day;

    public LearnPageRecyclerView(Context context, LinearLayout supplement) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.chineseDisplays = new ArrayList<>(getItemCount());
        this.PREPPhrase = supplement.findViewById(R.id.PREPPhrase);
        this.discriminateMessage = supplement.findViewById(R.id.discriminateMessage);
        this.day = supplement.findViewById(R.id.day);
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
    }

    @Override
    public int getItemCount() {
        return PartOfSpeechEnum.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {


        private ChineseDisplay chineseInput;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            chineseInput = new ChineseDisplay();
            chineseInput.setPartOfSpeechTextView(itemView.findViewById(R.id.partOfSpeechTextView));
            chineseInput.setChineseAnswerTextView(itemView.findViewById(R.id.chineseAnswerTextView));
            chineseDisplays.add(chineseInput);
        }

    }

    /**
     * 传入单词对象,设置答案框的内容为单词内容.该方法主要是检查功能
     * 如果传入的对象是null 代表清除中文答案框中的所有内容
     *
     * @param word 传入单词对象
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAnswerLabelTextFromWord(Word word) {
        for (ChineseDisplay chineseDisplay : this.chineseDisplays) {
            chineseDisplay.getChineseAnswerTextView().setText(word == null ? "" : word.getAllChineseMap().getOrDefault(chineseDisplay.getPartOfSpeech(), ""));
        }
        if (word == null) {
            PREPPhrase.setText("");
            discriminateMessage.setText("");
            day.setText("");
        } else {
            PREPPhrase.setText(word.getPREPPhrase());
            discriminateMessage.setText(word.getDiscriminate());
            day.setText(String.valueOf(word.getDays()));
        }
    }

}
