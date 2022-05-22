package Bohnanza;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Formatter;

public class Cartas {

	private String nombre;				//nombre de la carta
	private int numero;					//total de cartas en el juego
	private int[] valor = new int[5];	//array para almacenar el valor de la carta

	
	
	public Cartas(int carta){

		Scanner sc = null;

		try {

			sc = new Scanner(new File("CartasBohnanza.txt")).useDelimiter(",");
			
			//te lee todo el documento fila a fila
			for(int row = 1; row < carta; row++){
				sc.nextLine();
			}
			
			//selecciona el nombre, el numero y el valor de cada carta
			setNombre(sc.next());
			setNumero(Integer.valueOf(sc.next()));
			for(int coinNumber = 1; coinNumber < 5; coinNumber++)
				valor[coinNumber] = Integer.valueOf(sc.next());
			

		} catch (FileNotFoundException error) {

			System.err.println("File not found - check the file name");

		}
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int[] getValor() {
		return valor;
	}

	public void setValor(int[] valor) {
		this.valor = valor;
	}

}