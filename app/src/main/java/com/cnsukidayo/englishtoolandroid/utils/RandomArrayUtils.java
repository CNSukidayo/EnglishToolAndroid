package com.cnsukidayo.englishtoolandroid.utils;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;

public class RandomArrayUtils {

    public static <T> ArrayList<T> randomEleList(List<T> source, int count) {
        final int[] randomList = ArrayUtil.sub(RandomUtil.randomInts(source.size()), 0, count);
        ArrayList<T> result = new ArrayList<>();
        for (int e : randomList) {
            result.add(source.get(e));
        }
        return result;
    }

}
