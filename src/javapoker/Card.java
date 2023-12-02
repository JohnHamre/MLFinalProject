package javapoker;
public class Card{
    private short rank, suit;

    public static String[] suits = { "hearts", "spades", "diamonds", "clubs" };
    public static String[] ranks  = { "Ace", "2", "3", "4", "5", "6", "7", 
                                       "8", "9", "10", "Jack", "Queen", "King" };

    public static String rankAsString( int __rank ) {
        return ranks[__rank];
    }

    public Card(short suit, short rank)
    {
        this.rank=rank;
        this.suit=suit;
    }

    public @Override String toString()
    {
          return ranks[rank] + " of " + suits[suit];
    }

    public short getRank() {
         return rank;
    }

    public short getSuit() {
        return suit;
    }
    
    public int toID() {
    	return this.suit * 13 + this.rank;
    }
    
    public static Card fromID(int id) {
    	return new Card((short) ((float)id / 13.0), (short) (id % 13));
    }
    
    public static String nameFromID(int id) {
    	return suits[(short) ((float)id / 13.0)] + " of " + ranks[id % 13];
    }
}