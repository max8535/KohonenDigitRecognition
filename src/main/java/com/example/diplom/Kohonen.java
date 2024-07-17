package com.example.diplom;

import java.util.ArrayList;
import java.util.List;

public class Kohonen {
    private int NEURONS_NUMBER = 10;

    private double N = 0.01;
    private int COUNT_ATTRIBUTES_IN_VECTOR = 784;

    private double min_potential = 0.90;



    private int EPOCHS = 500;
    private int learning_pool_size = 1000;

    public int getEPOCHS() {
        return EPOCHS;
    }

    public void setEPOCHS(int EPOCHS) {
        this.EPOCHS = EPOCHS;
    }

    public int getLearning_pool_size() {
        return learning_pool_size;
    }

    public void setLearning_pool_size(int learning_pool_size) {
        this.learning_pool_size = learning_pool_size;
    }

    public double getN() {
        return N;
    }

    public void setN(double n) {
        N = n;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }

    private List<Neuron> neurons;


    public Kohonen() {
        neurons = new ArrayList<>(NEURONS_NUMBER);
        for (int i = 0; i < NEURONS_NUMBER; i++) {
            neurons.add(new Neuron(COUNT_ATTRIBUTES_IN_VECTOR));
        }

    }
    public void print() {
        for (int i = 0; i < NEURONS_NUMBER; i++) {
            for (int j = 0; j < neurons.get(i).getWeights().length; j++) {
                System.out.println(neurons.get(i).getWeights()[j]);
            }
            System.out.println("==========================");
        }

    }
    public void calculatePotentials(int winner) {
        for(int i = 0; i < NEURONS_NUMBER; i++) {
            double potential = neurons.get(i).getPotential();
            if(i==winner){ neurons.get(i).setPotential(potential-min_potential);}
            else {
                if (potential>1){neurons.get(i).setPotential(1);}
                else
                {neurons.get(i).setPotential(potential + 0.10);}

            }

        }

    }
    public void resetPotentials() {
        for(int i = 0; i < NEURONS_NUMBER; i++) {
            neurons.get(i).setPotential(0.89);
        }

    }
    public void trainWTA(double[] data) {
        int index = findNeuronWinnerWTA(data);
        trainNeuronWTA(index,data,getN());
        calculatePotentials(index);
    }

    public int findNeuronWinnerWTA(double[] vector) {
        double maxdistance = Double.MAX_VALUE;
        int winner = 0;
        for (int i = 0; i < NEURONS_NUMBER; i++) {
            Neuron tmp = neurons.get(i);
            if (tmp.getPotential()>=min_potential) {
                double iDistance = neurons.get(i).calcDistanceBetweenNeuronAndInputVector(vector);
                if (iDistance < maxdistance) {
                    winner = i;
                    maxdistance = iDistance;
                }
            }
        }
        return winner;
    }
    public int recognize(double[] vector) {
        double maxdistance = Double.MAX_VALUE;
        int winner = 0;
        for (int i = 0; i < NEURONS_NUMBER; i++) {
                double iDistance = neurons.get(i).calcDistanceBetweenNeuronAndInputVector(vector);
                if (iDistance < maxdistance) {
                    winner = i;
                    maxdistance = iDistance;
                }
        }
        return winner;
    }

    public void trainNeuronWTA(int neuronWinner, double[] inputVector, double n) {
        for (int w = 0; w < neurons.get(neuronWinner).getWeights().length; w++) {
            double newWeight = neurons.get(neuronWinner).getWeights()[w] + (n * (inputVector[w] - neurons.get(neuronWinner).getWeights()[w]));
            neurons.get(neuronWinner).setWeight(w, newWeight);
        }
    }

}
