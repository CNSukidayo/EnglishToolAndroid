package com.cnsukidayo.englishtoolandroid.core.entitys;

import android.net.Uri;

import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;
import com.cnsukidayo.englishtoolandroid.core.enums.WordCategory;

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
    private transient volatile Uri audioUri;
    // 和PC端不同的是,现在单词是否被标记也作为单词的属性放到类中 注意现在不可以录入单词,否则会出问题
    private boolean flag = false;
    // 音频标记
    private boolean voiceFlag = false;

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

    public void negationVoiceFlag() {
        setVoiceFlag(!isVoiceFlag());
    }

    public Uri getAudioUri() {
        if (audioUri == null) {
            audioUri = Uri.fromFile(new File(audioPath));
        }
        return audioUri;
    }

    /**
     * 有些时候会没有基础路径
     *
     * @param baseFile
     * @return
     */
    public Uri getAudioUri(String baseFile) {
        if (audioUri == null) {
            audioPath = baseFile + File.separator + audioPath.replace("D:\\Java Project\\English Tool\\resource\\", "").replace('\\', '/');
            audioUri = Uri.fromFile(new File(audioPath));
        }
        return audioUri;
    }

    public boolean isVoiceFlag() {
        return voiceFlag;
    }

    public void setVoiceFlag(boolean voiceFlag) {
        this.voiceFlag = voiceFlag;
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
                ", audioUri=" + audioUri +
                ", flag=" + flag +
                ", voiceFlag=" + voiceFlag +
                '}';
    }
}
