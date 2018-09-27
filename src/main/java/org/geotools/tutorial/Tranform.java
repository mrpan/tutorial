package org.geotools.tutorial;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

public class Tranform {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Tranform t =new Tranform();
		double[] point ={103.6,30.6};
		Coordinate c = t.transformCoordinate("EPSG:4326", "EPSG:4490", point);
		System.out.println(c);
	}
	public Coordinate transformCoordinate(String fromProj,String toProj,double[] point) throws Exception {
		CoordinateReferenceSystem sourceCRS = CRS.decode(fromProj);
		CoordinateReferenceSystem targetCRS = CRS.decode(toProj);
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS,true);
		Coordinate coordinate =new Coordinate(point[1],point[0]);
		Coordinate targetCoordinate = JTS.transform( coordinate, null, transform );
		return targetCoordinate;
	}

}
