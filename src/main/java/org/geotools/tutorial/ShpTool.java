package org.geotools.tutorial;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import utils.Location;
import utils.PositionUtil;
import utils.Utils;

import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class ShpTool extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShpTool frame = new ShpTool();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ShpTool() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		JButton button = new JButton("添加csv文件");
		JButton buttonshp = new JButton("预览shp文件");
		JButton shppy = new JButton("shp文件wgs84-gcj02");
		JButton gcjto84 = new JButton("shp文件gcj02-wgs84");
		JButton bdTo84 = new JButton("shp文件bd09-wgs84");
		JButton splitButton = new JButton("分段");
		JLabel lengthLabel = new JLabel("长度：");
		
		JButton bufferButton = new JButton("buffer");
		JLabel bufferLength = new JLabel("长度：");
		
		final JTextField lengthText = new JTextField(20);
		lengthText.setText("100");//增加默认值
		final JTextField bufferLengthText = new JTextField(20);
		bufferLengthText.setText("100");//增加默认值
		
		
		buttonshp.setLocation(121, 10);
		buttonshp.setSize(110, 23);
		button.setBounds(10, 10, 110, 23);
		button.setVerticalAlignment(SwingConstants.TOP);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		shppy.setLocation(240, 10);
		shppy.setSize(180, 23);
		
		gcjto84.setLocation(10,60);
		gcjto84.setSize(180, 30);
		
		bdTo84.setLocation(190,60);
		bdTo84.setSize(180, 30);
		splitButton.setLocation(10,120);
		splitButton.setSize(180, 30);
		
		bufferButton.setLocation(10,180);
		bufferButton.setSize(180, 30);
		
		lengthLabel.setLocation(200, 120);
		lengthLabel.setSize(80,30);
		lengthText.setBounds(240, 120, 80, 30);
		
		bufferLength.setLocation(200, 180);
		bufferLength.setSize(80,30);
		bufferLengthText.setBounds(240, 180, 80, 30);
		JButton geomButton = new JButton("shp转geom字符串");
		JRadioButton r1=new JRadioButton("wgs84Togcj02");    
		JRadioButton r2=new JRadioButton("gcj02Towgs84");
		JRadioButton r3=new JRadioButton("不偏移"); 
		geomButton.setLocation(10, 240);
		geomButton.setSize(180,30);
		r1.setBounds(190, 240, 150, 30);
		r2.setBounds(340, 240, 150, 30);
		r3.setBounds(490, 240, 120, 30);
		ButtonGroup bg=new ButtonGroup();
		r1.setActionCommand("1");
		r2.setActionCommand("2");
		r3.setActionCommand("0");
		bg.add(r1);
		bg.add(r2); 
		bg.add(r3);
		Container contailner = getContentPane();
		final Csv2Shape cts=new Csv2Shape();
		button.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
					cts.buildShp();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		       }});
		getContentPane().setLayout(null);
		contailner.add(button);
		
		buttonshp.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
					lookShp();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		contailner.add(buttonshp);
		
		shppy.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
		    		   shpOffset();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		contailner.add(shppy);
		
		gcjto84.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
		    		   shpGcjTo84();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		contailner.add(gcjto84);
		bdTo84.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
		    		   shpBDTo84();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		
		contailner.add(bdTo84);
		
		splitButton.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
		    		   Integer length=Integer.parseInt(lengthText.getText());
		    		   if(length<=0) {
		    			   JOptionPane.showMessageDialog(null,"长度填写错误", "分段长度", JOptionPane.ERROR_MESSAGE);
		    			   return;
		    		   }
		    		   Spliter spliter =new Spliter();
		    		   spliter.start(length);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		contailner.add(splitButton);
		contailner.add(lengthLabel);
		contailner.add(lengthText);
		
		bufferButton.addActionListener(new ActionListener(){
		       public void actionPerformed(ActionEvent e) {
		    	   try {
		    		   double length=Double.parseDouble(bufferLengthText.getText());
		    		   if(length<=0) {
		    			   JOptionPane.showMessageDialog(null,"长度填写错误", "分段长度", JOptionPane.ERROR_MESSAGE);
		    			   return;
		    		   }
		    		   Buffer buffer =new Buffer();
		    		   buffer.start(length);
		    		   
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});
		contailner.add(bufferButton);
		contailner.add(bufferLength);
		contailner.add(bufferLengthText);
		
		geomButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					ButtonModel bm =bg.getSelection();
					System.out.println(bm);
					if(bm!=null) {
						String selectedValue=bm.getActionCommand();
						Integer value=Integer.parseInt(selectedValue);
						shpToGeom(value);
					}else {
						JOptionPane.showMessageDialog(null,"请选择类型", "PLAIN_MESSAGE", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		contailner.add(geomButton);
		contailner.add(r1);
		contailner.add(r2);
		contailner.add(r3);
	}
	
	private void shpToGeom(int type) throws IOException {
		 File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }
	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        ((ShapefileDataStore) store).setCharset(Charset.forName("utf-8"));
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureCollection collection = featureSource.getFeatures();
	        SimpleFeatureIterator iterator = collection.features();//所有要素
	        
         List<SimpleFeature> features = new ArrayList<SimpleFeature>();
         SimpleFeatureType featureType =null;
         File newfile=Utils.getNewCSVFile(file);
         try {
        	 while(iterator.hasNext()) {
        		  SimpleFeature feature = iterator.next();
	              Geometry geometry =  (Geometry) feature.getDefaultGeometry();
	              String geomstring =Utils.transformGeometryToKWB(type, geometry);
	              List<Object> attrsList=feature.getAttributes();
	              String values="";
	              for(int i=0;i<attrsList.size();i++) {
	            	  if(i==0) {
	            		  continue;
//	            		  values=attrsList.get(i).toString();
	            	  }else {
	            		  values+=attrsList.get(i).toString()+",";
	            	  }
	              }
	             
	              values+=geomstring+"\n";
	              System.out.println(values);
	              Utils.appendFile(values, newfile);
	             
        	 }
        	 JOptionPane.showMessageDialog(null,"转换成功", "PLAIN_MESSAGE", JOptionPane.PLAIN_MESSAGE);
         }catch(Exception e) {
        	 e.printStackTrace();
         }
	}
	
	private void lookShp() throws Exception{
		 File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }

	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        SimpleFeatureSource featureSource = store.getFeatureSource();

	        // Create a map content and add our shapefile to it
	        MapContent map = new MapContent();
	        map.setTitle("预览");
	        
	        Style style = SLD.createSimpleStyle(featureSource.getSchema());
	        Layer layer = new FeatureLayer(featureSource, style);
	        map.addLayer(layer);

	        // Now display the map
	        JMapFrame.showMap(map);
	}
	/**
	 * shp偏移 84-gcj02
	 * @throws IOException
	 */
	private void shpOffset() throws IOException{
		 File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }

	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureCollection collection = featureSource.getFeatures();
	        SimpleFeatureIterator iterator = collection.features();//所有要素
	        
            List<SimpleFeature> features = new ArrayList<SimpleFeature>();
            SimpleFeatureType featureType =null;
	        try {
	            while( iterator.hasNext()  ){
	                 SimpleFeature feature = iterator.next();
	                 Geometry geometry =  (Geometry) feature.getDefaultGeometry();
	                 featureType = feature.getFeatureType();//feature类型
	                 SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
	                 Coordinate[] coordinates =  null;
	                 List<Coordinate> coords = new ArrayList<Coordinate>();
	                 String geomType = geometry.getGeometryType();
	                 GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	                 List<Object> attr=feature.getAttributes();
	                 SimpleFeature multifeature=null;
	                 System.out.println(geomType);
	                 switch(geomType){
	                 case "MultiPolygon":
	                	 int geometryNum = geometry.getNumGeometries();
	                	 List<Polygon> polygons = new ArrayList<Polygon>(); 
	                	 for(int i=0;i<geometryNum;i++){//polygon个数
	                		 Polygon po= (Polygon) geometry.getGeometryN(i); 
	                		 int numInteriorRing = po.getNumInteriorRing();
	                		 LineString  exteriorRing = po.getExteriorRing();
	                		 Coordinate[] exteriorcoordinates = exteriorRing.getCoordinates();
	                		 List<Coordinate> newExteriorcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<exteriorcoordinates.length ;j++){
			                	 Location gps = PositionUtil.wgs84_To_gcj02(exteriorcoordinates[j].x,exteriorcoordinates[j].y);
			                	 
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newExteriorcoordinates.add(cd);
			                 }
	                		 LinearRing  newExteriorRing = geometryFactory.createLinearRing(newExteriorcoordinates.toArray(new Coordinate[newExteriorcoordinates.size()]));
	                		 List<LinearRing> newInteriorRings = new ArrayList<LinearRing>();
	                		 for(int j=0;j<numInteriorRing;j++){//内圈
	                			 LineString interiorRing = po.getInteriorRingN(j);
	                			 Coordinate[] interiorcoordinates = interiorRing.getCoordinates();
	                			 List<Coordinate> newInteriorcoordinates = new ArrayList<Coordinate>();
	                			 for(int n =0 ;n<interiorcoordinates.length ;n++){
				                	 Location gps = PositionUtil.wgs84_To_gcj02(interiorcoordinates[n].x,interiorcoordinates[n].y);
				                	 
				                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
				                	 newInteriorcoordinates.add(cd);
				                 }
	                			 Coordinate[] tem = newInteriorcoordinates.toArray(new Coordinate[newInteriorcoordinates.size()]);
	                			 LinearRing newInteriorRing = geometryFactory.createLinearRing(tem);
	                			 newInteriorRings.add(newInteriorRing);
	                		 }
	                		 LinearRing[] ls =  newInteriorRings.toArray(new LinearRing[newInteriorRings.size()]);
	                		 Polygon polygon =geometryFactory.createPolygon(newExteriorRing, ls);
	                		 polygons.add(polygon);
	                	 }
	                	 
		                
		                 Polygon[] polygons2 = polygons.toArray(new Polygon[polygons.size()]);
		                 MultiPolygon	multipolygon = geometryFactory.createMultiPolygon(polygons2);
//		                 featureBuilder.add(multipolygon);
		                 attr.set(0, multipolygon);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiLineString":
	                	 
	                	 int lineStringNum = geometry.getNumGeometries();
	                	 List<LineString> lineStrings = new ArrayList<LineString>(); 
	                	 
	                	 for(int i=0;i<lineStringNum;i++){//多个LineString
	                		 LineString lineString = (LineString) geometry.getGeometryN(i);
	                		 Coordinate[] lineStringcoordinates = lineString.getCoordinates();
	   
	                		 List<Coordinate> newLineStringcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<lineStringcoordinates.length ;j++){
			                	 Location gps = PositionUtil.wgs84_To_gcj02(lineStringcoordinates[j].x,lineStringcoordinates[j].y);
			                	 
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newLineStringcoordinates.add(cd);
			                 }
	                		 Coordinate[] newls = newLineStringcoordinates.toArray(new Coordinate[newLineStringcoordinates.size()]);
	                		 LineString newLineString = geometryFactory.createLineString(newls); 
	                		 lineStrings.add(newLineString);
	                	 }
	                	 
	                	 LineString[] lineStrings2 = lineStrings.toArray(new LineString[lineStrings.size()]);
	                	 
	                	
		                 MultiLineString	multiline = geometryFactory.createMultiLineString(lineStrings2);
		                 attr.set(0, multiline);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiPoint":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
		                	 Location gps = PositionUtil.wgs84_To_gcj02(coordinates[i].x,coordinates[i].y);
		                	 System.out.println(gps.toString());
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 System.out.println(coords);
	                	 MultiPoint multipoint =geometryFactory.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
		                
	                	  attr.set(0, multipoint);
			          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                 case "Polygon":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
		                	 Location gps = PositionUtil.wgs84_To_gcj02(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
		                
		                 Polygon spolygon =geometryFactory.createPolygon(coords.toArray(new Coordinate[coords.size()]));
		               
		                  attr.set(0, spolygon);
				          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "LineString":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 
		                	 Location gps = PositionUtil.wgs84_To_gcj02(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 LineString slineString =geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));
	                	 attr.set(0, slineString);
				     multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "Point":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
		                	 Location gps = PositionUtil.wgs84_To_gcj02(coordinates[i].x,coordinates[i].y);
		                	 if(gps==null) {
		                		 continue;
		                	 }
		                	 System.out.println(gps.toString());
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 if(coords.size()==0) {
	                		 continue;
	                	 }
	                	 Point point =geometryFactory.createPoint(coords.get(0));
	                	 attr.set(0, point);
					  multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                	
	                 }
	                 
	                 
	            }
	            outputSHP(file,features,featureType);
	        }catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        finally {
//	        	store.dispose();
//	            iterator.close();
	        }
	}
	
	private void shpGcjTo84() throws IOException{
		 File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }

	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureCollection collection = featureSource.getFeatures();
	        SimpleFeatureIterator iterator = collection.features();//所有要素
	        
           List<SimpleFeature> features = new ArrayList<SimpleFeature>();
           SimpleFeatureType featureType =null;
	        try {
	            while( iterator.hasNext()  ){
	                 SimpleFeature feature = iterator.next();
	                 Geometry geometry =  (Geometry) feature.getDefaultGeometry();
	                 featureType = feature.getFeatureType();//feature类型
	                 SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
	                 Coordinate[] coordinates =  null;
	                 List<Coordinate> coords = new ArrayList<Coordinate>();
	                 String geomType = geometry.getGeometryType();
	                 GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	                 List<Object> attr=feature.getAttributes();
	                 SimpleFeature multifeature=null;
	                 System.out.println(geomType);       
	                 switch(geomType){
	                 case "MultiPolygon":
	                	 int geometryNum = geometry.getNumGeometries();
	                	 List<Polygon> polygons = new ArrayList<Polygon>(); 
	                	 for(int i=0;i<geometryNum;i++){//polygon个数
	                		 Polygon po= (Polygon) geometry.getGeometryN(i); 
	                		 int numInteriorRing = po.getNumInteriorRing();
	                		 LineString  exteriorRing = po.getExteriorRing();
	                		 Coordinate[] exteriorcoordinates = exteriorRing.getCoordinates();
	                		 List<Coordinate> newExteriorcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<exteriorcoordinates.length ;j++){
			                	 Location gps = PositionUtil.gcj02_To_wgs84(exteriorcoordinates[j].x,exteriorcoordinates[j].y);
			                	 
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newExteriorcoordinates.add(cd);
			                 }
	                		 LinearRing  newExteriorRing = geometryFactory.createLinearRing(newExteriorcoordinates.toArray(new Coordinate[newExteriorcoordinates.size()]));
	                		 List<LinearRing> newInteriorRings = new ArrayList<LinearRing>();
	                		 for(int j=0;j<numInteriorRing;j++){//内圈
	                			 LineString interiorRing = po.getInteriorRingN(j);
	                			 Coordinate[] interiorcoordinates = interiorRing.getCoordinates();
	                			 List<Coordinate> newInteriorcoordinates = new ArrayList<Coordinate>();
	                			 for(int n =0 ;n<interiorcoordinates.length ;n++){
				                	 Location gps = PositionUtil.gcj02_To_wgs84(interiorcoordinates[n].x,interiorcoordinates[n].y);
				                	 
				                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
				                	 newInteriorcoordinates.add(cd);
				                 }
	                			 Coordinate[] tem = newInteriorcoordinates.toArray(new Coordinate[newInteriorcoordinates.size()]);
	                			 LinearRing newInteriorRing = geometryFactory.createLinearRing(tem);
	                			 newInteriorRings.add(newInteriorRing);
	                		 }
	                		 LinearRing[] ls =  newInteriorRings.toArray(new LinearRing[newInteriorRings.size()]);
	                		 Polygon polygon =geometryFactory.createPolygon(newExteriorRing, ls);
	                		 polygons.add(polygon);
	                	 }
	                	 
		                
		                 Polygon[] polygons2 = polygons.toArray(new Polygon[polygons.size()]);
		                 MultiPolygon	multipolygon = geometryFactory.createMultiPolygon(polygons2);
//		                 featureBuilder.add(multipolygon);
		                 attr.set(0, multipolygon);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiLineString":
	                	 
	                	 int lineStringNum = geometry.getNumGeometries();
	                	 List<LineString> lineStrings = new ArrayList<LineString>(); 
	                	 
	                	 for(int i=0;i<lineStringNum;i++){//多个LineString
	                		 LineString lineString = (LineString) geometry.getGeometryN(i);
	                		 Coordinate[] lineStringcoordinates = lineString.getCoordinates();
	   
	                		 List<Coordinate> newLineStringcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<lineStringcoordinates.length ;j++){
			                	 Location gps = PositionUtil.gcj02_To_wgs84(lineStringcoordinates[j].x,lineStringcoordinates[j].y);
			                	 
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newLineStringcoordinates.add(cd);
			                 }
	                		 Coordinate[] newls = newLineStringcoordinates.toArray(new Coordinate[newLineStringcoordinates.size()]);
	                		 LineString newLineString = geometryFactory.createLineString(newls); 
	                		 lineStrings.add(newLineString);
	                	 }
	                	 
	                	 LineString[] lineStrings2 = lineStrings.toArray(new LineString[lineStrings.size()]);
	                	 
	                	
		                 MultiLineString	multiline = geometryFactory.createMultiLineString(lineStrings2);
		                 attr.set(0, multiline);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiPoint":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
		                	 Location gps = PositionUtil.gcj02_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 System.out.println(gps.toString());
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 System.out.println(coords);
	                	 MultiPoint multipoint =geometryFactory.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
		                
	                	  attr.set(0, multipoint);
			          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                 case "Polygon":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
		                	 Location gps = PositionUtil.gcj02_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
		                
		                 Polygon spolygon =geometryFactory.createPolygon(coords.toArray(new Coordinate[coords.size()]));
		               
		                  attr.set(0, spolygon);
				          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "LineString":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 
	                		 Location gps = PositionUtil.gcj02_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 LineString slineString =geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));
	                	 attr.set(0, slineString);
				     multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "Point":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
	                		 Location gps = PositionUtil.gcj02_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 Point point =geometryFactory.createPoint(coords.get(0));
		                
	                	 attr.set(0, point);
					  multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                	
	                 }
	                 
	                 
	            }
	            outputSHP(file,features,featureType);
	        }catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        finally {
//	        	store.dispose();
//	            iterator.close();
	        }
	}
	
	/**
	 * bd09 to 84
	 * @throws IOException
	 */
	private void shpBDTo84() throws IOException{
		 File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }

	        FileDataStore store = FileDataStoreFinder.getDataStore(file);
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureCollection collection = featureSource.getFeatures();
	        SimpleFeatureIterator iterator = collection.features();//所有要素
	        
          List<SimpleFeature> features = new ArrayList<SimpleFeature>();
          SimpleFeatureType featureType =null;
	        try {
	            while( iterator.hasNext()  ){
	                 SimpleFeature feature = iterator.next();
	                 Geometry geometry =  (Geometry) feature.getDefaultGeometry();
	                 featureType = feature.getFeatureType();//feature类型
	                 SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
	                 Coordinate[] coordinates =  null;
	                 List<Coordinate> coords = new ArrayList<Coordinate>();
	                 String geomType = geometry.getGeometryType();
	                 GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	                 List<Object> attr=feature.getAttributes();
	                 SimpleFeature multifeature=null;
	                 System.out.println(geomType);
	                 
	                 switch(geomType){
	                 case "MultiPolygon":
	                	 int geometryNum = geometry.getNumGeometries();
	                	 List<Polygon> polygons = new ArrayList<Polygon>(); 
	                	 for(int i=0;i<geometryNum;i++){//polygon个数
	                		 Polygon po= (Polygon) geometry.getGeometryN(i); 
	                		 int numInteriorRing = po.getNumInteriorRing();
	                		 LineString  exteriorRing = po.getExteriorRing();
	                		 Coordinate[] exteriorcoordinates = exteriorRing.getCoordinates();
	                		 List<Coordinate> newExteriorcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<exteriorcoordinates.length ;j++){
			                	 Location gps = PositionUtil.bd09_To_wgs84(exteriorcoordinates[j].x,exteriorcoordinates[j].y);
			           
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newExteriorcoordinates.add(cd);
			                 }
	                		 LinearRing  newExteriorRing = geometryFactory.createLinearRing(newExteriorcoordinates.toArray(new Coordinate[newExteriorcoordinates.size()]));
	                		 List<LinearRing> newInteriorRings = new ArrayList<LinearRing>();
	                		 for(int j=0;j<numInteriorRing;j++){//内圈
	                			 LineString interiorRing = po.getInteriorRingN(j);
	                			 Coordinate[] interiorcoordinates = interiorRing.getCoordinates();
	                			 List<Coordinate> newInteriorcoordinates = new ArrayList<Coordinate>();
	                			 for(int n =0 ;n<interiorcoordinates.length ;n++){
				                	 Location gps = PositionUtil.bd09_To_wgs84(interiorcoordinates[n].x,interiorcoordinates[n].y);
				                	 
				                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
				                	 newInteriorcoordinates.add(cd);
				                 }
	                			 Coordinate[] tem = newInteriorcoordinates.toArray(new Coordinate[newInteriorcoordinates.size()]);
	                			 LinearRing newInteriorRing = geometryFactory.createLinearRing(tem);
	                			 newInteriorRings.add(newInteriorRing);
	                		 }
	                		 LinearRing[] ls =  newInteriorRings.toArray(new LinearRing[newInteriorRings.size()]);
	                		 Polygon polygon =geometryFactory.createPolygon(newExteriorRing, ls);
	                		 polygons.add(polygon);
	                	 }
	                	 
		                
		                 Polygon[] polygons2 = polygons.toArray(new Polygon[polygons.size()]);
		                 MultiPolygon	multipolygon = geometryFactory.createMultiPolygon(polygons2);
//		                 featureBuilder.add(multipolygon);
		                 attr.set(0, multipolygon);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiLineString":
	                	 
	                	 int lineStringNum = geometry.getNumGeometries();
	                	 List<LineString> lineStrings = new ArrayList<LineString>(); 
	                	 
	                	 for(int i=0;i<lineStringNum;i++){//多个LineString
	                		 LineString lineString = (LineString) geometry.getGeometryN(i);
	                		 Coordinate[] lineStringcoordinates = lineString.getCoordinates();
	   
	                		 List<Coordinate> newLineStringcoordinates = new ArrayList<Coordinate>();
	                		 for(int j =0 ;j<lineStringcoordinates.length ;j++){
			                	 Location gps = PositionUtil.bd09_To_wgs84(lineStringcoordinates[j].x,lineStringcoordinates[j].y);
			                	 
			                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
			                	 newLineStringcoordinates.add(cd);
			                 }
	                		 Coordinate[] newls = newLineStringcoordinates.toArray(new Coordinate[newLineStringcoordinates.size()]);
	                		 LineString newLineString = geometryFactory.createLineString(newls); 
	                		 lineStrings.add(newLineString);
	                	 }
	                	 
	                	 LineString[] lineStrings2 = lineStrings.toArray(new LineString[lineStrings.size()]);
	                	 
	                	
		                 MultiLineString	multiline = geometryFactory.createMultiLineString(lineStrings2);
		                 attr.set(0, multiline);
		                 multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "MultiPoint":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
		                	 Location gps = PositionUtil.bd09_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 System.out.println(gps.toString());
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 System.out.println(coords);
	                	 MultiPoint multipoint =geometryFactory.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
		                
	                	  attr.set(0, multipoint);
			          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                 case "Polygon":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
		                	 Location gps = PositionUtil.bd09_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
		                
		                 Polygon spolygon =geometryFactory.createPolygon(coords.toArray(new Coordinate[coords.size()]));
		               
		                  attr.set(0, spolygon);
				          multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "LineString":
	                	 coordinates= geometry.getCoordinates();
	                	 
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 
		                	 Location gps = PositionUtil.bd09_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 LineString slineString =geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));
	                	 attr.set(0, slineString);
				     multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 
		                 features.add(multifeature);
	                	 break;
	                 case "Point":
	                	 coordinates= geometry.getCoordinates();
	                	 for(int i =0 ;i<coordinates.length ;i++){
	                		 System.out.println(coordinates[i].toString());
		                	 Location gps = PositionUtil.bd09_To_wgs84(coordinates[i].x,coordinates[i].y);
		                	 System.out.println(gps.toString());
		                	 Coordinate cd  = new Coordinate(gps.getLon(),gps.getLat());
		                	 coords.add(cd);
		                 }
	                	 Point point =geometryFactory.createPoint(coords.get(0));
		                
	                	 attr.set(0, point);
					  multifeature = featureBuilder.buildFeature(null, attr.toArray());
		                 features.add(multifeature);
	                	 break;
	                	
	                 }
	                 
	                 
	            }
	            outputSHP(file,features,featureType);
	        }catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        finally {
//	        	store.dispose();
//	            iterator.close();
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
	 private static File getNewCSVFile(File file) {
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
 private void outputSHP(File file,List<SimpleFeature> features,SimpleFeatureType type) throws IOException{
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
     /*
      * The Shapefile format has a couple limitations:
      * - "the_geom" is always first, and used for the geometry attribute name
      * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
      * - Attribute names are limited in length 
      * - Not all data types are supported (example Timestamp represented as Date)
      * 
      * Each data store has different limitations so check the resulting SimpleFeatureType.
      */
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
         JOptionPane.showMessageDialog(null,"转换成功", "PLAIN_MESSAGE", JOptionPane.PLAIN_MESSAGE);
     } else {
         System.out.println(typeName + " does not support read/write access");
         System.exit(1);
     }
 }
 

}
