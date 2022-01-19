package ec.gov.senplades.pruebas;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;

public class EjemploPanel3 extends Frame {

	EjemploPanel3(String m) {
		super("ExampleLayout2: "+m);
		setSize(340,280) ;
		Panel p = new Panel();
		p.setLayout(new BorderLayout());
		p.add("North", new TextArea(8,40));
		p.add("West", new Checkbox("Save config"));
		p.add("East", new Checkbox("Ignore colors"));
		p.add("South", new Button ("Exit"));
		add(p);
		setVisible(true);
		}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new EjemploPanel3("BorderLayout");
	}

}
