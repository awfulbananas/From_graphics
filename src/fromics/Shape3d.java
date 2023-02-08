package fromics;

import java.awt.Graphics;
import java.util.List;

//3d doesn't work yet, I'll fix it before adding comments to 3d classes
//this one should be mostly self-explanatory though
public abstract class Shape3d extends Linkable{
	
	public Shape3d(double x, double y, double z) {
		super(x, y, z);
	}
	
	public abstract List<Point> getPoints();

	public abstract void drawTransformedPoints(Graphics g, List<Point> tPoints);

}
