package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class Utils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void outputSHP(File file, List<SimpleFeature> features, SimpleFeatureType type) throws IOException{
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
	    
	     System.out.println("SHAPE:"+SHAPE_TYPE);

	     if (featureSource instanceof SimpleFeatureStore) {
	         SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
	         /*
	          * SimpleFeatureStore has a method to add features from a
	          * SimpleFeatureCollection object, so we use the ListFeatureCollection
	          * class to wrap our list of features.
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
	         JOptionPane.showMessageDialog(null,"处理成功", "PLAIN_MESSAGE", JOptionPane.PLAIN_MESSAGE);
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

}
