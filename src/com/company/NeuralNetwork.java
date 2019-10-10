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

    ReadSettings settings;

    public NeuralNetwork(ReadSettings settings, int inputNumber, int outputNumber) {

        /*
            конструктор класса NeuralNetwork содержит инициализацию
            архитектуры нейронной сети
         */

        this.settings = settings;




        List<Neuron> iNeuronList = new ArrayList<>(inputNumber);
        List<Neuron> hNeuronList = new ArrayList<>(settings.getNumberOfNeurons());
        List<Neuron> oNeuronList = new ArrayList<>(outputNumber);
        List<ArrayList<Neuron>> hLayers = new ArrayList<>(settings.getNumberOfHiddenLayers());//4648468468464684846468468



    }


}
