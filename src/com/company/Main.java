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


        List<double[]> inputArrays = new ArrayList<>(); //список всех входных векторов
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
            inputArrays.add(textVector);
        }


        System.out.println("Global dictionary size: "+ globalDictionary.size());
        System.out.println("Global dictionary: ");
        //for (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
        //    System.out.println(entry.getKey() + " : " + entry.getValue());
        //}






















        ///////////////**************************************
        /*
            тут необходимо сформировать 2 выборки входов и желаемых выходов:
            1. для обучения
            2. для тестирования
            формирование должно происходить по такому же принципу как и в NeuralNetwork
         */


        //подпрограмма формирования списков с векторами текстов

        int[] ratio;
        /*
            вспомогательный массив для определения
            обучающей/тестовой выборки.
            нулевой элемент -- количество текстов с данной темой
            1й элемент -- размер обучающей выборки
            2й элемент -- размер тестовой выборки
         */


        //HashMap<String, int[]> themesRatio = new HashMap<>();
        HashMap<String, Integer> topicNumber = new HashMap<>();
        //topicNumber - ключ - имя темы, число - номер темы в
        //списке numOfTopics. также этот номер должен соответствовать
        //номерам выходных нейронов каждой темы

        List<int[]> numOfTopics = new ArrayList<>();
        //numOfTopics - [0] - кол-во текстов
        //[1] - кол-во обуч. текстов, [2] - кол-во тестовых текстов


        List<Integer> learnNumbers = new ArrayList<>();
        //массив с номерами текстов обучающей выборки
        List<Integer> testNumbers = new ArrayList<>();
        //массив с номерами текстов тестовой выборки


        int topicCount = 0;
        for (int i = 0; i < listOfTexts.size(); i++) {
            //в цикле перебираем все тексты, запоминаем номера тем
            //запоминаем кол-во текстов на нужную тему
            String key = listOfTexts.get(i).getTheme();

            if (topicNumber.containsKey(key)) {
            /*
                если в hashMap была обнаружена тема,
                то увеличиваем счётчик количества текстов, в списке
                numOfTopics в соответствующей теме
             */

                numOfTopics.get(topicNumber.get(key))[0] = numOfTopics.get(topicNumber.get(key))[0] + 1;
                //увеличение счётчика количества текстов


            }
            else
            {
                topicNumber.put(key, topicCount);
                //запоминаем номер темы
                ratio = new int[3];
                ratio[0] = 1;
                ratio[1] = 0;
                ratio[2] = 0;
                numOfTopics.add(topicCount, ratio);
                //инициализируем счётчик количества текстов для новой темы
                topicCount++;
            }
        }


        for (int i = 0; i < numOfTopics.size(); i++) {
            //цикл подсчёта количества обучающих и тестовых образцов текстов
            numOfTopics.get(i)[1] = (int)Math.ceil(numOfTopics.get(i)[0]*settings.getTrainingTestRatio());
            //общее кол-во текстов умножаем на коэффициент и округляем вверх
            numOfTopics.get(i)[2] = numOfTopics.get(i)[0] - numOfTopics.get(i)[1];
            //вычисляем размер тестовой выборки
        }


        for (int i = 0; i < listOfTexts.size(); i++) {
        /*
            формируем массив learnNumbers[], в котором будут номера обучающих текстов
            и формируем массив testNumbers[] с номерами тестовых текстов
         */

            if (numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[1] != 0) {
                learnNumbers.add(i);
                numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[1] = numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[1] - 1;
            }
            else if (numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[2] != 0) {
                testNumbers.add(i);
                numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[2] = numOfTopics.get(topicNumber.get(listOfTexts.get(i).getTheme()))[2] - 1;
            }
        /*
            переделать !!!!!!%%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%
         */
        }


        /*
            формируем списки List<double[]> inputs, List<double[]> outputs
         */
        List<double[]> dataLearningInputs = new ArrayList<>();
        List<double[]> dataTestingInputs = new ArrayList<>();
        List<double[]> dataLearningOutputs = new ArrayList<>();
        List<double[]> dataTestingOutputs = new ArrayList<>();
        double[] tempArray;
        for (int i = 0; i < learnNumbers.size(); i++) {
            int tempVal = learnNumbers.get(i);
            dataLearningInputs.add(listOfTexts.get(tempVal).getVector());
            tempArray = numberToArray(topicNumber.get(listOfTexts.get(i).getTheme()), numberOfThemes);
            dataLearningOutputs.add(tempArray);
        }

        for (int i = 0; i < testNumbers.size(); i++) {
            int tempVal = testNumbers.get(i);
            dataTestingInputs.add(listOfTexts.get(tempVal).getVector());
            tempArray = numberToArray(topicNumber.get(listOfTexts.get(i).getTheme()), numberOfThemes);
            dataTestingOutputs.add(tempArray);
        }
        ///////////////**************************************




















        /*
            продумать, куда и на каком этапе лучше добавлять входные вектора со словами
         */
        //NeuralNetwork nn = new NeuralNetwork(settings, globalDictionary.size(), numberOfThemes);


        /**
         * NeuralNetwork nn = new NeuralNetwork(); - создание объекта
         * *nn.readSettingsFile(String path); - задание полного пути к файлу настроек, чтение файла и загрузка настроек
         * *nn.initialization(int numInputs, int numOutputs); - создание архитектуры НС
         * *nn.setData(List<double[]> inputs, List<double[]> desireOutputs); - добавление данных в НС (автоматом сформирует тест/обуч выборку)
         * nn.setDataLearning(List<double[]> inputs, List<double[]> desireOutputs); - добавление обучающих данных в НС
         * nn.setDataTest(List<double[]> inputs, List<double[]> desireOutputs); - добавление тестовых данных в НС
         * *nn.learning(); - запуск обучения, причём условие остановки берётся из файла
         * *nn.learning(int epoch); - запуск обучения, условие остановки - кол-во эпох
         * *nn.learning(int epoch, int winRate); - запуск обучения, условие остановки - кол-во эпох или винрейт
         * *nn.singlePass(double[] input); - одиночный проход данных через нейронную сеть и выдача вектора выходов
         * *nn.singlePass(double[] input, double[] output); - одиночный проход данных через и выдача (результата прохода или вектора совпадений)
         * *nn.singleLearningPass(double[] input, double[] output); - одиночных проход данных с обучением
         * nn.printResult(); - вывести результаты на экран
         * nn.printResultFile(); - вывести результаты по пути в настройках, либо по умолчанию
         * nn.printResultFile(String path); - вывести результаты по заданному пути
         * nn.printWeights(); - вывести веса всех связей
         * nn.printWeightsFile(); - вывести веса всех связей в файл
         *
         *
         *
         * *nn.setParameter("settingName", "settingValue"); - запись параметра вручную
         *
         * NeuralNetwork nn = new NeuralNetwork(settingsStruct, inputs, desireOutputs);
         *
         */

        NeuralNetwork2 nn2 = new NeuralNetwork2();
        nn2.readSettingsFile("D://javaPrg/NN_Settings.txt");
        nn2.initialization(globalDictionary.size(), numberOfThemes);
        //nn2.setDataLearning(dataLearningInputs, dataLearningOutputs);
        nn2.setDataLearning(dataTestingInputs, dataTestingOutputs);
        //nn2.setDataTesting(dataTestingInputs, dataTestingOutputs);
        nn2.setDataTesting(dataTestingInputs, dataTestingOutputs);
        nn2.learning(1000);


        //nn.learning(globalDictionary, listOfTexts);
        //nn.reportMessages();








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
            bw.write("Точность: " + nn2.getWinRate()+" %\n");
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



    public static double[] numberToArray(int number, int arraySize) {
        double[] outputArray = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            if (number == i) {
                outputArray[i] = 1;
            }
            else {
                outputArray[i] = 0;
            }
        }
        return outputArray;
    }


}
