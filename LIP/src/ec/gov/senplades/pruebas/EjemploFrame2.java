package ec.gov.senplades.pruebas;

import java.awt.Frame;

public class EjemploFrame2 extends Frame {

	EjemploFrame2(String m) {
		super("Example2: "+m);
		setSize(150,100);
		setVisible(true);
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EjemploFrame2("Sub Clase");
	}

}
