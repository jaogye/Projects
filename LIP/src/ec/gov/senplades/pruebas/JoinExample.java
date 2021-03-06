package ec.gov.senplades.pruebas;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class shows how to "join" two feature sources.
 * 
 * @author Jody
 *
 *
 * @source $URL: http://svn.osgeo.org/geotools/tags/2.7-M0/demo/example/src/main/java/org/geotools/demo/features/JoinExample.java $
 */
public class JoinExample {

    /**
     * 
     * @param args shapefile to use, if not provided the user will be prompted
     */
    public static void main( String[] args ) throws Exception {
        System.out.println("Welcome to GeoTools:" + GeoTools.getVersion());
        
        File file, file2;
        if (args.length == 0){
            file = JFileDataStoreChooser.showOpenFile("shp", null);
        }
        else {
            file = new File( args[0] );
        }
        if (args.length <= 1){
            file2 = JFileDataStoreChooser.showOpenFile("shp", null);
        }
        else {
            file2 = new File( args[1] );
        }        
        if (file == null || !file.exists() || file2 == null || !file2.exists()){
            System.exit(1);
        }
        Map<String,Object> connect = new HashMap<String,Object>();
        connect.put("url", file.toURI().toURL());
        DataStore shapefile = DataStoreFinder.getDataStore(connect);
        String typeName = shapefile.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> shapes = shapefile.getFeatureSource( typeName );
        
        Map<String,Object> connect2 = new HashMap<String,Object>();
        connect.put("url", file2.toURI().toURL());
        DataStore shapefile2 = DataStoreFinder.getDataStore(connect);
        String typeName2 = shapefile2.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> shapes2 = shapefile2.getFeatureSource( typeName2 );
        
        joinExample( shapes, shapes2);
        System.exit(0);
    }

    private static void joinExample( FeatureSource<SimpleFeatureType, SimpleFeature> shapes, FeatureSource<SimpleFeatureType, SimpleFeature> shapes2 ) throws Exception {
        SimpleFeatureType schema = shapes.getSchema();
        String typeName = schema.getTypeName();
        String geomName = schema.getGeometryDescriptor().getLocalName();
        
        SimpleFeatureType schema2 = shapes2.getSchema();
        String typeName2 = schema2.getTypeName();
        String geomName2 = schema2.getGeometryDescriptor().getLocalName();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);        
        
        DefaultQuery outerGeometry = new DefaultQuery( typeName, Filter.INCLUDE, new String[]{ geomName } );        
        FeatureCollection outerFeatures = shapes.getFeatures( outerGeometry );
        FeatureIterator iterator = outerFeatures.features();
        int max = 0;
        try {
            while( iterator.hasNext() ){
                SimpleFeature feature = (SimpleFeature) iterator.next();
                try {
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    if( !geometry.isValid()) {
                        // skip bad data
                        continue;
                    }
                    Filter innerFilter = ff.intersects( ff.property(geomName2), ff.literal( geometry ));
                    //Filter innerFilter = ff.dwithin(ff.property(geomName2), ff.literal( geometry ),1.0,"km");
                    //Filter innerFilter = ff.beyond(ff.property(geomName2), ff.literal( geometry ),1.0,"km");
                    //Filter innerFilter = ff.not( ff.disjoint(ff.property(geomName2), ff.literal( geometry )) );
                    
                    DefaultQuery innerQuery = new DefaultQuery( typeName2, innerFilter, DefaultQuery.NO_NAMES );
                    FeatureCollection join = shapes2.getFeatures( innerQuery );
                    int size = join.size();                
                    max = Math.max( max, size );
                }
                catch( Exception skipBadData ){
                }
            }
        }
        finally {
            iterator.close();            
        }
        System.out.println("At most "+max+" "+typeName2+" features in a single "+typeName+" feature");
    }
}
