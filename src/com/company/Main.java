package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.print("Started!");

        /*
            добавить объект класса Settings, который
            будет открывать файл с настройками,
            считывать их и запоминать.
         */

        ReadSettings settings = new ReadSettings("D://javaPrg/NN_Settings.txt");
        if (settings.checkError() == true)
            return;




        List<String> listOfTextAddress = new ArrayList<>();



        System.out.println(settings.getDocPath());
        File folder = new File(settings.getDocPath());
        File[] files = folder.listFiles();
        int numberOfThemes = files.length;
        List<String> listOfThemeAddress = new ArrayList<>(numberOfThemes);
        for (int i = 0; i < numberOfThemes; i++) {
            String tempString = files[i].getAbsolutePath();
            tempString = tempString.replaceAll(":\\\\","://");
            tempString = tempString.replaceAll("\\\\", "/");
            System.out.println(tempString);
            listOfThemeAddress.add(tempString);
        }


        for (int i = 0; i < numberOfThemes; i++) {
            File folderOfThemes = new File(listOfThemeAddress.get(i));
            File[] listOfFiles = folderOfThemes.listFiles();
            for (int j = 0; j < listOfFiles.length; j++) {
                System.out.println(listOfFiles[j].getAbsolutePath());
                listOfTextAddress.add(listOfFiles[j].getAbsolutePath());
            }
        }

        /*
        добавить подрограмму получения списка всех адресов
        всех файлов, затем все адреса поместить в listOfTextAddress
         */



        //создать словарь всех слов, наполнить его из списка listOfTexts
        HashMap<String, Integer> dictionary = new HashMap<>();




        List<Text> listOfTexts = new ArrayList<>();

        Parser p = new Parser();

        for (String e : listOfTextAddress) {
            listOfTexts.add(p.parse(e));

                //добавить подпрограмму объединения словарей

        }






        /*
            добавить параметры hiddenLayers, neuronNumber, inputNumber, outputNumber.
            некоторые параметры берутся из словаря (количество входных нейронов),
            некоторые определяются исходя из количества тем (количество выходных нейронов),
            некоторые определяются файлом с настройками (количество скрытых слоев и
            количество нейронов в скрытом слое)
         */

        NeuralNetwork nn = new NeuralNetwork(settings, dictionary.size(), numberOfThemes);



        /*
            добавить вывод всех результатов в txt файл
            (результаты кластеризации/классификации, использованые входные данные,
            матрица слов, другие параметры
         */



        /*
            добавить визуализацию работы алгоритма.
            (вывод графиков)
         */


    }


}
