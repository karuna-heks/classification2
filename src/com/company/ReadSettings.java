package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadSettings {
    /*
        объект класса ReadSettings должен проходить по указанному пути
        с файлом настроек, считывать их, переводить их в значения переменных
        затем, по запросу, возвращать их в программу
     */

    /*
        добавить значения по умолчанию для пустых переменных
        и выводить предупреждения, в случае использования значений по умолчанию,
        в том числе, в отчёте
     */
    private String docPath;
    private String reportPath;
    private String language;
    private String basisFind;
    private String task;
    private String wordsRatingMethod;
    private String clusterAnalysisMethod;
    private float trainingTestRatio;
    private int epochNumber;
    private int numberOfHiddenLayers;
    private int numberOfNeurons;
    private String activationFunction;
    private String teachingMethod;
    private float backPropFactor;
    private boolean errorStatus;

    public String getDocPath() {
        return docPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    public String getLanguage() {
        return language;
    }

    public String getBasisFind() {
        return  basisFind;
    }

    public String getTask() {
        return task;
    }

    public String getWordsRatingMethod() {
        return  wordsRatingMethod;
    }

    public String getClusterAnalysisMethod() {
        return clusterAnalysisMethod;
    }

    public float getTrainingTestRatio() {
        return trainingTestRatio;
    }

    public int getEpochNumber() {
        return epochNumber;
    }

    public int getNumberOfHiddenLayers() {
        return numberOfHiddenLayers;
    }

    public int getNumberOfNeurons() {
        return numberOfNeurons;
    }

    public String getActivationFunction() {
        return activationFunction;
    }

    public String getTeachingMethod() {
        return teachingMethod;
    }

    public float getBackPropFactor() {
        return backPropFactor;
    }

    public boolean checkError() {
        return errorStatus;
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
            добавить удаление пробелов и добавление комментариев
         */


        try {
            File input = new File(path);
            Scanner sc = new Scanner(input);



            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                //System.out.println("String original: "+s);
                s = s.replaceAll("\\s","");
                //System.out.println("String without space: "+s);
                String [] ar = s.split(":=");
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
                    case "NumberOfHiddenLayers":
                        numberOfHiddenLayers = Integer.parseInt(ar[1]);
                        //System.out.println("numberOfHiddenLayers: "+numberOfHiddenLayers);
                        break;
                    case "NumberOfNeurons":
                        numberOfNeurons = Integer.parseInt(ar[1]);
                        //System.out.println("numberOfNeurons: "+numberOfNeurons);
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
