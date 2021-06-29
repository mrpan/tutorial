package org.geotools.tutorial;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKBReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.WKBWriter;


public class WKBUtil {
	public static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		Coordinate coord = new Coordinate(4, 5);  
		Coordinate coord = new Coordinate(1, 2);
	    Point point = geometryFactory.createPoint( coord );
//	    point.normalize();
	    point.setSRID(4326);
	    WKBWriter wkbWriter = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN,true);
	    System.out.println(point);
	    String wkb = WKBWriter.toHex(wkbWriter.write(point)); 
	    System.out.println(wkb);
	    WKBReader wkbReader =new WKBReader();
	    byte[] test= wkbReader.hexToBytes("0101000020E6100000B29DEFA7C6EB5940C74B378941803E40");
	    Geometry g= wkbReader.read(test);
	    System.out.println(g);
	}
	
	public static String toWKBString(Geometry geom) {
//		 geom.setSRID(4326);
		 WKBWriter wkbWriter = new WKBWriter(2,ByteOrderValues.LITTLE_ENDIAN,true); 
		 String wkb = WKBWriter.toHex(wkbWriter.write(geom)); 
		 return wkb;
	}

}
//postgis
//0101000020E6100000CBA145B6F3D559402B8716D9CE573E40

//0101000020E6100000B29DEFA7C6EB5940C74B378941803E40

//0101000000B29DEFA7C6EB5940C74B378941803E40
//0101000000CBA145B6F3D559402B8716D9CE573E40
//010100002000000000B29DEFA7C6EB5940C74B378941803E40
