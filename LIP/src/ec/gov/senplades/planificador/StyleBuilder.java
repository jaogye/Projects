package ec.gov.senplades.planificador;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

public class StyleBuilder {

    private ArrayList<Set<FeatureId>> IDss; 
    private ArrayList<Color> colors;
    private ArrayList<Float> opacities ; 
    private enum GeomType { POINT, LINE, POLYGON };
    private FilterFactory2 ff ;
    private StyleFactory sf ;
    private GeomType geometryType;  
    private String geometryAttributeName;  
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;
    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN; 
    
    public StyleBuilder(GeomType geometryType, String geometryAttributeName, FilterFactory2 ff,StyleFactory sf) {
    	this.ff = ff; 
    	this.sf = sf ; 
    	this.geometryAttributeName = geometryAttributeName; 
    	this.geometryType=geometryType; 
    	
    	ArrayList<Set<FeatureId>> Idss = new ArrayList<Set<FeatureId>> ();
  
    }
    
    public void AddSelectedStyle(Set<FeatureId> IDs, Color Selected_Color, Float Opacity) { 
    
    	IDss.add(IDs);
    	colors.add(Selected_Color);
    	opacities.add(Opacity); 
    }
    
    
    private Rule createRule( Color outlineColor, Color fillColor, Float Opacity) {
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


 
    

}


