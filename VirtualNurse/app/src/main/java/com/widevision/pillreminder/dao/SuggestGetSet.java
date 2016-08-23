package com.widevision.pillreminder.dao;

/**
 * Created by mercury-one on 6/2/16.
 */
public class SuggestGetSet {
    String suggestion;
    public SuggestGetSet(String suggestion){
        this.suggestion=suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}