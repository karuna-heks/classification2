package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadSettings {
    /*
        объект класса ReadSettings должен проходить по указанному пути
        с файлом настроек, считывать их, переводить их в значения переменных
        затем, по запросу, возвращать их в программу
     */


    private String docPath;
    private String reportPath;
    private String language = "eng";
    private String basisFind = "stemming";
    private String task = "classification";
    private String wordsRatingMethod = "TFIDF";
    private String clusterAnalysisMethod = "kmeans";
    private float trainingTestRatio = 0.01f;
    private int epochNumber = 10000;
    private int numberOfHiddenLayers = 1;
    private int numberOfNeurons = 10;
    private String activationFunction = "sigmoid";
    private String teachingMethod = "backprop";
    private float backPropFactor = 0.8f;
    private boolean errorStatus;
    private ArrayList<Integer> structure;

    public String getDocPath() {
        //метод возвращает путь к документам
        return docPath;
    }

    public String getReportPath() {
        //метод возвращает путь к файлу отчета
        return reportPath;
    }

    public String getLanguage() {
        //метод возвращает название текущего языка
        return language;
    }

    public String getBasisFind() {
        // метод возвращает имя способа поиска основы
        return  basisFind;
    }

    public String getTask() {
        return task; //имя задачи (классификация или кластеризация)
    }

    public String getWordsRatingMethod() {
        return  wordsRatingMethod; //способ оценивания важности слов (TDIFD, TF)
    }

    public String getClusterAnalysisMethod() {
        return clusterAnalysisMethod; //метод анализа кластеров (kmeans, cmeans, kohonen)
    }

    public float getTrainingTestRatio() {
        return trainingTestRatio; //соотношение размера тестовой к тренировочной выборке
    }

    public int getEpochNumber() {
        return epochNumber; //количество эпох обучения
    }

    public int getNumberOfHiddenLayers() {
        return numberOfHiddenLayers; //количество скрытых слоев в нейронной сети
    }

    public int getNumberOfNeurons() {
        System.err.println("Method 'getNumberOfNeurons' not supported");
        System.exit(1);
        return numberOfNeurons; //количество нейронов в слое
    }

    public ArrayList<Integer> getStructure() {
        return structure;
    }

    public String getActivationFunction() {
        return activationFunction; //имя функции активации
    }

    public String getTeachingMethod() {
        return teachingMethod; //имя метода обучения (backprop, genetic)
    }

    public float getBackPropFactor() {
        return backPropFactor; //коэффициент для backprop способа обучения
    }

    public boolean checkError() {
        return errorStatus; //ошибка возникает при вызове конструктора без ввода стартовой строки
        //либо при ошибке во время попытки открытия файла настроек
    }

    public ReadSettings() {
        errorStatus = true;
    }

    public ReadSettings(String path) {
        /*
            открывается файл по пути path,
            парсер для считывания нужных строк и добавления их значений
            в переменные
         */

        /*
            добавить удаление пробелов и комментариев
         */
        structure = new ArrayList<>();

        try {
            File input = new File(path);
            Scanner sc = new Scanner(input);



            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                //System.out.println("String original: "+s);
                s = s.replaceAll("\\s",""); //удаляет все пробелы и табуляции в строке
                System.out.println("String without space: "+s);
                String[] ar = s.split(":=");
                switch (ar[0]) {
                    case "DocPath":
                        docPath = ar[1];
                        //System.out.println("DocPath: "+docPath);
                        break;
                    case "ReportPath":
                        reportPath = ar[1];
                        //System.out.println("ReportPath: "+reportPath);
                        break;
                    case "Language":
                        language = ar[1];
                        //System.out.println("Language: "+language);
                        break;
                    case "BasisFind":
                        basisFind = ar[1];
                        //System.out.println("basisFind: "+basisFind);
                        break;
                    case "Task":
                        task = ar[1];
                        //System.out.println("task: "+task);
                        break;
                    case "WordsRatingMethod":
                        wordsRatingMethod = ar[1];
                        //System.out.println("wordsRatingMethod: "+wordsRatingMethod);
                        break;
                    case "ClusterAnalysisMethod":
                        clusterAnalysisMethod = ar[1];
                        //System.out.println("clusterAnalysisMethod: "+clusterAnalysisMethod);
                        break;
                    case "TrainingTestRatio":
                        trainingTestRatio = Float.parseFloat(ar[1]);
                        //System.out.println("trainingTestRatio: "+trainingTestRatio);
                        break;
                    case "EpochNumber":
                        epochNumber = Integer.parseInt(ar[1]);
                        //System.out.println("epochNumber: "+epochNumber);
                        break;


                    /**case "NumberOfHiddenLayers":
                        numberOfHiddenLayers = Integer.parseInt(ar[1]);
                        //System.out.println("numberOfHiddenLayers: "+numberOfHiddenLayers);
                        break;
                    case "NumberOfNeurons":
                        //добавить возможность использовать переменное число нейронов
                        numberOfNeurons = Integer.parseInt(ar[1]);
                        //System.out.println("numberOfNeurons: "+numberOfNeurons);
                        break;
                     **/
                    case "Structure":
                        String[] str = ar[1].split("-");
                        for (int i = 0; (i < str.length)&&(i < 4); i++) {
                            structure.add(Integer.parseInt(str[i]));
                        }
                        numberOfHiddenLayers = structure.size()+1;
                        break;
                    case "ActivationFunction":
                        activationFunction = ar[1];
                        //System.out.println("activationFunction: "+activationFunction);
                        break;
                    case "TeachingMethod":
                        teachingMethod = ar[1];
                        //System.out.println("teachingMethod: "+teachingMethod);
                        break;
                    case "BackPropFactor":
                        backPropFactor = Float.parseFloat(ar[1]);
                        //System.out.println("backPropFactor: "+backPropFactor);
                        break;
                    default:
                        break;
                }
            }
            sc.close();

            errorStatus = false;
        } catch (FileNotFoundException e) {
            System.err.println("Settings file not found");
            errorStatus = true;
        }
    }
}
