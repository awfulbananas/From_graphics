package fromics;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

//an object for drawing 3D shapes, all 3D shapes should be linked to a projector
//3d doesn't work yet, I'll fix it before adding more comments to 3d classes
public class Projector3d extends Linkable {
	
	Camera3d view;

	public Projector3d(Camera3d view) {
		super(0,0);
		this.view = view;
	}
	
	@Override
	public void drawAll(Graphics g) {
		draw(g, getAbsX(), getAbsY(), getAbsAng());
		for(Linkable l : linked) {
			if(Shape3d.class.isAssignableFrom(l.getClass())) {
				List<Point> tPoints = new LinkedList<>();
				for(Point p : ((Shape3d)l).getPoints()) {
					tPoints.add(project(p, view));
				}
				((Shape3d)l).drawTransformedPoints(g, tPoints);
			} else {
				l.drawAll(g);
			}
		}
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {}
	
	public static Point project(Point p, Camera3d view) {
		Point relative = view.relativeLoc(p);
		Point plane = view.getPlanePos();
		return new Point(relative.X(), relative.Y()).div(relative.Z()).mult(plane.Z()).add(plane.X(), plane.Y());
	}

}
