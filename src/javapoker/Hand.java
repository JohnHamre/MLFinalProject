package javapoker;

import java.util.HashMap;

public class Hand {
    private Card[] cards;
    private int[] value;

public Hand(Card[] passedCards)
    {
    	value = new int[6];

        int[] ranks = new int[14];
        //miscellaneous cards that are not otherwise significant
        int[] orderedRanks = new int[7]; // fixed to length 7
        boolean flush=false, straight=false;
        int sameCards=1,sameCards2=1;
        int largeGroupRank=0,smallGroupRank=0;
        int index=0;
        int topStraightValue=0;

        // Count the number of each rank that we have, for example, if we have 3 fives in our hand, rank[5] = 3
        for (int i=0; i<=13; i++)
        {
            ranks[i]=0;
        }
        for (int i=0; i<7; i++)
        {
            ranks[ passedCards[i].getRank() ]++;
        }

        // updated the flush checker to account for 7 card hand rather than 5 card hand
        int[] numPerSuit = {0, 0, 0, 0};

        for (int i=0; i<7; i++) {
        	numPerSuit[(int)passedCards[i].getSuit()]++;
        }

        for (int i = 0; i < 4; i++)
            if (numPerSuit[i] >= 5) flush = true;

        // this function should still work with the 7 card hand
        // checks whether we have a pair or two pair
        for (int i=13; i>=1; i--)
        {
                 if (ranks[i] > sameCards)
                 {
                     if (sameCards != 1)
                     //if sameCards was not the default value
                     {
                         sameCards2 = sameCards;
                         smallGroupRank = largeGroupRank;
                     }

                     sameCards = ranks[i];
                     largeGroupRank = i;

                 } else if (ranks[i] > sameCards2)
                 {
                     sameCards2 = ranks[i];
                     smallGroupRank = i;
                 }
        }

        // The next if and for loops are checking the order of our hand from highest to lowest
        if (ranks[1]==1) //if ace, run this before because ace is highest card
        {
            orderedRanks[index]=14;
            index++;
        }

        for (int i=13; i>=2; i--)
        {
            if (ranks[i]==1)
            {
                orderedRanks[index]=i; //if ace
                index++;
            }
        }
        
        // Check for Straight
        // fixed this to account for 7 card hand
        for (int i=1; i<=9; i++)
        //can't have straight with lowest value of more than 10
        {
            if (ranks[i] >= 1 && ranks[i+1] >= 1 && ranks[i+2] >=1 && 
                ranks[i+3] >= 1 && ranks[i+4] >=1 )
            {
                straight=true;
                topStraightValue=i+4; //4 above bottom value
                //break; Harry thinks don't break, because we might have a 6 card straight in which case we aren't getting the correct topStraightValue if we were to brek
            }
        }

        // Check for highest straight
        // fixed this to account for 7 card hand
     
        if (ranks[10] >= 1 && ranks[11] >= 1 && ranks[12] >= 1 && 
            ranks[13] >= 1 && ranks[1] >= 1) //ace high
        {
            straight=true;
            topStraightValue=14; //higher than king
        }
        
        for (int i=0; i<=5; i++)
        {
            value[i]=0;
        }


        //start hand evaluation
        if ( sameCards==1 ) {
            value[0]=1;
            value[1]=orderedRanks[0];
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
        }

        if (sameCards==2 && sameCards2==1)
        {
            value[0]=2;
            value[1]=largeGroupRank; //rank of pair
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
            value[4]=orderedRanks[2];
        }

        if (sameCards==2 && sameCards2==2) //two pair
        {
            value[0]=3;
            //rank of greater pair
            value[1]= largeGroupRank>smallGroupRank ? largeGroupRank : smallGroupRank;
            value[2]= largeGroupRank<smallGroupRank ? largeGroupRank : smallGroupRank;
            value[3]=orderedRanks[0];  //extra card
        }

        if (sameCards==3 && sameCards2!=2)
        {
            value[0]=4;
            value[1]= largeGroupRank;
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
        }

        if (straight && !flush)
        {
            value[0]=5;
            value[1]=topStraightValue;
        }

        if (flush && !straight)
        {
            value[0]=6;
            value[1]=orderedRanks[0]; //tie determined by ranks of cards
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
        }

        if (sameCards==3 && sameCards2==2)
        {
            value[0]=7;
            value[1]=largeGroupRank;
            value[2]=smallGroupRank;
        }

        if (sameCards==4)
        {
            value[0]=8;
            value[1]=largeGroupRank;
            value[2]=orderedRanks[0];
        }

        if (straight && flush)
        {
            value[0]=9;
            value[1]=topStraightValue;
        }

    }

