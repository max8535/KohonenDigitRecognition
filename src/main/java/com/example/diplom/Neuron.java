package com.example.diplom;

import java.util.Random;

public class Neuron {

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    private double[] weights;

    public double getPotential() {
        return potential;
    }

    public void setPotential(double potential) {
        this.potential = potential;
    }

    private double potential = 0.75;


    public Neuron(int countAttrs) {
        weights = new double[countAttrs];
        for (int a = 0; a < countAttrs; a++) {
            weights[a] = new Random().nextDouble();

        }
    }

    public double calcDistanceBetweenNeuronAndInputVector(double[] inputVector) {
        double currentDistanceToNeuron = 0;
        for (int i = 0; i < weights.length; i++) {
            currentDistanceToNeuron += Math.pow(inputVector[i] - weights[i], 2);
        }
        return Math.sqrt(currentDistanceToNeuron);
    }



    public double[] getWeights() {
        return weights;
    }

    public void setWeight(int idx, double weight) {
        this.weights[idx] = weight;
    }

    @Override
    public String toString() {
        return String.valueOf(weights[0]);
    }
}