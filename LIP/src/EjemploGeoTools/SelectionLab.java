package EjemploGeoTools;
// docs start source
/*
 *    GeoTools - The Open Source Java GIS Tookit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This file is hereby placed into the Public Domain. This means anyone is
 *    free to do whatever they wish with this file. Use it well and enjoy!
 */
//package org.geotools.demo;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import ec.gov.senplades.planificador.MenuItemP;
import ec.gov.senplades.planificador.fPuntual;

/**
 * In this example we create a map tool to select a feature clicked
 * with the mouse. The selected feature will be painted yellow.
 *
 * @source $URL$
 */
public class SelectionLab {

    /*
     * Factories that we will use to create style and filter objects
     */
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

	private	JPanel	topPanel;
    /*
     * Convenient constants for the type of feature geometry in the shapefile
     */
    private enum GeomType { POINT, LINE, POLYGON };

    /*
     * Some default style variables
     */
    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN;
    private static final Color SELECTED_COLOUR = Color.YELLOW;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;

    private JMapFrame mapFrame;
    private FeatureSource<SimpleFeatureType, SimpleFeature> featureSource,featureSource2;
        
    private String geometryAttributeName;
    private GeomType geometryType;
    
    /*
     * The application method
     */
    public static void main(String[] args) throws Exception {
        SelectionLab me = new SelectionLab();

        /*File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        } */

        File file = new File("c:/java/lip2/areas_censales_rurales.shp");
        me.displayShapefile(file);
        
        
    }
// docs end main

    

    
       
// docs start display shapefile
    /**
     * This method connects to the shapefile; retrieves information about
     * its features; creates a map frame to display the shapefile and adds
     * a custom feature selection tool to the toolbar of the map frame.
     */
    public void displayShapefile(File file) throws Exception {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();

        System.out.println("Poligonos");
        System.out.println(featureSource.getSchema()); 
        
        setGeometry();
        
        File file2 = new File("c:/java/lip2/centroides_total.shp");
        FileDataStore storeCentros = FileDataStoreFinder.getDataStore(file2);
        featureSource2 = storeCentros.getFeatureSource();
        System.out.println("Centros");
        System.out.println(featureSource2.getSchema()); 

        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);        
        Filter filter2 = ff.like(ff.property( "codigo"),  "090150999033" ); 
       
        Expression expr = ff.property("codigo");
        Object value = expr.evaluate( featureSource ); // evaluate
        String name; 
        if( value instanceof String){
            name = (String) value;
        }
        else {
            name = "(invalid name)";
        }
        System.out.println(name);
        
        if( filter2.evaluate( featureSource ) ){
            // the feature was "selected" by the filter
            System.out.println( "Si Existe" );
        } else 
            System.out.println( "No Existe" );
        
        //setGeometry2();        
        
        /*
         * Create the JMapFrame and set it to display the shapefile's features
         * with a default line and colour style
         */
        MapContext map = new DefaultMapContext();
        map.setTitle("Feature selection tool example");
        Style style = createDefaultStyle();
        
        map.addLayer(featureSource, style);
        map.addLayer(featureSource2, null);

        mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

        /*
         * Before making the map frame visible we add a new button to its
         * toolbar for our custom feature selection tool
         */
        JToolBar toolBar = mapFrame.getToolBar();
        JButton btn = new JButton("Select");
        toolBar.addSeparator();
        toolBar.add(btn);

        /*
         * When the user clicks the button we want to enable
         * our custom feature selection tool. Since the only
         * mouse action we are interested in is 'clicked', and
         * we are not creating control icons or cursors here,
         * we can just create our tool as an anonymous sub-class
         * of CursorTool.
         */
        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {

                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                selectFeatures(ev);
                            }
                        });
            }
        });

        /**
         * Finally, we display the map frame. When it is closed
         * this application will exit.
         */
        
		
        Menu(mapFrame);
    }
