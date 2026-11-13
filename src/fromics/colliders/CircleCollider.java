package fromics.colliders;

import fromics.Point;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

//a class representing a collider with a circular shape
public class CircleCollider extends Collidable {
	//the radius of collision
	private double radius;

	//creates a new CircleCollider
	public CircleCollider(double x, double y, double radius) {
		super(x, y);
		this.radius = radius;
	}
	
	//returns the collision type for this Collidable, which is TYPE_OVAL
	@Override
	public CollisionType getCollisionType() {
		return CollisionType.OVAL;
	}
	
	//returns whether this Collidable is colliding with Collidable other
	//collision with Polygons isn't implemented yet
	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case OVAL:
				return other.copy().sub(this).mag() < radius + ((CircleCollider)other).getRadius();
			case POINT:
				return other.copy().sub(this).mag() < radius;
			case POLYGON, RECT:
				return other.check(this);
			default:
				return false;
		}
		//TODO: add collision for other types
	}

	@Override
	public boolean checkPoint(Point p) {
		return p.copy().sub(this).sMag() <= radius * radius;
	}

	//returns the radius of this OvalCollider
	public double getRadius() {
		return radius;
	}

	public void setRadius(double rad) {
		radius = rad;
	}
	
	//draws an oval representing this OvalCollider
	@Override
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
		g.drawOval((int)(X() + xOff), (int)(Y() + yOff), (int)(2 * radius), (int)(2 * radius));
	}

}
