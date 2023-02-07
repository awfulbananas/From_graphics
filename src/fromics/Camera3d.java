package fromics;

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
		super(loc.x, loc.y, loc.z);
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
		return rotations.x;
	}
	
	public double getYRot() {
		return rotations.y;
	}

	public double getZRot() {
		return rotations.z;
	}
	
	public Point getPlanePos() {
		return drawPlane;
	}
	
	public Point relativeLoc(Point from) {
		return rotation3(rotation2(rotation1(from.copy().sub(this))));
	}
	
	private Point rotation1(Point p) {
		Point newp = new Point(Math.cos(rotations.y) * p.x - Math.sin(rotations.y) * p.z,
				p.y, Math.cos(rotations.y) * p.z + Math.sin(rotations.y) * p.x);
		return newp;
	}
	
	private Point rotation2(Point p) {
		Point newp = new Point(p.x, Math.cos(rotations.y) * p.y - Math.sin(rotations.y) * p.z,
				Math.cos(rotations.y) * p.z + Math.sin(rotations.y) * p.y);
		return newp;
	}
	
	private Point rotation3(Point p) {
		Point newp = new Point(Math.cos(rotations.z) * p.x - Math.sin(rotations.z) * p.y,
				Math.cos(rotations.z) * p.y + Math.sin(rotations.z) * p.x, p.z);
		return newp;
	}
	
}
