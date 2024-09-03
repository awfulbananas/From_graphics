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
			case Collidable.TYPE_OVAL, Collidable.TYPE_POLYGON:
				return other.check(this);
			case Collidable.TYPE_POINT:
				return copy().sub(other).sMag() == 0;
            default:
				return false;
		}
	}
}
