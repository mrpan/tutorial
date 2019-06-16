package org.geotools.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import utils.Utils;

public class Buffer {
	private SimpleFeatureType simpleFeaturetype = null;

	public static void main(String args[]) throws Exception {
		Buffer test = new Buffer();
		test.start(100);
	}

	public void start(double length) throws Exception {
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return;
		}
		// File file = new File("shp/test_line.shp");
		List<SimpleFeature> list = readShpfile(file);
		List<SimpleFeature> result = bufferFeatures(list, length);
		Utils.outputSHP(file, result, simpleFeaturetype);

	}

	public List<SimpleFeature> bufferFeatures(List<SimpleFeature> list, double length) {
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		for (int i = 0; i < list.size(); i++) {
			SimpleFeature sFeature = list.get(i);
			SimpleFeature buffer = bufferFeature(sFeature, length);
			features.add(buffer);
		}
		return features;
	}

	public SimpleFeature bufferFeature(SimpleFeature feature, double distance) {
		// extract the geometry
		GeometryAttribute gemProperties = feature.getDefaultGeometryProperty();
		CoordinateReferenceSystem originCRS = gemProperties.getDescriptor().getCoordinateReferenceSystem();

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		Geometry pGeom = geom;
		MathTransform toTransform, fromTransform = null;
		// reproject the geometry to a local projection
		if (!(originCRS instanceof ProjectedCRS)) {

			Point c = geom.getCentroid();
			double x = c.getCoordinate().x;
			double y = c.getCoordinate().y;

			String code = "AUTO:42001," + x + "," + y;//通用横轴墨卡托
			CoordinateReferenceSystem auto;
			try {
				auto = CRS.decode(code);
				toTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, auto);
				fromTransform = CRS.findMathTransform(auto, DefaultGeographicCRS.WGS84);
				pGeom = JTS.transform(geom, toTransform);
			} catch (MismatchedDimensionException | TransformException | FactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// buffer
		Geometry out = buffer(pGeom, distance);
		Geometry retGeom = out;
		// reproject the geometry to the original projection
		if (!(originCRS instanceof ProjectedCRS)) {
			try {
				retGeom = JTS.transform(out, fromTransform);
			} catch (MismatchedDimensionException | TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// return a new feature containing the geom
		SimpleFeatureType schema = feature.getFeatureType();

		SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
		ftBuilder.setCRS(originCRS);
		// ftBuilder.setDefaultGeometry("buffer");
		List ads = schema.getAttributeDescriptors();
		ftBuilder.add("the_geom", Polygon.class);

		for (int i = 0; i < ads.size(); i++) {
			AttributeDescriptor attrDes = (AttributeDescriptor) ads.get(i);
			String name = attrDes.getLocalName();
			if ("the_geom".indexOf(name) >= 0) {
				continue;
			}
			ftBuilder.add(attrDes);
		}
		// ftBuilder.addAll(schema.getAttributeDescriptors());
		ftBuilder.setName(schema.getName());

		SimpleFeatureType nSchema = ftBuilder.buildFeatureType();
		if (simpleFeaturetype == null) {// 记录featuretype
			simpleFeaturetype = nSchema;
		}

		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(nSchema);
		List<Object> atts = feature.getAttributes();
		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i) instanceof Geometry) {
				atts.set(i, retGeom);
			}
		}
		SimpleFeature newFeature = builder.buildFeature(null, atts.toArray());
		return newFeature;
	}

	private Geometry buffer(Geometry geom, double distance) {

		Geometry buffer = geom.buffer(distance);

		return buffer;

	}
	//读取shp 文件
	private List<SimpleFeature> readShpfile(File file) throws IOException {
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		SimpleFeatureCollection collection = featureSource.getFeatures();
		SimpleFeatureIterator iterator = collection.features();// 所有要素
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		while (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			features.add(feature);
		}
		return features;
	}
}