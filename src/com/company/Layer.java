package com.company;

import java.util.ArrayList;
import java.util.List;

public class Layer {



    private List<Neuron> neuronList = new ArrayList<>(); //список нейронов
    private int sizeOfLayer = 0;
    private int sizeOfInput = 0;
    private double tempVal;
    private double[] tempArray;
    private boolean readyToCalculate = false;
    private double[] err;
    private double[] lastOutput;

    public void addNeuron(Neuron neuron) {
        neuronList.add(neuron);
        sizeOfLayer = neuronList.size();
        sizeOfInput = neuronList.get(sizeOfLayer-1).getSizeOfInput();
    }



    public void initializeLayer() {
        readyToCalculate = true;
        tempArray = new double[sizeOfLayer];
        err = new double[sizeOfInput];
        lastOutput = new double[sizeOfLayer];
    }



    public Neuron getNeuron(int i){
        return neuronList.get(i);
    }



    public int getLayerSize() {
        return sizeOfLayer;
    }

    public int getSizeOfInput() {
        return sizeOfInput;
    }



    public double[] layerCalculation(double[] input) {
        for (int i = 0; i < sizeOfLayer; i++) {
            tempArray[i] = neuronList.get(i).calcNeuron(input);
        }
        return tempArray;
    }



    public void backPropagation(double[] input, double[] desireOut) {
        for (int i = 0; i < sizeOfLayer; i++) {
            neuronList.get(i).backPropNeuron(input[i] - desireOut[i]);
        }

    }


    public void backPropagation(double[] input) {
        for (int i = 0; i < sizeOfLayer; i++) {
            neuronList.get(i).backPropNeuron(input[i]);
        }
    }



    public double[] getSumOfErr() {
        for (int i = 0; i < sizeOfInput; i++) {
            tempVal = 0;
            for (int j = 0; j < sizeOfLayer; j++) {
                tempVal = tempVal + neuronList.get(j).getError(i);
            }
            err[i] = tempVal;
        }
        return err;
    }


    public double[] getLastOutput() {
        for (int i = 0; i < sizeOfLayer; i++) {
            lastOutput[i] = neuronList.get(i).getLastOut();
        }
        return lastOutput;
    }
}