// docs end display shapefile

	void Menu(JMapFrame oForma) {
 
		oForma.setTitle("Planificador Multisectorial");

		//topPanel = new JPanel();
		
		//topPanel.setLayout( new BorderLayout() );
		//oForma.getContentPane().add( topPanel );
		
		JMenuBar mb = oForma.getJMenuBar();
		JMenu m, m2;
		MenuItemP oItem; 
		mb = new JMenuBar();
		oForma.setJMenuBar(mb) ;
		mb.add(m = new JMenu("Archivo"));  
		m.add(oItem = new MenuItemP("Abrir", "Abrir")); //oItem.addActionListener((ActionListener) oForma); 
		
		m.add(oItem = new MenuItemP("Guardar","Guardar")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Guardar Como", "GuardarComo")); //oItem.addActionListener(this);
		m.add(m2 = new JMenu("Exportar")); 
		m2.add(oItem = new MenuItemP("Excel", "Excel")); //oItem.addActionListener(this);
		m2.add(oItem = new MenuItemP("DBF Zip","DBFZip")); //oItem.addActionListener(this);
		m2.add(oItem = new MenuItemP("Mapas", "Mapas")); //oItem.addActionListener(this);		
		
		m.add(oItem = new MenuItemP("Imprimir","Imprimir")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Consolidar","Consolidar")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Comparar","Comparar")); //oItem.addActionListener(this);
		
		mb.add(m = new JMenu("Ver Mapas"));

		m.add(oItem = new MenuItemP("Infraestructural Actual","InfraestructuralActual")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Zonas de Riesgo","ZonasRiesgo")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Tiempos de Desplazamiento", "TiemposDesplazamiento")); //oItem.addActionListener(this);
		m.addSeparator(); 
		m.add(oItem = new MenuItemP("Costos de Construcci�n Vias Primer Orden","CostosConstruccionVias1")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Costos de Construcci�n Vias Segundo Orden","CostosConstruccionVias2")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Costos de Construcci�n Vias Tercer Orden","CostosConstruccionVias3")); //oItem.addActionListener(this);
		m.addSeparator();
		m.add(oItem = new MenuItemP("Indicadores Sociales", "IndicadoresSociales")); //oItem.addActionListener(this);

		mb.add(m = new JMenu("Ingreso de datos"));
		m.add(oItem = new MenuItemP("InfraestructuralActual", "IngresoInfraestructuralActual")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Indicadores", "Indicadores")); //oItem.addActionListener(this);
		m.addSeparator();
		m.add(oItem = new MenuItemP("Productos","Productos")); //oItem.addActionListener(this);			
		m.add(oItem = new MenuItemP("Insumos", "Insumos")); //oItem.addActionListener(this);		
		
		mb.add(m = new JMenu("Localizar"));
		m.add(oItem = new MenuItemP("Fijar Territorio","FijarTerritorio")); //oItem.addActionListener(this);
		m.addSeparator();
		m.add(oItem = new MenuItemP("P-medianas sin limite","PMedianasSinLimite")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-medianas con limite","PMedianasSinLimite")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-centros sin limite","PCentrosSinLimite")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("P-centros con limite","PCentrosConLimite")); //oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("M�xima Cobertura","MaximaCobertura")); //oItem.addActionListener(this);

		mb.add(m = new JMenu("Herramientas")); 
		m.add(oItem = new MenuItemP("Calculos de Impactos","CalculosImpactos")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Opciones","Opciones" )); //oItem.addActionListener(this);		

		mb.add(m = new JMenu("Reportes"));
		m.add(oItem = new MenuItemP("Tiempos de Accesos","TiemposAccesos")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Tama�os de los Establecimientos", "Tama�osEstablecimientos")); //oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Cobertura","Cobertura")); //oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Presupuesto de Inversi�n","PresupuestoInversi�n" )); //oItem.addActionListener(this);		
		m.add(oItem = new MenuItemP("Impactos","Impactos")); //oItem.addActionListener(this);
		m.add(oItem = new MenuItemP("Analisis Financiero", "AnalisisFinanciero")); //oItem.addActionListener(this);		
		
		//JPanel p = new JPanel();
		//p.add(new JTextArea());
		//oForma.add(p);
		//oForma.setVisible(true);
		//oForma.oPlanificador = new Planificador(this); 

		oForma.setSize(1280,970) ;
        oForma.setVisible(true);

	}

    
    
    
