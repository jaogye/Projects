/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * fVerInfraestructuraActual.java
 *
 * Created on 05/07/2010, 06:31:14 PM
 */

package ec.gov.senplades.planificador;

/**
 *
 * @author jalvarado
 */
public class fVerCostoConstruccionVias extends javax.swing.JFrame {

    /** Creates new form fVerInfraestructuraActual */
    public fVerCostoConstruccionVias() {
        initComponents();
        setVisible(true); 
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        cmdCancelar = new javax.swing.JButton();
        cmdAceptar = new javax.swing.JButton();
        cmbOrden = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Costo de Construcci�n de Vias");

        cmdCancelar.setText("Cancelar");
        cmdCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelarActionPerformed(evt);
            }
        });

        cmdAceptar.setText("Aceptar");
        cmdAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAceptarActionPerformed(evt);
            }
        });

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Primer Orden","segundo Orden","Tercer Orden" }));
        cmbOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOrdenActionPerformed(evt);
            }
        });

        jLabel2.setText("Orden Via");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel2)
                .addGap(10, 10, 10)
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(102, 102, 102)
                    .addComponent(cmdAceptar)
                    .addGap(92, 92, 92)
                    .addComponent(cmdCancelar)
                    .addGap(131, 131, 131)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(205, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(225, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdAceptar)
                        .addComponent(cmdCancelar))
                    .addGap(52, 52, 52)))
        );

        pack();
    }// </editor-fold>

    private void cmdCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        setVisible(false);
}

    private void cmdAceptarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    	new fMuestra("Se muestra un mapa donde se pinta los costos de \n " +
    			"construcci�n, de acuerdo con el orden de la via"); 
    }

    private void cmbOrdenActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
}

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new fVerCostoConstruccionVias().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JComboBox cmbOrden;
    private javax.swing.JButton cmdAceptar;
    private javax.swing.JButton cmdCancelar;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration

}