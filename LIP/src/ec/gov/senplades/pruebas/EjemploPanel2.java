package ec.gov.senplades.pruebas;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;

public class EjemploPanel2 extends Frame {

	EjemploPanel2(String m) {
		super("ExampleLayout: "+m);
		setSize(240,80);
		Panel p = new Panel();
		add(p) ;
		p.setLayout(new GridLayout(3,2)) ; // use a 3x2 grid
		p.add(new Checkbox ("Save config"));
		p.add(new Button ("Save"));
		p.add(new Checkbox("Save changes"));
		p.add(new Button("Abort & do not save"));
		p.add(new Checkbox("Ignore colors"));
		p.add(new Button("Quit"));
		setVisible(true);
		}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EjemploPanel2("GridLayout");
	}

}
