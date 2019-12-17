package com.company;


public class Neuron {
    /*
        класс Neuron содержит всю информацию о нейроне,
        веса синапсов, входящих в него
        функцию активации
     */


    private double[] synapses; //веса синапсов
    private double output;
    private double[] inputArray; //входы для каждого синапса
    private String func = "sigmoid"; //функция активации (по умолчанию сигмоида)
    private double lastOut = 0; //сохраняем последнее значение выхода НС для backprop //мб удалить
    private double delta_w; //dw дельта весов
    private double learningRate; //скорость обучения

    public double calcNeuron(double[] array) {
        inputArray = array;
        output = 0;
        for (int i = 0; i < inputArray.length; i++) {
            output = output + inputArray[i]*synapses[i];
        } //вычисление произведения весов синапсов на входы


        //проход полученного значения через функцию активации
        switch (func) {
            case "sigmoid":
                output = sigmoidFun(output);
                break;
            case "linear":
                output = linearFun(output);
                break;
            case "step":
                output = stepFun(output);
                break;
            default:
                output = sigmoidFun(output);
                System.out.println("Warning: neuron1");
                break;
        }

        lastOut = output; //мб достаточно просто output
        return output;
    }

    public void backPropNeuron(double error) {
        switch (func) {
            case "sigmoid":
                delta_w = sigmoidDerivative(lastOut)*error;
                break;
            case "linear":
                delta_w = linearDerivative(lastOut)*error;
                break;
            case "step":
                delta_w = stepDerivative(lastOut)*error;
                break;
            default:
                delta_w = sigmoidDerivative(lastOut)*error;
                System.out.println("Warning: neuron2");
                break;
        }

        for (int i = 0; i < synapses.length; i++) {
            synapses[i] = synapses[i] - inputArray[i]*delta_w*learningRate;
        }
    }

    public double getError(int index) {
        return delta_w*synapses[index];
    }

    private double sigmoidFun(double val) {
        //дописать сигмоиду
        //return sigmoid(val);
        val = 1/(1+Math.exp(-val));
        return val;
    }

    private double sigmoidDerivative(double val) {
        return val*(1-val);
    }

    private double linearFun(double val) {
        //дописать линейную функцию
        //return linear(val);
        return val;
    }

    private double linearDerivative(double val) {
        //дописать производную линейной функции
        //return linear(val);
        return val;
    }

    private double stepFun(double val) {
        //дописать ступенчатую функцию
        //return step(val);
        return val;
    }

    private double stepDerivative(double val) {
        //дописать производную ступенчатой функции
        //return step(val);
        return val;
    }

    public double getWeightsSum() {
        double sum = 0;
        for (int i = 0; i < synapses.length; i++) {
            sum = sum + synapses[i];
        }
        return sum;
    }

    public void setRandomWeights() {
        for (int i = 0; i < synapses.length; i++) {
            synapses[i] = Math.random()*2-1;
            /*
                кладём в каждую ячейку массива весов нейронов случайный вес,
                возможно, следует применить метод:
                Random random = new Random();
                int num = random.nextInt(100);
             */

            /*
                при Math.random(); диапазон по умолчанию 0-1,
                следует арифметически преобразовать выражение,
                чтобы задать нужный диапазон с нужной точностью
             */

            /*
                возможно, следует избавиться от типа double и перейти к другим,
                более точным или менее тяжелым
             */
        }
    }


    public Neuron(int num, String func, double learningRate) {
        /*
            в конструкторе должно определяться:
            - кол-во синапсов
            - ф-ия активации
            - случайная генерация весов синапсов или чёткий выбор веса
         */

        synapses = new double[num];
        this.learningRate = learningRate;
        this.func = func;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public Neuron(int num) {
        synapses = new double[num];
    }

    public double getLastOut() {
        return lastOut;
    }
}
