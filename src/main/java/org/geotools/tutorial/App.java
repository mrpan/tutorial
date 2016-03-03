package org.geotools.tutorial;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	  MapContent map = new MapContent();
          map.setTitle("Quickstart");
          

          // Now display the map
          JMapFrame.showMap(map);
//    	JButton open = new JButton("open");  
    }
}