    void display()
    {
        String s;
        switch( value[0] )
        {

            case 1:
                s="high card";
                break;
            case 2:
                s="pair of " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 3:
                s="two pair " + Card.rankAsString(value[1]) + " " + 
                                Card.rankAsString(value[2]);
                break;
            case 4:
                s="three of a kind " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 5:
                s=Card.rankAsString(value[1]) + " high straight";
                break;
            case 6:
                s="flush";
                break;
            case 7:
                s="full house " + Card.rankAsString(value[1]) + " over " + 
                                  Card.rankAsString(value[2]);
                break;
            case 8:
                s="four of a kind " + Card.rankAsString(value[1]);
                break;
            case 9:
                s="straight flush " + Card.rankAsString(value[1]) + " high";
                break;
            default:
                s="error in Hand.display: value[0] contains invalid value";
        }
        s = "                " + s;
        System.out.println(s);
    }

    void displayAll()
    {
        for (int x=0; x<5; x++)
            System.out.println(cards[x]);
    }

    // changed so that ties are labeled as wins
    public int compareTo(Hand that)
    {
        for (int x=0; x<6; x++)
        {
            if (this.value[x]>that.value[x])
                return 1;
            else if (this.value[x]<that.value[x])
                return -1;
        }
        return 1;
    }

