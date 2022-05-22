package Bohnanza;

public interface Decisiones {

	// decide whether the card should be planted
	public boolean plantDecide(Cartas carta);
	
	// pick a field in which the card will be planted
	public Campo pickFieldToPlant(Cartas carta);

	// decide whether to send a trade request
	public boolean sendTradeRequest(Cartas carta, Jugador jugador);
		
	// pick the undesired card that you would like to trade
	public Cartas pickCardToTrade(Jugador jugador);

	// decide which player is the trading partner
	public int acceptTradeRequest(Cartas[][] comerciar);
	
	//decide whether to trade or plant the following card
	public boolean plantDecideTA(Cartas carta);

}