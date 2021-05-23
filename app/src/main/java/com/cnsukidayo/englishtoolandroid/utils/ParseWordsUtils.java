package com.cnsukidayo.englishtoolandroid.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.cnsukidayo.englishtoolandroid.core.enums.PartOfSpeechEnum;
import com.cnsukidayo.englishtoolandroid.core.enums.WordCategory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class ParseWordsUtils {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<JSONObject> parseJsonToJsonObjectArrayNotNull(File filePath) {
        return Optional.ofNullable(((JSONArray) JSONUtil.parse(JSONUtil.readJSON(filePath, StandardCharsets.UTF_8))).toList(JSONObject.class)).orElse(Collections.emptyList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Word> parseJsonAndGetWordsWithList(File filePath) {
        List<Word> list = new ArrayList<>();
        List<JSONObject> jsonObjects = parseJsonToJsonObjectArrayNotNull(filePath);
        for (JSONObject jsonObject : jsonObjects) {
            Word word = new Word();
            word.setAllChineseMap(((Map<PartOfSpeechEnum, String>) jsonObject.get("allChineseMap", Map.class)));
            word.setAudioPath(jsonObject.get("audioPath", String.class));
            word.setEnglish(jsonObject.get("english", String.class));
            word.setDays(jsonObject.get("days", Integer.class));
            word.setCategory(WordCategory.valueOf(jsonObject.get("category", String.class)));
            word.setFlag(jsonObject.get("flag", Boolean.class));
            word.setVoiceFlag(jsonObject.get("voiceFlag", Boolean.class));
            list.add(word);
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Map<String, Word> parseJsonAndGetWordsWithMap(File filePath) {
        Map<String, Word> map = new HashMap<>();
        List<JSONObject> jsonObjects = parseJsonToJsonObjectArrayNotNull(filePath);
        for (JSONObject jsonObject : jsonObjects) {
            Word word = new Word();
            word.setAllChineseMap(((Map<PartOfSpeechEnum, String>) jsonObject.get("allChineseMap", Map.class)));
            word.setAudioPath(jsonObject.get("audioPath", String.class));
            word.setEnglish(jsonObject.get("english", String.class));
            word.setDays(jsonObject.get("days", Integer.class));
            word.setCategory(WordCategory.valueOf(jsonObject.get("category", String.class)));
            map.put(word.getEnglish(), word);
        }
        return map;
    }


}
