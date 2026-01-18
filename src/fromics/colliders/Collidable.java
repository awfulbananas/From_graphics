package fromics.colliders;

import fromics.Linkable;

/**
 * Collidable is an abstract class representing an object with position and the ability to detect collision with other Collidables
 */
public abstract class Collidable extends Linkable {
	//collision types:

	//collision is something else, must be the one to implement collision
	//there aren't any built-in classes for this type
	public static final int TYPE_OTHER = 0;
	
	//collides in an oval around the Collidable
	public static final int TYPE_OVAL = 1;
	
	//collides in a rectangle around the Collidable     only circles for now, other ovals not implemented yet
	public static final int TYPE_RECT = 2;
	
	//collision is based multiple linked Collidables    not implemented yet
	public static final int TYPE_COMPOSITE = 3;
	
	//collision is based on a single Point
	public static final int TYPE_POINT = 4;
	
	//collision is based on a polygon represened by a list of Points
	public static final int TYPE_POLYGON = 5;

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
	public abstract int getCollisionType();
	
	//returns whether this Collidable is colliding with Collidable other
	public abstract boolean check(Collidable other);

	//returns the location which is colliding with the given Collidable, the specific
//	public abstract Point getColLoc(Collidable other);
}
