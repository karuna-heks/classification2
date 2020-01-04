package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




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
 *
 * *nn.setParameter("settingName", "settingValue"); - запись параметра вручную
 *
 * NeuralNetwork nn = new NeuralNetwork(settingsStruct, inputs, desireOutputs);
 *
 */




public class NeuralNetwork2 {




    //// параметры настройки НС из файла
    private String reportPath;
    private float trainingTestRatio = 0.7f;
    private int epochNumber;
    private ArrayList<Integer> structure;
    private int numberOfHiddenLayers;
    private String activationFunction = "sigmoid";
    private float backPropFactor = 0.05f;
    private boolean errorStatus;
    private List<Integer> sizeOfLayers = new ArrayList<>();
    ////
    private int numberOfOutputs;
    private double[] tempOutputArray;


    //// слои нейронов
    private Layer hiddenLayer;
    private List<Layer> layers = new ArrayList<>();
    ////


    private double[] inputSignals;
    private double[] tempArrayBackProp;

    double winRate;

    //// списки с входными и выходными данными НС
    private List<double[]> inputData;
    private List<double[]> outputData;
    private List<double[]> inputDataLearning;
    private List<double[]> outputDataLearning;
    private List<double[]> inputDataTesting;
    private List<double[]> outputDataTesting;
    /////

    private List<double[]> listOfTempArrays = new ArrayList<>();



