package com.cnsukidayo.englishtoolandroid.core.entitys;

import java.util.List;
import java.util.Map;

public class Imagination {
    // allIn
    private Map<String, Word> allWorld;

    private List<Word> activeList;
    private List<Word> errList;


    public Map<String, Word> getAllWorld() {
        return allWorld;
    }

    public void setAllWorld(Map<String, Word> allWorld) {
        this.allWorld = allWorld;
    }

    public List<Word> getActiveList() {
        return activeList;
    }

    public void setActiveList(List<Word> activeList) {
        this.activeList = activeList;
    }

    public List<Word> getErrList() {
        return errList;
    }

    public void setErrList(List<Word> errList) {
        this.errList = errList;
    }
}
