package datagen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javapoker.Card;
import javapoker.Deck;
import javapoker.Hand;
import mlfinal.DataSet;
import mlfinal.Example;

public class DatasetGenerator {
	
	private static int numFeatures = 104;
	
	/**
	 * Empty constructor. OOP architecture
	 */
	public DatasetGenerator() {}
	
	/**
	 * Generate a new DataSet with numEntries distinct entries
	 * @param numEntries number of distinct entries
	 * @return A filled Poker dataset.
	 */
	public DataSet generateNewDataset(int numEntries) {
		HashMap<Integer, String> featureMap = new HashMap<Integer, String>();
		
		for(int i = 0; i < numFeatures; i++) {
			featureMap.put(i, Card.nameFromID(i % 52));
		}
		
		DataSet data = new DataSet(featureMap);
		
		for(int i = 0; i < numEntries; i++) {
			Example e = generateExample();
			data.addData(e);
		}
		return data;
	}
	
	/**
	 * File option for dataset generation
	 * @param numEntries Number of distinct entries in the data.
	 * @param filepath Filepath to output the data file to.
	 */
	public void generateNewDataset(int numEntries, String filepath) {
		DataSet outputSet = generateNewDataset(numEntries);
		// Create a file, then populate it.
	    try 
	    {
	    	File myObj = new File(filepath);
	        if (myObj.createNewFile()) {
	        	try {
	                FileWriter myWriter = new FileWriter(filepath);
	                for(Example e : outputSet.getData())
	                {
	                	String writeStr = "";
	                	for(int i = 0; i < e.getFeatureSet().size(); i++) {
	                		writeStr += e.getFeature(i) + ", ";
	                	}
	                	writeStr += e.getLabel() + "\n";
	                	myWriter.write(writeStr);
	                }
	                
	                myWriter.close();
	              } catch (IOException e) {
	                System.out.println("An error occurred.");
	                e.printStackTrace();
	              }
	        } else {
	        	System.out.println("File already exists.");
	        }
	    } catch (IOException e) {
	        	System.out.println("An error occurred.");
	        	e.printStackTrace();
	    }
	}
	
	/**
	 * Generates a single random hand of a poker game with a label.
	 * @return An example with the player cards and board one-hot encoded, plus a label for who won, -1 for opponent, and 1 for player.
	 */
	public Example generateExample() {
		// The example to be returned. Created empty.
		Example outputExample = new Example();
		// Create a new deck.
		Deck deck = new Deck();
		// Create the hands to draw into, including the board
		Card[] playerHand = new Card[2];
		Card[] board = new Card[5];
		Card[] opponentHand = new Card[2];
		for(int i = 0; i <= 1; i++) playerHand[i] = deck.drawFromDeck();
		for(int i = 0; i <= 4; i++) board[i] = deck.drawFromDeck();
		for(int i = 0; i <= 1; i++) opponentHand[i] = deck.drawFromDeck();

		// Generate the label from hand logic.
		
		// Generate the full hands (called boards) for each evaluation.
		Card[] playerBoard = new Card[7];
		Card[] opponentBoard = new Card[7];
		for(int i = 0; i <= 1; i++) {
			playerBoard[i] = playerHand[i];
			opponentBoard[i] = opponentHand[i];
		}
		for(int i = 0; i <= 4; i++) {
			playerBoard[i + 2] = board[i];
			opponentBoard[i + 2] = board[i];
		}
		
		// Compare the two hands to determine the winner.
		// -1 if opponent won, 1 if NN wins
		double label = (new Hand(playerBoard)).compareTo(new Hand(opponentBoard));
		// Set values
		// Create a one-hot array.
		double[] onehot_array = new double[104];
		// Doing this manually because anything else is kinda clunky.
		onehot_array[playerHand[0].getSuit() * 13 + playerHand[0].getRank()] = 1.0;
		onehot_array[playerHand[1].getSuit() * 13 + playerHand[1].getRank()] = 1.0;
		onehot_array[52 + board[0].getSuit() * 13 + board[0].getRank()] = 1.0;
		onehot_array[52 + board[1].getSuit() * 13 + board[1].getRank()] = 1.0;
		onehot_array[52 + board[2].getSuit() * 13 + board[2].getRank()] = 1.0;
		
		for(int i = 0; i < onehot_array.length; i++) {
			outputExample.setFeature(i, onehot_array[i]);
		}
		outputExample.setLabel(label);
		
		return outputExample;
	}
}
