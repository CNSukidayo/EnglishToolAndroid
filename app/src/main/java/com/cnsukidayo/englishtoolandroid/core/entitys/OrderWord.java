package com.cnsukidayo.englishtoolandroid.core.entitys;

import java.io.Serializable;

/**
 * 带有顺序的单词
 *
 * @author sukidayo
 * @date 2021/1/26
 */
public class OrderWord implements Serializable {
    private int order;
    private Word word;


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "OrderWord{" +
                "order=" + order +
                ", word=" + word +
                '}';
    }
}
