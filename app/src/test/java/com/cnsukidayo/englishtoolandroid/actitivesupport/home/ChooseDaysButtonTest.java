package com.cnsukidayo.englishtoolandroid.actitivesupport.home;

import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ChooseDaysButtonTest {

    @Test
    public void getThisDayJsonFile() {
        String str = "D:\\Java Project\\English Tool\\resource\\day1\\advantageous.mp3";
        System.out.println(str.replace("D:\\Java Project\\English Tool\\resource\\", ""));
    }

    @Test
    public void testTimedFormat() {
        LocalTime parse = LocalTime.parse("00:00:20", DateTimeFormatter.ISO_LOCAL_TIME);
        System.out.println(parse);
        LocalTime localTime = parse.minusSeconds(1);
        System.out.println(localTime);
    }

    @Test
    public void testMap() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "1");
        map.values().iterator().next();
        System.out.println(map.size());
    }

}