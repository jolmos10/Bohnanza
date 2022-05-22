package Bohnanza;

import java.util.ArrayList;

//Every player has a hand whcih they can see
public class Mano{
	
	//An array list that holds the cards in each players hand.
	private ArrayList<Cartas> cartasMano = new ArrayList<Cartas>();
	
	//constructor
	public Mano(){
	}
	
	//The getters and setters for the hand
	public ArrayList<Cartas> getCartaMano() {
		return cartasMano;
	}

	public void setCartaMano(ArrayList<Cartas> cartasMano) {
		this.cartasMano = cartasMano;
	}
	
	//adds a card to the players hand
	public void addCard(Cartas carta){
		cartasMano.add(carta);
	}
	
	//removes a card from the players hand
	public void removeCard(int a){
		cartasMano.remove(a);
	}
	
	//gets a specific card from the hand
	public Cartas getCarta(int a){
		return cartasMano.get(a);
	}
	
}