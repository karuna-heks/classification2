package com.company;

import java.util.HashMap;
import java.util.Map;

public class Text {
    /*
    класс Text содержит всю информацию о тексте
    его имя, его тематику, его словарь
    другие его данные.
    класс может только хранить эти данные и передавать дальше
    сам он их не изменяет
     */

    private Map<String, Integer> dictionary = new HashMap<>();
    private String name;
    private String theme;


    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setTheme(String th) {
        theme = th;
    }

    public String getTheme() {
        return theme;
    }

    public void setDictionary(Map d) {
        dictionary = d;
    }

    public Map getDictionary() {
        return dictionary;
    }




}
