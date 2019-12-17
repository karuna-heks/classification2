package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
/*
    класс Parser открывает файл по указанному пути,
    выполняет парсинг файла, формирует словарь,
    считывает дополнительные параметры текста
    (название, принадлежность к теме), заполняет этими
    данными объект класса Text, затем возвращает этот объект
 */

    private Text t;
    private HashMap<String, Integer> dictionary;
    private ArrayList<String> wordException;

    public Parser() {

        wordException = new ArrayList<>();

        wordException.add("is");
        wordException.add("are");
        wordException.add("the");
        wordException.add("there");
        wordException.add("their");
        wordException.add("them");
        wordException.add("that");
        wordException.add("in");
        wordException.add("into");
        wordException.add("two");
        wordException.add("after");
        wordException.add("thi");
        wordException.add("have");
        wordException.add("has");
        wordException.add("such");
        wordException.add("who");
        wordException.add("as");
        wordException.add("ar");
        wordException.add("at");
        wordException.add("be");
        wordException.add("which");
        wordException.add("for");
        wordException.add("it");
        wordException.add("of");
        wordException.add("or");
        wordException.add("on");
        wordException.add("to");
        wordException.add("wa");
        wordException.add("from");
        wordException.add("with");
        wordException.add("also");
        wordException.add("a");
        wordException.add("if");

    }

    public Text parse(String path) {

        try {
            File input = new File(path);
            //Scanner scan = new Scanner(input);
            Scanner scan = new Scanner(input, "UTF-8");
            t = new Text();



            /*
            добавить инструменты передачи всей
            информации в объект t
             */

            dictionary = new HashMap<>();
            dictionary.clear(); //очистка словаря
            //System.out.println("*1*1");
            while (scan.hasNext()) {
                //прочитывает каждую строку по-отдельности
                //и отправляет на стеммер
                stemmer(scan.nextLine());
                //System.out.println("*2*2*");
            }
            scan.close();



            if (dictionary.size() == 0) {
                scan = new Scanner(input, "UTF-16");
                while (scan.hasNext()) {
                    stemmer(scan.nextLine());
                }
                scan.close();
                if (dictionary.size() == 0) {
                    System.err.println("Dictionary is empty");
                    System.exit(1);
                }
            }



            t.setDictionary(dictionary); //передаем словарь
            t.setName(parsePathForName(path)); //передаем имя текста
            t.setTheme(parsePathForTheme(path)); //передаем тему текста
            //System.out.println("Theme: " + t.getTheme());
            //System.out.println("Topic name: " + t.getName());
            //System.out.println("Dictionary size: "+ dictionary.size());

            //System.out.println("Dictionary: ");
            //for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
            //    System.out.println(entry.getKey() + " : "+entry.getValue());
            //}




            return t;
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            return t;
        }
    }

    public String parsePathForTheme(String p) {
        // метод парсит строку, содержащую путь и получает имя темы
        String[] ar = p.split("/");
        //разбиваем строку с путем по разделителю /

        if (ar.length <= 1) {
            //если разбиение по разделителю / не работает, то разбиваем по \
            ar = p.split("\\\\");
        }
        return ar[ar.length-2];
        //выбираем из массива предпоследний элемент (т.к. последний это имя текста)
    }

    public String parsePathForName(String p) {
        //метод парсит строку пути для получения имени темы
        String[] ar = p.split("/");
        if (ar.length <= 1) {
            ar = p.split("\\\\");
        }


        ar = ar[ar.length-1].split("\\.");



        return ar[ar.length-2];
    }

    private void stemmer(String s) {

        /*
            алгоритм стемминга
            1. выделение слова
            2. отсечение окончания
            3. занесение слова в словарь
         */
        s = s.toLowerCase();
        //приведение к нижнему регистру

        String[] str = s.split("\\P{L}+");
        Integer tempVal;
        //разделение слов по основным разделителям

        //лемматизация и/или стемминг
        for (int i = 0; i < str.length; i++) {
            boolean brk = false;
            if (str[i].length() < 1) {
                continue;
            }

            for (int j = 0; j < str[i].length(); j++) {
                if ((str[i].charAt(j) < 97)||(str[i].charAt(j) > 122)) {
                    brk = true;
                    break;
                }
            }
            if (brk == true)
                continue;

            /*if ((str[i].charAt(0) < 97) || (str[i].charAt(0) > 122)) {
                //операция необходима для того, чтобы выкидывать иероглифы и другие
                //нечитаемые символы.
                //возможно, следует убрать этот фильтр и просто попробовать изменить
                //регулярное выражение в s.split
                continue;
            }*/
            PorterStemming stem = new PorterStemming();


            //разкомментировать для стемминга портера
            String tempString = stem.stemming(str[i]);

            for (int j = 0; j < wordException.size(); j++) {
                if (tempString.equals(wordException.get(j))) {
                    brk = true;
                    break;
                }
            }
            if (brk == true)
                continue;



            //закомментить для портера
            //String tempString = str[i];


            if (!dictionary.containsKey(tempString)) {

                dictionary.put(tempString, 1);
            }
            else {
                tempVal = dictionary.get(tempString);
                dictionary.replace(tempString, tempVal+1);
            }
        }


    }
}
