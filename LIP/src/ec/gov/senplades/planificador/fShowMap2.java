package ec.gov.senplades.planificador;
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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.style.ContrastMethod;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;


public class fShowMap2 {

	
    public StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    public FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN;
    private static final Color SELECTED_COLOUR = Color.CYAN;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 5.0f;
    private enum GeomType { POINT, LINE, POLYGON };    
    public FeatureSource<SimpleFeatureType, SimpleFeature> featureSource,featureSource2;

    public JMenuMap mapFrame;    
    
    private AbstractGridCoverage2DReader  reader; 
    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile
     * and displays its contents on the screen in a map frame
     */    
    public static void main(String[] args) throws Exception {
        
        new fShowMap2();
    
    }
   
    
    public fShowMap2() throws Exception {

        MapContext map = new DefaultMapContext();
		map.setCoordinateReferenceSystem(CRS.decode("EPSG:32717"));
        
        File rasterFile = new File("c:/java/lip2/raster/mosai_tiempo.tif");
        AbstractGridFormat format = GridFormatFinder.findFormat( rasterFile );   
        
        System.out.println("Formato raster"); 
        System.out.println(format);        
        reader = format.getReader(rasterFile);

        System.out.println("reader"); 
        System.out.println(reader);        
        
        //Style rasterStyle; 
        //rasterStyle = createGreyscaleStyle(1);
        //rasterStyle = createGreyscaleStyle();
        //rasterStyle = createRGBStyle(); 
        //map.addLayer(reader, rasterStyle); */
    	
        File file = new File("c:/java/lip2/areas_censales_rurales.shp");    	
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();
              
        File file2 = new File("c:/java/lip2/centroides_total.shp");
        FileDataStore storeCentros = FileDataStoreFinder.getDataStore(file2);
        featureSource2 = storeCentros.getFeatureSource();
        
        map.setTitle("Feature selection tool example");
        //Style style1 = createDefaultStyle();

        Style style1 = createDefaultStyle(featureSource, Color.BLUE, Color.yellow); 
        map.addLayer(featureSource, style1);
        Style style2 = createDefaultStyle(featureSource2, Color.BLACK, Color.black);        
        map.addLayer(featureSource2, style2);

        //Style styleLinea = SLD.createLineStyle(Color.red, 1); 
        //Style styleLinea = SLD.createPolygonStyle(Color.BLACK, Color.RED, (float) 0.5); 
        //createLineStyle(Color.red, 1);
        //map.addLayer(GetLineas(), styleLinea); 
        
        mapFrame = new JMenuMap(map);
        //mapFrame.enableToolBar(true);
        //mapFrame.enableStatusBar(true);

		mapFrame.setTitle("Planificador Multisectorial");
        mapFrame.setSize(1280,970) ;
        
        JToolBar toolBar = mapFrame.getToolBar();
        JButton cmdPuntual = new JButton("Puntual");
        toolBar.addSeparator();
        toolBar.add(cmdPuntual);

        cmdPuntual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                     new CursorTool() {
                     public void onMouseClicked(MapMouseEvent ev) {
                         Puntual(ev);
                         }
                     } 
                ) ;
            }
        }      );

        
        mapFrame.setVisible(true);
    }

    
    void Puntual(MapMouseEvent ev) {

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

        GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
        String geometryAttributeName = geomDesc.getLocalName();
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
            //new fPuntual(this, xx,  yy); 

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    	
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
    
    private Style createDefaultStyle() {
        Rule rule = createRule(featureSource, LINE_COLOUR, FILL_COLOUR);

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
        Rule selectedRule = createRule(featureSource,SELECTED_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(featureSource,LINE_COLOUR, FILL_COLOUR);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }


    
    private FeatureCollection<SimpleFeatureType, SimpleFeature> GetLineas() throws Exception{
    	
 	   //SimpleFeatureType lineType =  DataUtilities.createType("LINE", "centerline:LineString,name:\"\",id:0");
 	   SimpleFeatureType polyType =  DataUtilities.createType("POLYGON", "the_geom:MultiPolygon:srid=32717,DPA_ZONDIS:String,codigo:String"); 
       System.out.println("Coordenadas del poligono") ;
 	   System.out.println(polyType.getCoordinateReferenceSystem()) ; 
 	   
 	   Coordinate[] aPuntos  =  new Coordinate[4];  

	   aPuntos[0] = new Coordinate(-1.20, -70); 
	   aPuntos[1] = new Coordinate(-3, -79); 
	   aPuntos[2] = new Coordinate( 0, -80);  
	   aPuntos[3] = new Coordinate(-1.20, -70);		
	   
	   GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
	   //LineString linea = geometryFactory.createLineString(aPuntos);
	   LinearRing linea = geometryFactory.createLinearRing(aPuntos); 

	   Polygon oPoligono = geometryFactory.createPolygon(linea, null); 
	   
   
	   // Sistema de coordenadas mundial, latitud y longitud	   
	   CoordinateReferenceSystem geoWGS =  CRS.decode("EPSG:4326");
	
	   // Sistema de coordenadas para Ecuador
	   CoordinateReferenceSystem geoUTM = CRS.decode("EPSG:32717");
	   
	   MathTransform transform = CRS.findMathTransform(geoWGS, geoUTM);
 
       //Geometry targetGeometry =  JTS.transform( linea, transform);
       Geometry targetGeometry =  JTS.transform( oPoligono, transform);       
        	   
       //SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( lineType);
       SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( polyType); 
       
       featureBuilder.add(targetGeometry);
       featureBuilder.add("0105012");
       featureBuilder.add("PEPO"); 
       
       SimpleFeature feature = featureBuilder.buildFeature(null);
       FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
       collection.add(feature);

     return collection; 
    }
    
    private Style createDefaultStyle(FeatureSource<SimpleFeatureType, SimpleFeature> feature, Color outlineColor, Color fillColor) {
        Rule rule = createRule(feature, outlineColor, fillColor);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    private Rule createRule(FeatureSource<SimpleFeatureType, SimpleFeature> feature,Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (GetGeometryType(feature)) {
            case POLYGON:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, GetGeometryAttributeName(feature));
                break;

            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, GetGeometryAttributeName(feature));
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

                symbolizer = sf.createPointSymbolizer(graphic, GetGeometryAttributeName(feature));
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private GeomType GetGeometryType(FeatureSource<SimpleFeatureType, SimpleFeature> feature) {
        GeometryDescriptor geomDesc = feature.getSchema().getGeometryDescriptor();

        Class<?> clazz = geomDesc.getType().getBinding();

        if (Polygon.class.isAssignableFrom(clazz) ||
                MultiPolygon.class.isAssignableFrom(clazz)) {
            return( GeomType.POLYGON);

        } else if (LineString.class.isAssignableFrom(clazz) ||
                MultiLineString.class.isAssignableFrom(clazz)) {
             return( GeomType.LINE);

        } else {
            return (GeomType.POINT);
        }

    }

    private String GetGeometryAttributeName(FeatureSource<SimpleFeatureType, SimpleFeature> feature) {
        GeometryDescriptor geomDesc = feature.getSchema().getGeometryDescriptor();
        return geomDesc.getLocalName();

    }
    
    private Style createGreyscaleStyle(int band) {
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(band), ce);

        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }

    private Style createGreyscaleStyle() {
        GridCoverage2D cov = null;
        try {
            cov = reader.read(null);
        } catch (IOException giveUp) {
            throw new RuntimeException(giveUp);
        }
        int numBands = cov.getNumSampleDimensions();
        Integer[] bandNumbers = new Integer[numBands];
        for (int i = 0; i < numBands; i++) { bandNumbers[i] = i+1; }
        Object selection = JOptionPane.showInputDialog(
                mapFrame,
                "Band to use for greyscale display",
                "Select an image band",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bandNumbers,
                1);
        if (selection != null) {
            int band = ((Number)selection).intValue();
            return createGreyscaleStyle(band);
        }
        return null;
    }
    
    private Style createRGBStyle() {
        GridCoverage2D cov = null;
        try {
            cov = reader.read(null);
        } catch (IOException giveUp) {
            throw new RuntimeException(giveUp);
        }
        // We need at least three bands to create an RGB style
        int numBands = cov.getNumSampleDimensions();
        if (numBands < 3) {
            return null;
        }
        // Get the names of the bands
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++) {
            GridSampleDimension dim = cov.getSampleDimension(i);
            sampleDimensionNames[i] = dim.getDescription().toString();
        }
        final int RED = 0, GREEN = 1, BLUE = 2;
        int[] channelNum = { -1, -1, -1 };
        // We examine the band names looking for "red...", "green...", "blue...".
        // Note that the channel numbers we record are indexed from 1, not 0.
        for (int i = 0; i < numBands; i++) {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null) {
                if (name.matches("red.*")) {
                    channelNum[RED] = i + 1;
                } else if (name.matches("green.*")) {
                    channelNum[GREEN] = i + 1;
                } else if (name.matches("blue.*")) {
                    channelNum[BLUE] = i + 1;
                }
            }
        }
        // If we didn't find named bands "red...", "green...", "blue..."
        // we fall back to using the first three bands in order
        if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
            channelNum[RED] = 1;
            channelNum[GREEN] = 2;
            channelNum[BLUE] = 3;
        }
        // Now we create a RasterSymbolizer using the selected channels
        SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        for (int i = 0; i < 3; i++) {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }
    
}
