package org.geotools.tutorial;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geotools.data.*;
import org.geotools.factory.*;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Function;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.*;

import java.io.File;
import java.util.*;

public class GeotoolsTest {

	public GeotoolsTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FilterFactory filterFactory=CommonFactoryFinder.getFilterFactory();
		Filter filter = filterFactory.less( filterFactory.property( "size" ), filterFactory.literal( 2 ) );
		//org.geotools.styling.StyleFactory
		StyleFactory styleFactory =CommonFactoryFinder.getStyleFactory();
		
		Set<Function> funcset= CommonFactoryFinder.getFunctions(null);
		Set<FileDataStoreFactorySpi> fdstore=CommonFactoryFinder.getFileDataStoreFactories(null);
		//org.opengis.feature.FeatureFactory
		FeatureFactory featureFactory=(FeatureFactory) CommonFactoryFinder.getFeatureFactory(null);
		Object[] array=null;
		SimpleFeatureType type=null;
		String id=null;
//		SimpleFeature sf=featureFactory.createSimpleFeature(array, type, id);
		
		FeatureTypeFactory ftFactory=CommonFactoryFinder.getFeatureTypeFactory(null);
//		SimpleFeatureType sftype=ftFactory.createSimpleFeatureType(null, null, null, (Boolean) false, null, null, null);
		
		FeatureCollections fc=CommonFactoryFinder.getFeatureCollections();
		
		File file = new File("example.shp");
		Map map = new HashMap();
		map.put( "url", file.toURL() );
//		DataAccess<FeatureType,Feature> dataAccess=DataAccessFinder.getDataStore(null);
		
		DataStore dataStore=DataStoreFinder.getDataStore(map);
		String[] typenames=dataStore.getTypeNames();
		
		FileDataStore fileDataStore=FileDataStoreFinder.getDataStore(file);
		FileDataStore fileDataStore2=FileDataStoreFinder.getDataStore(file.toURL());
//		SimpleFeatureType featuretype= fileDataStore.getSchema();
//		SimpleFeatureSource sfSource=fileDataStore.getFeatureSource();
		//org.locationtech.jts.geom.GeometryFactory
		GeometryFactory geometryFactory= JTSFactoryFinder.getGeometryFactory();
		Coordinate cd=null;
//		Point p=geometryFactory.createPoint(cd);

		PrecisionModel pm =geometryFactory.getPrecisionModel();
		System.out.println(pm.getType());

		DatumFactory datumFactory= ReferencingFactoryFinder.getDatumFactory(null);
		CSFactory csFactory=ReferencingFactoryFinder.getCSFactory(null);

		DatumAuthorityFactory datumAuthorityFactory=ReferencingFactoryFinder.getDatumAuthorityFactory("EPSG",null);
		System.out.println(datumAuthorityFactory.getAuthority());

		CSAuthorityFactory csAuthorityFactory =ReferencingFactoryFinder.getCSAuthorityFactory("EPSG", null);

		CRSAuthorityFactory crsAuthorityFactory =ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG",null);
		CoordinateReferenceSystem coordinateReferenceSystem=crsAuthorityFactory.createCoordinateReferenceSystem("EPSG:3857");
//		System.out.println(coordinateReferenceSystem);
		GeographicCRS geographicCRS=crsAuthorityFactory.createGeographicCRS("EPSG:4326");
//		System.out.println(geographicCRS);
		MathTransformFactory mathTransformFactory= ReferencingFactoryFinder.getMathTransformFactory(null);

		CoordinateOperationFactory coordinateOperationFactory=ReferencingFactoryFinder.getCoordinateOperationFactory(null);
		CoordinateOperation coordinateOperation =coordinateOperationFactory.createOperation(coordinateReferenceSystem,geographicCRS);
		System.out.println(coordinateOperation);
		MathTransform mathTransform = coordinateOperation.getMathTransform();
		System.out.println(mathTransform);

		CoordinateOperationAuthorityFactory coordinateOperationAuthorityFactory= ReferencingFactoryFinder.getCoordinateOperationAuthorityFactory("EPSG",null);

		Hints hints = GeoTools.getDefaultHints();
		FactoryRegistry registry = new FactoryCreator(Arrays.asList(new Class[] {FilterFactory.class,}));
		Iterator i = registry.getServiceProviders( FilterFactory.class, null, hints );
		while( i.hasNext() ){
			FilterFactory factory = (FilterFactory) i.next();
			System.out.println(factory);
		}
		System.out.println(GeoTools.getBuildProperties());
		FeatureCollection collection = FeatureCollections.newCollection();

	}

}
