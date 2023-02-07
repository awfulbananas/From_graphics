package fromics;

import java.awt.Graphics;
import java.util.List;
import java.util.function.Consumer;

public class Lines3d extends Shape3d {
	//closed should define whether to close the shape made by the lines
	private final boolean closed;
	
	private List<Point> corners;

	public Lines3d(double x, double y, double z, List<Point> corners, boolean closed) {
		super(x, y, z);
		this.closed = closed;
		this.corners = corners;
	}

	@Override
	public List<Point> getPoints() {
		return corners;
	}

	@Override
	public void drawTransformedPoints(Graphics g, List<Point> tPoints) {
		Linkable.drawPoints(g, getAbsX(), getAbsY(), getAbsAng(), 1.0, corners.toArray(new Point[0]), closed);
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		// TODO Auto-generated method stub

	}
	
	public void transformLines(Consumer<Point> c) {
		for(Point p : corners) {
			p.transform(c);
		}
	}

}
