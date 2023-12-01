package mlfinal;

import javapoker.Card;
import javapoker.Deck;
import javapoker.Hand;

public class Main {
	public static void main(String args[]) {
		generateExample();
	}
	
	public static Example generateExample() {
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
		//double playerWon = (new Hand(playerBoard)).compareTo(new Hand(opponentBoard));
		// Set values
		// Create a one-hot array.
		double[] onehot_array = new double[104];
		// Doing this manually because anything else is kinda clunky.
		onehot_array[playerHand[0].getSuit() * 13 + playerHand[0].getRank()] = 1.0;
		onehot_array[playerHand[1].getSuit() * 13 + playerHand[1].getRank()] = 1.0;
		onehot_array[52 + board[0].getSuit() * 13 + board[0].getRank()] = 1.0;
		onehot_array[52 + board[1].getSuit() * 13 + board[1].getRank()] = 1.0;
		onehot_array[52 + board[2].getSuit() * 13 + board[2].getRank()] = 1.0;
		
		return outputExample;
	}
}
