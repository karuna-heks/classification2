package com.company;


import java.util.*;

//import static com.company.Main.numberToArray;

public class textAnalyzer {
    /*
        класс имеет методы для анализа локальных словарей,
        которые он получает из списка текстов listOfTexts (списка
        с объектами класса Text, которые уже должны содержать
        локальные словари
     */


    private List<Text> listOfTexts;
    private String numericalStatisticType = "quantity"; //вид статистической меры (tfidf, tf, quantity)
    private float tfidfCoefficient = 0.3f;
    private HashMap<String, Integer> globalDictionary;
    private int numberOfThemes;
    private float trainingTestRatio = 0.7f;

    List<double[]> dataLearningInputs = new ArrayList<>();
    List<double[]> dataTestingInputs = new ArrayList<>();
    List<double[]> dataLearningOutputs = new ArrayList<>();
    List<double[]> dataTestingOutputs = new ArrayList<>();


    public void analyzerInit(List<Text> listOfTexts) {
        this.listOfTexts = listOfTexts;
    }

    public void analyzerInit(List<Text> listOfTexts, String numericalStatisticType) {
        this.listOfTexts = listOfTexts;
        this.numericalStatisticType = numericalStatisticType;
    }

    public void analyzerInit(List<Text> listOfTexts, String numericalStatisticType, float tfidfCoefficient) {
        this.listOfTexts = listOfTexts;
        this.numericalStatisticType = numericalStatisticType;
        this.tfidfCoefficient = tfidfCoefficient;
    }

