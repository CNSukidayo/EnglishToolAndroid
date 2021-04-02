package com.cnsukidayo.englishtoolandroid.core.home;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.LinearViewHolder> {

    private LayoutInflater layoutInflater;
    private final File[] allJsonDaysFile;
    private final File baseFile;
    private Map<Integer, ChooseDaysButton> allChooseDaysButton;

    public HomeRecyclerViewAdapter(File baseFile, Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.allJsonDaysFile = new File(baseFile, File.separator + EnglishToolProperties.json).listFiles();
        this.baseFile = baseFile;
        this.allChooseDaysButton = new HashMap<>(allJsonDaysFile.length);
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.activity_choose_json_button, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.chooseDaysButton.getTextView().setText(allJsonDaysFile[position].getName().substring(0, allJsonDaysFile[position].getName().indexOf('.')));
        holder.chooseDaysButton.setThisDayJsonFile(allJsonDaysFile[position]);
        holder.chooseDaysButton.setBasePath(baseFile.getAbsolutePath());

        allChooseDaysButton.put(position, holder.chooseDaysButton);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.allJsonDaysFile.length;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {


        private ChooseDaysButton chooseDaysButton;

        LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            chooseDaysButton = new ChooseDaysButton();
            chooseDaysButton.setCheckBox(itemView.findViewById(R.id.chooseJsonCheckBox));
            chooseDaysButton.setTextView(itemView.findViewById(R.id.chooseJsonTextView));
            chooseDaysButton.setLinearLayout(itemView.findViewById(R.id.chooseJsonLinearLayout));
        }

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
