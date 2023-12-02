package mlfinal;

import datagen.DatasetGenerator;

public class Main {
	public static void main(String args[]) {
		DatasetGenerator datagen = new DatasetGenerator();
		datagen.generateNewDataset(100000, "data/poker_train_big.csv");
	}
}
