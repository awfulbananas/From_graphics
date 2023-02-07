package fromics;

public abstract class PointCollider extends Collidable {

	public PointCollider(double x, double y) {
		super(x, y);
	}

	@Override
	public int getCollisionType() {
		return 4;
	}

	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case 1:
				return other.copy().sub(this).mag() < other.getWidth();
			case 4:
				return copy().sub(other).mag() < other.getWidth();
			default:
				return false;
		}
		//TODO: add other collision types
	}

	@Override
	public double getWidth() {
		return 0;
	}

	@Override
	public double getHeight() {
		return 0;
	}
}
