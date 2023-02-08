package fromics;

import java.awt.Graphics;

public class OvalCollider extends Collidable {
	//the radius of collision
	private double radius;

	//creates a new CircleCollider
	public OvalCollider(double x, double y, double radius) {
		super(x, y);
		this.radius = radius;
	}
	
	//returns the collision type for this Collidable, which is TYPE_OVAL
	@Override
	public int getCollisionType() {
		return 1;
	}
	
	//returns whether this Collidable is colliding with Collidable other
	//collision with Polygons isn't implemented yet
	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case Collidable.TYPE_OVAL:
				return other.copy().sub(this).mag() < radius + ((OvalCollider)other).getRadius();
			case Collidable.TYPE_POINT:
				return other.copy().sub(this).mag() < radius;
			default:
				return false;
		}
		//TODO: add collision for other types
	}

	//returns the radius of this OvalCollider
	public double getRadius() {
		return radius;
	}
	
	//draws an oval representing this OvalCollider
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		g.drawOval((int)(X() + xOff), (int)(Y() + yOff), (int)(2 * radius), (int)(2 * radius));
	}

}
