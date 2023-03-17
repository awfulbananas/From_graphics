package fromics;

import java.util.function.Consumer;
//a class representing either a vector or a Point in space in 2 or more dimensions with double precision
//with operations for manipulating them

//most operations affect the point they're called on, so for non-destructive operations, use .copy()

//when clockwise or counterclockwise is mentioned, keep in mind that in java, positive y is down, not up
public class Point {
	//the array of values for the location of the Point,
	//from lowest dimension to highest, 
	//ie. x-value is vals[0], y-value is vals[1], etc.
	protected double[] vals;
	
	//constructs a 2d Point at (0, 0)
	public Point() {
		this(0, 0);
	}
	
	//constructs a 2d Point at (x, y)
	public Point(double x, double y) {
		vals = new double[2];
		vals[0] = x;
		vals[1] = y;
	}
	
	//constructs a 3d Point at (x, y, z)
	public Point(double x, double y, double z) {
		vals = new double[3];
		vals[0] = y;
		vals[1] = y;
		vals[2] = z;
	}
	
	//constructs a Point with the given array of values as it'a location,
	//with the number of dimensions equal to the length of the array
	public Point(double[] vals) {
		if(vals.length < 2) {
			throw new IllegalArgumentException("dimension counts less than 2 not supported");
		}
		this.vals = vals;
	}
	
	//constructs a Point with the given number of dimensions, with each position as 0
	public Point(int dimensions) {
		if(dimensions < 2) {
			throw new IllegalArgumentException("dimension counts less than 2 not supported");
		}
		vals = new double[dimensions];
	}
	
	//returns a copy of this Point
	public Point copy() {
		return new Point(vals.clone());
	}
	
	//adds another dimension to this Point, initializing the value of that dimension to 0
	public Point addDim() {
		double[] oldVals = vals;
		vals = new double[vals.length + 1];
		for(int i = 0; i < oldVals.length; i++) {
			vals[i] = oldVals[i];
		}
		vals[oldVals.length] = 0;
		return this;
	}
	
	//returns a String representation of this Point
	//in the format (x, y) for 2 dimensions, (x, y, z) for 3 dimensions, etc.
	@Override
	public String toString() {
		String s = "(" + vals[0];
		for(int i = 1; i < vals.length; i++) {
			s += ", " + vals[i];
		}
		return s + ")";
	}
	
	//returns the distance from the origin of this Point, of it's length as a vector
	public double mag() {
		double n = 0;
		for(double d : vals) {
			n += d * d;
		}
		return Math.sqrt(n);
	}
	
	//returns the distance from the origin of this point squared, and runs faster than mag()
	public double sMag() {
		double n = 0;
		for(double d : vals) {
			n += d * d;
		}
		return n;
	}
	
	//return this Point's x-value
	public double X() {
		return vals[0];
	}
	
	//sets this Point's x-value to n
	public void setX(double n) {
		vals[0] = n;
	}
	
	//return this Point's y-value
	public double Y() {
		return vals[1];
	}
	
	//sets this Point's y-value to n
	public void setY(double n) {
		vals[1] = n;
	}
	
	//return this Point's z-value if it has one
	public double Z() {
		if(vals.length < 2) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
		return vals[2];
	}
	
	//sets this Point's z-value to n if it has a z-value
	public void setZ(double n) {
		if(vals.length < 2) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
		vals[2] = n;
	}
	
	//gets the value of dimension dim of this Point, where dimension 0 is x, dimension 1 is y, etc.
	public double get(int dim) {
		if(vals.length <= dim) {
			return 0;
		}
		return vals[dim];
	}
	
	//gets the value of dimension dim of this Point to n, where dimension 0 is x, dimension 1 is y, etc.
	public void set(int dim, double n) {
		vals[dim] = n;
	}
	
	//gets the distance between this Point and Point p
	public double dist(Point p) {
		if(p.vals.length != this.vals.length) {
			throw new IllegalArgumentException("points to compare must have the same number of dimensions");
		}
		return p.copy().sub(this).mag();
	}
	
	//sets this point to a vector of length 1 going in the same direction
	//then returns this point
	public Point normalize() {
		div(mag());
		return this;
	}
	
