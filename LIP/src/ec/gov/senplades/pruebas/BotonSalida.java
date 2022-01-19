package ec.gov.senplades.pruebas;

import java.awt.Button;
import java.awt.Event;

public class BotonSalida extends Button {

	BotonSalida(String label) {
		super(label);
	}
	public boolean action(Event e, Object what) {
		System.exit(0);
		return true;
	}
}
