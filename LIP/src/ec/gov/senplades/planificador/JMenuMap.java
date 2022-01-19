package ec.gov.senplades.planificador;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


public class JMenuMap extends JMapFrame implements	ActionListener  {
	
	Planificador oPlanificador; 

	
	JMenuMap(MapContext map) {
        super(map); 
        
        this.enableStatusBar(true);
        this.enableToolBar(true);
        this.initComponents();
        
		setTitle("Planificador Multisectorial");
		setSize(1280,970) ;
		
		oPlanificador = new Planificador(this);  
		
        try {
			this.showMap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		MenuItemP oItem = (MenuItemP) event.getSource() ; 
		//System.out.println( oItem.sMetodo );
		try {
		      Class<?> cl=Class.forName("ec.gov.senplades.planificador.Planificador");
		      Method mthd=cl.getMethod(oItem.sMetodo);
		      mthd.invoke(oPlanificador);
		  } catch (Exception e) {
		     e.printStackTrace();
		  } 		
	}
	
	
    public void showMap() throws Exception  {

        BuildMenu(this);
        this.setVisible(true);
        
    }
// docs end display shapefile

	void BuildMenu(JMapFrame oForma) {

		JMenuBar mb = oForma.getJMenuBar();
		JMenu m, m2;
		MenuItemP oItem; 
		mb = new JMenuBar();
		oForma.setJMenuBar(mb) ;
		mb.add(m = new JMenu("Archivo"));  
		m.add(oItem = new MenuItemP("Abrir", "Abrir")); oItem.addActionListener(this); 
		
		m.add(oItem = new MenuItemP("Guardar","Guardar")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Guardar Como", "GuardarComo")); oItem.addActionListener(this);	 
		m.add(oItem = new MenuItemP("Fijar Territorio","FijarTerritorio")); oItem.addActionListener(this);
		m.addSeparator();
		m.add(oItem = new MenuItemP("Exportar", "Exportar")); oItem.addActionListener(this);
		//m.add(oItem = new MenuItemP("Imprimir","Imprimir")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Consolidar","Consolidar")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Comparar","Comparar")); oItem.addActionListener(this);
		
		mb.add(m = new JMenu("Ver Mapas"));

		m.add(oItem = new MenuItemP("Infraestructural Actual","VerInfraestructuralActual")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Zonas de Riesgo","VerZonasRiesgo")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Tiempos de Desplazamiento", "VerTiemposDesplazamiento")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Costos de Construcción de Vias ","VerCostosConstruccionVias")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Indicadores Sociales", "VerIndicadoresSociales")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Zonas de Influencia", "VerZonasInfluencia")); oItem.addActionListener(this);		

		mb.add(m = new JMenu("Ingreso de datos"));
		m.add(oItem = new MenuItemP("InfraestructuralActual", "IngresoInfraestructuralActual")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Zonas de Riesgo", "ZonasRiesgos")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Indicadores Adicionales", "Indicadores")); oItem.addActionListener(this);
		m.addSeparator();
		m.add(oItem = new MenuItemP("Productos","Productos")); oItem.addActionListener(this);			
		m.add(oItem = new MenuItemP("Insumos", "Insumos")); oItem.addActionListener(this);		
		m.addSeparator();
		m.add(oItem = new MenuItemP("Tipo de Intervenciones", "TipoIntervenciones")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Formulas de Intervenciones", "FormulaIntervenciones")); oItem.addActionListener(this);
		m.addSeparator();		
		m.add(oItem = new MenuItemP("Inflación", "Inflacion")); oItem.addActionListener(this);
		
		mb.add(m = new JMenu("Localizar"));
		m.add(oItem = new MenuItemP("P-medianas sin limite","PMedianasSinLimite")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-medianas con limite","PMedianasConLimite")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-centros sin limite","PCentrosSinLimite")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-centros con limite","PCentrosConLimite")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Máxima Cobertura","MaximaCobertura")); oItem.addActionListener(this);

		mb.add(m = new JMenu("Herramientas")); 
		m.add(oItem = new MenuItemP("Calculos de Zonas de Influencia","CalculosZonasInfluencia")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Calculos de Impactos","CalculosImpactos")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Rebalanceo de Capacidades", "Rebalanceo")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Opciones","Opciones" )); oItem.addActionListener(this);		

		mb.add(m = new JMenu("Reportes"));
		m.add(oItem = new MenuItemP("Impactos","InfImpactos")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Desglose de Impactos", "InfDesgloseImpactos")); oItem.addActionListener(this);				
		m.add(oItem = new MenuItemP("Intervención Puntual","InfIntervencionPuntual" )); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Intervención Lineal","InfIntervencionLineal")); oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Intervención Poligonal", "InfIntervencionPoligonal")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Actividades", "InfActividad")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Vacios de Cobertura", "InfVaciosCobertura")); oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Rebalanceo de Capacidades", "InfRebalanceo")); oItem.addActionListener(this);		
		
	}
  
}
