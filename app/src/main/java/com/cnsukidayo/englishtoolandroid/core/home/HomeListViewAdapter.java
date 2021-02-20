package com.cnsukidayo.englishtoolandroid.core.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cnsukidayo.englishtoolandroid.R;

public class HomeListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;

    public HomeListViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 10;
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
        } else {
            chooseDaysButton = (ChooseDaysButton) convertView.getTag();
        }
        chooseDaysButton.getTextView().setText("内容" + position);
        return convertView;
    }

}
