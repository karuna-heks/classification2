package com.company;

import java.io.*;
import java.util.*;
import java.util.List;



public class Main {

    public static void main(String[] args) {

        System.out.println("Start");
        TestTimer timer = new TestTimer();
        timer.startTimer();



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



        textAnalyzer analyzer = new textAnalyzer();
        analyzer.analyzerInit(listOfTexts, "tf");
        analyzer.setParameter("trainingTestRatio", settings.getTrainingTestRatio());
        analyzer.setParameter("numberOfThemes", numberOfThemes);
        analyzer.startAnalyze();


         List<double[]> dataLearningInputs;
         List<double[]> dataTestingInputs;
         List<double[]> dataLearningOutputs;
         List<double[]> dataTestingOutputs;
         dataLearningInputs = analyzer.getDataLearningInputs();
         dataTestingInputs = analyzer.getDataTestingInputs();
         dataLearningOutputs = analyzer.getDataLearningOutputs();
         dataTestingOutputs = analyzer.getDataTestingOutputs();



        NeuralNetwork2 nn2 = new NeuralNetwork2();
        nn2.readSettingsFile("D://javaPrg/NN_Settings.txt");
        nn2.initialization(analyzer.getGlobalDictionary().size(), numberOfThemes);
        nn2.setDataLearning(dataLearningInputs, dataLearningOutputs);
        //nn2.setDataLearning(dataTestingInputs, dataTestingOutputs);
        nn2.setDataTesting(dataTestingInputs, dataTestingOutputs);
        nn2.learning();





        try {
            //формирование отчёта о работе

            timer.stopTimer();
            String timeDif = timer.getDiffence();
            String timeStart = timer.getStartTime();
            String timeStop = timer.getStopTime();
            String timeStartSimple = timer.getStartTimeSimple();
            String finalReportPath = settings.getReportPath() + "/" + timeStartSimple + "_report/";

            File destFile = new File(finalReportPath);
            destFile.mkdirs();



            String reportName = finalReportPath + "reportMain.txt";
            FileWriter writer = new FileWriter(reportName, false);
            BufferedWriter bw = new BufferedWriter(writer);


            bw.write("Время начала: "+timeStart+", Время окончания: "+timeStop+"\n");
            bw.write("Время выполнения: "+timeDif+"\n");
            bw.write("Количество тем: "+numberOfThemes+"\n");
            bw.write("Общее количество текстов: "+listOfTexts.size()+"\n");
            bw.write("Количество эпох обучения: "+settings.getEpochNumber()+"\n");
            bw.write("Точность: " + nn2.getWinRate()+" %\n");
            bw.close();
            writer.close();

            reportName = finalReportPath + "reportDetail.txt";
            writer = new FileWriter(reportName);
            bw = new BufferedWriter(writer);


            bw.write("Размер глобального словаря: "+analyzer.getGlobalDictionary().size()+"\n");
            bw.write("Глобальный словарь:\n");

            for (Map.Entry<String, Integer> entry : analyzer.getGlobalDictionary().entrySet()) {
                bw.write(entry.getKey() + " : " + entry.getValue()+"\n");
            }
            bw.write("Конец глобального словаря");


            nn2.createReport(finalReportPath); //сформировать и сохранить график обучения
        }
        catch (IOException e) {
            System.err.format("IOException. File report error");
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
