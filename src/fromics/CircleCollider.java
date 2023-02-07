package fromics;

public abstract class CircleCollider extends Collidable {
	
	double radius;

	public CircleCollider(double x, double y, double radius) {
		super(x, y);
		this.radius = radius;
	}

	@Override
	public int getCollisionType() {
		return 1;
	}

	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case 1:
				return other.copy().sub(this).mag() < radius + other.getWidth();
			case 4:
				return other.copy().sub(this).mag() < radius;
			default:
				return false;
		}
		//TODO: add collision for other types
	}

	@Override
	public double getWidth() {
		return radius;
	}

	@Override
	public double getHeight() {
		return radius;
	}

}
