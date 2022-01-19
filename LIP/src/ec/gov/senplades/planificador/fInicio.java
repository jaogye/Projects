package ec.gov.senplades.planificador;


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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.InfoToolHelper;
import org.geotools.swing.tool.VectorLayerHelper;
import org.geotools.swing.utils.MapLayerUtils;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import ec.gov.senplades.dataBase.DataBase;


/**
 * In this example we create a map tool to select a feature clicked
 * with the mouse. The selected feature will be painted yellow.
 *
 * @source $URL$
 */
public class fInicio {

    /*
     * Factories that we will use to create style and filter objects
     */
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

    /*
     * Convenient constants for the type of feature geometry in the shapefile
     */

    private static final int nCapas =3;    
    private static final int RURAL =0; 
    private static final int URBANO =1;
    private static final int CENTROIDE =2;    
    private Capa [] capas;
    
    private JMapFrame mapFrame;
    public  DataBase DB; 
    
	SimpleFeatureType [] IntervencionType ;
	SimpleFeature featureActual; 
	ArrayList<Point2D> puntos; 

    private static final int PUNTUAL =0; 
    private static final int LINEAL =1;
    private static final int POLIGONAL =2;    
    private static final int ACTIVIDAD =3;    
    
    public int claseIntervencion;
    LineString lineaAbierta;
    public int nPuntual=0; 
    public int nLineal=0;
    public int nPoligonal=0;
    public int nActividad=0;    
    /*
     * The application method
     */
    
    public fInicio() throws IOException {
    	DB = new DataBase(); 
    	
        this.CargaTiposIntervenciones();
        File [] archivos = new File[nCapas]; 
        
       /* archivos[0] = new File("c:/java/lip2/areas_censales_rurales.shp") ; 
        archivos[1] = new File("c:/java/lip2/areas_censales_urbanas.shp") ;
        archivos[2] = new File("c:/java/lip2/centroides_total.shp") ;*/

        archivos[0] = new File("mapas/areas_censales_rurales.shp") ; 
        archivos[1] = new File("mapas/areas_censales_urbanas.shp") ;
        archivos[2] = new File("mapas/centroides_total.shp") ;
        
        capas = new Capa [nCapas];
        capas[0] = new Capa(archivos[0], ff, sf) ;        	
        capas[1] = new Capa(archivos[1], SLD.createPolygonStyle(Color.BLACK, Color.RED, (float) 1) , ff, sf) ;
        capas[2] = new Capa(archivos[2], null, ff, sf) ;
        puntos = new ArrayList<Point2D>  ();  

        
    }
    
