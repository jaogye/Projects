package ec.gov.senplades.reportes;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;


public class fMuestraImagen extends JFrame
{
	//ImageIcon imagen ;	
	JScrollPane ScrollPanel;
	JLabel etiqueta; 
	//Image  image = null;
	ImageIcon imagen = null; 
	
	public fMuestraImagen(String sFile)
	{
		
        // La ventana
        JFrame ventana = new JFrame("Imagen");
        
        // El panel de scroll
        JScrollPane scroll = new JScrollPane();
        
        // La etiqueta.
        JLabel etiqueta = new JLabel();
        
        // Se carga la imagen, con path absoluto para evitar problemas y debe
        // ser un gif.
        Icon imagen = new ImageIcon (sFile);
        
        // Se mete la imagen en el label
        etiqueta.setIcon (imagen);
        
        // Se mete el scroll en la ventana
        ventana.getContentPane().add(scroll);
        
        // Se mete el label en el scroll
        scroll.setViewportView(etiqueta);
        
        // Y se visualiza todo.
        ventana.pack();
        ventana.setVisible(true);
		
	}
	
	
	@SuppressWarnings("deprecation")
	public static void main(String H[])
	{
		final fMuestraImagen p = new fMuestraImagen("imagenes/puntual.jpg");
		
		//COLOCAMOS EL CODIGO QUE PERMITE CERRAR LA VENTANA
		p.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				//System.exit(0);
				p.setVisible(false);
				
			}
		});
	}
}

