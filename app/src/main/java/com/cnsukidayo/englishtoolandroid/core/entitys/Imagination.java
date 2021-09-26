package com.cnsukidayo.englishtoolandroid.core.entitys;

import java.util.List;
import java.util.Map;

public class Imagination {
    // allIn
    private Map<String, Word> allWorld;
    // 主动想出
    private Map<String, Word> active;
    // 被动提示
    private Map<String, Word> passive;
    // 错题集
    private Map<String, Word> err;

    private List<Word> activeList;
    private List<Word> passiveList;
    private List<Word> errList;


    public Map<String, Word> getAllWorld() {
        return allWorld;
    }

    public void setAllWorld(Map<String, Word> allWorld) {
        this.allWorld = allWorld;
    }

    public Map<String, Word> getActive() {
        return active;
    }

    public void setActive(Map<String, Word> active) {
        this.active = active;
    }

    public Map<String, Word> getPassive() {
        return passive;
    }

    public void setPassive(Map<String, Word> passive) {
        this.passive = passive;
    }

    public Map<String, Word> getErr() {
        return err;
    }

    public void setErr(Map<String, Word> err) {
        this.err = err;
    }

    public List<Word> getActiveList() {
        return activeList;
    }

    public void setActiveList(List<Word> activeList) {
        this.activeList = activeList;
    }

    public List<Word> getPassiveList() {
        return passiveList;
    }

    public void setPassiveList(List<Word> passiveList) {
        this.passiveList = passiveList;
    }

    public List<Word> getErrList() {
        return errList;
    }

    public void setErrList(List<Word> errList) {
        this.errList = errList;
    }
}
