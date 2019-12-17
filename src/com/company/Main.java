package com.company;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Start");
        TestTimer timer = new TestTimer();
        timer.startTimer();



        /*
            добавить объект класса Settings, который
            будет открывать файл с настройками,
            считывать их и запоминать.
         */


        // добавить ввод пути для настроек через консоль
        ReadSettings settings = new ReadSettings("D://javaPrg/NN_Settings.txt");
        if (settings.checkError() == true)
            return;




        List<String> listOfTextAddress = new ArrayList<>();



        System.out.println(settings.getDocPath());
        File folder = new File(settings.getDocPath());
        File[] files = folder.listFiles();
        int numberOfThemes = files.length;
        List<String> listOfThemeAddress = new ArrayList<>(numberOfThemes); //список с адресами тем
        System.out.println("Поиск доступных тем...");
        for (int i = 0; i < numberOfThemes; i++) {
            String tempString = files[i].getAbsolutePath();
            tempString = tempString.replaceAll(":\\\\","://");
            tempString = tempString.replaceAll("\\\\", "/");
            //System.out.println(tempString);
            listOfThemeAddress.add(tempString);
        }

        System.out.println("Поиск доступных текстов...");
        for (int i = 0; i < numberOfThemes; i++) {
            //открывание каждой папки с текстом по-отдельности
            File folderOfThemes = new File(listOfThemeAddress.get(i));
            File[] listOfFiles = folderOfThemes.listFiles();
            for (int j = 0; j < listOfFiles.length; j++) {
                //формирование списка с адресами всех текстов
                String tempString = listOfFiles[j].getAbsolutePath();
                tempString = tempString.replaceAll(":\\\\","://");
                tempString = tempString.replaceAll("\\\\", "/");
                //System.out.println(tempString);
                listOfTextAddress.add(tempString);
                System.out.println("Найдено "+j+" текстов в: "+listOfThemeAddress.get(i));
            }
        }
        //на выходе имеем список с адресами всех файлов, сохраненном в listOfTextAddress



        //создать словарь всех слов, наполнить его из списка listOfTexts
        HashMap<String, Integer> globalDictionary = new HashMap<>();




        List<Text> listOfTexts = new ArrayList<>();

        Parser p = new Parser();
        int numberOfTexts = listOfTextAddress.size();
        int temp_i = 0;

        System.out.println("Начало парсинга текстов...");
        for (String e : listOfTextAddress) {
            temp_i++;
            //итеративно проходим по всему списку listOfTextsAddress
            //с путями для каждого файла
            //для парсинга текста и формирования локальных словарей
            //Text tempText = new Text();
            //tempText = p.parse(e);
            listOfTexts.add(p.parse(e));
            System.out.println("Выполнен парсинг "+temp_i+" текстов из "+numberOfTexts);
        }









        System.out.println("Формирование глобального словаря...");
        for (int i = 0; i < listOfTexts.size(); i++) {
        //for (Text t : listOfTexts) {
            //проход для формирования глобального словаря
            //HashMap<String, Integer> localDictionary = t.getDictionary();
            HashMap<String, Integer> localDictionary = listOfTexts.get(i).getDictionary();
            for  (Map.Entry<String, Integer> entry : localDictionary.entrySet()) {
                String tempKey = entry.getKey();
                if (globalDictionary.containsKey(tempKey)) {
                    //если этот ключ уже слово уже есть в глобальном словаре
                    //то увеличиваем счётчик встречаемости этого слова в корпусе
                    Integer tempValGlob = globalDictionary.get(tempKey);
                    Integer tempValLoc = entry.getValue();
                    //System.out.println("tempValGlob = "+tempValGlob);
                    //System.out.println("tempValLoc = "+tempValLoc);
                    //System.out.println("tempKey = "+tempKey);
                    globalDictionary.replace(tempKey, tempValGlob + tempValLoc);
                }
                else {
                    //если этого слова ещё нет, то добавляем его
                    //и запоминаем, сколько раз оно встретилось
                    Integer tempValLoc = entry.getValue();
                    //System.out.println("tempValLoc = "+tempValLoc);
                    //System.out.println("tempKey = "+tempKey);
                    globalDictionary.put(tempKey, tempValLoc);
                }
                //System.out.println("Global dictionary size: "+ globalDictionary.size());
            }
            //System.out.println("Global dictionary size: "+ globalDictionary.size());
            //System.out.println("Global dictionary: ");
            //for (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
            //    System.out.println(entry.getKey() + " : " + entry.getValue());
            //}
            //System.out.println("End of global dictionary.");
        }


        //List<double[]> inputArrays = new ArrayList<>(); //список всех входных векторов
        //<>
        System.out.println("Формирование векторов текстов...");
        for (Text t : listOfTexts) {
            //проход для формирования векторов текстов
            double[] textVector = new double[globalDictionary.size()];
            HashMap<String, Integer> localDictionary = t.getDictionary();
            int i = 0;
            for  (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
                String tempKey = entry.getKey();
                if (localDictionary.containsKey(tempKey)) {
                    textVector[i] = (double)localDictionary.get(tempKey);
                }
                else {
                    textVector[i] = 0;
                }
                i++;
            }
            t.setVector(textVector);
            //inputArrays.add(textVector);
        }


        System.out.println("Global dictionary size: "+ globalDictionary.size());
        System.out.println("Global dictionary: ");
        //for (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
        //    System.out.println(entry.getKey() + " : " + entry.getValue());
        //}



        /*
            продумать, куда и на каком этапе лучше добавлять входные вектора со словами
         */
        NeuralNetwork nn = new NeuralNetwork(settings, globalDictionary.size(), numberOfThemes);




        nn.learning(globalDictionary, listOfTexts);
        nn.reportMessages();








        try {
            //формирование отчёта о работе

            timer.stopTimer();
            String timeDif = timer.getDiffence();
            String timeStart = timer.getStartTime();
            String timeStop = timer.getStopTime();
            String timeStartSimple = timer.getStartTimeSimple();


            String reportName = timeStartSimple + "_reportMain.txt";
            FileWriter writer = new FileWriter(reportName);
            BufferedWriter bw = new BufferedWriter(writer);


            bw.write("Время начала: "+timeStart+", Время окончания: "+timeStop+"\n");
            bw.write("Время выполнения: "+timeDif+"\n");
            bw.write("Количество тем: "+numberOfThemes+"\n");
            bw.write("Общее количество текстов: "+listOfTexts.size()+"\n");
            bw.write("Количество эпох обучения: "+settings.getEpochNumber()+"\n");
            bw.write("Точность: " + nn.getLastWinrate()+" %\n");
            bw.close();
            writer.close();

            reportName = timeStartSimple + "_reportDetail.txt";
            writer = new FileWriter(reportName);
            bw = new BufferedWriter(writer);


            bw.write("Размер глобального словаря: "+globalDictionary.size()+"\n");
            bw.write("Глобальный словарь:\n");

            for (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
                bw.write(entry.getKey() + " : " + entry.getValue()+"\n");
            }
            bw.write("Конец глобального словаря");


        }
        catch (IOException e) {
            System.err.format("IOException");
        }













        //Report report = new Report();
        //report.importLearningData();
        //report.importSettings();
        //report.createReportFile();




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
