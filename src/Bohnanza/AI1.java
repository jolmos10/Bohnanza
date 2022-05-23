package Bohnanza;


public class AI1 extends Jugador implements Decisiones{

	//constructor
	AI1(int playerNum, Baraja pile) {
		super(playerNum, pile);
	}
	
	//counts the number of cards that are imediatily availible for me to plant
	public int countAvailableCards(Cartas card){
		int availableCards = 0;
		
		//counts the number of the particular card in hand
		for(int c = 0; c < manoJ.getCartaMano().size(); c++){
			if(manoJ.getCartaMano().get(c) == card)
				availableCards++;
		}
		
		//counts the number of the particular card in field.
		for (int x = 0; x < 2; x ++){
			for(int c = 0; c < campoJ.length; c++){
				if(campoJ[x].cardCheck(card) == true)
					availableCards++;
			}
		}
		
		return availableCards;
	}
	
	//counts the cards that I may recieve and plant in the future of this game.
	public int countPossibleCards(Cartas card){
		//the possible number of cards from the deck
		int possibleCards = card.getNumero();
		
		//subtract the cards that we know the location of
		possibleCards -= countAvailableCards(card) + countUnavailableCards(card);
		return possibleCards;
	}
	
	//counts the cards that I have no chance to plant this game
	public int countUnavailableCards(Cartas card){
		int unavailableCards = 0;
		
		//counts number of cards in all fields
		//loop through players
		for(int p = 0; p < 4; p++){
			//loop through fields
			for(int f = 0; f < 2; f++){
				//exclude
				if (p != numeroJugadores){
					if(campos[p][f].getTipo() == card)
						unavailableCards = unavailableCards + campos[p][f].getTipo(); 
				}
			}
		}
		
		//add cards that have been discarded
		for(int c = 0; c < getBaraja().getDiscardPile().size(); c++)
			if(getBaraja().getDiscardPile().get(c) == card)
				unavailableCards++;
		
		return unavailableCards;
		
	}
	
	//calculate a score for each card based on thier availibility in the current game state
	public double scoreCard(Cartas card){

		int score = ((countAvailableCards(card) * 2) + (countPossibleCards(card))) / 104;
		return score;
	}
	
	//picks the field to plant a card
	@Override
	public Campo pickFieldToPlant(Cartas currentCard){
			
		// if field 1 contains the same card type, pick field 1
		if (campoJ[0].getTipo() == currentCard){
			return campoJ[0];
		// else if field 2 contains the same card type, pick field 2
		}else if (campoJ[1].getTipo() == currentCard){
			return campoJ[1];
			
		//if field 1 is empty, pick field 1
		} else if (campoJ[0].isEmpty()){
			return campoJ[0];
		
		//if field 2 is empty, pick field 2 
		} else if (campoJ[1].isEmpty()){
			return campoJ[1];
			
		}else{
			//otherwise pick the field that has the card with a higher score
			if (scoreCard(campoJ[0].getTipo()) > scoreCard(campoJ[1].getTipo()))
				return campoJ[0];
			//otherwise pick the field that has the card with a higher score
			else if (scoreCard(campoJ[0].getTipo()) < scoreCard(campoJ[1].getTipo()))
				return campoJ[1];
			//pick the field with less unavailible cards
			else if (countUnavailableCards(campoJ[0].getTipo()) < countUnavailableCards(campoJ[1].getTipo()))
				return campoJ[0];
			//pick the field with less unavailible cards
			else if (countUnavailableCards(campoJ[0].getTipo()) > countUnavailableCards(campoJ[1].getTipo()))
				return campoJ[1];
			else
				return campoJ[0];
		}		
	}
	
	//decide whether or not to plant another card from hand
	@Override
	public boolean plantDecide(Cartas card){
		//plant if field is empty or if field has the same card present
		if (campoJ[0].isEmpty() || campoJ[1].isEmpty() || campoJ[0].getTipo() == card || campoJ[1].getTipo() == card)
			return true;
		return false;
	}
	
	//decides whether or not to send a trade request
	@Override
	public boolean sendTradeRequest(Cartas card){
		//if i can trade for a card in my fields do it.
		if(card == campoJ[0].getTipo() || card == campoJ[1].getTipo() && manoJ.getCartaMano().contains(card) )
			return true;
		return false;
	}
	
	//selects a card to offer as a trade
	@Override
	public Cartas pickCardToTrade(Jugador player){
		
		//records the index of the worst cardd in hand
		int lowestScore = 0;
		
		//loops through hand and looks at cards.
		//picks the worst card that is still a valid trade
		for(int x = 0; x < manoJ.getCartaMano().size(); x ++){
			for(int y = 0; y < 2; x ++){
				//and if the cards are not in either field 
				if (manoJ.getCartaMano().get(x) != campoJ[y].getTipo()
						////and if the card is in the other players field
						&& (manoJ.getCartaMano().get(x) == player.campoJ[y].getTipo()) && 
							//and if the card has the lowest score.
							scoreCard(manoJ.getCartaMano().get(x)) < scoreCard(manoJ.getCartaMano().get(lowestScore)))
					lowestScore = x;		
			}
		}
		//return the valid card with the lowest score
		return manoJ.getCartaMano().get(lowestScore);
	}
	
	//decides whether or not, and selects player to trade with
	@Override
	//recieve an array of cards [players][card they give you,the card they want] and pick who/what to accept.
	public int acceptTradeRequest(Cartas[][] tradeCards) {
		
		//accept the player that will get you a plantable card
		for(int p = 0; p < tradeCards.length; p++){
			if(p != numeroJugadores)
				if(tradeCards[p][0] == campoJ[0].getTipo() || tradeCards[p][0] == campoJ[1].getTipo())
					return p;
		}
		return -1;
	}
	
	//decides whether to plant or trade the following card in hand.
	public boolean plantDecideTA(Cartas card) {

		//if card in in my field plant it.
		if(campoJ[0].getTipo() == card || campoJ[1].getTipo() == card)
			return true;
		
		//otherwise plant it
		return false;
	}
}