	//divides all this Point's values by d
	//returnsthis Point
	public Point div(double d) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] /= d;
		}
		return this;
	}
	
	//multiplies all this Point's valued by d 
	//returns this Point
	public Point mult(double d) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] *= d;
		}
		return this;
	}
	
	//subtracts Point p from this Point. if p has a different number of dimensions,
	//then only the shared dimensions are subtracted
	public Point sub(Point p) {
		for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
			this.vals[i] -= p.vals[i];
		}
		return this;
	}
	
	//subtracts x from the x-value of this Point, and y from the y-value of this Point
	public Point sub(double x, double y) {
		vals[0] -= x;
		vals[1] -= y;
		return this;
	}
	
	//returns the dot product between this Point and Point p
	//if one of the Points is normalized, this can be thought of as getting
	//the other Point's distance along the axis along that Point
	//if both Points are normalized, this can be used to get the cosine of the angle between them
	public double dot(Point p) {
		if(p.vals.length != this.vals.length) {
			throw new IllegalArgumentException("dot product requires the same number of dimensions between points");
		}
		int sum = 0;
		for(int i = 0; i < vals.length; i++) {
			sum += this.vals[i] * p.vals[i];
		}
		return sum;
	}
	
	//sets all of this Point's values to the absolute of that value
	//returns this Point
	public Point abs() {
		for(int i = 0; i < vals.length; i++) {
			vals[i] = Math.abs(vals[i]);
		}
		return this;
	}
	
	//adds Point p to this Point. if p has a different number of dimensions to this one,
	//only the shared dimensions are added.
	//returns this Point
	public Point add(Point p) {
		if(p.vals.length > this.vals.length) {
			throw new IllegalArgumentException("points to add must have the same number of dimensions or less");
		}
		for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
			this.vals[i] += p.vals[i];
		}
		return this;
	}
	
	//adds x to the x-value of this Point, and y to the y-value of this Point
	public Point add(double x, double y) {
		vals[0] += x;
		vals[1] += y;
		return this;
	}
	
	//applies a 2d linear transformation to this Point, where the transformed location of (1, 0)
	//is iHatLoc, and the transformed location of (0, 1) is jHatLoc, and is also equivalent to
	//multiplying the matrix with iHatLoc and jHatLoc as it's columns by this Vector
	//returns this Point
	public Point matrixTransform(Point iHatLoc, Point jHatLoc) {
		double newX = Y() * jHatLoc.X() + X() * iHatLoc.X();
		double newY = Y() * jHatLoc.Y() + X() * iHatLoc.Y();
		setX(newX);
		setY(newY);
		return this;
	}
	
	//multiplies this Point by Point o as if they are complex numbers in the
	//format x + yi, then returns this Point.
	//throws an IllegalArgumentException if either Point has more than 2 dimensions
	public Point cMult(Point o) {
		if(o.vals.length > 2 || this.vals.length > 2) {
			throw new IllegalArgumentException();
		}
		double newX = X() * o.X() - Y() * o.Y();
		double newY = X() * o.Y() + Y() * o.X();
		setX(newX);
		setY(newY);
		return this;
	}
	
	//divides this Point by Point o as if they are complex numbers in the
	//format x + yi, then returns this Point.
	//throws an IllegalArgumentException if either Point has more than 2 dimensions
	public Point cDiv(Point o) {
		double oSqrd = o.X() * o.X() + o.Y() * o.Y();
		double newX = (X() * o.X() + Y() * o.Y()) / oSqrd;
		double newY = (Y() * o.X() - X() * o.Y()) / oSqrd;
		setX(newX);
		setY(newY);
		return this;
	}
	
	//returns a new Point rotated 90 degrees counterclockwise
	//around the origin from this one, with the same magnitude
	public Point getPerpendicular() {
		return new Point(Y(), -X());
	}
	
	//rotates this Point rot degrees around the origin clockwise,
	//then returns this Point
	public Point rot(double rot) {
		double unitX = Math.cos(rot);
		double unitY = Math.sin(-rot);
		double oldX = X();
		double oldY = Y();
		setX(oldX * unitX + oldY * unitY);
		setY(oldY * unitX - oldX * unitY);
		return this;
	}
	
	//transforms this Point into the space of Point space, then returns this Point
	//this Point is also multiplied by the magnitude of space in the process, 
	//so if you want to avoid this, normalize space first
	public Point toSpace(Point space) {
		double oldX = X();
		double oldY = Y();
		setX(oldX * space.X() + oldY * space.Y());
		setY(oldY * space.X() - oldX * space.Y());
		return this;
	}
	
	//returns whether this Point is equal to Point p
	//only returns true if they have the same number of dimensions,
	//and all values are equal between them
	public boolean equals(Point p) {
		if(p.vals.length != this.vals.length) {
			return false;
		}
		for(int i = 0; i < vals.length; i++) {
			if(this.vals[i] != p.vals[i]) {
				return false;
			}
		}
		return true;
	}
	
	//returns the angle from (0, 0) to this Point
	public double ang() {
		return Math.atan2(Y(), X());
	}
	
	//accepts this Point with Consumer<Point> c
	public void transform(Consumer<Point> c) {
		c.accept(this);
	}
}