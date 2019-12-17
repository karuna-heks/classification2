package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NeuralNetwork {
    /*
        нейронная сеть, которая содержит всю информацию о НС
        и выполняет все вычисления внутри сети
     */




    private ReadSettings settings;
    //объект с настройками

    private double[] inputSignals;
    //вспомогательный массив с входными сигналами НС

    private List<Neuron> neuronList1;
    private List<Neuron> neuronList2;
    private List<Neuron> neuronList3;
    private List<Neuron> neuronList4;
    //слои нейронов


    //private List<Integer> listOfTraining = new ArrayList<>();


    private int[] ratio;
    /*
        вспомогательный массив для определения
        обучающей/тестовой выборки.
        нулевой элемент -- количество текстов с данной темой
        1й элемент -- размер обучающей выборки
        2й элемент -- размер тестовой выборки
     */


    //HashMap<String, int[]> themesRatio = new HashMap<>();
    private HashMap<String, Integer> topicNumber = new HashMap<>();
    //topicNumber - ключ - имя темы, число - номер темы в
    //списке numOfTopics. также этот номер должен соответствовать
    //номерам выходных нейронов каждой темы

    private List<int[]> numOfTopics = new ArrayList<>();
    //numOfTopics - [0] - кол-во текстов
    //[1] - кол-во обуч. текстов, [2] - кол-во тестовых текстов


    private List<Integer> learnNumbers = new ArrayList<>();
    //массив с номерами текстов обучающей выборки
    private List<Integer> testNumbers = new ArrayList<>();
    //массив с номерами текстов тестовой выборки


    private double learningSpeed; //скорость обучения
    private int epochSetpoint; //количество эпох максимальное

    private int winnerCount = 0; //кол-во побед
    private int loseCount = 0; //кол-во поражений
    private double winRate = 0; //% побед

    private int sizeOfLayer1;
    private int sizeOfLayer2;
    private int sizeOfLayer3;
    private int sizeOfLayer4;




    public NeuralNetwork(ReadSettings settings, int inputNumber, int outputNumber) {

        /*
            конструктор класса NeuralNetwork содержит инициализацию
            архитектуры нейронной сети
         */

        this.settings = settings; //получаем настройки НС

        inputSignals = new double[inputNumber];
        learningSpeed = settings.getBackPropFactor();
        epochSetpoint = settings.getEpochNumber();




        switch (settings.getNumberOfHiddenLayers()) {
            /*
                возможно, стоит предусмотреть ещё и удаление лишних списков
                с нейронами
             */
            case 1:
                sizeOfLayer1 = outputNumber;
                sizeOfLayer2 = 0;
                sizeOfLayer3 = 0;
                sizeOfLayer4 = 0;
                neuronList1 = new ArrayList<>();
                neuronList2 = new ArrayList<>();
                neuronList3 = new ArrayList<>();
                neuronList4 = new ArrayList<>();

                break;
            case 2:
                sizeOfLayer1 = settings.getStructure().get(0);
                sizeOfLayer2 = outputNumber;
                sizeOfLayer3 = 0;
                sizeOfLayer4 = 0;
                neuronList1 = new ArrayList<>();
                neuronList2 = new ArrayList<>();
                neuronList3 = new ArrayList<>();
                neuronList4 = new ArrayList<>();
                break;
            case 3:
                sizeOfLayer1 = settings.getStructure().get(0);
                sizeOfLayer2 = settings.getStructure().get(1);
                sizeOfLayer3 = outputNumber;
                sizeOfLayer4 = 0;
                neuronList1 = new ArrayList<>();
                neuronList2 = new ArrayList<>();
                neuronList3 = new ArrayList<>();
                neuronList4 = new ArrayList<>();
                break;
            case 4:
                sizeOfLayer1 = settings.getStructure().get(0);
                sizeOfLayer2 = settings.getStructure().get(1);
                sizeOfLayer3 = settings.getStructure().get(2);
                sizeOfLayer4 = outputNumber;
                neuronList1 = new ArrayList<>();
                neuronList2 = new ArrayList<>();
                neuronList3 = new ArrayList<>();
                neuronList4 = new ArrayList<>();
                break;
            default:
                sizeOfLayer1 = outputNumber;
                sizeOfLayer2 = 0;
                sizeOfLayer3 = 0;
                sizeOfLayer4 = 0;
                neuronList1 = new ArrayList<>();
                neuronList2 = new ArrayList<>();
                neuronList3 = new ArrayList<>();
                neuronList4 = new ArrayList<>();
                System.out.println("warning");
                break;
        }


        Neuron inputNeuron;
        Neuron secondNeuron;
        Neuron thirdNeuron;
        Neuron forthNeuron;
        // создаём один объек нейрона для отправки его в список нейронов
        /*
            заполняем список нейронами и даём им случайные веса
            необходимо это проделать для всех списков и всех нейронов
            с учётом количества слоёв нейронной сети
         */

        for (int i = 0; i < sizeOfLayer1; i++) {
            inputNeuron = new Neuron(inputNumber, settings.getActivationFunction(), learningSpeed);
            neuronList1.add(i, inputNeuron);
            neuronList1.get(i).setRandomWeights();
        }

        for (int i = 0; i < sizeOfLayer2; i++) {
            secondNeuron = new Neuron(sizeOfLayer1, settings.getActivationFunction(), learningSpeed);
            neuronList2.add(i, secondNeuron);
            neuronList2.get(i).setRandomWeights();
        }

        for (int i = 0; i < sizeOfLayer3; i++) {
            thirdNeuron = new Neuron(sizeOfLayer2, settings.getActivationFunction(), learningSpeed);
            neuronList3.add(i, thirdNeuron);
            neuronList3.get(i).setRandomWeights();
        }

        for (int i = 0; i < sizeOfLayer4; i++) {
            forthNeuron = new Neuron(sizeOfLayer3, settings.getActivationFunction(), learningSpeed);
            neuronList4.add(i, forthNeuron);
            neuronList4.get(i).setRandomWeights();
        }
    }




    public String reportMessages() {
        /*
            метод reportMessages должен отправлять сообщения о возникших
            ошибках. при этом, каждый новый вызов должен отправлять новую
            ошибку до тех пор, пока все ошибки не будут исчерпаны
         */

        /*
            необходимо сделать метод, сформировать тексты ошибок и сформировать
            условия, при которых они возникают
         */

        return "test";
    }






    public void learning(HashMap<String, Integer> dictionary, List<Text> listOfTexts) {
        /*
            метод содержащий полный алгоритм обучения до выполнения
            условия остановки
         */
        
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
            //цикл подсчёта количество обучающих и тестовых образцов текстов
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


        int outputCount = 0;
        for (int epoch = 0; epoch < epochSetpoint; epoch++) {
            //цикл самого процесса обучения
            //на данном этапе только кол-во эпох является условием остановки

            for (int i = 0; i < learnNumbers.size(); i++) {
                int tempVal = learnNumbers.get(i);
                inputSignals = listOfTexts.get(tempVal).getVector();
                backSinglePass(singlePass(inputSignals), getAns(listOfTexts.get(tempVal)));
            }

            winnerCount = 0;
            loseCount = 0;
            winRate = 0;
            for (int i = 0; i < testNumbers.size(); i++) {
                //на вход метода поступает количество пройденных эпох и массив с тестовыми выборками
                int tempVal = testNumbers.get(i);
                inputSignals = listOfTexts.get(tempVal).getVector();
                if (checkWinnerNum(singlePass(inputSignals)) == getAnsNum(listOfTexts.get(tempVal))) {
                    winnerCount++;
                }
                else {
                    loseCount++;
                }

            }

            if (outputCount == 10) {
                winRate = winnerCount*100/(winnerCount+loseCount);
                System.out.println("Epoch = "+epoch+". Winrate = "+winRate+"%");
                outputCount = 0;
                continue;
            }
            outputCount++;

            //winRate = winnerCount*100/(winnerCount+loseCount);
            //System.out.println("Epoch = "+epoch+". Winrate = "+winRate+"%");

        }

        for (int i = 0; i < neuronList2.size() ; i++) {
            System.out.println("Weight "+ (i+1) + ": "+neuronList2.get(i).getWeightsSum());
        }

        for (int i = 0; i < testNumbers.size(); i++) {
            //на вход метода поступает количество пройденных эпох и массив с тестовыми выборками
            int tempVal = testNumbers.get(i);
            inputSignals = listOfTexts.get(tempVal).getVector();
            System.out.print("Победитель: "+checkWinnerNum(singlePass(inputSignals))+":");
            System.out.println(getAnsNum(listOfTexts.get(tempVal)));


        }


    }


    private double[] singlePass(double[] input) {
        /*
            метод выполняющий 1 полный прямой проход нейронной сети
            и выдающий вектор результатов
         */

        /*
            переделать output'ы
         */

        switch (settings.getNumberOfHiddenLayers()) {
            case 1:
                //выполнить проход с одним слоем НС и передать управление
                //на метод, обработывающий победителя
                double[] output = layerCalculation(input, neuronList1);
                return output;
                //neuronList1 = new ArrayList<>(outputNumber);
            case 2:
                //выполнить проходы с двумя слоями НС и передать управление
                //на метод, обработывающий победителя
                double[] output21 = layerCalculation(input, neuronList1);
                double[] output22 = layerCalculation(output21, neuronList2);
                return output22;

                //neuronList1 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList2 = new ArrayList<>(outputNumber);
            case 3:
                //выполнить проходы с тремя слоями НС и передать управление
                //на метод, обработывающий победителя
                double[] output31 = layerCalculation(input, neuronList1);
                double[] output32 = layerCalculation(output31, neuronList2);
                double[] output33 = layerCalculation(output32, neuronList3);
                return output33;

                //neuronList1 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList2 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList3 = new ArrayList<>(outputNumber);
            case 4:
                //выполнить проходы с четыремя слоями НС и передать управление
                //на метод, обработывающий победителя
                double[] output41 = layerCalculation(input, neuronList1);
                double[] output42 = layerCalculation(output41, neuronList2);
                double[] output43 = layerCalculation(output42, neuronList3);
                double[] output44 = layerCalculation(output43, neuronList4);
                return output44;

                //neuronList1 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList2 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList3 = new ArrayList<>(settings.getNumberOfNeurons());
                //neuronList4 = new ArrayList<>(outputNumber);
            default:
                //neuronList1 = new ArrayList<>(outputNumber);
                double[] output1 = layerCalculation(input, neuronList1);
                System.out.println("warning");
                return output1;
        }
        /*
            мб следует разделить весь участок на этапы, где
            началом каждого этапа будет являться определение входов слоя
            а окончанием -- вычисление выходов слоя
         */
    }

    private int[] getAns(Text text) {
        /*
            метод выдаёт вектор желаемых ответов нейронной сети
            (все элементы = 0; элемент, номер которого соответствует
            теме текста text, равен 1)
         */
        int ansNumber = topicNumber.get(text.getTheme());
        int[] ans = new int[topicNumber.size()];
        Arrays.fill(ans, 0);
        ans[ansNumber] = 1;
        return ans;
    }

    private int getAnsNum(Text text) {
        /*
            метод выдаёт номер темы
         */
        return topicNumber.get(text.getTheme());
    }

    private double[] layerCalculation(double[] input, List<Neuron> neuronList) /*+*/{
        /* +
            метод выполнящий вычисление одного слоя нейронной сети
            на вход должен подаваться список с нейронами слоя
         */
        double[] a = new double[neuronList.size()];
        for (int i = 0; i < neuronList.size(); i++) {
            a[i] = neuronList.get(i).calcNeuron(input);
        }
        return a;
    }

    private int[] checkWinner(double[] input) /*+-*/{
        /* +-
            метод проходит по массиву input, ищет индекс максимального
            элемента. создаёт массив output, размер которого совпадает
            с размером массива input. обнуляет все элементы.
            у элемента, индекс которого равняется индексу максимального
            элемента массива input, значение делает равное 1
         */

        int[] output = new int[input.length];
        int indexOfMax = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] > input[indexOfMax]) {
                indexOfMax = i;
            }
        }

        Arrays.fill(output, 0);
        output[indexOfMax] = 1;
        return output;

    }

    private int checkWinnerNum(double[] input) {
        /* +
            метод получает вектор выходов НС и выдаёт
            номер темы победителя
         */
        int[] output = new int[input.length];
        int indexOfMax = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] > input[indexOfMax]) {
                indexOfMax = i;
            }
        }
        return indexOfMax;
    }

    private double sumOfErr(List<Neuron> neuronList, int neuronIndex) /*+*/{
        /* +
            функция вычисляет ошибку для нейронов скрытого слоя,
            забирая произведение (dW на вес) последующего нейрона
         */
        double out = 0;
        for (int i = 0; i < neuronList.size(); i++) {
            out = out + neuronList.get(i).getError(neuronIndex);
        }
        return out;
    }

    private void backSinglePass(double[] outputs, int[] ans) /*+*/{
       /*
            метод выполняющий одну итерацию обратного распространения для обучения
            сети
         */

        double[] err;
        switch (settings.getNumberOfHiddenLayers()) {
            case 1:
              for (int i = 0; i < neuronList1.size(); i++) {
                  neuronList1.get(i).backPropNeuron(ans[i] - outputs[i]);
              }
              break;
            case 2:
               for (int i = 0; i < neuronList2.size(); i++) {
                   neuronList2.get(i).backPropNeuron(ans[i] - outputs[i]);
               }
               err = new double[neuronList1.size()]; //neuronList1
               for (int i = 0; i < neuronList1.size(); i++) {//мб тут neuronList1.size
                   err[i] = sumOfErr(neuronList2, i);
               }
               for (int i = 0; i < neuronList1.size(); i++) {
                   neuronList1.get(i).backPropNeuron(err[i]);
               }
                break;
            case 3:
                for (int i = 0; i < neuronList3.size(); i++) {
                    neuronList3.get(i).backPropNeuron(ans[i] - outputs[i]);
                }
                err = new double[neuronList2.size()];
                for (int i = 0; i < neuronList2.size(); i++) {
                    err[i] = sumOfErr(neuronList3, i);
                }
                for (int i = 0; i < neuronList2.size(); i++) {
                    neuronList2.get(i).backPropNeuron(err[i]);
                }
                err = new double[neuronList1.size()];
                for (int i = 0; i < neuronList1.size(); i++) {
                    err[i] = sumOfErr(neuronList2, i);
                }
                for (int i = 0; i < neuronList1.size(); i++) {
                    neuronList1.get(i).backPropNeuron(err[i]);
                }
                break;
            case 4:
                for (int i = 0; i < neuronList4.size(); i++) {
                    neuronList4.get(i).backPropNeuron(ans[i] - outputs[i]);
                }
                err = new double[neuronList3.size()];
                for (int i = 0; i < neuronList3.size(); i++) {
                    err[i] = sumOfErr(neuronList4, i);
                }
                for (int i = 0; i < neuronList3.size(); i++) {
                    neuronList3.get(i).backPropNeuron(err[i]);
                }
                err = new double[neuronList2.size()];
                for (int i = 0; i < neuronList2.size(); i++) {
                    err[i] = sumOfErr(neuronList3, i);
                }
                for (int i = 0; i < neuronList2.size(); i++) {
                    neuronList2.get(i).backPropNeuron(err[i]);
                }
                err = new double[neuronList1.size()];
                for (int i = 0; i < neuronList1.size(); i++) {
                    err[i] = sumOfErr(neuronList2, i);
                }
                for (int i = 0; i < neuronList1.size(); i++) {
                    neuronList1.get(i).backPropNeuron(err[i]);
                }
                break;
            default:
                for (int i = 0; i < neuronList1.size(); i++) {
                    neuronList1.get(i).backPropNeuron(ans[i] - outputs[i]);
                }
                System.out.println("warning");
                break;

        }
    }

    public double getLastWinrate() {
        return winRate;
    }

    private void checkStop(int actEpoch) {

    }


}