    public void CargaTiposIntervenciones() {

    	IntervencionType = new SimpleFeatureType [8]; 
    	// Cargo los tipo de intervenciones como tipos de elementos geograficos 
        int nCol =  this.DB.TipoIntervencion.FindColumna("Nombre");
        int nCol2 =  this.DB.TipoIntervencion.FindColumna("Clase");        
        for (int i=0; i < 8 ; i++) {
        	String sTipo = (String) this.DB.TipoIntervencion.Datos[i][nCol];
        	Integer nClase = (Integer) this.DB.TipoIntervencion.Datos[i][nCol2];        	
        	ArrayList<Object []> oTipos =  this.DB.Parametro.SelectPorFiltro(
          		   new String [] {"Nombre", "TipoDato"} , new Object [][] {{"TipoIntervencion_id", i}} );
        	
        	String sDef = null; 
        	if (nClase == PUNTUAL)
        	   sDef = "the_geom:MultiPolygon:srid=32717";
        	if (nClase == LINEAL)
         	   sDef = "the_geom:LineString:srid=32717"; 
        	if (nClase == POLIGONAL)
         	   sDef = "the_geom:MultiPolygon:srid=32717"; 

        		
        	for (Object [] oReg : oTipos) {
        		   sDef = sDef + "," + (String) oReg[0] + ":" + (String) oReg[1]; 
        	    }
        	   try {
				IntervencionType[i] = DataUtilities.createType(sTipo, sDef);
			} catch (SchemaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
        }

    }
    
    public static void main(String[] args) throws Exception {
        fInicio me = new fInicio();
        me.displayMapa();
    }

    
    public void displayMapa() throws Exception {
               
        /*
         * Create the JMapFrame and set it to display the shapefile's features
         * with a default line and colour style
         */
        MapContext map = new DefaultMapContext();

        for (int i=0; i < nCapas ; i++) {
            map.addLayer(capas[i].featureSource, capas[i].style);       	
        } 
        
        /*Iterator readers = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = (ImageReader)readers.next();
        Object source = new File("imagenees/mosai_tiempo2.jpg"); 
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);*/
        
        mapFrame = new JMenuMap(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

        /*
         * Before making the map frame visible we add a new button to its
         * toolbar for our custom feature selection tool
         */
        JToolBar toolBar = mapFrame.getToolBar();
        JButton cmdPuntual = new JButton("Puntual");
        toolBar.addSeparator();
        toolBar.add(cmdPuntual);


        cmdPuntual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                Puntual(ev);
                            }
                        });
            }
        });

        JButton cmdLineal = new JButton("Lineal");
        toolBar.add(cmdLineal);
        cmdLineal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                Lineal(ev);
                            }
                        });
            }
        });

        JButton cmdPoligonal = new JButton("Poligonal");
        toolBar.add(cmdPoligonal);
        cmdPoligonal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                Poligonal(ev);
                            }
                        });
            }
        });

        JButton cmdActividad = new JButton("Actividad");
        toolBar.add(cmdActividad);
        cmdActividad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                Actividad(ev);
                            }
                        });
            }
        });
        
        JButton cmdDistancia = new JButton("Distancia");
        toolBar.add(cmdDistancia);
        cmdDistancia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                Distancia(ev);
                            }
                        });
            }
        });

        JButton cmdCaminoCorto = new JButton("Camino mas corto");
        toolBar.add(cmdCaminoCorto);
        cmdCaminoCorto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                                RutaCorta(ev);
                            }
                        });
            }
        });
        
        
        // Cargo el menu        
        //Menu(mapFrame);
    }
