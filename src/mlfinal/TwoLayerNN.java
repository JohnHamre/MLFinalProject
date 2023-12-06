package mlfinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TwoLayerNN implements Classifier {
	
	private double eta = 0.1;
	private int hiddenNodes;
	private int iterations = 200;
	
	// Maps feature index to the arraylist of weights going to the hidden nodes.
	private HashMap<Integer, ArrayList<Double>> layer_1_weights = new HashMap<>();
	// Maps the hidden nodes to the output nodes.
	private HashMap<Integer, ArrayList<Double>> layer_2_weights = new HashMap<>();
	// Store the hidden node outputs so they can be accessed in backprop step
	ArrayList<Double> hiddenNodeOutputs = new ArrayList<>();
	
	// Bias variables. 
	// The hidden node version is added to hidden node outputs in Predict
	private ArrayList<Double> inputBias = new ArrayList<>();
	private double hiddenNodeBias = 0;

	public TwoLayerNN(int hiddenNodes) {
		this.hiddenNodes = hiddenNodes;
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

		Random rand = new Random();
		
		// Populate layer 1 weights with random values from -0.1 to 0.1
		for(int i : data.getAllFeatureIndices()) {
			ArrayList<Double> weights = new ArrayList<>();
			for(int j = 0; j < hiddenNodes; j++) {
				// Add one random double for each hidden node
				// These are the corresponding weights for this feature and that node.
				weights.add(rand.nextDouble(-0.1, 0.1));
			}
			layer_1_weights.put(i, weights);
		}
		
		// Now populate layer 2.
		for(int i = 0; i < hiddenNodes; i++) {
			ArrayList<Double> weights = new ArrayList<>();
			// Only one output node in this model, so handled this way.
			for(int j = 0; j < 1; j++) {
				// Add one random double for each hidden node
				// These are the corresponding weights for this feature and that node.
				weights.add(rand.nextDouble(-0.1, 0.1));
			}
			layer_2_weights.put(i, weights);
		}
		
		// Randomize input bias
		for(int i = 0; i < hiddenNodes; i++) {
			inputBias.add(rand.nextDouble(-0.1, 0.1));
		}

		// Training is really short then
		for(int iter = 0; iter < iterations; iter++) 
		{
			for(Example e : data.getData()) {
				backpropogate(e);
			}
		}
	}

	@Override
	public double classify(Example example) {
		// TODO Auto-generated method stub
		return predict(example) > 0.0 ? 1.0 : -1.0;
	}

	@Override
	public double confidence(Example example) {
		// TODO Auto-generated method stub
		return Math.abs(predict(example));
	}

	private double predict(Example example) {
		// ArrayList that will soon store the outputs from the hidden nodes.
		hiddenNodeOutputs = new ArrayList<>();
		
		// Computes a hashmap of input values for this example.
		HashMap<Integer, Double> featureValues = new HashMap<>();
		for(int i : layer_1_weights.keySet()) {
			// e.getFeature returns 0 when it doesn't have the feature, so this
			// case is already done without any additional work needed
			featureValues.put(i, example.getFeature(i));
		}
		
		// Compute the value of each hidden node.
		for(int i = 0; i < hiddenNodes; i++) {
			double sum = 0.0;
			// Iterate through the weights and values j.
			for(Integer j : layer_1_weights.keySet()) {
				// Take the sum of each feature-weight pair product
				sum += featureValues.get(j) * layer_1_weights.get(j).get(i);
			}
			// Bias
			sum += inputBias.get(i);
			// Activation function and store to array.
			hiddenNodeOutputs.add(Math.tanh(sum));
		}
		
		// Compute the output layer value.
		double sum = 0.0;
		// Iterate through the weights and values j.
		for(Integer j : layer_2_weights.keySet()) {
			// Take the sum of each feature-weight pair product
			sum += hiddenNodeOutputs.get(j) * layer_2_weights.get(j).get(0);
		}
		// Bias
		sum += hiddenNodeBias;
		// Activation function and return.
		return Math.tanh(sum);
	}

	private void backpropogate(Example example) {
		// first, forward propogate to compute predicted value and hidden node outputs
		predict(example);
		HashMap<Integer, ArrayList<Double>> v = new HashMap<>();
		HashMap<Integer, ArrayList<Double>> w = new HashMap<>();

		// Compute dot product of v and h
		double VdotH = 0.0;
		for (Integer i : layer_2_weights.keySet()) {
			// each layer_2_weights array list only has 1 value (at index 0)
			VdotH += layer_2_weights.get(i).get(0) * hiddenNodeOutputs.get(i); 
		}

		// add Bias
		VdotH += hiddenNodeBias;

		// compute f(v · h)
		Double f = Math.tanh(VdotH);

		// compute delta out = (label - f(v · h)) f'(v · h)
		Double deltaOut = (example.getLabel() - f) * (1 - Math.pow(f, 2));		
		
		// Output layer update

		for (Integer k : layer_2_weights.keySet()) {
			ArrayList<Double> weights = layer_2_weights.get(k);
			Double updatedWeight = weights.get(0);
			
			// Add the gradient descent step
			updatedWeight += eta * hiddenNodeOutputs.get(k) * deltaOut;
			ArrayList<Double> newWeights = new ArrayList<>();
			newWeights.add(updatedWeight);

			v.put(k, newWeights);
		}

		// Update output layer bias
		hiddenNodeBias += eta * deltaOut;

		// Compute slope of the activations -- f'(w_k · x) -- for each hidden node
		HashMap<Integer, Double> hiddenNodeActivationSlopes = new HashMap<>();

		for (int k = 0; k < hiddenNodes; k++) {
			// Compute w_k · x
			Double WdotX = 0.0;
			for (Integer j : layer_1_weights.keySet()) {
				WdotX += layer_1_weights.get(j).get(k) * example.getFeature(j);
			}

			// Add the bias
			WdotX += inputBias.get(k);

			// f'(w_k · x)
			Double activationSlope = 1 - Math.pow(Math.tanh(WdotX),2);

			hiddenNodeActivationSlopes.put(k, activationSlope);
		}

		// Now we have everything we need to update the first layer
		for (int k = 0; k < hiddenNodes; k++) {
			ArrayList<Double> newWeights = new ArrayList<>();
			for (Integer j : layer_1_weights.keySet()) {
				Double w_kj = layer_1_weights.get(j).get(k);
				w_kj += eta * example.getFeature(j) * hiddenNodeActivationSlopes.get(k) * layer_2_weights.get(k).get(0) * deltaOut;
				
				newWeights.add(w_kj);
			}

			w.put(k, newWeights);

			// Update the bias
			inputBias.set(k, inputBias.get(k) + eta * hiddenNodeActivationSlopes.get(k) * layer_2_weights.get(k).get(0) * deltaOut);
		}
		
		for (Integer i : layer_1_weights.keySet()) {
            ArrayList<Double> inner = new ArrayList<>();
            for (int k = 0; k < hiddenNodes; k++) {
                inner.add(w.get(k).get(i));
            }
            layer_1_weights.put(i, inner);
        }

		layer_2_weights = v;
		
	}
}