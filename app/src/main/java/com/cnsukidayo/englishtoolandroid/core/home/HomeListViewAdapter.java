package com.cnsukidayo.englishtoolandroid.core.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.resource.ResourceUtil;

public class HomeListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private final File[] allJsonDaysFile;
    private List<ChooseDaysButton> allChooseDaysButton;

    public HomeListViewAdapter(Context context, File baseFile) {
        layoutInflater = LayoutInflater.from(context);
        this.allJsonDaysFile = new File(baseFile, File.separator + EnglishToolProperties.json).listFiles();
        this.allChooseDaysButton = new ArrayList<>(allJsonDaysFile.length);
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
        if (convertView == null) {
            // 这一步相当于是根据Context来获得到activity_choose_json_button这个页面,然后根据这个页面获取对应的组件?并且最终返回这个页面作为ListView的一个组件?
            convertView = layoutInflater.inflate(R.layout.activity_choose_json_button, null);
            chooseDaysButton = new ChooseDaysButton();
            chooseDaysButton.setCheckBox(convertView.findViewById(R.id.chooseJsonCheckBox));
            chooseDaysButton.setTextView(convertView.findViewById(R.id.chooseJsonTextView));
            chooseDaysButton.setLinearLayout(convertView.findViewById(R.id.chooseJsonLinearLayout));
            convertView.setTag(chooseDaysButton);
            allChooseDaysButton.add(chooseDaysButton);
        } else {
            chooseDaysButton = (ChooseDaysButton) convertView.getTag();
        }
        chooseDaysButton.getTextView().setText(allJsonDaysFile[position].getName().substring(0, allJsonDaysFile[position].getName().indexOf('.')));
        chooseDaysButton.setThisDayJsonFile(allJsonDaysFile[position]);
        return convertView;
    }

    /**
     * 设置所有按钮的选中状态
     *
     * @param status true代表所有按钮选中 false代表所有按钮不选中
     */
    public void changeChoseStatus(boolean status) {
        for (ChooseDaysButton chooseDaysButton : allChooseDaysButton) {
            chooseDaysButton.changeChoseStatus(status);
        }
    }

    /**
     * 得到封装所有按钮的集合
     *
     * @return 返回集合
     */
    public List<ChooseDaysButton> getAllChooseDaysButton() {
        return allChooseDaysButton;
    }

    public void printJson() {
        for (ChooseDaysButton chooseDaysButton : allChooseDaysButton) {
            if (chooseDaysButton.isChoseFlag()) {
                System.out.println(ResourceUtil.readStr(chooseDaysButton.getThisDayJsonFile().getPath(), Charset.forName("UTF-8")));
            }
        }
    }

}
