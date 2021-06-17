package com.cnsukidayo.englishtoolandroid.actitivesupport.learn;

import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.entitys.WordInclude;

import java.io.Serializable;
import java.util.List;

public class IncludeWordManager implements Serializable {
    // 这个List必须得按照order的顺序排放
    private List<WordInclude> allWordInclude;
    // 即将被添加到某个分类的单词,这也存放在IncludeWordManager中,因为只有一个单词会被添加,这对用户是不可见的.
    private transient Word toAddWord;

    public IncludeWordManager() {
    }

    /**
     * 添加新的分类
     *
     * @param wordInclude 新分类
     */
    public void addNewInclude(WordInclude wordInclude) {
        allWordInclude.add(wordInclude);
    }

    public void removeIncludeByID(int id) {

    }

    /**
     * 根据顺序删除某个分类
     *
     * @param order 当前分类在列表中的顺序
     */
    public void removeIncludeByOrder(int order) {
        allWordInclude.remove(order);
        for (; order < allWordInclude.size(); order++) {
            allWordInclude.get(order).setOrder(order);
        }
    }

    /**
     * 将当前顺序对应的分类上移
     *
     * @param order 需要上移的分类的顺序
     * @return 返回是否移动成功(比如当当前顺序为0时无法上移)
     */
    public boolean moveUp(int order) {
        if (order == 0) {
            return false;
        }
        // 当前分类
        WordInclude wordInclude = allWordInclude.get(order);
        allWordInclude.set(order, allWordInclude.get(order - 1));
        allWordInclude.set(order - 1, wordInclude);
        return true;
    }

    /**
     * 将当前顺序对应的分类下移
     *
     * @param order 需要下移的分类的顺序
     * @return 返回是否移动成功(比如当当前顺序为0时无法上移)
     */
    public boolean moveDown(int order) {
        if (order == allWordInclude.size() - 1) {
            return false;
        }
        WordInclude wordInclude = allWordInclude.get(order);
        allWordInclude.set(order, allWordInclude.get(order + 1));
        allWordInclude.set(order + 1, wordInclude);
        return true;
    }

    /**
     * 获得当前分类总数
     */
    public int getIncludeCount() {
        return allWordInclude.size();
    }

    /**
     * 根据ID获取分类
     *
     * @param order
     * @return
     */
    public WordInclude getWordIncludeByOrder(int order) {
        return allWordInclude.get(order);
    }

    public void setAllWordInclude(List<WordInclude> allWordInclude) {
        this.allWordInclude = allWordInclude;
    }

    public Word getToAddWordTag() {
        return toAddWord;
    }

    public void setToAddWordWordTag(Word toAddWord) {
        this.toAddWord = toAddWord;
    }
}
