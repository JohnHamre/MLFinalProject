package javapoker;
public class Hand {
    private Card[] cards;
    private int[] value;

    Hand(Card[] passedCards)
    {

        int[] ranks = new int[14];
        //miscellaneous cards that are not otherwise significant
        int[] orderedRanks = new int[5];
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
        for (int i=0; i<=7; i++)
        {
            ranks[ cards[i].getRank() ]++;
        }

        // updated the flush checker to account for 7 card hand rather than 5 card hand
        HashMap<String, Integer> numPerSuit = new HashMap<>();
        numPerSuit.put("hearts", 0);
        numPerSuit.put("spades", 0);
        numPerSuit.put("clubs", 0);
        numPerSuit.put("diamonds", 0);

        for (int i=0; i<7; i++) {
            numPerSuit.put(numPerSuit.getSuit(), get(passedCards[i] + 1));
        }
        
        for (String suit : numPerSuit.keySet()) {
            if (numPerSuit.getSuit >= 5) flush = true;
        }

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
            //value[1]=;
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
            //value[1]=;
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
            if (this.value[x]>=that.value[x])
                return 1;
            else if (this.value[x]<that.value[x])
                return -1;
        }
    }
}