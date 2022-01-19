package ec.gov.senplades.planificador;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class Capa {

    private ArrayList<Set<FeatureId>> IDss; 
    private ArrayList<Color> colors;
    private ArrayList<Float> opacities ; 
    
    private enum GeomType { POINT, LINE, POLYGON };
    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN;
    //private static final Color SELECTED_COLOUR = Color.YELLOW;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;
	
    public FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;  
    public String geometryAttributeName;
    public GeomType geometryType;
    Style style;    
    private FilterFactory2 ff ;
    private StyleFactory sf ;
    
    public Capa(File file, FilterFactory2 ff,StyleFactory sf ) throws IOException {
    	this.ff = ff; 
    	this.sf = sf ; 
    	FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();
        setGeometry();
        style = createDefaultStyle();

        IDss= new ArrayList<Set<FeatureId>>(); 
        colors= new ArrayList<Color>();
        opacities = new ArrayList<Float>(); 
        
    }

    public Capa(File file, Style style , FilterFactory2 ff,StyleFactory sf ) throws IOException {
    	this.ff = ff; 
    	this.sf = sf ; 
    	FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();
        setGeometry();
        this.style = style;

        IDss= new ArrayList<Set<FeatureId>>(); 
        colors= new ArrayList<Color>();
        opacities = new ArrayList<Float>(); 
    
    }
    
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
    
    private Style createDefaultStyle() {
        Rule rule = createRule(LINE_COLOUR, FILL_COLOUR);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    private Rule createRule(Color outlineColor, Color fillColor) { 
    	return createRule(outlineColor, fillColor, OPACITY); 
    }

    private Rule createRule(Color outlineColor, Color fillColor, Float Opacity) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (geometryType) {
            case POLYGON:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(Opacity));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;

            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;

            case POINT:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(Opacity));

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


    public void AddSelectedStyle(Set<FeatureId> IDs, Color Selected_Color, Float Opacity) { 
        
    	IDss.add(IDs);
    	colors.add(Selected_Color);
    	opacities.add(Opacity); 
    }

    public Style createSelectedStyle() {

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
    	int i=0; 
        for (Set<FeatureId> IDs: IDss) {
            Color ColorSel = colors.get(i); 
            Float opacidad = opacities.get(i);            
            Rule selectedRule = createRule(ColorSel, ColorSel, opacidad);
            selectedRule.setFilter(ff.id(IDs));
            fts.rules().add(selectedRule);            
        	i++; 
        }

        Rule otherRule = createRule(LINE_COLOUR, FILL_COLOUR, (float) 1);
        otherRule.setElseFilter(true);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    
    
}
