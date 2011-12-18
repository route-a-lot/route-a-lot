package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.GeographicalOperator;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTGeographicalOperator
 implements GeographicalOperator
{
    /** Associations */
    private QuadTree zoomlevels;

	@Override
	public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildZoomlevels() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Selection select(Coordinates pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
			Coordinates bottomRight) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
			Coordinates bottomRight) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToBaseLayer(MapElement element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToOverlay(MapElement element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromStream(InputStream stream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveToStream(OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
}

