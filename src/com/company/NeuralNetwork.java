package com.company;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    /*
        нейронная сеть, которая содержит всю информацию о НС
        и выполняет все вычисления внутри сети
     */

    /*
        добавить методы для работы со слоями нейронной сети
        методы для вычисления значений нейронов.
        методы для коррекции весов синапсов
     */

    /*
        добавить методы learn или teach,
        где на вход будет подаваться массив в виде словаря

     */



    ReadSettings settings;

    List<Neuron> iNeuronList;
    List<Neuron> hNeuronList;
    List<Neuron> oNeuronList;
    List<ArrayList<Neuron>> hLayers;


    public NeuralNetwork(ReadSettings settings, int inputNumber, int outputNumber) {

        /*
            конструктор класса NeuralNetwork содержит инициализацию
            архитектуры нейронной сети
         */

        this.settings = settings; //получаем настройки НС


        /*
            добавить отдельную логику использования НС с 1, 2, или (3+) слоями
         */


        iNeuronList = new ArrayList<>(inputNumber);
        hNeuronList = new ArrayList<>(settings.getNumberOfNeurons());
        oNeuronList = new ArrayList<>(outputNumber);
        hLayers = new ArrayList<>(settings.getNumberOfHiddenLayers());
        //не желательно запаковывать нейроны внутри списка списков, т.к. будет тратиться
        //много времени на лишнюю распаковку
        //iNeuronList.set(1, Neuron);

        Neuron inputNeuron = new Neuron(inputNumber);


        /*
            заполняем список нейронами и даём им случайные веса
            необходимо это проделать для всех списков и всех нейронов
            с учётом количества слоёв нейронной сети
         */
        for (int i = 0; i < iNeuronList.size(); i++) {
            iNeuronList.set(i, inputNeuron);
            iNeuronList.get(i).setRandomWeights();
        }


    }


    public void calculation(double[] array) {

        for (int i = 0; i < iNeuronList.size(); i++) {
            iNeuronList.get(i).calcNeuron(array);
        }


    }


}
