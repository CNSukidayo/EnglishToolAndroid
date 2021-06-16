package com.cnsukidayo.englishtoolandroid.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.cnsukidayo.englishtoolandroid.core.entitys.Word;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParseWordsUtils {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Word> parseJsonAndGetWordsWithList(File... filePath) {
        return parseJson(null, filePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Word> parseJsonAndGetWordsWithList(List<Word> list, File... filePath) {
        return parseJson(list, filePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<Word> parseJson(List<Word> list, File... filePath) {
        if (list == null) {
            list = new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Word>>() {
        }.getType();
        for (File file : filePath) {
            try {
                list.addAll(gson.fromJson(new FileReader(file), type));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


}
