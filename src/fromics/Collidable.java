package fromics;

public abstract class Collidable extends Linkable {
	public static final int TYPE_OTHER = 0;//collision is something else, must be the one to implement collision
	public static final int TYPE_CIRCLE = 1;//collides in an oval around the Collidable
	public static final int TYPE_RECT = 2;//collides in a rectangle around the Collidable
	public static final int TYPE_COMPOSITE = 3;//collision is based multiple linked Collidables
	public static final int TYPE_POINT = 4;
	public static final int TYPE_POLYGON = 5;
	//TODO: make abstract classes for basic collision for types other than circle
	
	public Collidable(double x, double y) {
		super(x, y);
	}
	
	public abstract int getCollisionType();
	public abstract boolean check(Collidable other);
	public abstract double getWidth();
	public abstract double getHeight();
}
