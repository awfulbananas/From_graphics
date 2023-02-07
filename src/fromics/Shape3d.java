package fromics;

import java.awt.Graphics;
import java.util.List;

public abstract class Shape3d extends Linkable{
	
	public Shape3d(double x, double y, double z) {
		super(x, y, z);
	}
	
	public abstract List<Point> getPoints();

	public abstract void drawTransformedPoints(Graphics g, List<Point> tPoints);

}
