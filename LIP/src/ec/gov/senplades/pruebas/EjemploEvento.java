package ec.gov.senplades.pruebas;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;

public class EjemploEvento extends Frame {

	EjemploEvento(String m) {
		super("ExampleButton: "+m);
		setSize(340,280) ;
		Panel p = new Panel();
		p.setLayout(new BorderLayout());
		p.add("North", new TextArea());
		p.add("West", new Checkbox("Save config"));
		p.add("East", new Checkbox("Ignore colors"));
		Panel q = new Panel();
		q.add(new BotonSalida("Exit"));
		p.add("South", q);
		add(p);
		setVisible(true);
	}
	public static void main(String arg[]) {
		new EjemploEvento("Event");
	}
}
