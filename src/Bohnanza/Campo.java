package Bohnanza;
public class Campo{
	
	private Cartas carta;			//rellena el tipo de carta que va en el campo
	private int numCartas = 0;		//completa con el numero de cartas en el campo
	private int numeroCampo;		//da un valor a cada campo para diferenciarlos

	//asigna un numero a cada campo
	public Campo(int numero){
		setNumeroCampo(numero);
	}

	public Cartas getTipo() {
		return carta;
	}

	public void setTipo(Cartas carta) {
		this.carta = carta;
	}

	public int getNumCartas() {
		return numCartas;
	}

	public void setNumCartas(int numCartas) {
		this.numCartas = numCartas;
	}

	public int getNumeroCampo() {
		return numeroCampo;
	}

	public void setNumeroCampo(int numeroCampo) {
		this.numeroCampo = numeroCampo;
	}
	
	//si planas una carta la añade al campo
	public void increaseNumCards(int numCartas) {
		this.numCartas += numCartas;
	}
	
	//comprueba que la carta que quieres plantar se puede plantar
	public boolean cardCheck(Cartas carta){
		return (carta == null || carta.getNombre() == carta.getNombre()) ? true : false;
	}

	//devuelve un boolean para saber si no hay cartas en el campo
	public boolean isEmpty(){
		return (carta == null) ? true : false;
	}

	//metodo para cosechar las judias
	public int coinFromHarvest(){
		for(int n = 4; n > 0; n--){
			if(numCartas >= carta.getValor()[n])
				return n;
		}
		return 0;
	}
}