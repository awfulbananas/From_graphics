package fromics;

//3d doesn't work yet, I'll fix it before adding comments to 3d classes
public class Camera3d extends Point{
	public static final double DEFAULT_DRAWING_PLANE_DIST = 3;
	
	private Point rotations;
	private Point drawPlane;
	
	public Camera3d() {
		super(0, 0, 0);
		rotations = new Point(3);
		drawPlane = new Point(0, 0, DEFAULT_DRAWING_PLANE_DIST);
	}
	
	public Camera3d(Point loc, Point rotations, Point drawPlane) {
		super(loc.X(), loc.Y(), loc.Z());
		this.rotations = rotations;
		this.drawPlane = drawPlane;
	}
	
	public Camera3d(double x, double y, double z) {
		super(x, y, z);
		rotations = new Point(3);
		drawPlane = new Point(0, 0, DEFAULT_DRAWING_PLANE_DIST);
	}
	
	public Camera3d(double x, double y, double z, double xRot, double yRot, double zRot) {
		super(x, y, z);
		rotations = new Point(xRot, yRot, zRot);
		drawPlane = new Point(0, 0, DEFAULT_DRAWING_PLANE_DIST);
	}
	
	public void setRotations(Point rotations) {
		this.rotations = rotations;
	}
	
	public Point getRotations() {
		return rotations;
	}
	
	public double getXRot() {
		return rotations.X();
	}
	
	public double getYRot() {
		return rotations.Y();
	}

	public double getZRot() {
		return rotations.Z();
	}
	
	public Point getPlanePos() {
		return drawPlane;
	}
	
	public Point relativeLoc(Point from) {
		return rotation3(rotation2(rotation1(from.copy().sub(this))));
	}
	
	private Point rotation1(Point p) {
		Point newp = new Point(Math.cos(rotations.Y()) * p.X() - Math.sin(rotations.Y()) * p.Z(),
				p.Y(), Math.cos(rotations.Y()) * p.Z() + Math.sin(rotations.Y()) * p.X());
		return newp;
	}
	
	private Point rotation2(Point p) {
		Point newp = new Point(p.X(), Math.cos(rotations.Y()) * p.Y() - Math.sin(rotations.Y()) * p.Z(),
				Math.cos(rotations.Y()) * p.Z() + Math.sin(rotations.Y()) * p.Y());
		return newp;
	}
	
	private Point rotation3(Point p) {
		Point newp = new Point(Math.cos(rotations.Z()) * p.X() - Math.sin(rotations.Z()) * p.Y(),
				Math.cos(rotations.Z()) * p.Y() + Math.sin(rotations.Z()) * p.X(), p.Z());
		return newp;
	}
	
}
