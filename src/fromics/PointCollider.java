package fromics;

//a class representing a Collidable with Point collision
public abstract class PointCollider extends Collidable {
	
	//creates a new PointCollider at (x, y)
	public PointCollider(double x, double y) {
		super(x, y);
	}

	//returns the collision type of this Collidable,
	//which is Collidable.TYPE_POINT
	@Override
	public int getCollisionType() {
		return Collidable.TYPE_POINT;
	}
	
	//returns whether this Collidable is colliding with Collidable other
	//this is only Collision type which currently collides with all the other ones
	//because Points are easy to code collision logic for
	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case Collidable.TYPE_OVAL:
				return other.copy().sub(this).mag() < ((OvalCollider)other).getRadius();
			case Collidable.TYPE_POINT:
				return copy().sub(other).mag() == 0;
			case Collidable.TYPE_POLYGON:
				return other.check(this);
			default:
				return false;
		}
	}
}
