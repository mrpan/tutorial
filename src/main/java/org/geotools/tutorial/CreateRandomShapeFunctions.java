package org.geotools.tutorial;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.shape.random.RandomPointsInGridBuilder;

public class CreateRandomShapeFunctions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static Geometry randomPointsInGrid(Geometry geom,int nPts) {
		RandomPointsInGridBuilder shapeBuilder =new RandomPointsInGridBuilder();
		shapeBuilder.setExtent(geom.getEnvelopeInternal());
		shapeBuilder.setNumPoints(nPts);
		return shapeBuilder.getGeometry();
	}

}