// docs start select features
    /**
     * This method is called by our feature selection tool when
     * the user has clicked on the map.
     *
     * @param pos map (world) coordinates of the mouse cursor
     */
    void selectFeatures(MapMouseEvent ev) {

        System.out.println("Mouse click at: " + ev.getMapPosition());

        /*
         * Construct a 5x5 pixel rectangle centred on the mouse click position
         */
        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);

        /*
         * Transform the screen rectangle into bounding box in the coordinate
         * reference system of our map context. Note: we are using a naive method
         * here but GeoTools also offers other, more accurate methods.
         */
        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect,
                mapFrame.getMapContext().getCoordinateReferenceSystem());

        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
        
        System.out.println(ff.literal(bbox)); 
        System.out.println(ff.property(geometryAttributeName));
        
        Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));


                //ff.bbox(ff.property(geometryAttributeName), bbox);

        /*
         * Use the filter to identify the selected features
         */
        try {
            FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures =
                    featureSource.getFeatures(filter);

            FeatureIterator<SimpleFeature> iter = selectedFeatures.features();
            Set<FeatureId> IDs = new HashSet<FeatureId>();
            try {
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    IDs.add(feature.getIdentifier());

                    System.out.println("   " + feature.getIdentifier());
                }

            } finally {
                iter.close();
            }

            if (IDs.isEmpty()) {
                System.out.println("   no feature selected");
            }

            displaySelectedFeatures(IDs);
            double xx = ev.getMapPosition().x; 
            double yy = ev.getMapPosition().y;
            //new fPuntual(null, xx,  yy); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
// docs end select features

// docs start display selected
    /**
     * Sets the display to paint selected features yellow and
     * unselected features in the default style.
     *
     * @param IDs identifiers of currently selected features
     */
    public void displaySelectedFeatures(Set<FeatureId> IDs) {
        Style style;

        if (IDs.isEmpty()) {
            style = createDefaultStyle();

        } else {
            style = createSelectedStyle(IDs);
        }

        mapFrame.getMapContext().getLayer(0).setStyle(style);
        mapFrame.getMapPane().repaint();
    }
// docs end display selected

// docs start default style
    /**
     * Create a default Style for feature display
     */
    private Style createDefaultStyle() {
        Rule rule = createRule(LINE_COLOUR, FILL_COLOUR);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
// docs end default style

// docs start selected style
    /**
     * Create a Style where features with given IDs are painted
     * yellow, while others are painted with the default colors.
     */
    private Style createSelectedStyle(Set<FeatureId> IDs) {
        Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(LINE_COLOUR, FILL_COLOUR);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
// docs end selected style

// docs start create rule
    /**
     * Helper for createXXXStyle methods. Creates a new Rule containing
     * a Symbolizer tailored to the geometry type of the features that
     * we are displaying.
     */
    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (geometryType) {
            case POLYGON:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;

            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;

            case POINT:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

                Mark mark = sf.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = sf.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(ff.literal(POINT_SIZE));

                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }
// docs end create rule

// docs start set geometry
    /**
     * Retrieve information about the feature geometry
     */
    private void setGeometry() {
        GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
        geometryAttributeName = geomDesc.getLocalName();

        Class<?> clazz = geomDesc.getType().getBinding();

        if (Polygon.class.isAssignableFrom(clazz) ||
                MultiPolygon.class.isAssignableFrom(clazz)) {
            geometryType = GeomType.POLYGON;

        } else if (LineString.class.isAssignableFrom(clazz) ||
                MultiLineString.class.isAssignableFrom(clazz)) {

            geometryType = GeomType.LINE;

        } else {
            geometryType = GeomType.POINT;
        }

    }
    
    // docs end set geometry

}