    public void startAnalyze() {


        //создать словарь всех слов, наполнить его из списка listOfTexts
        globalDictionary = new HashMap<>();

        System.out.println("Формирование глобального словаря...");
        for (int i = 0; i < listOfTexts.size(); i++) {
            //проход для формирования глобального словаря
            HashMap<String, Integer> localDictionary = listOfTexts.get(i).getDictionary();
            for  (Map.Entry<String, Integer> entry : localDictionary.entrySet()) {
                String tempKey = entry.getKey();
                if (globalDictionary.containsKey(tempKey)) {
                    //если этот ключ уже есть в глобальном словаре
                    //то увеличиваем счётчик встречаемости этого слова в корпусе
                    Integer tempValGlob = globalDictionary.get(tempKey);
                    Integer tempValLoc = entry.getValue();
                    globalDictionary.replace(tempKey, tempValGlob + tempValLoc);
                }
                else {
                    //если этого слова ещё нет, то добавляем его
                    //и запоминаем, сколько раз оно встретилось
                    Integer tempValLoc = entry.getValue();
                    globalDictionary.put(tempKey, tempValLoc);
                }
            }
            //for (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
            //    System.out.println(entry.getKey() + " : " + entry.getValue());
            //}
            //System.out.println("End of global dictionary.");
        }

        //System.out.println("Global dictionary size: "+ globalDictionary.size());
        //System.out.println("Global dictionary: ");


        System.out.println("Формирование векторов текстов...");



        //формирование вектора текста будет происходить на основе одной из выбранных
        //статистических мер. (количественная, частотная, tfidf)
        //тут происходит выполнение алгоритма формирования вектора на основе выбранной меры,
        //имя которой должно храниться в переменной numericalStatisticType
        switch(numericalStatisticType) {
            case "quantity": //количественная мера
                quantityMeasureCalc(); //запуск алгоритма формирования вектора текста на основе количественной меры
                break;
            case "tfidf": //отношение числа вхождений некоторого слова к общему числу слов документа
                tfidfMeasureCalc();
                break;
            case "tf": //частотная мера
                tfMeasureCalc();
                break;
            default: //если параметр выбран неверный либо он оказался пуст, то вывести предупреждение и выбрать
                //меру по умолчанию
                System.out.println("Warning. Default case: numericalStatisticType");
                quantityMeasureCalc();
                break;
        }


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
            //цикл подсчета количества текстов на каждую тему
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
                ratio[0] = 1; //параметр количества текстов на данную тему
                ratio[1] = 0; //параметр кол-ва текстов обуч выборки
                ratio[2] = 0; //параметр кол-ва текстов тестовой
                numOfTopics.add(topicCount, ratio);
                //инициализируем счётчик количества текстов для новой темы
                topicCount++;
            }
        }


        for (int i = 0; i < numOfTopics.size(); i++) {
            //цикл подсчёта количества обучающих и тестовых образцов текстов
            numOfTopics.get(i)[1] = (int)Math.ceil(numOfTopics.get(i)[0]*trainingTestRatio);
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

        }


        /*
            формируем списки List<double[]> inputs, List<double[]> outputs
            которые являются списками входных и выходных векторов НС
         */

        double[] tempArray;
        for (int i = 0; i < learnNumbers.size(); i++) {
            int tempVal = learnNumbers.get(i);
            dataLearningInputs.add(listOfTexts.get(tempVal).getVector());
            tempArray = numberToArray(topicNumber.get(listOfTexts.get(tempVal).getTheme()), numberOfThemes);
            dataLearningOutputs.add(tempArray);
        }

        for (int i = 0; i < testNumbers.size(); i++) {
            int tempVal = testNumbers.get(i);
            dataTestingInputs.add(listOfTexts.get(tempVal).getVector());
            tempArray = numberToArray(topicNumber.get(listOfTexts.get(tempVal).getTheme()), numberOfThemes);
            dataTestingOutputs.add(tempArray);
        }
    }


    public List<double[]> getDataLearningInputs() {
        return dataLearningInputs;
    }

    public List<double[]> getDataTestingInputs() {
        return dataTestingInputs;
    }

    public List<double[]> getDataLearningOutputs() {
        return dataLearningOutputs;
    }

    public List<double[]> getDataTestingOutputs() {
        return dataTestingOutputs;
    }

    public void shuffleDataLearning() {
        //метод для перемешивания элементов в списках векторов для обучения

        Random rnd = new Random();

        for (int i = 0; i < dataLearningInputs.size(); i++) {
            int index = rnd.nextInt(i+1);
            double[] arr1 = dataLearningInputs.get(index);
            double[] arr2 = dataLearningOutputs.get(index);

            dataLearningInputs.set(index, dataLearningInputs.get(i));
            dataLearningOutputs.set(index, dataLearningOutputs.get(i));

            dataLearningInputs.set(i, arr1);
            dataLearningOutputs.set(i, arr2);
        }
    }

    public void shuffleDataTesting() {
        //метод для перемешивания элементов в списках векторов для тестирования

        Random rnd = new Random();

        for (int i = 0; i < dataTestingInputs.size(); i++) {
            int index = rnd.nextInt(i+1);
            double[] arr1 = dataTestingInputs.get(index);
            double[] arr2 = dataTestingOutputs.get(index);

            dataTestingInputs.set(index, dataTestingInputs.get(i));
            dataTestingOutputs.set(index, dataTestingOutputs.get(i));

            dataTestingInputs.set(i, arr1);
            dataTestingOutputs.set(i, arr2);
        }
    }

    public void setParameter(String name, String value) {
        switch (name) {
            case "numericalStatisticType":
                numericalStatisticType = value;
                break;
            default:
                break;
        }
    }

    public void setParameter(String name, float value) {

        switch (name) {
            case "tfidfCoefficient":
                tfidfCoefficient = value;
                break;
            case "trainingTestRatio":
                trainingTestRatio = value;
                break;
            default:
                break;
        }
    }

    public void setParameter(String name, int value) {

        switch (name) {
            case "numberOfThemes":
                numberOfThemes = value;
        }
    }

    public HashMap<String, Integer> getGlobalDictionary() {
        return globalDictionary;
    }


    public double[] numberToArray(int number, int arraySize) {
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

    private void quantityMeasureCalc() {
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
    }

    private void tfMeasureCalc() {
        for (Text t : listOfTexts) {
            //проход для формирования векторов текстов
            double[] textVector = new double[globalDictionary.size()];
            HashMap<String, Integer> localDictionary = t.getDictionary();
            int i = 0;
            for  (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
                String tempKey = entry.getKey();
                if (localDictionary.containsKey(tempKey)) {
                    textVector[i] = (double)localDictionary.get(tempKey)*100/globalDictionary.size();
                }
                else {
                    textVector[i] = 0;
                }
                i++;
            }
            t.setVector(textVector);
        }
    }

    private void tfidfMeasureCalc() {

        double tf, idf;
        double wordInDocCount;
        for (Text t : listOfTexts) {
            //проход для формирования векторов текстов
            double[] textVector = new double[globalDictionary.size()];
            HashMap<String, Integer> localDictionary = t.getDictionary();
            int i = 0;
            for  (Map.Entry<String, Integer> entry : globalDictionary.entrySet()) {
                String tempKey = entry.getKey();
                if (localDictionary.containsKey(tempKey)) {
                    //textVector[i] = (double)localDictionary.get(tempKey)*100/globalDictionary.size();
                    tf = (double)localDictionary.get(tempKey)*100/globalDictionary.size();
                    wordInDocCount = 0;
                    for (Text t2 : listOfTexts) {
                        if (t2.getDictionary().containsKey(tempKey)) {
                            wordInDocCount++;
                        }
                    }
                    idf = Math.log10(listOfTexts.size()/wordInDocCount);
                    textVector[i] = tf*idf;
                }
                else {
                    textVector[i] = 0;
                }
                i++;
            }
            t.setVector(textVector);
        }

    }
}
