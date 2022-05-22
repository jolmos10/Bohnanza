package Bohnanza;

public class Jugador implements Decisiones{
	
	protected int numeroJugadores;
	protected int banco = 0;
	protected Mano manoJ = new Mano();
	protected Campo[] campoJ = new Campo[2];
	protected Intercambios[] cambios = new Intercambios[2];
	protected Campo[][] campos = new Campo[4][2]; //first index is player num  //Second index is fieldnum
	protected Baraja baraja;
	
	public Jugador(int numeroJugadores, Baraja baraja){
		setNumJ(numeroJugadores);
		setBaraja(baraja);
		for(int n = 0; n < 2; n++){
			campoJ[n] = new Campo(n);
			cambios[n] = new Intercambios(n);
		}
			
	}
	
	/** getters and setters **/
	public Baraja getBaraja() {
		return baraja;
	}

	public void setBaraja(Baraja baraja) {
		this.baraja = baraja;
	}

	public Campo[][] get3Campos() {
		return campos;
	}

	public void set3Campos(Campo[][] campos) {
		this.campos = campos;
	}

	public int getNumJ() {
		return numeroJugadores;
	}

	public void setNumJ(int numeroJugadores) {
		this.numeroJugadores = numeroJugadores;
	}

	public int getBanco() {
		return banco;
	}

	public void setBanco(int banco) {
		this.banco = banco;
	}
	
	public void addBanco(int banco) {
		this.banco = this.banco + banco;
	}

	public Mano getMano() {
		return manoJ;
	}

	public void setMano(Mano manoJ) {
		this.manoJ = manoJ;
	}

	public Campo[] getCampos() {
		return campoJ;
	}

	public void setCampos(Campo[] campoJ) {
		this.campoJ = campoJ;
	}

	public Intercambios[] getTradingArea() {
		return cambios;
	}

	public void setTradingArea(Intercambios[] cambios) {
		this.cambios = cambios;
	}

	
	/** methods **/
	public boolean plantDecide(Cartas carta) {
		return false;
	}
	
	public Campo pickFieldToPlant(Cartas carta) {
		return null;
	}
	
	public boolean sendTradeRequest(Cartas carta) {
		return false;
	}

	public int acceptTradeRequest(Cartas[][] cambiaCartas) {
		return 0;
	}


	public boolean sendTradeRequest(Cartas carta, Jugador jugador) {
		// TODO Auto-generated method stub
		return false;
	}

	public Cartas pickCardToTrade(Jugador jugador) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean plantDecideTA(Cartas cartas) {
		// TODO Auto-generated method stub
		return false;
	}

}