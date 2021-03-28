package com.cnsukidayo.englishtoolandroid.utils;

import java.io.File;
import java.util.Arrays;

public class SortUtils {
    /**
     * 根据文件名称进行排序
     *
     * @param files 文件
     */
    public static void sortWordWithName(File... files) {
        Arrays.sort(files, (file1, file2) -> Integer.parseInt(file1.getName().substring(0, file1.getName().lastIndexOf('.')).replaceAll("[a-zA-Z|-]", "")) - Integer.parseInt(file2.getName().substring(0, file2.getName().lastIndexOf('.')).replaceAll("[a-zA-Z|-]", "")));
    }

}
