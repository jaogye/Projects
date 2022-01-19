package ec.gov.senplades.pruebas;

import java.awt.Button;
import java.awt.Frame;

public class EjemploFrame4 extends Frame {

	/**
	 * @param args
	 */
	
	EjemploFrame4(String m) {
		super("Example4: "+m);
		setSize(100, 150) ;
		add(new Button("Save"));
		setVisible(true);
		}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  new EjemploFrame4("Button");
	}

}
