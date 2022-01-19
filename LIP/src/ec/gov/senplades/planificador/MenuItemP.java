package ec.gov.senplades.planificador;

import javax.swing.JMenuItem;

public class MenuItemP extends JMenuItem {
    String sMetodo;
    
    public MenuItemP(String sNombre, String Metodo) {
    	super(sNombre); 
    	this.sMetodo = Metodo; 
    }
    
}