    private static void runTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
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
		double playerWon = (new Hand(playerBoard)).compareTo(new Hand(opponentBoard));
		
		
        // Test passes if playerHand is the stronger hand
		if (playerWon > 0) {
            System.out.println("Test passed!");
        }
        else {
            System.out.println("Test failed!");
            // Print the board states if test fails
            for(Card c : playerBoard) {
			    System.out.println(Card.ranks[(int) c.getRank()] + " of " + Card.suits[(int) c.getSuit()] );
            }
            System.out.println("----------------");
            for(Card c : opponentBoard) {
                System.out.println(Card.ranks[(int) c.getRank()] + " of " + Card.suits[(int) c.getSuit()] );
            }
            System.out.println();
        }
        System.out.println("---------------- ----------------");
    }

    // Set of tests to run. In each test, playerHand should be the winning hand.

    private static void highCardTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing high card");

        // Initialize cards for flush test
        board[0] = new Card((short)2, (short)1);
        board[1] = new Card((short)3, (short)3);
        board[2] = new Card((short)0, (short)5);
        board[3] = new Card((short)1, (short)7);
        board[4] = new Card((short)2, (short)9);

        playerHand[0] = new Card((short)0, (short)8);
        playerHand[1] = new Card((short)0, (short)12);

        opponentHand[0] = new Card((short)3, (short)12);
        opponentHand[1] = new Card((short)2, (short)6);

        runTest(playerHand, board, opponentHand);

    }

    private static void pairTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing pairs");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)3);
        board[2] = new Card((short)2, (short)5);
        board[3] = new Card((short)3, (short)7);
        board[4] = new Card((short)0, (short)9);

        playerHand[0] = new Card((short)0, (short)8);
        playerHand[1] = new Card((short)0, (short)9);

        opponentHand[0] = new Card((short)3, (short)12);
        opponentHand[1] = new Card((short)2, (short)11);

        runTest(playerHand, board, opponentHand);

    }

    private static void twoPairTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing two pairs");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)3);
        board[2] = new Card((short)2, (short)5);
        board[3] = new Card((short)3, (short)7);
        board[4] = new Card((short)0, (short)9);

        playerHand[0] = new Card((short)3, (short)7);
        playerHand[1] = new Card((short)3, (short)5);

        opponentHand[0] = new Card((short)0, (short)2);
        opponentHand[1] = new Card((short)1, (short)9);

        runTest(playerHand, board, opponentHand);

    }

    private static void tripTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing trips");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)3);
        board[2] = new Card((short)2, (short)5);
        board[3] = new Card((short)3, (short)7);
        board[4] = new Card((short)0, (short)9);

        playerHand[0] = new Card((short)0, (short)3);
        playerHand[1] = new Card((short)3, (short)3);

        opponentHand[0] = new Card((short)3, (short)9);
        opponentHand[1] = new Card((short)2, (short)7);

        runTest(playerHand, board, opponentHand);

    }

    private static void straightTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing straights");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)3);
        board[2] = new Card((short)2, (short)5);
        board[3] = new Card((short)3, (short)7);
        board[4] = new Card((short)0, (short)9);

        playerHand[0] = new Card((short)0, (short)4);
        playerHand[1] = new Card((short)0, (short)6);

        opponentHand[0] = new Card((short)3, (short)9);
        opponentHand[1] = new Card((short)2, (short)9);

        runTest(playerHand, board, opponentHand);

    }

    private static void flushTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing flush");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)0, (short)3);
        board[2] = new Card((short)0, (short)5);
        board[3] = new Card((short)1, (short)7);
        board[4] = new Card((short)2, (short)9);

        playerHand[0] = new Card((short)0, (short)2);
        playerHand[1] = new Card((short)0, (short)10);

        opponentHand[0] = new Card((short)3, (short)4);
        opponentHand[1] = new Card((short)2, (short)6);

        runTest(playerHand, board, opponentHand);

    }

    private static void houseTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing full house");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)0, (short)3);
        board[2] = new Card((short)0, (short)5);
        board[3] = new Card((short)1, (short)5);
        board[4] = new Card((short)2, (short)7);

        playerHand[0] = new Card((short)3, (short)5);
        playerHand[1] = new Card((short)2, (short)3);

        opponentHand[0] = new Card((short)0, (short)2);
        opponentHand[1] = new Card((short)0, (short)10);

        runTest(playerHand, board, opponentHand);

    }


    private static void quadTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing quads");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)1);
        board[2] = new Card((short)2, (short)3);
        board[3] = new Card((short)3, (short)3);
        board[4] = new Card((short)2, (short)5);

        playerHand[0] = new Card((short)2, (short)1);
        playerHand[1] = new Card((short)3, (short)1);

        opponentHand[0] = new Card((short)3, (short)5);
        opponentHand[1] = new Card((short)2, (short)12);

        runTest(playerHand, board, opponentHand);

    }

    private static void straightFlushTest(Card[] playerHand, Card[] board, Card[] opponentHand) {
        System.out.println("Testing straight flush");

        // Initialize cards for flush test
        board[0] = new Card((short)0, (short)1);
        board[1] = new Card((short)1, (short)1);
        board[2] = new Card((short)3, (short)6);
        board[3] = new Card((short)3, (short)7);
        board[4] = new Card((short)3, (short)8);

        playerHand[0] = new Card((short)3, (short)9);
        playerHand[1] = new Card((short)3, (short)10);

        opponentHand[0] = new Card((short)2, (short)1);
        opponentHand[1] = new Card((short)3, (short)1);

        runTest(playerHand, board, opponentHand);

    }

    

    public static void main(String args[]) {
		// Create the hands to draw into, including the board
		Card[] playerHand = new Card[2];
		Card[] board = new Card[5];
		Card[] opponentHand = new Card[2];

        highCardTest(playerHand, board, opponentHand);
        pairTest(playerHand, board, opponentHand);
        twoPairTest(playerHand, board, opponentHand);
        tripTest(playerHand, board, opponentHand);
        straightTest(playerHand, board, opponentHand);
        flushTest(playerHand, board, opponentHand);
        houseTest(playerHand, board, opponentHand);
        quadTest(playerHand, board, opponentHand);
        straightFlushTest(playerHand, board, opponentHand);

        
	}
}