package ec.gov.senplades.pruebas;

import java.awt.Checkbox;
import java.awt.Frame;

public class EjemploFrame3 extends Frame {

	EjemploFrame3(String m) {
		super("Example3: "+m);
		setSize(100,150) ;
		add(new Checkbox("Confirma Estado"));
		setVisible(true);
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EjemploFrame3("Checkbox");
	}

}
