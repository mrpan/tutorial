package org.geotools.tutorial;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.xml.styling.SLDParser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Image {
	public static void saveImage(final MapContent map, final String file, final int imageWidth) {

	    GTRenderer renderer = new StreamingRenderer();
	    renderer.setMapContent(map);
	    Rectangle imageBounds = null;
	    ReferencedEnvelope mapBounds = null ;
	    
	    try {
	    	System.setProperty("org.geotools.referencing.forceXY", "true");//强制改变坐标xy顺序
//	        mapBounds = map.getMaxBounds();
//	        System.out.println(mapBounds.getCoordinateReferenceSystem().toString());
	    	CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");//坐标xy顺序
	    	  mapBounds = new ReferencedEnvelope(105.5169 , 105.5257, 30.5440,30.526364943977255, sourceCRS);
	    	 System.out.println(mapBounds.getCoordinateReferenceSystem().toString());
	    	double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
	        imageBounds = new Rectangle(
	                0, 0, imageWidth, 256);//设置宽度和高度

	    } catch (Exception e) {
	        // failed to access map layers
	        throw new RuntimeException(e);
	    }

	    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

	    Graphics2D gr = image.createGraphics();
	    gr.setPaint(Color.white);
	    gr.fill(imageBounds);

	    try {
	        renderer.paint(gr, imageBounds, mapBounds);
	        File fileToSave = new File(file);
	        ImageIO.write(image, "png", fileToSave);

	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static Style readSld(){
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		SLDParser parser = new SLDParser(styleFactory);
	    // the xml instance document above
		File file = new File("E:/mapdata/sld/ec2io.sld");
		Style styles[]=null;
		try {
			parser.setInput(file);
			styles = parser.readXML();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    
		return styles[0];
	}
	public static void main(String[] args) throws Exception{
//		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		File file = new File("E:/mapdata/tempgrid/temp_grid.shp");
//        if (file == null) {
//            return;
//        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // Create a map content and add our shapefile to it
        MapContent map = new MapContent();
//        map.setTitle("Quickstart");
//        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Style style= readSld();
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);
        saveImage(map,"E:/test/test.png",256);
	} 
}
