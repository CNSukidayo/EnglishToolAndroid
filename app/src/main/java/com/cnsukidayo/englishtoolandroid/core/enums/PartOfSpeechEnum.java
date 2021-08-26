package com.cnsukidayo.englishtoolandroid.core.enums;

/**
 * @author sukidayo
 * @date 2021/1/30
 */
public enum PartOfSpeechEnum {
    ADJ("adj"),
    ADV("adv"),
    V("v"),
    VI("vi"),
    VT("vt"),
    N("n"),
    CONJ("conj"),
    PRON("pron"),
    NUM("num"),
    ART("art"),
    PREP("prep"),
    INT("int");
    private final String name;

    PartOfSpeechEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