// docs end display shapefile


   // Este metodo es para mostrar la ventana con los parametros de las intervenciones 
    void Puntual(MapMouseEvent ev) {

    	claseIntervencion = PUNTUAL; 
    	
    	String sCodigo =""; 
    	sCodigo=(String) this.getPropiedad(ev.getMapPosition(),0, "codigo" ); 
    	if (sCodigo.isEmpty()) 
    		sCodigo=(String)  this.getPropiedad(ev.getMapPosition(),1, "codigo" );  
    		
        System.out.println("Mouse click at: " + ev.getMapPosition());
        /*
         * Construct a 5x5 pixel rectangle centred on the mouse click position
         */
        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);
        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect, mapFrame.getMapContext().getCoordinateReferenceSystem());

        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
                
        //Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
        Filter filter = ff.intersects(ff.property(capas[RURAL].geometryAttributeName), ff.literal(bbox));
                //ff.bbox(ff.property(geometryAttributeName), bbox);

        /*
         * Use the filter to identify the selected features
         */
        try {
            FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures =
                    capas[RURAL].featureSource.getFeatures(filter);

            FeatureIterator<SimpleFeature> iter = selectedFeatures.features();
            System.out.println(selectedFeatures.getSchema()); 
          
            Set<FeatureId> IDsPuntual= new HashSet<FeatureId>(); 


            try {
                while (iter.hasNext()) {
                    featureActual = iter.next();
                    
                   /* Object mm = feature.getDefaultGeometry();
                    Geometry oo = (Geometry) mm; 
                    System.out.println("Geometria");
                    System.out.println(mm);
                    System.out.println(oo); */                    
                    IDsPuntual.add(featureActual.getIdentifier());

                }

            } finally {
                iter.close();
            }

            if (IDsPuntual.isEmpty()) {
                System.out.println("   no feature selected");
            }

            capas[RURAL].AddSelectedStyle(IDsPuntual,Color.YELLOW, (float) 1); 
            // display los poligonos seleccionados 
            Style style = capas[RURAL].createSelectedStyle();
            

            mapFrame.getMapContext().getLayer(0).setStyle(style);
            mapFrame.getMapPane().repaint();
            
            Point punto = ev.getPoint();
            Point2D puntoMundo = screenToWorld.transform(punto, null);        
            new fPuntual(this, puntoMundo, sCodigo); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
    
    
    Object getPropiedad(DirectPosition2D pos, int nLayer, String sName) {
    		
    	final double DEFAULT_DISTANCE_FRACTION = 0.01d;
    	WeakHashMap<MapLayer, InfoToolHelper> helperTable = new WeakHashMap<MapLayer, InfoToolHelper>();
        MapContext context = mapFrame.getMapPane().getMapContext();
        MapLayer layer = context.getLayer(nLayer);
        
            if (layer.isSelected()) {
                InfoToolHelper helper = null;

                String layerName = layer.getTitle();
                if (layerName == null || layerName.length() == 0) {
                    layerName = layer.getFeatureSource().getName().getLocalPart();
                }
                if (layerName == null || layerName.length() == 0) {
                    layerName = layer.getFeatureSource().getSchema().getName().getLocalPart();
                }

                helper = helperTable.get(layer);
                if (helper == null) {
                    if (MapLayerUtils.isGridLayer(layer)) {
                        try {
                            Class<?> clazz = Class.forName("org.geotools.swing.tool.GridLayerHelper");
                            Constructor<?> ctor = clazz.getConstructor(MapContext.class, MapLayer.class);
                            helper = (InfoToolHelper) ctor.newInstance(context, layer);
                            helperTable.put(layer, helper);

                        } catch (Exception ex) {
                            throw new IllegalStateException("Failed to create InfoToolHelper for grid layer", ex);
                        }

                    } else {
                        try {
                            Class<?> clazz = Class.forName("org.geotools.swing.tool.VectorLayerHelper");
                            Constructor<?> ctor = clazz.getConstructor(MapContext.class, MapLayer.class);
                            helper = (InfoToolHelper) ctor.newInstance(context, layer);
                            helperTable.put(layer, helper);

                        } catch (Exception ex) {
                            throw new IllegalStateException("Failed to create InfoToolHelper for vector layer", ex);
                        }
                    }
                }

                Object info = null;
                String sValor = ""; 
                if (helper instanceof VectorLayerHelper) {
                    ReferencedEnvelope mapEnv = mapFrame.getMapPane().getDisplayArea();
                    double searchWidth = DEFAULT_DISTANCE_FRACTION * (mapEnv.getWidth() + mapEnv.getHeight()) / 2;
                    try {
                        info = helper.getInfo(pos, Double.valueOf(searchWidth));
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }

                    if (info != null) {
                        FeatureIterator<? extends Feature> iter = null;
                        FeatureCollection selectedFeatures = (FeatureCollection) info;
                        try {
                            iter = selectedFeatures.features();
                            while (iter.hasNext()) {
                                //report(layerName, iter.next());
                            	sValor = getPropiedad(iter.next(), sName).toString();  
                                if ( !sValor.isEmpty() )
                                	return sValor;                               	
                            }

                        } catch (Exception ex) {
                            throw new IllegalStateException(ex);

                        } finally {
                            if (iter != null) {
                                iter.close();
                            }
                        }
                    }

                } else {
                    try {
                        info = helper.getInfo(pos);
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }

                    if (info != null) {
                        List<Number> bandValues = (List<Number>) info;
                        if (!bandValues.isEmpty()) {
                            //report(layerName, bandValues);
                            System.out.println(bandValues );  
                        }
                    }
                }
            }
        
    	return ""; 
    }
    
    
    Object getPropiedad(Feature feature, String sName) {
    	
        Collection<Property> props = feature.getProperties();

        for (Property prop : props) {
            String name = prop.getName().getLocalPart();
            Object value = prop.getValue();
            if ( sName.equals(name) ) {
            	return value; 
            }
        }

       return ""; 
    	
    }
    void Lineal(MapMouseEvent ev)  {
    	claseIntervencion = LINEAL;

        Point punto = ev.getPoint();    	

        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Point2D puntoMundo = screenToWorld.transform(punto, null);

    	if (puntos.size()==1) {
    		Point2D puntoInicio = puntos.get(0); 
    	 	Coordinate[] aPuntos  =  new Coordinate[2];  
    		aPuntos[0] = new Coordinate(puntoInicio.getX(), puntoInicio.getY()); 
    		aPuntos[1] = new Coordinate(puntoMundo.getX(), puntoMundo.getY());
    		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
    		LineString linea = geometryFactory.createLineString(aPuntos); 

    		SimpleFeatureType lineType = null;
			try {
				lineType = DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0");
			} catch (SchemaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( lineType); 
    	       
    	    featureBuilder.add(linea);
    	    SimpleFeature feature = featureBuilder.buildFeature(null);
    	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
    	    collection.add(feature);

            Style styleLinea = SLD.createLineStyle(Color.ORANGE, 2); 
            mapFrame.getMapContext().addLayer(collection, styleLinea);     	    
            mapFrame.repaint(); 
            puntos.add(puntoMundo);
            new fLineal(this, puntos ); 
            puntos.clear();            
    		
    	}
    	else
    	{
            puntos.add(puntoMundo); 
    	}	
    }
    
    void Distancia(MapMouseEvent ev)  {
    	claseIntervencion = LINEAL;

        Point punto = ev.getPoint();    	

        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Point2D puntoMundo = screenToWorld.transform(punto, null);

    	if (puntos.size()==1) {
    		Point2D puntoInicio = puntos.get(0); 
    	 	Coordinate[] aPuntos  =  new Coordinate[2];  
    		aPuntos[0] = new Coordinate(puntoInicio.getX(), puntoInicio.getY()); 
    		aPuntos[1] = new Coordinate(puntoMundo.getX(), puntoMundo.getY());
    		
    		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
    		LineString linea = geometryFactory.createLineString(aPuntos); 

    		SimpleFeatureType lineType = null;
			try {
				lineType = DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0");
			} catch (SchemaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( lineType); 
    	       
    	    featureBuilder.add(linea);
    	    SimpleFeature feature = featureBuilder.buildFeature(null);
    	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
    	    collection.add(feature);

            Style styleLinea = SLD.createLineStyle(Color.ORANGE, 2); 
            mapFrame.getMapContext().addLayer(collection, styleLinea);     	    
            mapFrame.repaint(); 
            puntos.add(puntoMundo);
            new fDistancia(this, puntos ); 
            puntos.clear();            
    		
    	}
    	else
    	{
            puntos.add(puntoMundo); 
    	}
    	    	
    }
    

    void RutaCorta(MapMouseEvent ev)  {

        Point punto = ev.getPoint();    	

        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Point2D puntoMundo = screenToWorld.transform(punto, null);

    	if (puntos.size()==1) {
    		Point2D puntoInicio = puntos.get(0);
    		   			
    	 	Coordinate[] aPuntos  =  new Coordinate[12];    		
            aPuntos[0]  = new Coordinate(puntoInicio.getX(), puntoInicio.getY()); 
            aPuntos[11] = new Coordinate(puntoMundo.getX(), puntoMundo.getY());
            
    		double longitud = Math.sqrt((aPuntos[0].x-aPuntos[11].x)*(aPuntos[0].x-aPuntos[11].x)+ (aPuntos[0].y-aPuntos[11].y)*(aPuntos[0].y-aPuntos[11].y))/5;
    		double dx = aPuntos[11].x - aPuntos[0].x; 
    		double dy = aPuntos[11].y - aPuntos[0].y;    		
    		
    		double xx, yy; 
    		for (int i=1; i < 11; i++) {
                 xx = aPuntos[i-1].x + dx / 10 + longitud*(Math.random() - 0.5 );           			
                 yy = aPuntos[i-1].y + dy / 10 + longitud*(Math.random() - 0.5 );
                 aPuntos[i] = new Coordinate(xx,yy); 
    		}
    		
    		
    		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
    		LineString linea = geometryFactory.createLineString(aPuntos); 

    		SimpleFeatureType lineType = null;
			try {
				lineType = DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0");
			} catch (SchemaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( lineType); 
    	       
    	    featureBuilder.add(linea);
    	    SimpleFeature feature = featureBuilder.buildFeature(null);
    	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
    	    collection.add(feature);

            Style styleLinea = SLD.createLineStyle(Color.PINK, 2); 
            mapFrame.getMapContext().addLayer(collection, styleLinea);     	    
            mapFrame.repaint(); 
            puntos.clear(); 
    		
    	}
    	else
    	{
            puntos.add(puntoMundo); 
    	}
    	
    	
    	
    }

    
    
    void Poligonal(MapMouseEvent ev)  {
    	
    	claseIntervencion = POLIGONAL;

        Point punto = ev.getPoint();    	

        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();
        Point2D punto2 = screenToWorld.transform(punto, null);

    	if (puntos.size()> 0) {

        	Coordinate[] aPuntos  =  new Coordinate[puntos.size()+1];
        	int i=0;
        	for (Point2D oo: puntos  ){
            	aPuntos[i] = new Coordinate(oo.getX(), oo.getY());
            	i++; 
        	}
        	 	
        	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );  		    		
            Rectangle screenRect = new Rectangle(punto.x-2, punto.y-2, 6, 6);
            Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();

            SimpleFeature feature; 
            
            
            if (worldRect.contains(puntos.get(0))) {
            	// Completo el poligono
            	aPuntos[i] = new Coordinate(puntos.get(0).getX(), puntos.get(0).getY());

                SimpleFeatureType polyType = null;
    			try {
    				polyType = DataUtilities.createType("POLYGON", "the_geom:MultiPolygon:srid=32717");
    				} catch (SchemaException e) {
    					// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
        		LinearRing linea = geometryFactory.createLinearRing(aPuntos); 
        		Polygon oPoligono = geometryFactory.createPolygon(linea, null); 
        		SimpleFeatureBuilder poligonoBuilder = new SimpleFeatureBuilder( polyType); 
        	    poligonoBuilder.add(oPoligono);        	    
        	    feature = poligonoBuilder.buildFeature(null);

            	FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
            	collection.add(feature);

                Style styleLinea = SLD.createPolygonStyle(Color.BLACK, Color.MAGENTA, (float) 0.5);  
                mapFrame.getMapContext().addLayer(collection, styleLinea);     	    
                mapFrame.repaint(); 
        	     
                puntos.add(puntos.get(0));
                new fPoligonal(this, puntos );
                collection.remove( feature); 
                puntos.clear();    
                mapFrame.repaint();               
                

            }  else
            {

            	// Completo la linea
            	aPuntos[i] = new Coordinate(punto2.getX(), punto2.getY());

        		SimpleFeatureType lineType = null;
    			try {
    				lineType = DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0");
    			} catch (SchemaException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}

        		SimpleFeatureBuilder lineaBuilder = new SimpleFeatureBuilder( lineType);
        		LineString linea = geometryFactory.createLineString(aPuntos); 
        	    lineaBuilder.add(linea);
        	    feature = lineaBuilder.buildFeature(null);

            	FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
            	collection.add(feature);

                Style styleLinea = SLD.createLineStyle(Color.MAGENTA, 2); 
                mapFrame.getMapContext().addLayer(collection, styleLinea);     	    
                mapFrame.repaint(); 

                puntos.add(punto2);
            }

    	}
    	else
    	{
            puntos.add(punto2); 
    	}
    	
    	
    }
    
    void Actividad(MapMouseEvent ev) {
    	
    	claseIntervencion = ACTIVIDAD; 

    	String sCodigo =""; 
    	sCodigo=(String) this.getPropiedad(ev.getMapPosition(),0, "codigo" ); 
    	if (sCodigo.isEmpty()) 
    		sCodigo=(String)  this.getPropiedad(ev.getMapPosition(),1, "codigo" ); 
    	
        System.out.println("Mouse click at: " + ev.getMapPosition());
        /*
         * Construct a 5x5 pixel rectangle centred on the mouse click position
         */
        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);

        AffineTransform screenToWorld = mapFrame.getMapPane().getScreenToWorldTransform();

        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect, mapFrame.getMapContext().getCoordinateReferenceSystem());

        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
                
        //Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
        Filter filter = ff.intersects(ff.property(capas[RURAL].geometryAttributeName), ff.literal(bbox));
                //ff.bbox(ff.property(geometryAttributeName), bbox);

        /*
         * Use the filter to identify the selected features
         */
        try {
            FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures =
                    capas[RURAL].featureSource.getFeatures(filter);

            FeatureIterator<SimpleFeature> iter = selectedFeatures.features();
            System.out.println(selectedFeatures.getSchema()); 
            Set<FeatureId> IDsActividad = new HashSet<FeatureId>(); 
            
            try {
                while (iter.hasNext()) {
                    featureActual = iter.next();                                       
                    IDsActividad.add(featureActual.getIdentifier());
                }

            } finally {
                iter.close();
            }

            if (IDsActividad.isEmpty()) {
                System.out.println("   no feature selected");
            }

            capas[RURAL].AddSelectedStyle(IDsActividad,Color.GREEN, (float) 1); 
            // display los poligonos seleccionados 
            Style style = capas[RURAL].createSelectedStyle();

            
            mapFrame.getMapContext().getLayer(0).setStyle(style);
            mapFrame.getMapPane().repaint();


            Point punto = ev.getPoint();
            Point2D puntoMundo = screenToWorld.transform(punto, null);        
            new fActividad(this, puntoMundo, sCodigo); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

    }
    

    public void AgregaIntervencionPuntual(int nTipoIntervencion, ArrayList<Object []> parametros ) {
     	   
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( IntervencionType[nTipoIntervencion]); 
        
        // Agrego la feature con la geometria del poligono 
        featureBuilder.add(featureActual.getDefaultGeometry());
        
        int ii=0; 
        ii++; 
        for (Object[] oReg: parametros) {
        	featureBuilder.add((Integer) oReg[1]);
        }

        FeatureCollection<SimpleFeatureType, SimpleFeature> collectionPuntual = FeatureCollections.newCollection();        
        SimpleFeature feature = featureBuilder.buildFeature(null);
        collectionPuntual.add(feature);
        
        mapFrame.getMapContext().addLayer(collectionPuntual, SLD.createPolygonStyle(Color.BLACK, Color.GREEN, (float) 0.2) ); 

    }
    
    public void AgregaIntervencionLineal(int nTipoIntervencion, ArrayList<Object []> parametros ) {
    	
    }
    
    public void AgregaIntervencionPoligonal(int nTipoIntervencion, ArrayList<Object []> parametros ) {
    	
    }

    public void AgregaActividad(int nTipoIntervencion, ArrayList<Object []> parametros ) {
    	
    }

    
}
