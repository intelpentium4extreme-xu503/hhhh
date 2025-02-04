import java.util.ArrayList;
import java.util.List;

public class ModelCode_CardGame {

    public static final int POCKETSIZE = 25;

    public static CardPool myCardPool;
    public static HandsMaxHeap myMaxHeap;

    public static Card[] myCards, tempCards;
    public static int pocketSize = POCKETSIZE;

    // [Problem 2] Generate All Possible Valid Hands from the Pocket Cards and store them in myMaxHeap
    public static void generateHands(Card[] thisPocket)
    {
        // If thisPocket has less than 5 cards, no hand can be generated, thus the heap will be empty
        
        // Otherwise, generate all possible valid hands from thisPocket and store them in myMaxHeap

        // Pay attention that memory needs to be allocated for the heap!

        myMaxHeap = new HandsMaxHeap(53130); // initiate the heap for all 53130 possible combinations (25C5)
        int[] indices = new int[5]; //store the index of the a combination

        // initiate the indexes
        for(int i = 0; i < 5; i++) 
            indices[i] = i;

        while(indices[0] <= pocketSize - 5) 
        {
            int a = 4;
            // insert the current combination
            // System.out.println("a = " + a + ", indices = " + indices[0] + ", " + indices[1] + ", " + indices[2] + ", " + indices[3] + ", " + indices[4]);
            Hands currentHands = new Hands(thisPocket[indices[0]], thisPocket[indices[1]], thisPocket[indices[2]], thisPocket[indices[3]], thisPocket[indices[4]]);
            if(currentHands.isAValidHand())
                myMaxHeap.insert(currentHands);

            // find the next combination
            while(a >= 0 && indices[a] == pocketSize - 5 + a)
                a--;
            if (a < 0) 
                break;

            indices[a]++;
            for (int i = a + 1; i < 5; i++) {
                indices[i] = indices[i - 1] + 1;
            }
        }
    }

    // Sorts the array of Cards in ascending order: ascending order of ranks; cards of equal ranks are sorted in ascending order of suits
    public static void sortCards(Card[] cards)
    {
        int j;
        Card temp;        
        int size = cards.length;
        
        for(int i = 1; i < size; i++) 
        { 
            temp = cards[i];		
            for(j = i; j > 0 && cards[j-1].isMyCardLarger(temp); j--) 
                cards[j] = cards[j-1]; 
            cards[j] = temp;
        }  
    }

    // Check if this hand of cards includes any played card
    public static boolean isHandContainUsed(Hands handToCheck, Card[] usedCards) 
    {
        //System.out.println("userCards length is " + usedCards.length);
        for (int i = 0; i < usedCards.length; i++)
        {
            if(handToCheck.hasCard(usedCards[i]))
                return true;
        }
        return false;
    }

