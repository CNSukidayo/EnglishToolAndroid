package com.cnsukidayo.englishtoolandroid.core.entitys;

import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;
import com.cnsukidayo.englishtoolandroid.core.enums.WordCategory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 虽然实现了Serializable接口,但是我们保证向下兼容,所以你并不能序列化和反序列化该类实例.
 *
 * @author sukidayo
 * @date 2021/1/26
 */
public class Word implements Serializable {
    private String english;
    /**
     * Key:词性 Value:对应的中文
     **/
    private Map<PartOfSpeechEnum, String> allChineseMap;
    private int days;
    private WordCategory category;
    private String audioPath;
    // 和PC端不同的是,现在单词是否被标记也作为单词的属性放到类中
    private boolean flag = false;

    public Word() {
        allChineseMap = new HashMap<>(PartOfSpeechEnum.values().length);
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public WordCategory getCategory() {
        return category;
    }

    public void setCategory(WordCategory category) {
        this.category = category;
    }

    public Map<PartOfSpeechEnum, String> getAllChineseMap() {
        return allChineseMap;
    }

    public void setAllChineseMap(Map<PartOfSpeechEnum, String> allChineseMap) {
        this.allChineseMap = allChineseMap;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void negationFlag() {
        setFlag(!isFlag());
    }

    @Override
    public String toString() {
        return "Word{" +
                "english='" + english + '\'' +
                ", allChineseMap=" + allChineseMap +
                ", days=" + days +
                ", category=" + category +
                ", audioPath='" + audioPath + '\'' +
                '}';
    }
}
