package ec.gov.senplades.planificador;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class fParametroInfraestructura extends Frame {
	
	private JButton cmdAceptar, cmdCancelar; 
	//FINTERVENCION oForma; 
	String sTipoCentro; 
	Panel oPanel; 
	ArrayList<Object []> parametros; 
	
	fParametroInfraestructura(String sTipoCentro,  ArrayList<Object []> parametros) {
		super("Parametro Infraestructura"+sTipoCentro);
        this.sTipoCentro = sTipoCentro;
        this.parametros = parametros; 
        //this.oForma = oForma; 
        
		Panel p = new Panel();
        
		JTextField oTexto; 
		setSize(400,parametros.size()*40+40);
        oPanel = new Panel(); 
		p.add(oPanel) ;
		
		oPanel.setLayout(new GridLayout(parametros.size(),2)) ;
		for (Object [] oReg: parametros) {
			oPanel.add(new JLabel ((String)oReg[0]));
			oPanel.add(oTexto = new JTextField ( oReg[1].toString() ));
			oTexto.setSize(100, 40); 
		}
		oPanel.setVisible(true );

		
		cmdAceptar = new JButton(); 
        cmdAceptar.setText("Aceptar");
        cmdAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAceptarActionPerformed(evt);
            }
        });

		cmdCancelar = new JButton();        
        cmdCancelar.setText("Cancelar");
        cmdCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelarActionPerformed(evt);
            }
        });

		Panel p2 = new Panel();
		p2.add(cmdAceptar); 
		p2.add(cmdCancelar);
		p2.setVisible(true ); 
		p.add(p2); 
		
		add(p); 
		setVisible(true);		

	}
	
	
    private void cmdAceptarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    	int i=0; 
        for (Component oo : oPanel.getComponents() ) {
        	if (oo instanceof JTextField){
        		JTextField oTexto = (JTextField) oo;
        		String sTexto = oTexto.getText();
        		Integer n = Integer.parseInt(sTexto); 
        		parametros.set(i, new Object[] {oTexto.getText(), n} ); 
        		i++; 
        	}
        	
        }
        
        setVisible(false); 
        }
     	


 

    

    private void cmdCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

    	this.setVisible(false);     	
    }

		public static void main() {
		//new fParametroInfraestructura("Parametro InfraEstructura");
		}
		}

	

