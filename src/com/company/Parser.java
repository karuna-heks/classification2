package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser {
/*
    класс Parser открывает файл по указанному пути,
    выполняет парсинг файла, формирует словарь,
    считывает дополнительные параметры текста
    (название, принадлежность к теме), заполняет этими
    данными объект класса Text, затем возвращает этот объект
 */

    private Map<String, Integer> dictionary = new HashMap<>();

    public Text parse(String path) {
        Text t = new Text();
        try {
            File input = new File(path);
            Scanner sc = new Scanner(input);




            /*
            добавить стеммер, добавить считыватель
            названия и темы,
            добавить инструменты передачи всей
            информации в объект t
             */


            dictionary.clear(); //очистка словаря

            while (sc.hasNextLine()) {
                stemmer(sc.nextLine());
            }
            sc.close();

            t.setDictionary(dictionary);
            //добавить передачу названия файла
            //добавить передачу темы файла
            //добавить передачу словаря



            return t;
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            return t;
        }
    }

    private void stemmer(String s) {

        /*
            алгоритм стемминга
            1. выделение слова
            2. отсечение окончания

         */


    }
}
