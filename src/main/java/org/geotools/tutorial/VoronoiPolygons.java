package org.geotools.tutorial;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

public class VoronoiPolygons {

	private static final double TRIANGULATION_TOLERANCE=0.0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GeometryFactory gf=new GeometryFactory();
		Coordinate[] coords  = new Coordinate[] {new Coordinate(104.05038, 30.65916), new Coordinate(104.05038, 30.64624),new Coordinate(104.06425, 30.64624),new Coordinate(104.06425, 30.65916),new Coordinate(104.05038, 30.65916)};
		Geometry polygon = gf.createPolygon(coords);
		Geometry geoms=CreateRandomShapeFunctions.randomPointsInGrid(polygon, 10);
		System.out.println(geoms);
//		gf.createGeometryCollection(geometries);
		Geometry voronoiPolygons=VoronoiPolygons.voronoiDiagram(geoms, polygon);
		System.out.println(voronoiPolygons);
	}
	
	
	public static Geometry voronoiDiagram(Geometry sitesGeom,Geometry clipGeom) {
		VoronoiDiagramBuilder builder= new VoronoiDiagramBuilder();
		builder.setSites(sitesGeom);
		if(clipGeom!=null) {
			builder.setClipEnvelope(clipGeom.getEnvelopeInternal());
		}
		builder.setTolerance(TRIANGULATION_TOLERANCE);
		Geometry result =builder.getDiagram(sitesGeom.getFactory());
		return result;
	}

}
