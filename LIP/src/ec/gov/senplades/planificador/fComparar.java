/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * fComparar.java
 *
 * Created on 16/07/2010, 09:49:22 AM
 */

package ec.gov.senplades.planificador;

/**
 *
 * @author jalvarado
 */
public class fComparar extends javax.swing.JFrame {

    /** Creates new form fComparar */
    public fComparar() {
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

        jButton1 = new javax.swing.JButton();
        cmdAceptar1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtArchivo = new javax.swing.JTextField();
        cmdCancelar = new javax.swing.JButton();

        setTitle("Compara corrida");

        jButton1.setText("...");

        cmdAceptar1.setText("Aceptar");
        cmdAceptar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAceptar1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Archivo");

        txtArchivo.setText("                                   ");

        cmdCancelar.setText("Cancelar");
        cmdCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(37, 37, 37)
                            .addComponent(txtArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addGap(18, 18, 18)
                            .addComponent(jButton1))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addComponent(cmdAceptar1)
                            .addGap(127, 127, 127)
                            .addComponent(cmdCancelar)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(26, 26, 26)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(68, 68, 68)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdCancelar)
                        .addComponent(cmdAceptar1))
                    .addGap(69, 69, 69)))
        );

        pack();
    }// </editor-fold>                        

    private void cmdAceptar1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
        setVisible(false);
}                                           

    private void cmdCancelarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
        setVisible(false);         
}                                           

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new fComparar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton cmdAceptar1;
    private javax.swing.JButton cmdCancelar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtArchivo;
    // End of variables declaration                   

}
