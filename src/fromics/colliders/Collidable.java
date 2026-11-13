package fromics.colliders;

import fromics.Linkable;
import fromics.Point;

/**
 * Collidable is an abstract class representing an object with position and the ability to detect collision with other Collidables
 */
public abstract class Collidable extends Linkable {
	/**
	 * constructs a new Collidable at the given position
	 * @param x the initial x position of this Collidable
	 * @param y the initial y position of this Collidable
	 */
	public Collidable(double x, double y) {
		super(x, y);
	}

	/**
	 *
	 * @return
	 */
	public abstract CollisionType getCollisionType();
	
	//returns whether this Collidable is colliding with Collidable other
	public abstract boolean check(Collidable other);

	public abstract boolean checkPoint(Point p);

	//returns the location which is colliding with the given Collidable, the specific
//	public abstract Point getColLoc(Collidable other);
}
