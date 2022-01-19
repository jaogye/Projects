package ec.gov.senplades.pruebas;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.Panel;

public class EjemploPanel1 extends Frame {

	/**
	 * @param args
	 */
	
	EjemploPanel1(String m) {
		super("ExamplePanel: "+m);
		setSize(100,150) ;
		Panel p = new Panel(); // create Panel
		add(p) ; // add Panel into Frame
		p.add(new Button ("Save")); // add Button into Panel
		p.add(new Checkbox ("Save settings")); // add Checkbox into Panel
		setVisible(true);
		}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EjemploPanel1("Inserting Components");
	}

}