    public void readSettingsFile(String path) {
        /**задание полного пути к файлу настроек, чтение файла и загрузка настроек**/


        try {
            File input = new File(path);
            Scanner sc = new Scanner(input);


            structure = new ArrayList<>();
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                //System.out.println("String original: "+s);
                s = s.replaceAll("\\s",""); //удаляет все пробелы и табуляции в строке
                //System.out.println("String without space: "+s);
                String[] ar = s.split(":=");
                switch (ar[0]) {

                    case "TrainingTestRatio":
                        trainingTestRatio = Float.parseFloat(ar[1]);
                        //System.out.println("trainingTestRatio: "+trainingTestRatio);
                        break;
                    case "EpochNumber":
                        epochNumber = Integer.parseInt(ar[1]);
                        //System.out.println("epochNumber: "+epochNumber);
                        break;
                    case "Structure":
                        String[] str = ar[1].split("-");
                        for (int i = 0; (i < str.length)&&(i < 4); i++) {
                            structure.add(Integer.parseInt(str[i]));
                        }
                        numberOfHiddenLayers = structure.size()+1;
                        if (numberOfHiddenLayers > 7) {
                            System.err.println("Слишком много скрытых слоев в НС. Максимум 6");
                            System.exit(1);
                        }
                        break;
                    case "ActivationFunction":
                        activationFunction = ar[1];
                        //System.out.println("activationFunction: "+activationFunction);
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



    public void initialization(int numInputs, int numOutputs) {
        /**создание архитектуры НС**/


        //формируем список sizeOfLayers, который хранит кол-во нейронов в каждом слое
        //и список layers, который хранит все скрытые слои с нейронами
        for (int i = 0; i < (numberOfHiddenLayers - 1); i++) {
            sizeOfLayers.add(structure.get(i));
            hiddenLayer = new Layer();
            layers.add(hiddenLayer);
        }
        sizeOfLayers.add(numOutputs);
        hiddenLayer = new Layer();
        layers.add(hiddenLayer);
        numberOfOutputs = numOutputs;
        tempOutputArray = new double[numberOfOutputs];

        //определяем размер массива входных сигналов
        inputSignals = new double[numInputs];


        //объявляем нейрон, далее будем создавать экземпляры и отправлять их в списки нейронов
        Neuron neuron;


        //проходим по первому слою, создаем новый экземпляр нейрона и отправляем его в новый слой
        for (int j = 0; j < sizeOfLayers.get(0); j++) {
            neuron = new Neuron(numInputs, activationFunction, backPropFactor);
            layers.get(0).addNeuron(neuron);
            layers.get(0).getNeuron(j).setRandomWeights(); //инициализируем веса случайно
        }
        layers.get(0).initializeLayer();


        //проходим по остальным слоям НС, добавляем в них нейроны с нужными характеристиками
        for (int i = 1; i < numberOfHiddenLayers; i++) {
            for (int j = 0; j < sizeOfLayers.get(i); j++) {
                neuron = new Neuron(sizeOfLayers.get(i-1), activationFunction, backPropFactor);
                layers.get(i).addNeuron(neuron);
                layers.get(i).getNeuron(j).setRandomWeights(); //инициализируем веса случайно
            }
            layers.get(i).initializeLayer();
        }

        //создаем список массивов, в котором будут храниться промежуточные вычисления на всех слоях
        double[] tempArrayInit;
        for (int i = 0; i < layers.size(); i++) {
            tempArrayInit = new double[layers.get(i).getLayerSize()];
            listOfTempArrays.add(tempArrayInit);
        }
    }



    public void setData(List<double[]> inputs, List<double[]> outputs) {
        /** добавление данных в НС (автоматом сформирует тест/обуч выборку) **/

        inputData = inputs;
        outputData = outputs;

        /*
            создать алгоритм случайного разбиения на тестовую и обучающую выборку
         */
        inputDataLearning = inputs;
        outputDataLearning = outputs;
        inputDataTesting = inputs;
        outputDataTesting = outputs;

        /*
            мб сразу сформировать массив номеров победителей
         */
    }



    public void setDataLearning(List<double[]> inputs, List<double[]> outputs) {
        /** добавление обучающих данных в НС **/
        inputDataLearning = inputs;
        outputDataLearning = outputs;
        /*
            мб сразу сформировать массив номеров победителей
         */
    }



    public void setDataTesting(List<double[]> inputs, List<double[]> outputs) {
        /** добавление тестовых данных в НС **/
        inputDataTesting = inputs;
        outputDataTesting = outputs;
        /*
            мб сразу сформировать массив номеров победителей
         */
    }



    public void learning() {
        /** запуск обучения, причём условие остановки берётся из файла **/

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        int winnerCount; //счетчик количества побед
        int loserCount; //счетчик количества поражений


        for (int epoch = 0; epoch < epochNumber; epoch++) {

            //перебираем массив обучающей выборки
            for (int i = 0; i < inputDataLearning.size(); i++) {
                //берем вход и запускаем сингл пасс с обучением
                singleLearningPass(inputDataLearning.get(i), outputDataLearning.get(i));
            }

            winnerCount = 0;
            loserCount = 0;
            //перебираем массив тестовой выборки
            for (int i = 0; i < inputDataTesting.size(); i++) {
                winnerNode = singlePassCompetitiveOutputValue(inputDataTesting.get(i));
                desireWinnerNode = checkWinningNode(outputDataTesting.get(i));
                if (winnerNode == desireWinnerNode) {
                    winnerCount++;
                }
                else {
                    loserCount++;
                }
            }
            winRate = winnerCount/(winnerCount+loserCount);
            System.out.println("Winrate = "+winRate+". Epoch = "+epoch);
        }
    }



    public void learning(int epochExtend) {
        /** запуск обучения, условие остановки - кол-во эпох **/

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        double winnerCount; //счетчик количества побед
        double loserCount; //счетчик количества поражений
        double winRate;

        for (int epoch = 0; epoch < epochExtend; epoch++) {

            //перебираем массив обучающей выборки
            for (int i = 0; i < inputDataLearning.size(); i++) {
                //берем вход и запускаем сингл пасс с обучением
                singleLearningPass(inputDataLearning.get(i), outputDataLearning.get(i));
            }

            winnerCount = 0;
            loserCount = 0;
            //перебираем массив тестовой выборки
            for (int i = 0; i < inputDataTesting.size(); i++) {
                winnerNode = singlePassCompetitiveOutputValue(inputDataTesting.get(i));
                desireWinnerNode = checkWinningNode(outputDataTesting.get(i));
                if (winnerNode == desireWinnerNode) {
                    winnerCount++;
                }
                else {
                    loserCount++;
                }
                /*if ((i == 0)||(i == 10)) {
                    tempArrayBackProp = layers.get(2).getLastOutput();
                    System.out.print("i ="+i+"; Output: ");
                    for (int j = 0; j < tempArrayBackProp.length; j++) {
                        System.out.print(tempArrayBackProp[j]+":");
                    }
                    System.out.print(" Desire Out: ");
                    tempArrayBackProp = outputDataTesting.get(i);
                    for (int j = 0; j < tempArrayBackProp.length; j++) {
                        System.out.print(tempArrayBackProp[j]+":");
                    }
                    System.out.print("\n");
                }*/
            }
            winRate = winnerCount/(winnerCount+loserCount);
            System.out.println("Winrate = "+winRate+"; Epoch = "+epoch);
        }
    }



    public void learning(int epochExtend, int winRateDesire) {
        /** запуск обучения, условие остановки - кол-во эпох или винрейт **/

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        int winnerCount; //счетчик количества побед
        int loserCount; //счетчик количества поражений
        double winRate = 0;

        for (int epoch = 0; ((epoch < epochExtend)||(winRate >= winRateDesire)); epoch++) {

            //перебираем массив обучающей выборки
            for (int i = 0; i < inputDataLearning.size(); i++) {
                //берем вход и запускаем сингл пасс с обучением
                singleLearningPass(inputDataLearning.get(i), outputDataLearning.get(i));
            }

            winnerCount = 0;
            loserCount = 0;
            //перебираем массив тестовой выборки
            for (int i = 0; i < inputDataTesting.size(); i++) {
                winnerNode = singlePassCompetitiveOutputValue(inputDataTesting.get(i));
                desireWinnerNode = checkWinningNode(outputDataTesting.get(i));
                if (winnerNode == desireWinnerNode) {
                    winnerCount++;
                }
                else {
                    loserCount++;
                }
            }
            winRate = winnerCount/(winnerCount+loserCount);
            System.out.println("Winrate = "+winRate+". Epoch = "+epoch);
        }
    }



    public double[] singlePass(double[] input) {
        /** одиночный проход данных через нейронную сеть и выдача вектора выходов **/

        //System.arraycopy(array,0,inputArray,0,sizeOfInputs);
        //listOfTempArrays.set(0, layers.get(0).layerCalculation(input)); //могут быть проблемы с адресами массивов
        tempArrayBackProp = listOfTempArrays.get(0);
        System.arraycopy(layers.get(0).layerCalculation(input), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        for (int i = 1; i < layers.size(); i++) {
            //listOfTempArrays.set(i, layers.get(i).layerCalculation(listOfTempArrays.get(i-1))); //тут тоже
            tempArrayBackProp = listOfTempArrays.get(i);
            System.arraycopy(layers.get(i).layerCalculation(listOfTempArrays.get(i-1)), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        }
        return listOfTempArrays.get(numberOfHiddenLayers-1);
    }



    public int singlePassCompetitiveOutputValue(double[] input) {
        /** одиночный проход данных через по принципу победитель получает всё и выдача номера узла победителя**/
        tempArrayBackProp = listOfTempArrays.get(0);
        System.arraycopy(layers.get(0).layerCalculation(input), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        for (int i = 1; i < layers.size(); i++) {
            tempArrayBackProp = listOfTempArrays.get(i);
            System.arraycopy(layers.get(i).layerCalculation(listOfTempArrays.get(i-1)), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        }
        return checkWinningNode(listOfTempArrays.get(numberOfHiddenLayers-1));
    }



    public double[] singlePassCompetitiveOutputVector(double[] input) {
        /** одиночный проход данных через по принципу победитель получает всё и выдача выходного вектора**/
        listOfTempArrays.set(0, layers.get(0).layerCalculation(input)); //могут быть проблемы с адресами массивов
        for (int i = 1; i < numberOfHiddenLayers; i++) {
            listOfTempArrays.set(i, layers.get(i).layerCalculation(listOfTempArrays.get(i-1))); //тут тоже
        }
        int winner = checkWinningNode(listOfTempArrays.get(numberOfHiddenLayers-1));
        for (int i = 0; i < numberOfOutputs; i++) {
            tempOutputArray[i] = 0;
        }
        tempOutputArray[winner] = 1;
        return tempOutputArray;
    }



    public double[] singlePassOutputErrorVector(double[] input, double[] desireOutput) {
        /** одиночный проход данных и выдача вектора ошибок**/
        listOfTempArrays.set(0, layers.get(0).layerCalculation(input)); //могут быть проблемы с адресами массивов
        for (int i = 1; i < numberOfHiddenLayers; i++) {
            listOfTempArrays.set(i, layers.get(i).layerCalculation(listOfTempArrays.get(i-1))); //тут тоже
        }
        for (int i = 0; i < numberOfOutputs; i++) {
            tempOutputArray[i] = listOfTempArrays.get(numberOfHiddenLayers)[i] - desireOutput[i];
        }
        return tempOutputArray;
    }



    public void singleLearningPass(double[] input, double[] desireOutput) {
        /** одиночных проход данных с обучением **/

        tempArrayBackProp = listOfTempArrays.get(0);
        System.arraycopy(layers.get(0).layerCalculation(input), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        for (int i = 1; i < numberOfHiddenLayers; i++) {
            tempArrayBackProp = listOfTempArrays.get(i);
            System.arraycopy(layers.get(i).layerCalculation(listOfTempArrays.get(i-1)), 0, tempArrayBackProp, 0, tempArrayBackProp.length);
        }
        tempOutputArray = listOfTempArrays.get(numberOfHiddenLayers-1); //по идее, мы заранее знаем размер выходного массива, нужно убрать
        backSinglePass(tempOutputArray, desireOutput);
    }

    private void backSinglePass(double[] outputs, double[] ans) /*+*/{
        /*
            метод выполняющий одну итерацию обратного распространения для обучения
            сети
         */
        //double[] tempArrayBackProp = outputs;
        layers.get(numberOfHiddenLayers-1).backPropagation(outputs, ans);
        outputs = layers.get(numberOfHiddenLayers-1).getSumOfErr();
        for (int i = numberOfHiddenLayers-2; i >= 0; i--) {
            layers.get(i).backPropagation(outputs);
            outputs = layers.get(i).getSumOfErr();
        }
    }



    public int checkWinningNode(double[] output) {
        int numOfMax = 0;
        double maxValue = output[0];
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxValue) {
                maxValue = output[i];
                numOfMax = i;
            }
        }
        return numOfMax;
    }

    public double getWinRate() {
        return winRate;
    }







    public boolean setParameter(String parameterName, String parameterValue) {
        /** запись параметра вручную **/
        return true;
    }
}
