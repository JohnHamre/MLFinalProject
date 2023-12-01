package mlfinal;

import javapoker.Card;
import javapoker.Deck;

public class Main {
	public static void main(String args[]) {
		System.out.println("Hello Stanley");
	}
	
	public static Example generateExample() {
		// The example to be returned. Created empty.
		Example outputExample = new Example();
		// Create a new deck.
		Deck deck = new Deck();
		// Create the hands to draw into
		Card[] playerHand = new Card[2];
		Card[] board = new Card[5];
		Card[] opponentHand = new Card[2];
		for(int i = 0; i <= 1; i++) playerHand[i] = deck.drawFromDeck();
		for(int i = 0; i <= 4; i++) board[i] = deck.drawFromDeck();
		for(int i = 0; i <= 1; i++) opponentHand[i] = deck.drawFromDeck();
		
		// Generate the label from hand logic.
		
		double label = 0.0;
		// Set values
		
		return outputExample;
	}
}
