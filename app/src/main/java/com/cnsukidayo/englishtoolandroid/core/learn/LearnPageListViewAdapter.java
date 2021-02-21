package com.cnsukidayo.englishtoolandroid.core.learn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;

public class LearnPageListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;

    public LearnPageListViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        System.out.println("输出" + PartOfSpeechEnum.values().length);
        return PartOfSpeechEnum.values().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChineseInput chineseInput = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_learn_page_chinese_input, null);
            chineseInput = new ChineseInput();
            chineseInput.setPartOfSpeechTextView(convertView.findViewById(R.id.partOfSpeechTextView));
            chineseInput.setChineseInputText(convertView.findViewById(R.id.chineseInputText));
            chineseInput.setChineseAnswerTextView(convertView.findViewById(R.id.chineseAnswerTextView));
            convertView.setTag(chineseInput);
        } else {
            chineseInput = (ChineseInput) convertView.getTag();
        }
        chineseInput.getPartOfSpeechTextView().setText(PartOfSpeechEnum.values()[position].getName() + ".");
        return convertView;
    }
}
