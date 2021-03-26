package com.cnsukidayo.englishtoolandroid.core.home;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private final File[] allJsonDaysFile;
    private Map<Integer,ChooseDaysButton> allChooseDaysButton;
    private final File baseFile;

    public HomeListViewAdapter(Context context, File baseFile) {
        layoutInflater = LayoutInflater.from(context);
        this.baseFile = baseFile;
        this.allJsonDaysFile = new File(baseFile, File.separator + EnglishToolProperties.json).listFiles();
        this.allChooseDaysButton = new HashMap<>(allJsonDaysFile.length);
    }

    @Override
    public int getCount() {
        return this.allJsonDaysFile.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChooseDaysButton chooseDaysButton = null;
        if (allChooseDaysButton.size() <= position ||convertView == null) {
            // 这一步相当于是根据Context来获得到activity_choose_json_button这个页面,然后根据这个页面获取对应的组件?并且最终返回这个页面作为ListView的一个组件?
            convertView = layoutInflater.inflate(R.layout.activity_choose_json_button, null);
            chooseDaysButton = new ChooseDaysButton();
            chooseDaysButton.setCheckBox((CheckBox) convertView.findViewById(R.id.chooseJsonCheckBox));
            chooseDaysButton.setTextView((TextView) convertView.findViewById(R.id.chooseJsonTextView));
            chooseDaysButton.setLinearLayout((LinearLayout) convertView.findViewById(R.id.chooseJsonLinearLayout));
            chooseDaysButton.getTextView().setText(allJsonDaysFile[position].getName().substring(0, allJsonDaysFile[position].getName().indexOf('.')));
            chooseDaysButton.setThisDayJsonFile(allJsonDaysFile[position]);
            chooseDaysButton.setBasePath(baseFile.getAbsolutePath());
            allChooseDaysButton.put(position,chooseDaysButton);
            convertView.setTag(chooseDaysButton);
        } else {
            convertView.setTag(allChooseDaysButton.get(position));
        }
        return convertView;
    }

    /**
     * 设置所有按钮的选中状态
     *
     * @param status true代表所有按钮选中 false代表所有按钮不选中
     */
    public void changeChoseStatus(boolean status) {
        for (ChooseDaysButton chooseDaysButton : allChooseDaysButton.values()) {
            chooseDaysButton.changeChoseStatus(status);
        }
    }

    /**
     * 得到所有选中的单词
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Word> getAllCheckWords() {
        List<Word> words = new ArrayList<>(80);
        for (ChooseDaysButton chooseDaysButton : allChooseDaysButton.values()) {
            chooseDaysButton.getThisDayWords(words);
        }
        return words;
    }

    /**
     * 判断当前用户至少选择了一天
     *
     * @return true:用户选择了一天 false:用户一天都没选择.
     */
    public boolean assertOneDay() {
        for (ChooseDaysButton chooseDaysButton : allChooseDaysButton.values()) {
            if (chooseDaysButton.isChoseFlag()) {
                return true;
            }
        }
        return false;
    }

}
