package mlfinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ThreeLayerNN implements Classifier {

    private double eta = 0.1;
    private int hiddenNodes1;
    private int hiddenNodes2;
    private int iterations = 200;

    // Maps feature index to the arraylist of weights going to the first hidden layer.
    private HashMap<Integer, ArrayList<Double>> layer_1_weights = new HashMap<>();
    // Maps the first hidden layer nodes to the second hidden layer nodes.
    private HashMap<Integer, ArrayList<Double>> layer_2_weights = new HashMap<>();
    // Maps the second hidden layer nodes to the output nodes.
    private HashMap<Integer, ArrayList<Double>> layer_3_weights = new HashMap<>();
    // Store the hidden layer outputs so they can be accessed in backprop step
    ArrayList<Double> hiddenNodeOutputs1 = new ArrayList<>();
    ArrayList<Double> hiddenNodeOutputs2 = new ArrayList<>();

    // Bias variables
    private ArrayList<Double> inputBias = new ArrayList<>();
    private double hiddenNodeBias1 = 0;
    private double hiddenNodeBias2 = 0;

    public ThreeLayerNN(int hiddenNodes1, int hiddenNodes2) {
        this.hiddenNodes1 = hiddenNodes1;
        this.hiddenNodes2 = hiddenNodes2;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    public void setIterations(int val) {
        this.iterations = val;
    }

    @Override
    public void train(DataSet data) {
        // Clear out weights to begin training.
        layer_1_weights = new HashMap<>();
        layer_2_weights = new HashMap<>();
        layer_3_weights = new HashMap<>();

        Random rand = new Random();

        // Populate layer 1 weights with random values from -0.1 to 0.1
        for(int i : data.getAllFeatureIndices()) {
            ArrayList<Double> weights = new ArrayList<>();
            for(int j = 0; j < hiddenNodes1; j++) {
                weights.add(rand.nextDouble(-0.1, 0.1));
            }
            layer_1_weights.put(i, weights);
        }

        // Populate layer 2 weights
        for(int i = 0; i < hiddenNodes1; i++) {
            ArrayList<Double> weights = new ArrayList<>();
            for(int j = 0; j < hiddenNodes2; j++) {
                weights.add(rand.nextDouble(-0.1, 0.1));
            }
            layer_2_weights.put(i, weights);
        }

        // Populate layer 3 weights
        for(int i = 0; i < hiddenNodes2; i++) {
            ArrayList<Double> weights = new ArrayList<>();
            // Only one output node in this model, so handled this way.
            for(int j = 0; j < 1; j++) {
                weights.add(rand.nextDouble(-0.1, 0.1));
            }
            layer_3_weights.put(i, weights);
        }

        // Randomize input bias
        for(int i = 0; i < hiddenNodes1; i++) {
            inputBias.add(rand.nextDouble(-0.1, 0.1));
        }

        // Training iterations
        for(int iter = 0; iter < iterations; iter++) {
            for(Example e : data.getData()) {
                backpropagate(e);
            }
        }
    }

    @Override
    public double classify(Example example) {
        return predict(example) > 0.0 ? 1.0 : -1.0;
    }

    @Override
    public double confidence(Example example) {
        return 0;
    }

    private double predict(Example example) {
        hiddenNodeOutputs1 = new ArrayList<>();
        hiddenNodeOutputs2 = new ArrayList<>();

        HashMap<Integer, Double> featureValues = new HashMap<>();
        for(int i : layer_1_weights.keySet()) {
            featureValues.put(i, example.getFeature(i));
        }

        // Compute the value of each node in the first hidden layer.
        for(int i = 0; i < hiddenNodes1; i++) {
            double sum = 0.0;
            for(Integer j : layer_1_weights.keySet()) {
                sum += featureValues.get(j) * layer_1_weights.get(j).get(i);
            }
            sum += inputBias.get(i);
            hiddenNodeOutputs1.add(Math.tanh(sum));
        }

        // Compute the value of each node in the second hidden layer.
        for(int i = 0; i < hiddenNodes2; i++) {
            double sum = 0.0;
            for(Integer j : layer_2_weights.keySet()) {
                sum += hiddenNodeOutputs1.get(j) * layer_2_weights.get(j).get(i);
            }
            sum += hiddenNodeBias1;
            hiddenNodeOutputs2.add(Math.tanh(sum));
        }

        // Compute the output layer value.
        double sum = 0.0;
        for(Integer j : layer_3_weights.keySet()) {
            sum += hiddenNodeOutputs2.get(j) * layer_3_weights.get(j).get(0);
        }
        sum += hiddenNodeBias2;
        return Math.tanh(sum);
    }

    private void backpropagate(Example example) {
        predict(example);

        // Backpropagation for the output layer (layer 3)
        ArrayList<Double> deltaOutput = new ArrayList<>();
        for (int i = 0; i < hiddenNodes2; i++) {
            // Compute the error gradient for each output node in layer 3
            double delta = (example.getLabel() - hiddenNodeOutputs2.get(i)) * (1 - Math.pow(hiddenNodeOutputs2.get(i), 2));
            deltaOutput.add(delta);
        }

        // Update weights and biases for layer 3 (output layer)
        for (int i = 0; i < hiddenNodes2; i++) {
            ArrayList<Double> weights = layer_3_weights.get(i);
            double updatedWeight = weights.get(0) + eta * hiddenNodeOutputs2.get(i) * deltaOutput.get(i);
            weights.set(0, updatedWeight);
            layer_3_weights.put(i, weights);
        }
        hiddenNodeBias2 += eta * deltaOutput.get(0);

        // Backpropagation for layer 2
        ArrayList<Double> deltaHidden2 = new ArrayList<>();
        for (int i = 0; i < hiddenNodes2; i++) {
            double error = 0.0;
            for (int j = 0; j < hiddenNodes2; j++) {
                error += deltaOutput.get(j) * layer_3_weights.get(j).get(0);
            }
            double delta = error * (1 - Math.pow(hiddenNodeOutputs2.get(i), 2));
            deltaHidden2.add(delta);
        }

        // Update weights and biases for layer 2
        for (int i = 0; i < hiddenNodes1; i++) {
            ArrayList<Double> weights = layer_2_weights.get(i);
            for (int j = 0; j < hiddenNodes2; j++) {
                double updatedWeight = weights.get(j) + eta * hiddenNodeOutputs1.get(i) * deltaHidden2.get(j);
                weights.set(j, updatedWeight);
            }
            layer_2_weights.put(i, weights);
        }
        hiddenNodeBias1 += eta * deltaHidden2.get(0);

        // Backpropagation for layer 1
        ArrayList<Double> deltaHidden1 = new ArrayList<>();
        for (int i = 0; i < hiddenNodes1; i++) {
            double error = 0.0;
            for (int j = 0; j < hiddenNodes2; j++) {
                error += deltaHidden2.get(j) * layer_2_weights.get(i).get(j);
            }
            double delta = error * (1 - Math.pow(hiddenNodeOutputs1.get(i), 2));
            deltaHidden1.add(delta);
        }

        // Update weights for layer 1
        for (Integer featureIndex : layer_1_weights.keySet()) {
            ArrayList<Double> weights = layer_1_weights.get(featureIndex);
            for (int j = 0; j < hiddenNodes1; j++) {
                double updatedWeight = weights.get(j) + eta * example.getFeature(featureIndex) * deltaHidden1.get(j);
                weights.set(j, updatedWeight);
            }
            layer_1_weights.put(featureIndex, weights);
        }

        // Update biases for input and hidden layers
        for (int i = 0; i < hiddenNodes1; i++) {
            inputBias.set(i, inputBias.get(i) + eta * deltaHidden1.get(i));
        }
    }
}