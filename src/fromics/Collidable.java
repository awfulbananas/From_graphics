package fromics;

//a class representing a Linkable which has some kind of collision
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
	
	//creates a new Collidable at (x, y)
	public Collidable(double x, double y) {
		super(x, y);
	}
	
	//returns the type of collision of this Collidable
	//which are defined at the top of this class
	public abstract int getCollisionType();
	
	//returns whether this Collidable is colliding with Collidable other
	public abstract boolean check(Collidable other);
}
