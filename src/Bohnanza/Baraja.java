package Bohnanza;

import java.util.ArrayList;
import java.util.Collections;

public class Baraja {

	private Cartas[] arrayCartas = new Cartas[9];					//Array de cartas
	private ArrayList<Cartas> baraja = new ArrayList<Cartas>();		//Guarda la info de la baraja
	
	public ArrayList<Cartas> getBaraja() {
		return baraja;
	}

	public void setBaraja(ArrayList<Cartas> baraja) {
		this.baraja = baraja;
	}

	private ArrayList<Cartas> descartes = new ArrayList<Cartas>();	//Guarda la pila de descartes
	private int rondas = 0;											//Cuenta las rondas
	
	public Baraja(){
		//
		for(int tipo = 1; tipo <=8; tipo++){
			arrayCartas[tipo] = new Cartas(tipo);
			for(int num = 1; num <= arrayCartas[tipo].getNumero();num++){
				baraja.add(arrayCartas[tipo]);
			}
		}
		barajea();
	}
	
	public ArrayList<Cartas> getDiscardPile() {
		return descartes;
	}

	public void setDiscardPile(ArrayList<Cartas> discardPile) {
		this.descartes = discardPile;
	}

	private void barajea(){
		Collections.shuffle(baraja);
	}
	
	//reparte las cartas al jugador de turno y le dice cual es
	public Cartas reparte(){		
		if (baraja.size() > 0) {
			Cartas tempCard = baraja.get(0);
			baraja.remove(0);
			return tempCard;
		} else {
			if (rondas == 3) {
				return reparte();
			} else {
				rellenaBarajas();
				rondas ++;
				return reparte();
			}
		}
	}
	
	//discards a card chosen by the player
	public void aDescartar(Cartas carta){
		descartes.add(carta);
	}
	
	//Refills the draw pile from the discard pile.
	public void rellenaBarajas(){ 
		for (int i = 0; i < descartes.size(); i ++)
			baraja.add(descartes.get(i));
			
		barajea();
		descartes.clear();
	}
	
	//setter and getter for the refill count
	public int getRondas() {
		return rondas;
	}

	public void setRondas(int rondas) {
		this.rondas = rondas;
	}
	
}


