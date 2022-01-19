package ec.gov.senplades.planificador;

import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 *
 * @author jalvarado
 */
public class fActividad extends javax.swing.JFrame {

	
	private Point2D punto ; 
    private String sTipoCentro; 
    private boolean bAmpliacion;  
	ArrayList<Object []> parametros ; 
	private fParametroInfraestructura oForma= null; 
	fInicio oFormaAnt; 
	int nTipoIntervencion ;
	
    /** Creates new form fPuntual */
    public fActividad(fInicio oFormaAnt, Point2D punto, String sCodigo) {
        this.oFormaAnt = oFormaAnt;
    	initComponents();
    	    	
        this.setVisible(true); 
        this.punto = punto; 
        txtX.setText( Double.toString(punto.getX()) ); 
        txtY.setText( Double.toString(punto.getY()) );
        txtAreaCensal.setText(sCodigo); 
        txtId.setText("Actividad-"+oFormaAnt.nActividad);         
        sTipoCentro = this.cmbTipoCentro.getSelectedItem().toString();
        Object[] res = this.oFormaAnt.DB.TipoIntervencion.SelectPorNombre(new String [] {"Id"}, sTipoCentro); 
        nTipoIntervencion = (Integer) res[0];           
        parametros =  this.oFormaAnt.DB.Parametro.SelectPorFiltro(
     		   new String [] {"Nombre", "Default"} , new Object [][] {{"TipoIntervencion_id", nTipoIntervencion}} );

    }


    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbTipoCentro = new javax.swing.JComboBox();
        cmbProgramaSocial = new javax.swing.JComboBox();        
        jLabel2 = new javax.swing.JLabel();
        txtX = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtY = new javax.swing.JTextField();
        cmdParametro = new javax.swing.JButton();
        cmdAceptar = new javax.swing.JButton();
        cmdCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtAreaCensal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();        
        txtUbicacion = new javax.swing.JTextField();

        jLabel7 = new javax.swing.JLabel();
        txtPoblacionBase = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRadio = new javax.swing.JTextField();
        cmdPoblacionObjetivo = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        
        
        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Actividad - Programa Social");

        jLabel1.setText("Tipo Actividad");

        ArrayList<Object []> oTipo =  this.oFormaAnt.DB.TipoIntervencion.SelectPorFiltro(
       		   new String [] {"Nombre"} , new Object [][] {{"Clase", oFormaAnt.claseIntervencion }} );    	
         
         String[] aTipo = new String[oTipo.size()];
         int i=0; 
         for (Object [] oo : oTipo ) {
         	 aTipo[i] = (String) oo[0]; 
         	 i++; 
         }
         
         cmbTipoCentro.setModel(new javax.swing.DefaultComboBoxModel(aTipo) );        
         cmbTipoCentro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoCentroActionPerformed(evt);
            }
        });

        jLabel2.setText("UTM X:");

        jLabel3.setText("UTM Y");

        cmdParametro.setText("Parámetros");
        cmdParametro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdParametroActionPerformed(evt);
            }
        });

        cmdAceptar.setText("Aceptar");
        cmdAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAceptarActionPerformed(evt);
            }
        });

        cmdCancelar.setText("Cancelar");
        cmdCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelarActionPerformed(evt);
            }
        });

        jLabel4.setText("Area Censal");

        txtAreaCensal.setText("                             ");

        jLabel5.setText("Código Ubicación ");

        txtUbicacion.setText("                                                   ");

        jLabel6.setText("Programa Social");

        cmbProgramaSocial.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alimentate Ecuador", "Bono Vivienda" }));
        cmbProgramaSocial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProgramaSocialActionPerformed(evt);
            }
        });

        jLabel7.setText("Población Base");

        txtPoblacionBase.setText("      ");

        jLabel8.setText("Radio acceso");

        txtRadio.setText("      ");        
        
        cmdPoblacionObjetivo.setText("Población Objetivo");
        cmdPoblacionObjetivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPoblacionObjetivoActionPerformed(evt);
            }
        });
        

        jLabel9.setText("Nombre Identificador");

        txtId.setText("                                         ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(177, Short.MAX_VALUE)
                .addComponent(cmdAceptar)
                .addGap(121, 121, 121)
                .addComponent(cmdCancelar)
                .addGap(124, 124, 124))
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbTipoCentro, javax.swing.GroupLayout.Alignment.LEADING, 0, 386, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbProgramaSocial, javax.swing.GroupLayout.Alignment.LEADING, 0, 386, Short.MAX_VALUE))
                        .addGap(137, 137, 137))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(64, 64, 64))
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(54, 54, 54)))
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtX, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtY, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtPoblacionBase, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtAreaCensal, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cmdPoblacionObjetivo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbProgramaSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipoCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtAreaCensal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addComponent(cmdParametro))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(9, 9, 9)
                            .addComponent(txtRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel3)
                            .addGap(15, 15, 15)
                            .addComponent(jLabel8))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(cmdPoblacionObjetivo))
                    .addComponent(txtPoblacionBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdAceptar)
                    .addComponent(cmdCancelar))
                .addGap(9, 9, 9))
        );

        pack();

        
    }// </editor-fold>

    private void cmbTipoCentroActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
        sTipoCentro = this.cmbTipoCentro.getSelectedItem().toString();
        Object[] res = this.oFormaAnt.DB.TipoIntervencion.SelectPorNombre(new String [] {"Id"}, sTipoCentro); 
        nTipoIntervencion = (Integer) res[0];           
        parametros =  this.oFormaAnt.DB.Parametro.SelectPorFiltro(
      		   new String [] {"Nombre", "Default"} , new Object [][] {{"TipoIntervencion_id", nTipoIntervencion}} );    	
    }                                             

    private void cmdAceptarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:

    	this.punto.setLocation(Double.parseDouble(this.txtX.getText()),   
    	Double.parseDouble(this.txtY.getText()) );
     	
        this.oFormaAnt.AgregaActividad(nTipoIntervencion, parametros);
    	this.oFormaAnt.nActividad++;        
    	this.setVisible(false); 
    }                                          

    private void cmdCancelarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    	
        this.punto = null;   
    	sTipoCentro = ""; 
    	this.setVisible(false);  
    }                                           

    private void cmdParametroActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        String sTipoCentro = this.cmbTipoCentro.getSelectedItem().toString();
        
        if (oForma == null) {

           oForma = new fParametroInfraestructura(sTipoCentro, parametros );
     	} else 
     	  oForma.setVisible(true); 	
    }                                            

    private void cmbProgramaSocialActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    
    private void cmdPoblacionObjetivoActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
     	new fPonderadorDemanda();     	
}
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new fActividad(null, null, "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JComboBox cmbTipoCentro;
    private javax.swing.JButton cmdAceptar;
    private javax.swing.JButton cmdCancelar;
    private javax.swing.JButton cmdParametro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtAreaCensal;
    private javax.swing.JTextField txtUbicacion;
    private javax.swing.JTextField txtX;
    private javax.swing.JTextField txtY;
    private javax.swing.JComboBox cmbProgramaSocial;    
    // End of variables declaration
    // Variables declaration - do not modify
    private javax.swing.JButton cmdPoblacionObjetivo;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;

    private javax.swing.JTextField txtPoblacionBase;
    private javax.swing.JTextField txtRadio;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtId;    

}