    public static void main(String args[]) throws Exception
    {
        Hands myMove;        
        
        myCardPool = new CardPool();        
        myCardPool.printPool();

        myCards = new Card[pocketSize];
        myCards = myCardPool.getRandomCards(POCKETSIZE);  
        sortCards(myCards);

        // print cards
        System.out.println("My Pocket Cards:");
        for(int j = 0; j < pocketSize; j++)
        {            
            myCards[j].printCard();
        }
        System.out.println();
        
        generateHands(myCards); // generates all valid hands from myCards and stores them in myMaxHeap
        myMaxHeap.printHeap(); // prints the contents of the initial heap

        // Print the contents of myMaxHeap
        
        // [Problem 3] Implementing Game Logic Part 1 - Aggresive AI: Always Picks the Strongest Hand
        System.out.println("The most aggressive moves: ");
        int c = 0;
        Card[] comparCards = new Card[5];
        for(int i = 0; pocketSize > 4; i++)
        {                               
            // Step 1:
            // - Check if the Max Heap contains any valid hands 
            //   - if yes, remove the Largest Hand from the heap as the current Move
            //   - otherwise, you are out of valid hands.  Just pick any 5 cards from the pocket as a "Pass Move"
            // - Once a move is chosen, print the Hand for confirmation. MUST PRINT FOR VALIDATION!!

            if(myMaxHeap.isEmpty()) // If the Max Heap does not contain any valid hands, then use the first five cards
            {
                myMove = new Hands(myCards[0], myCards[1], myCards[2], myCards[3], myCards[4]);
                tempCards = new Card[pocketSize - 5];
                for(int a = 5; a < pocketSize; a++)
                {
                    tempCards[a - 5] = myCards[a];
                }
                myCards = tempCards;
                pocketSize -= 5;
                myMove.printMyHand();
                System.out.println();
                System.out.println("Remaining Pocket Cards:");
                for(int j = 0; j < pocketSize; j++)
                {            
                    myCards[j].printCard();
                }
                System.out.println();
                System.out.println("------------------------------------------------");
            }
            else
            {
                Hands tempHands = myMaxHeap.removeMax(); // Get the largest combination in the heap
                if(i == 0) // If it is the first time, then just confirm the move
                {
                    myMove = tempHands;
                    myMove.printMyHand();
                    System.out.println();
                    tempCards = new Card[pocketSize - 5]; // Initiate the remaing cards
                    //comparCards = new Card[5];
                    int b = 0;
                    for(int a = 0; a < pocketSize; a++) // If a card is used, then add it to compareCards for future comparison, otherwise add it to the remaing cards
                    {
                        if(tempHands.hasCard(myCards[a]))
                        {
                            comparCards[c++] = myCards[a];
                        }
                        else
                        {
                            tempCards[b++] = myCards[a];
                        }
                    }
                    myCards = tempCards;
                    pocketSize -= 5;
                    System.out.println("Remaining Pocket Cards:");
                    for(int j = 0; j < pocketSize; j++)
                    {            
                        myCards[j].printCard();
                    }
                    System.out.println();
                    System.out.println("------------------------------------------------");

                } 
                else if(!isHandContainUsed(tempHands, comparCards)) // Check whether this combination includes used cards
                {
                    myMove = tempHands;
                    myMove.printMyHand();
                    System.out.println();
                    tempCards = new Card[pocketSize - 5];
                    Card[] comparetempCards = new Card[POCKETSIZE - pocketSize + 5]; // Expand the size of compareCards for appending used cards
                    for(int d = 0; d < comparCards.length; d ++)
                    {
                        comparetempCards[d] = comparCards[d];
                    }
                    comparCards = new Card[POCKETSIZE - pocketSize + 5];
                    for(int d = 0; d < comparCards.length; d ++)
                    {
                        comparCards[d] = comparetempCards[d];
                    }
                    int b = 0;
                    
                    /*
                    System.out.println("11111111111111111111111111111111111");
                    tempHands.printMyHand();
                    System.out.println();

                    for(int j = 0; j < pocketSize; j++)
                    {            
                        myCards[j].printCard();
                    }
                    System.out.println();
                    System.out.println("22222222222222222222222222222222222");
                     */

                    for(int a = 0; a < pocketSize; a++)
                    {
                        //System.out.println("a = " + a + ", b = " + b + ", pocketSize = " + pocketSize);
                        if(tempHands.hasCard(myCards[a]))
                        {
                            comparCards[c++] = myCards[a];
                        }
                        else
                        {
                            tempCards[b++] = myCards[a];
                        }
                    }
                    myCards = tempCards;
                    pocketSize -= 5;
                    System.out.println("Remaining Pocket Cards:");
                    for(int j = 0; j < pocketSize; j++)
                    {            
                        myCards[j].printCard();
                    }
                    System.out.println();
                    System.out.println("------------------------------------------------");
                    
                    /*
                    System.out.println("Compare Cards:");
                    System.out.println(comparCards.length);
                    for(int j = 0; j < comparCards.length; j++)
                    {            
                        comparCards[j].printCard();
                    }
                    System.out.println();
                     */       
                }
            }

            // Step 2:
            // - Remove the Cards used in the move from the pocket cards and update the Max Heap
            // - Print the remaining cards and the contents of the heap

        }
        
    }

}
