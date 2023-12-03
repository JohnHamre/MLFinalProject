package mlfinal;

import java.util.Collections;

public class Main {
	public static void main(String args[]) {
//		DatasetGenerator datagen = new DatasetGenerator();
//		datagen.generateNewDataset(100000, "data/poker_train_big.csv");
		
		DataSet data = new DataSet("data/poker_train_big.csv", DataSet.CSVFILE);
		
		CrossValidationSet csv = new CrossValidationSet(data, 10);
		
		double runningAcc = 0.0;
		double runningAcc2 = 0.0;
		for(int i = 0; i < 10; i++) {
			System.out.println("Starting " + i);
			Collections.shuffle(csv.getValidationSet(i).getTrain().getData());
			Collections.shuffle(csv.getValidationSet(i).getTest().getData());
			TwoLayerNN nn = new TwoLayerNN(20); 
			nn.setEta(0.05);
			nn.setIterations(25);
			nn.train(csv.getValidationSet(i).getTrain());
			
			double numCorrect = 0.0;
			for(Example e : csv.getValidationSet(i).getTest().getData()) {
				//System.out.println(nn.classify(e) + " : " + e.getLabel());
				if(nn.classify(e) == e.getLabel()) {
					numCorrect += 1.0;
				}
			}
			runningAcc += numCorrect / (double) csv.getValidationSet(i).getTest().getData().size();
			
			double numCorrect2 = 0.0;
			for(Example e : csv.getValidationSet(i).getTrain().getData()) {
				//System.out.println(nn.classify(e) + " : " + e.getLabel());
				if(nn.classify(e) == e.getLabel()) {
					numCorrect2 += 1.0;
				}
			}
			runningAcc2 += numCorrect2 / (double) csv.getValidationSet(i).getTrain().getData().size();
		}
		System.out.println("Test: " + ", " + (runningAcc / 10.0));
		System.out.println("Train: " + ", " + (runningAcc2 / 10.0));
	}
}
