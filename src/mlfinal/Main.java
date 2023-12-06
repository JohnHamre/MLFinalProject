package mlfinal;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
	public static void main(String args[]) {
//		DatasetGenerator datagen = new DatasetGenerator();
//		datagen.generateNewDataset(100000, "data/poker_train_big.csv");
		
		DataSet data = new DataSet("data/poker_train_big.csv", DataSet.CSVFILE);
		
		CrossValidationSet csv = new CrossValidationSet(data, 10);
		
		ArrayList<TwoLayerNN> nns = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			System.out.println("Training " + i);
			
			Collections.shuffle(csv.getValidationSet(i).getTrain().getData());
			Collections.shuffle(csv.getValidationSet(i).getTest().getData());
			TwoLayerNN nn = new TwoLayerNN(20); 
			nn.setEta(0.05);
			nn.setIterations(25);
			nn.train(csv.getValidationSet(i).getTrain());
			nns.add(nn);
		}
		
		for(double c = 0.0; c < 1.0; c += 0.05) {
			double runningAcc = 0.0;
			double runningAcc2 = 0.0;
			for(int i = 0; i < 10; i++)
			{				
				double numClassified = 0.0;
				double numCorrect = 0.0;
				for(Example e : csv.getValidationSet(i).getTest().getData()) {
					if (nns.get(i).confidence(e) >= c) {
						//System.out.println(nn.classify(e) + " : " + e.getLabel());
						if(nns.get(i).classify(e) == e.getLabel()) {
							numCorrect += 1.0;
						}
						numClassified += 1.0;
					}
				}
				runningAcc += numCorrect / numClassified;
				
				double numClassified2 = 0.0;
				double numCorrect2 = 0.0;
				for(Example e : csv.getValidationSet(i).getTrain().getData()) {
					//System.out.println(nn.classify(e) + " : " + e.getLabel());
					if (nns.get(i).confidence(e) >= c) {
						if(nns.get(i).classify(e) == e.getLabel()) {
							numCorrect2 += 1.0;
						}
						numClassified2 += 1.0;
					}
				}
				runningAcc2 += numCorrect2 / numClassified2;
			}
			System.out.println("Train: " + c + ", " + (runningAcc2 / 10.0));
			System.out.println("Test: " + c + ", " + (runningAcc / 10.0));
		}
	}
}
