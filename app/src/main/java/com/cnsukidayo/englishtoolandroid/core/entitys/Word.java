package com.cnsukidayo.englishtoolandroid.core.entitys;

import android.net.Uri;

import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;
import com.cnsukidayo.englishtoolandroid.core.enums.WordCategory;
import com.cnsukidayo.englishtoolandroid.core.enums.WordMarkColor;

import java.io.File;
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
    private String PREPPhrase;
    private String discriminate;
    private transient volatile Uri audioUri;
    // 和PC端不同的是,现在单词是否被标记也作为单词的属性放到类中
    private WordMarkColor wordMarkColor = WordMarkColor.DEFAULT;

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

    public Uri getAudioUri() {
        return audioUri;
    }

    public WordMarkColor getWordMarkColor() {
        return wordMarkColor;
    }

    public void setWordMarkColor(WordMarkColor wordMarkColor) {
        this.wordMarkColor = wordMarkColor;
    }

    public String getPREPPhrase() {
        return PREPPhrase;
    }

    public void setPREPPhrase(String PREPPhrase) {
        this.PREPPhrase = PREPPhrase;
    }

    public String getDiscriminate() {
        return discriminate;
    }

    public void setDiscriminate(String discriminate) {
        this.discriminate = discriminate;
    }

    /**
     * 有些时候会没有基础路径
     *
     * @param baseFile
     * @return
     */
    public Uri getAudioUri(String baseFile) {
        if (audioUri == null) {
            audioUri = Uri.fromFile(new File(baseFile + File.separator + audioPath.replace("D:\\Java Project\\English Tool\\resource\\", "").replace('\\', '/')));
        }
        return audioUri;
    }

    /**
     * 判断当前这个单词是不是一个空单词(即没有任何中文信息)
     *
     * @return
     */
    public boolean noChinese() {
        boolean result = true;
        for (String value : allChineseMap.values()) {
            if (!value.isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Word{" +
                "english='" + english + '\'' +
                ", allChineseMap=" + allChineseMap +
                ", days=" + days +
                ", category=" + category +
                ", audioPath='" + audioPath + '\'' +
                ", PREPPhrase='" + PREPPhrase + '\'' +
                ", discriminate='" + discriminate + '\'' +
                ", audioUri=" + audioUri +
                ", wordMarkColor=" + wordMarkColor +
                '}';
    }
}
