package com.cnsukidayo.englishtoolandroid.core.entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordInclude implements Serializable {
    // 当前分类特有的ID
    private int id;
    // 当前分类的排序
    private int order;
    // 分类的标题
    private String title;
    private boolean defaultTitle = true;
    // 分类的描述信息
    private String describe;
    private boolean defaultDescribe = true;
    // 含有顺序的单词
    private List<OrderWord> orderWordList;
    // 当前分类是否正在被左滑
    private transient boolean isLeftSlide = false;
    private transient boolean isOpen = false;

    /**
     * 获得一个空的WordInclude实例
     *
     * @return 返回值不为空
     */
    public static WordInclude getWordIncludeInstance() {
        WordInclude wordInclude = new WordInclude();
        wordInclude.orderWordList = new ArrayList<>();
        return wordInclude;
    }

    /**
     * 添加一个新的单词到当前分类中,顺序是最后一个.该方法会自动刷新标题和描述信息.
     *
     * @param newWord 新单词
     */
    public void addWord(Word newWord) {
        if (newWord != null && !containsWord(newWord)) {
            OrderWord orderWord = new OrderWord();
            // 直接添加时,单词顺序直接+1即可
            orderWord.setOrder(orderWordList.size() + 1);
            orderWord.setWord(newWord);
            orderWordList.add(orderWord);
            refreshTitleAndDescribe();
        }
    }

    /**
     * 刷新标题和描述信息
     */
    public void refreshTitleAndDescribe() {
        StringBuilder temp = new StringBuilder();
        if (defaultTitle || defaultDescribe) {
            // 顶多只加5个单词
            int count = 0;
            for (OrderWord value : orderWordList) {
                temp.append(value.getWord().getEnglish()).append("、");
                count++;
                if (count == 5) {
                    break;
                }
            }
        }
        String substring = "";
        if (temp.length() > 0) {
            substring = temp.substring(0, temp.length() - 1);
        }
        if (defaultTitle) {
            title = substring;
        }
        if (defaultDescribe) {
            describe = substring;
        }
    }

    /**
     * 根据顺序删除某个分类
     *
     * @param order 当前分类在列表中的顺序
     */
    public void removeIncludeByOrder(int order) {
        orderWordList.remove(order);
        for (; order < orderWordList.size(); order++) {
            orderWordList.get(order).setOrder(order);
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
        // 当前单词
        OrderWord orderWord = orderWordList.get(order);
        orderWordList.set(order, orderWordList.get(order - 1));
        orderWordList.set(order - 1, orderWord);
        return true;
    }

    /**
     * 将当前顺序对应的分类下移
     *
     * @param order 需要下移的分类的顺序
     * @return 返回是否移动成功(比如当当前顺序为0时无法上移)
     */
    public boolean moveDown(int order) {
        if (order == orderWordList.size() - 1) {
            return false;
        }
        OrderWord orderWord = orderWordList.get(order);
        orderWordList.set(order, orderWordList.get(order + 1));
        orderWordList.set(order + 1, orderWord);
        return true;
    }

    /**
     * 当前分类是否已经包含某个单词
     *
     * @param key 单词对象
     * @return true为已经包含, false为不包含
     */
    private boolean containsWord(Word key) {
        for (OrderWord orderWord : orderWordList) {
            if (orderWord.getWord().getEnglish().equals(key.getEnglish())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得当前分类的单词总数
     */
    public int getWordCount() {
        return orderWordList.size();
    }

    /**
     * 根据order获取对应的单词
     *
     * @param order
     * @return
     */
    public OrderWord getWordByOrder(int order) {
        return orderWordList.get(order);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }


    public boolean isDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(boolean defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public boolean isDefaultDescribe() {
        return defaultDescribe;
    }

    public void setDefaultDescribe(boolean defaultDescribe) {
        this.defaultDescribe = defaultDescribe;
    }

    public boolean isLeftSlide() {
        return isLeftSlide;
    }

    public void setLeftSlide(boolean leftSlide) {
        isLeftSlide = leftSlide;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
