package ec.gov.senplades.pruebas;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;

public class EjemploEvento2 extends Frame {

	TextArea txt;
	public static void main(String arg[]) {
	new EjemploEvento2("Event Handling");
	}
	EjemploEvento2(String m) {
	   super("Example: "+m);
	   setSize(340,280) ;
	   Panel p = new Panel();
	   p.setLayout(new BorderLayout());
	   p.add("North", txt = new TextArea());
	   p.add("West", new Checkbox("Save config"));
	   p.add("East", new Checkbox("Ignore colors"));
	   Panel q = new Panel();
	   q.add(new Button("Clear"));
	   p.add("South", q);
	   add(p);
	   setVisible(true);
	}
	
	public boolean action(Event e, Object target) {
	if (e.target instanceof Button) {
	   txt.setText("");
	   return true;
	  } else if (e.target instanceof Checkbox) {
	    Checkbox x = (Checkbox) e.target;
	    txt.appendText(x.getLabel()+ (x.getState() ? " is on\n" : " is off\n"));
	    return true;
	}
	return(super.action(e, target));
	}
	public boolean handleEvent(Event evt) {
	if (evt.id == Event.WINDOW_DESTROY)
	   System.exit(0) ;
	return(super.handleEvent(evt));
	}
}
