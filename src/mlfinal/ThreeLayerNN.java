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

        HashMap<Integer, ArrayList<Double>> w1 = new HashMap<>();
        HashMap<Integer, ArrayList<Double>> w2 = new HashMap<>();
        HashMap<Integer, ArrayList<Double>> w3 = new HashMap<>();

        double W1dotH = 0.0;
        for (int i = 0; i < hiddenNodes2; i++) {
            // each layer_2_weights array list only has 1 value (at index 0)
            W1dotH += layer_2_weights.get(i).get(0) * hiddenNodeOutputs2.get(i);
        }

        // add Bias
        W1dotH += hiddenNodeBias2;

        // compute f(v · h)
        double f1 = Math.tanh(W1dotH);

        // Compute delta for output layer
        double deltaOut = (example.getLabel() - f1) * (1 - Math.pow(f1, 2));

        // Update weights and biases for layer 3 (output layer)
        for (int k : layer_3_weights.keySet()) {
            ArrayList<Double> weights = layer_3_weights.get(k);
            Double updatedWeight = weights.get(0);

            updatedWeight += eta * hiddenNodeOutputs2.get(k) * deltaOut;
            ArrayList<Double> newWeights = new ArrayList<>();
            newWeights.add(updatedWeight);

            w3.put(k, newWeights);
        }
        hiddenNodeBias2 += eta * deltaOut;

        // Compute slope of the activations for layer 2
        HashMap<Integer, Double> hiddenNodeActivationSlopes2 = new HashMap<>();
        for (int k = 0; k < hiddenNodes2; k++) {
            double W2dotX = 0.0;
            for (int j : layer_2_weights.keySet()) {
                W2dotX += layer_2_weights.get(j).get(k) * hiddenNodeOutputs1.get(j);
            }

            W2dotX += hiddenNodeBias1;

            double activationSlope2 = 1 - Math.pow(Math.tanh(W2dotX), 2);
            hiddenNodeActivationSlopes2.put(k, activationSlope2);
        }

        // Update weights and biases for layer 2
        for (int k = 0; k < hiddenNodes2; k++) {
            ArrayList<Double> updatedWeights = new ArrayList<>();
            for (int j : layer_2_weights.keySet()) {
                double updatedWeight = layer_2_weights.get(j).get(k);
                updatedWeight += eta * hiddenNodeOutputs1.get(j) * hiddenNodeActivationSlopes2.get(k) * deltaOut;
                updatedWeights.add(updatedWeight);
            }
            w2.put(k, updatedWeights);

            hiddenNodeBias1 += eta * hiddenNodeActivationSlopes2.get(k) * deltaOut;
        }

        // Compute slope of the activations for layer 1
        HashMap<Integer, Double> hiddenNodeActivationSlopes1 = new HashMap<>();
        for (int k = 0; k < hiddenNodes1; k++) {
            double W1dotX = 0.0;
            for (int j : layer_1_weights.keySet()) {
                W1dotX += layer_1_weights.get(j).get(k) * example.getFeature(j);
            }

            W1dotX += inputBias.get(k);

            Double activationSlope1 = 1 - Math.pow(Math.tanh(W1dotX), 2);
            hiddenNodeActivationSlopes1.put(k, activationSlope1);
        }

        // Update weights and biases for layer 1
        for (int k = 0; k < hiddenNodes1; k++) {
            ArrayList<Double> updatedWeights = new ArrayList<>();
            for (int j : layer_1_weights.keySet()) {
                Double activationSlope1 = hiddenNodeActivationSlopes1.get(k);
                Double activationSlope2 = hiddenNodeActivationSlopes2.get(k);
                if (activationSlope1 != null && activationSlope2 != null) {
                    double updatedWeight = layer_1_weights.get(j).get(k);
                    updatedWeight += eta * example.getFeature(j) * activationSlope1 * activationSlope2 * deltaOut;
                    updatedWeights.add(updatedWeight);
                } else {
                    updatedWeights.add(0.0);
                }
            }
            w1.put(k, updatedWeights);

            Double activationSlope1 = hiddenNodeActivationSlopes1.get(k);
            Double activationSlope2 = hiddenNodeActivationSlopes2.get(k);
            if (activationSlope1 != null && activationSlope2 != null) {
                inputBias.set(k, inputBias.get(k) + eta * activationSlope1 * activationSlope2 * deltaOut);
            } else {
                inputBias.set(k,0.0);
            }
        }

        // Rearranging Layer 1 weights
        for (int i : layer_1_weights.keySet()) {
            ArrayList<Double> inner = new ArrayList<>();
            for (int k = 0; k < hiddenNodes1; k++) {
                inner.add(w1.get(k).get(i));
            }
            layer_1_weights.put(i, inner);
        }

        // Update weights
        layer_1_weights = w1;
        layer_2_weights = w2;
        layer_3_weights = w3;
    }
}