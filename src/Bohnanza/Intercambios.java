package Bohnanza;

public class Intercambios{
	
	private int numInter;			//el numero de usuario con el que intercambia
	private Cartas carta;			//la carta a comerciar
	
	public Intercambios(int numInter){
		this.numInter = numInter;
	}
	
	public int getNumInt() {
		return numInter;
	}

	public void setNumInt(int numInter) {
		this.numInter = numInter;
	}

	public Cartas getCartas(){
		return carta;
	}

	public void addCard(Cartas carta) {
		this.carta = carta;
	}
	
	public void removeCard(){
		this.carta = null;
	}

	public boolean isEmpty(){
		return (carta == null) ? true : false;
	}
	
}