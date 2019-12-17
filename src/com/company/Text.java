package com.company;

import java.util.HashMap;

public class Text {
    /*
    класс Text содержит всю информацию о тексте
    его имя, его тематику, его словарь
    другие его данные.
    класс может только хранить эти данные и передавать дальше
    сам он их не изменяет
     */

    private HashMap<String, Integer> dictionary = new HashMap<>();
    private String name;
    private String theme;
    private double[] textVector;

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

    public void setDictionary(HashMap<String, Integer> dictionary) {
        this.dictionary = dictionary;

    }

    public HashMap<String, Integer> getDictionary() {
        return dictionary;
    }

    public void setVector(double[] textVector) {
        this.textVector = textVector;
    }

    public double[] getVector() {
        return textVector;
    }




}
