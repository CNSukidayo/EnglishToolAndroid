package com.cnsukidayo.englishtoolandroid.context;

import android.os.Environment;

import java.io.File;

public class EnglishToolProperties {

    public static final String englishSourcePath = "MyEnglishResource";
    public static final String musicSourcePath = "MyMusicResource";
    public static final String json = "json";
    public static final String include = "include.json";
    public static final String timeRecord = "timeRecord.json";
    public static final String imagination = "imagination.json";
    public static final String internalEntireEnglishSourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + englishSourcePath + File.separator;
}
