package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private float trainingTestRatio = 0.7f;
    private int epochNumber;
    private ArrayList<Integer> structure;
    private int numberOfHiddenLayers;
    private String activationFunction = "sigmoid";
    private float backPropFactor = 0.05f;
    private boolean errorStatus;
    private List<Integer> sizeOfLayers = new ArrayList<>();

    //условие остановки алгоритма, где 2 - по кол-ву эпох, 1 - по кол-ву правильно угаданных текстов, 0 - оба условия
    private int stopCondition = 0;

    private float winRateCondition = 0.99f; //желаемое соотношение правильно угаданных текстов
    ////
    private int numberOfOutputs;
    private double[] tempOutputArray;


    //// слои нейронов
    private Layer hiddenLayer;
    private List<Layer> layers = new ArrayList<>();
    ////


    private double[] inputSignals;
    private double[] tempArrayBackProp;

    float winRate;

    //// списки с входными и выходными данными НС
    private List<double[]> inputDataLearning;
    private List<double[]> outputDataLearning;
    private List<double[]> inputDataTesting;
    private List<double[]> outputDataTesting;
    /////

    private List<double[]> listOfTempArrays = new ArrayList<>();


    //массивы для выходных данных графика
    private double[] epochArray; //массив с номерами эпох (для промежуточного хранения данных)
    private double[] winRateArray; //массив с винрейтом (для промежуточного хранения данных)
    private int plotCounter = 0; //счетчик текущего номера элемента массива
    double[] newEpochArray; //массив с номерами эпох (для итогового вывода данных)
    double[] newWinRateArray; //массив с винрейтом (для итогового вывода данных)

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
                    case "StopCondition":
                        stopCondition = Integer.parseInt(ar[1]);
                        break;
                    case "Winrate":
                        winRateCondition = Float.parseFloat(ar[1]);
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
        // запуск обучения, причём условие остановки берётся из файла

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        float winnerCount; //счетчик количества побед
        float loserCount; //счетчик количества поражений
        int lastEpoch = 0; //переменная хранит номер эпохи, на которой алгоритм был остановлен

        //объявляем размер массивов для выходных данных графика
        epochArray = new double[epochNumber];
        winRateArray = new double[epochNumber];


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
            saveDataToPlot(epoch, winRate);
            System.out.println("Winrate = "+winRate+". Epoch = "+epoch);
            if (stopCondition != 2) {
                if (winRate >= winRateCondition) {
                    System.out.println("Winrate condition is satisfied!");
                    //сохранить метку, на которой произошла остановка
                    lastEpoch = epoch;
                    break;
                }
            }
        }

        if (lastEpoch == 0) {
            //если выход из цикла произошёл по окончанию кол-ва эпох,
            //то отправить все данные на печать
            pushDataToPlot();
        }
        else {
            //если выход из цикла произошёл по условию получения нужного
            //количества соотношения угаданных текстов, то отправить
            //метку с номером последней эпохи и отправить данные на печать
            pushDataToPlot(lastEpoch);
        }
    }



    public void learning(int epochExtend) {
        // запуск обучения, условие остановки - кол-во эпох **/

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        float winnerCount; //счетчик количества побед
        float loserCount; //счетчик количества поражений
        int lastEpoch = 0; //переменная хранит номер эпохи, на которой алгоритм был остановлен

        //объявляем размер массивов для выходных данных графика
        epochArray = new double[epochExtend];
        winRateArray = new double[epochExtend];


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
            }
            winRate = winnerCount/(winnerCount+loserCount);
            saveDataToPlot(epoch, winRate);
            System.out.println("Winrate = "+winRate+". Epoch = "+epoch);
            if (stopCondition != 2) {
                if (winRate >= winRateCondition) {
                    System.out.println("Winrate condition is satisfied!");
                    //сохранить метку, на которой произошла остановка
                    lastEpoch = epoch;
                    break;
                }
            }
        }

        if (lastEpoch == 0) {
            //если выход из цикла произошёл по окончанию кол-ва эпох,
            //то отправить все данные на печать
            pushDataToPlot();
        }
        else {
            //если выход из цикла произошёл по условию получения нужного
            //количества соотношения угаданных текстов, то отправить
            //метку с номером последней эпохи и отправить данные на печать
            pushDataToPlot(lastEpoch);
        }
    }



    public void learning(int epochExtend, float winRateDesire) {
        // запуск обучения, условие остановки - кол-во эпох или винрейт **/

        int winnerNode; //номер узла победителя
        int desireWinnerNode; //номер желаемого узла победителя
        float winnerCount; //счетчик количества побед
        float loserCount; //счетчик количества поражений
        int lastEpoch = 0; //переменная хранит номер эпохи, на которой алгоритм был остановлен

        //объявляем размер массивов для выходных данных графика
        epochArray = new double[epochExtend];
        winRateArray = new double[epochExtend];


        for (int epoch = 0; ((epoch < epochExtend)&&(winRate < winRateDesire)); epoch++) {

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
            saveDataToPlot(epoch, winRate);
            System.out.println("Winrate = "+winRate+". Epoch = "+epoch);
            if (stopCondition != 2) {
                if (winRate >= winRateCondition) {
                    System.out.println("Winrate condition is satisfied!");
                    //сохранить метку, на которой произошла остановка
                    lastEpoch = epoch;
                    break;
                }
            }
        }

        if (lastEpoch == 0) {
            //если выход из цикла произошёл по окончанию кол-ва эпох,
            //то отправить все данные на печать
            pushDataToPlot();
        }
        else {
            //если выход из цикла произошёл по условию получения нужного
            //количества соотношения угаданных текстов, то отправить
            //метку с номером последней эпохи и отправить данные на печать
            pushDataToPlot(lastEpoch);
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


    private void saveDataToPlot(double x, double y) {
        //метод сохраняет данные для отправки на печать
        epochArray[plotCounter] = x;
        winRateArray[plotCounter] = y;
        plotCounter++;
    }


    private void pushDataToPlot(int epoch) {
        //метод отправляет нужное количество данных на печать
        newEpochArray = new double[epoch];
        newWinRateArray = new double[epoch];

        for (int i = 0; i < epoch; i++) {
            newEpochArray[i] = epochArray[i];
            newWinRateArray[i] = winRateArray[i];
        }

    }

    private void pushDataToPlot() {
        //метод отправляет все данные на печать
        newEpochArray = epochArray;
        newWinRateArray = winRateArray;
    }


    public void createReport(String reportPath) {


        try {
            Plot plot = Plot.plot(null).
                    series(null, Plot.data().
                            xy(newEpochArray, newWinRateArray), null);

            plot.save(reportPath + "learningGraph", "png");
        }
        catch (IOException e) {
            System.err.format("IOException. File plot error");
        }
    }



    public boolean setParameter(String parameterName, String parameterValue) {
        /** запись параметра вручную **/
        return true;
    }
}
