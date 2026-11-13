package fromics.colliders;

import fromics.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

//a class representing a Collidable with Point collision
public class PointCollider extends Collidable {
	
	//creates a new PointCollider at (x, y)
	public PointCollider(double x, double y) {
		super(x, y);
	}

	//returns the collision type of this Collidable,
	//which is Collidable.TYPE_POINT
	@Override
	public CollisionType getCollisionType() {
		return CollisionType.POINT;
	}
	
	//returns whether this Collidable is colliding with Collidable other
	//this is only Collision type which currently collides with all the other ones
	//because Points are easy to code collision logic for
	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case POINT:
				return copy().sub(other).sMag() == 0;
			default:
				return other.check(this);
		}
	}

	@Override
	public boolean checkPoint(Point p) {
		return equals(p);
	}

	@Override
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
		g.fillOval((int)X() - 1, (int)Y() - 1, 3, 3);
	}
}
