package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.tutorial.WKBUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class Utils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void outputSHP(File file, List<SimpleFeature> features, SimpleFeatureType type) throws IOException {
		File newFile = getNewShapeFile(file);

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(type);

		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();

		System.out.println("SHAPE:" + SHAPE_TYPE);

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			/*
			 * SimpleFeatureStore has a method to add features from a
			 * SimpleFeatureCollection object, so we use the ListFeatureCollection class to
			 * wrap our list of features.
			 */
			SimpleFeatureCollection collection = new ListFeatureCollection(type, features);
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(collection);
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				newDataStore.dispose();
				transaction.close();
			}
			JOptionPane.showMessageDialog(null, "处理成功", "PLAIN_MESSAGE", JOptionPane.PLAIN_MESSAGE);
		} else {
			System.out.println(typeName + " does not support read/write access");
			System.exit(1);
		}
	}

	private static File getNewShapeFile(File file) {
		String path = file.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			// the user cancelled the dialog
			System.exit(0);
		}

		File newFile = chooser.getSelectedFile();
		if (newFile.equals(file)) {
			System.out.println("Error: cannot replace " + file);
			System.exit(0);
		}

		return newFile;
	}
/**
 * geometry偏移转换
 * @param geometry
 * @param transformType
 * @return
 */
	public static Geometry geometryTransformFunc(Geometry geometry, int transformType) {
		String geomType = geometry.getGeometryType();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		Geometry resultGeometry = null;
		Coordinate[] coordinates = geometry.getCoordinates();
		List<Coordinate> coords = new ArrayList<Coordinate>();
		switch (geomType) {
		case "MultiPolygon":
			int geometryNum = geometry.getNumGeometries();
			List<Polygon> polygons = new ArrayList<Polygon>();
			for (int i = 0; i < geometryNum; i++) {// polygon个数
				Polygon po = (Polygon) geometry.getGeometryN(i);
				int numInteriorRing = po.getNumInteriorRing();
				LineString exteriorRing = po.getExteriorRing();
				Coordinate[] exteriorcoordinates = exteriorRing.getCoordinates();
				List<Coordinate> newExteriorcoordinates = new ArrayList<Coordinate>();
				for (int j = 0; j < exteriorcoordinates.length; j++) {
					
					Location gps = transformLonlat(transformType,exteriorcoordinates[j].x, exteriorcoordinates[j].y);
					Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
					newExteriorcoordinates.add(cd);
				}
				LinearRing newExteriorRing = geometryFactory.createLinearRing(
						newExteriorcoordinates.toArray(new Coordinate[newExteriorcoordinates.size()]));
				List<LinearRing> newInteriorRings = new ArrayList<LinearRing>();
				for (int j = 0; j < numInteriorRing; j++) {// 内圈
					LineString interiorRing = po.getInteriorRingN(j);
					Coordinate[] interiorcoordinates = interiorRing.getCoordinates();
					List<Coordinate> newInteriorcoordinates = new ArrayList<Coordinate>();
					for (int n = 0; n < interiorcoordinates.length; n++) {
						Location gps = transformLonlat(transformType,interiorcoordinates[n].x, interiorcoordinates[n].y);
						Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
						newInteriorcoordinates.add(cd);
					}
					Coordinate[] tem = newInteriorcoordinates.toArray(new Coordinate[newInteriorcoordinates.size()]);
					LinearRing newInteriorRing = geometryFactory.createLinearRing(tem);
					newInteriorRings.add(newInteriorRing);
				}
				LinearRing[] ls = newInteriorRings.toArray(new LinearRing[newInteriorRings.size()]);
				Polygon polygon = geometryFactory.createPolygon(newExteriorRing, ls);
				polygons.add(polygon);
			}

			Polygon[] polygons2 = polygons.toArray(new Polygon[polygons.size()]);
			resultGeometry = geometryFactory.createMultiPolygon(polygons2);
			break;
		case "MultiLineString":

			int lineStringNum = geometry.getNumGeometries();
			List<LineString> lineStrings = new ArrayList<LineString>();

			for (int i = 0; i < lineStringNum; i++) {// 多个LineString
				LineString lineString = (LineString) geometry.getGeometryN(i);
				Coordinate[] lineStringcoordinates = lineString.getCoordinates();

				List<Coordinate> newLineStringcoordinates = new ArrayList<Coordinate>();
				for (int j = 0; j < lineStringcoordinates.length; j++) {
					Location gps = transformLonlat(transformType,lineStringcoordinates[j].x, lineStringcoordinates[j].y);
					
					Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
					newLineStringcoordinates.add(cd);
				}
				Coordinate[] newls = newLineStringcoordinates.toArray(new Coordinate[newLineStringcoordinates.size()]);
				LineString newLineString = geometryFactory.createLineString(newls);
				lineStrings.add(newLineString);
			}

			LineString[] lineStrings2 = lineStrings.toArray(new LineString[lineStrings.size()]);

			resultGeometry = geometryFactory.createMultiLineString(lineStrings2);

			break;
		case "MultiPoint":
			for (int i = 0; i < coordinates.length; i++) {
				System.out.println(coordinates[i].toString());
				Location gps =transformLonlat(transformType,coordinates[i].x, coordinates[i].y);
				System.out.println(gps.toString());
				Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
				coords.add(cd);
			}
			System.out.println(coords);
			resultGeometry = geometryFactory.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
			break;
		case "Polygon":

			coordinates = geometry.getCoordinates();

			for (int i = 0; i < coordinates.length; i++) {
				Location gps =transformLonlat(transformType,coordinates[i].x, coordinates[i].y);
				Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
				coords.add(cd);
			}

			Polygon spolygon = geometryFactory.createPolygon(coords.toArray(new Coordinate[coords.size()]));

			break;
		case "LineString":
			coordinates = geometry.getCoordinates();

			for (int i = 0; i < coordinates.length; i++) {

				Location gps =transformLonlat(transformType,coordinates[i].x, coordinates[i].y);
				Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
				coords.add(cd);
			}
			resultGeometry = geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));

			break;
		case "Point":
			coordinates = geometry.getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				System.out.println(coordinates[i].toString());
				Location gps =transformLonlat(transformType,coordinates[i].x, coordinates[i].y);
				if (gps == null) {
					continue;
				}
				System.out.println(gps.toString());
				Coordinate cd = new Coordinate(gps.getLon(), gps.getLat());
				coords.add(cd);
			}
			resultGeometry = geometryFactory.createPoint(coords.get(0));

			break;
		}

		return resultGeometry;

	}
	
	public static Location transformLonlat(int transformType, double lon,double lat) {
		Location gps = null;
		if(transformType==PositionUtil.W2G) {
			gps = PositionUtil.wgs84_To_gcj02(lon, lat);
		}else if(transformType==PositionUtil.G2W) {
			gps = PositionUtil.gcj02_To_wgs84(lon, lat);
		}else if(transformType==PositionUtil.W2B) {
			gps = PositionUtil.wgs84_To_bd09(lon, lat);
		}else if(transformType==PositionUtil.B2W) {
			gps = PositionUtil.bd09_To_wgs84(lon, lat);
		}else if(transformType==PositionUtil.NoGB) {
			gps.setLon(lon);
			gps.setLat(lat);
		}
		return gps;
	}
	public static String transformGeometryToKWB(int transformType,Geometry geometry) {
		Geometry transGeometry=geometryTransformFunc(geometry, transformType);
		return WKBUtil.toWKBString(transGeometry);
	}
	
	public static void appendFile( String content,File file) {  
//		String fileName = "E:/grid/20/grid.txt";
		String fileName = file.getPath();
		
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
            FileWriter writer = new FileWriter(fileName, true);  
            writer.write(content);  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
    } 
	public static File getNewCSVFile(File file) {
        String path = file.getAbsolutePath();
        String newPath = path.substring(0, path.length() - 4) + ".csv";

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("csv");
        chooser.setDialogTitle("Save CSV File");
        chooser.setSelectedFile(new File(newPath));

        int returnVal = chooser.showSaveDialog(null);

        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
            // the user cancelled the dialog
            System.exit(0);
        }

        File newFile = chooser.getSelectedFile();
        if (newFile.equals(file)) {
            System.out.println("Error: cannot replace " + file);
            System.exit(0);
        }

        return newFile;
    }

}
