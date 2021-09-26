package com.cnsukidayo.englishtoolandroid.core.entitys;

public class TimeRecord {
    private String article;
    private String cautiousReadTime;
    private String doubleCautiousReadTime;
    private String translateTime;
    private String matchParagraphTime;
    private String matchWordTime;

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getCautiousReadTime() {
        return cautiousReadTime;
    }

    public void setCautiousReadTime(String cautiousReadTime) {
        this.cautiousReadTime = cautiousReadTime;
    }

    public String getDoubleCautiousReadTime() {
        return doubleCautiousReadTime;
    }

    public void setDoubleCautiousReadTime(String doubleCautiousReadTime) {
        this.doubleCautiousReadTime = doubleCautiousReadTime;
    }

    public String getTranslateTime() {
        return translateTime;
    }

    public void setTranslateTime(String translateTime) {
        this.translateTime = translateTime;
    }

    public String getMatchParagraphTime() {
        return matchParagraphTime;
    }

    public void setMatchParagraphTime(String matchParagraphTime) {
        this.matchParagraphTime = matchParagraphTime;
    }

    public String getMatchWordTime() {
        return matchWordTime;
    }

    public void setMatchWordTime(String matchWordTime) {
        this.matchWordTime = matchWordTime;
    }

}
