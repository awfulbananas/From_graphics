package fromics;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Point is a class representing an n-dimensional Vector with double precision.
 * it can be used in any number of dimensions, though some operations only apply to the first two,
 * operations generally modify the Point they're called from, so if you don't want to modify the Point
 * then use the .copy() method
 * the first three dimensions are called X, Y, and Z respectively, and methods which reference
 * those refer to the corresponding dimension
 * in comments, when examples are given, Points are represented with parentheses around
 * an ordered list of each dimension in order, for example (2, 3) would have an x value of
 * 2.0, and a y value of 3.0, and (8, 7 ,9) would have an x value of 8.0, a y value of 7.0, and
 * a z value of 9.0
 * importantly, since this was made for use on screens, positive rotation is counter-clockwise
 * given that the x dimension increases to the right, and the y dimension increases downwards
 * this class can also be used completely independently of the rest of this library,
 * and is probably useful as such
 * @author Joseph Fromel
 */
public class Point {
	/*
	 *the array of values for the location of the Point,
	 *from lowest dimension to highest,
	 *ie. x-value is vals[0], y-value is vals[1], etc.
	 */
	protected double[] vals;

	/**
	 * constructs a new 2 dimensional Point at (0, 0)
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * constructs a new 2 dimensional Point at the given location
	 * @param x the initial x value of the Point
	 * @param y the initial y value of the Point
	 */
	public Point(double x, double y) {
		vals = new double[2];
		vals[0] = x;
		vals[1] = y;
	}

	/**
	 * constructs a new 3 dimensional Point at he given location
	 * @param x the initial x value of the Point
	 * @param y the initial y Value of the Point
	 * @param z the initial z value of the Point
	 */
	public Point(double x, double y, double z) {
		vals = new double[3];
		vals[0] = x;
		vals[1] = y;
		vals[2] = z;
	}

	/**
	 * constructs a new Point using the given array of doubles.
	 * the Point is initialized such that the x value is the
	 * first element of the array, the y value is the second element,
	 * and so on, or in other words, the Point is initialized as
	 * (vals[0], vals[1],...,vals[vals.length - 1])
	 * the array should be non-null, and it's length should be >= 2,
	 * or an IllegalArgumentException will be thrown.
	 * modifying the Point will also modify the array, so be careful with
	 * this constructor TODO: make this not modify the given array
	 * @param vals the array which the Point's dimensions are initialized to
	 */
	public Point(double[] vals) {
		if(vals.length < 2) {
			throw new IllegalArgumentException("dimension counts less than 2 not supported");
		}
		this.vals = vals;
	}

	/**
	 * constructs a new Point with the given number of dimensions, and each
	 * dimension initialized to 0.
	 * for example, new Point(2) would construct (0, 0), and new Point(5) would
	 * construct (0, 0, 0, 0, 0)
	 * @param dimensions the initial number of dimensions for this Point
	 */
	public Point(int dimensions) {
		if(dimensions < 2) {
			throw new IllegalArgumentException("dimension counts less than 2 not supported");
		}
		vals = new double[dimensions];
	}

	/**
	 * creates a copy of this Point with identical values in each dimension
	 * @return the created copy of this Point
	 */
	public Point copy() {
		Point newP = new Point(vals.length);
		for(int i = 0; i < vals.length; i++) {
			newP.set(i, vals[i]);
		}
		return newP;
	}

	/**
	 * sets the values of the dimensions shared between this Point
	 * and Point p to the values of Point p, then returns this Point
	 * @param p the Point to clone
	 * @return this Point
	 */
	public Point clone(Point p) {
		int length = Math.min(dims(), p.dims());
		for(int i = 0; i < length; i++) {
			this.vals[i] = p.vals[i];
		}
		return this;
	}

	/**
	 * adds a new dimension to this Point, and initializes that new dimension to 0.
	 * for example, calling addDim() on (2, 3) would make it (2, 3, 0),
	 * and calling addDim() on (5, 7, 4) would make it (5, 7, 4, 0)
	 * @return this Point after being modified
	 */
	public Point addDim() {
		double[] oldVals = vals;
		vals = new double[vals.length + 1];
		for(int i = 0; i < oldVals.length; i++) {
			vals[i] = oldVals[i];
		}
		vals[oldVals.length] = 0;
		return this;
	}

	/**
	 * removes the largest dimension from this Point.
	 * for example, calling remDim() on (2, 15, 7) would make it (2, 15),
	 * and calling remDim() on (12, 19, 11) would make it (12, 19)
	 * @return this Point after being modified
	 */
	public Point remDim() {
		if(vals.length <= 2) {
			throw new IllegalStateException();
		}
		double[] oldVals = vals;
		vals = new double[vals.length - 1];
		for(int i = 0; i < vals.length; i++) {
			vals[i] = oldVals[i];
		}
		return this;
	}

	/**
	 * returns the number of dimensions of this Point.
	 * for example, calling dims() on (3, 7, 4) would return 3,
	 * and calling dims() on (12, 4, 5, -7, 2) would return 5
	 * @return the number of dimensions of this Point
	 */
	public int dims() {
		return vals.length;
	}

	/**
	 * returns a String representation of this Point as a comma-separated
	 * list of values surrounded by parentheses. for example, (2.0,3.0,7.0), or
	 * (9.0,3.0). I would compare an example Point to the output of this method,
	 * but this returns in pretty much the same format as the examples in comments
	 * this method can also be used in conjunction with Point.fromString(String) to
	 * easily save and load Points from text files
	 * @return a String representation of this Point
	 */
	@Override
	public String toString() {
		String s = "(" + vals[0];
		for(int i = 1; i < vals.length; i++) {
			s += "," + vals[i];
		}
		return s + ")";
	}

	/**
	 * creates a new Point using the values from the String representation of a Point
	 * as returned from toString(). this can be used in conjunction with toString() to easily
	 * save and load Points from text files.
	 * this doesn't have any explicit format checking for the input, but it will not function
	 * as expected unless the input is exact
	 * @param data the String representation of a Point to be pared
	 * @return the Point constructed from the given String
	 */
	public static Point fromString(String data) {
		data = data.substring(1, data.length() - 1);
		String[] valStrings = data.split(",");
		double[] vals = new double[valStrings.length];
		for(int i = 0; i < valStrings.length; i++) vals[i] = Double.parseDouble(valStrings[i]);
		return new Point(vals);
	}

	/**
	 * calculates the magnitude of this Point, the same as the distance to the origin,
	 * slower than .smag()
	 * @return the magnitude of this Point
	 */
	public double mag() {
		double n = 0;
		for(double d : vals) {
			n += d * d;
		}
		return Math.sqrt(n);
	}

	/**
	 * calculates the magnitude of this Point squared,
	 * faster than .mag()
	 * @return the magnitude of this Point squared
	 */
	public double sMag() {
		double n = 0;
		for(double d : vals) {
			n += d * d;
		}
		return n;
	}

	/**
	 * returns the value of the first dimension of this Point,
	 * usually representing the x dimension
	 * @return the first dimension of this Point
	 */
	public double X() {
		return vals[0];
	}

	/**
	 * sets the value of the first dimension of this Point,
	 * usually representing the x dimension
	 * @param n the value to set the first dimension of this Point to
	 */
	public void setX(double n) {
		vals[0] = n;
	}

	/**
	 * returns the value of the second dimension of this Point,
	 * usually representing the y dimension
	 * @return the second dimension of this Point
	 */
	public double Y() {
		return vals[1];
	}

	/**
	 * sets the value of the second dimension of this Point,
	 * usually representing the y dimension
	 * @param n the value to set the second dimension of this Point to
	 */
	public void setY(double n) {
		vals[1] = n;
	}

	/**
	 * returns the value of the third dimension of this Point,
	 * usually representing the z dimension
	 * @return the third dimension of this Point
	 * @throws IllegalStateException if this Point doesn't have a third dimension
	 */
	public double Z() {
		if(vals.length < 2) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
		return vals[2];
	}

	/**
	 * sets the value of the third dimension of this Point,
	 * usually representing the z dimension
	 * @param n the value to set the third dimension of this Point to
	 */
	public void setZ(double n) {
		if(vals.length < 3) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
		vals[2] = n;
	}

	/**
	 * returns the value of a specific dimension of this Point.
	 * dim(0) is the same as X(), dim(1) is the same as Y(), and
	 * dim(2) is the same as Z
	 * @param dim the dimension of the Point to return
	 * @return the value of the given dimension, or 0 if the Point doesn't have the given dimension
	 * 		   and the given dimension is greater than 0
	 * @throws IndexOutOfBoundsException if the given dimension is less than 0
	 */
	public double get(int dim) {
		if(vals.length <= dim) {
			return 0;
		}
		return vals[dim];
	}

	/**
	 * sets a specific dimension of this Point to a given value
	 * @param dim the dimension to be set
	 * @param n the value to set the given dimension to
	 * @throws IndexOutOfBoundsException if dim is >= the number of dimensions
	 * 		   of this Point, or if dim is less than 0
	 */
	public void set(int dim, double n) {
		vals[dim] = n;
	}

	/**
	 * returns the distance between this Point and a given one,
	 * only in the shared dimensions of the two Points,
	 * ie. (new Point(1, 0, 2)).dist(new Point(-1, 0)) would return 0
	 * @param p the Point to get the distance to
	 * @return the distance to the given Point
	 */
	public double dist(Point p) {
		if(p.vals.length != this.vals.length) {
			throw new IllegalArgumentException("points to compare must have the same number of dimensions");
		}
		return p.copy().sub(this).mag();
	}

	/**
	 * normalizes this Point such that it's direction remains the same, but it's magnitude
	 * is one, then returns this Point
	 * @return this Point
	 */
	public Point normalize() {
		if(sMag() != 0) div(mag());
		return this;
	}

	/**
	 * divides all the dimensions of this Point by the given value,
	 * then returns this Point
	 * @param d the value to divide by
	 * @return this Point
	 */
	public Point div(double d) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] /= d;
		}
		return this;
	}

	/**
	 * divides each of this Point's dimensions by the dimensions of the
	 * given Point, using only the shared dimensions,
	 * then returns this Point
	 * ie. (new Point(8, 9, 7)).comDiv(new Point(4, 3)) would return (2, 3, 7)
	 * @param p the Point to divide by
	 * @return this Point
	 */
	public Point comDiv(Point p) {
		for(int i = 0; i < Math.min(p.vals.length, this.vals.length); i++) {
			this.vals[i] /= p.vals[i];
		}
		return this;
	}

	/**
	 * scales this Point by the given double value, multiplying all the dimensions
	 * by the given value, then returns this Point
	 * @param d the value to multiply by
	 * @return this Point
	 */
	public Point mult(double d) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] *= d;
		}
		return this;
	}

	/**
	 * scales this Point by the given long value, multiplying all the dimensions
	 * by the given value, then returns this Point
	 * @param l the value to multiply by
	 * @return this Point
	 */
	public Point mult(long l) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] *= l;
		}
		return this;
	}

	/**
	 * multiplies each of this Point's dimensions by the dimensions of the
	 * given Point, using only the shared dimensions,
	 * then returns this Point
	 * ie. (new Point(2, 3, 5)).comMult(new Point(4, 3)) would return (8, 9, 5)
	 * @param p the Point to divide by
	 * @return this Point
	 */
	public Point comMult(Point p) {
		for(int i = 0; i < Math.min(p.vals.length, this.vals.length); i++) {
			this.vals[i] *= p.vals[i];
		}
		return this;
	}

	/**
	 * subtracts the given Point from this Point, using only shared dimensions,
	 * then returns this Point
	 * @param p the Point to subtract from this one
	 * @return this Point
	 */
	public Point sub(Point p) {
		for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
			this.vals[i] -= p.vals[i];
		}
		return this;
	}

	/**
	 * subtracts the given values from this Point's x and y dimensions,
	 * then returns this Point.
	 * this is equivalent to .sub(new Point(x, y))
	 * @param x
	 * @param y
	 * @return
	 */
	public Point sub(double x, double y) {
		vals[0] -= x;
		vals[1] -= y;
		return this;
	}

	/**
	 * clamps this Point to a maximum length, doing nothing
	 * if it's length is lower, and setting it's length to the given value,
	 * while maintaining direction, if it's length is higher, then
	 * returns this Point
	 * @param maxLength the length to clamp to
	 * @return this Point
	 */
    public Point clampLength(double maxLength) {
        if(this.sMag() > maxLength * maxLength) {
            normalize();
            mult(maxLength);
        }
        return this;
    }

	/**
	 * returns the dot product between this Point and a given one.
	 * this can be used to get the distance along the "Axis" of one
	 * of the points, if it is normalized, and if both points are normalized,
	 * this is the same as the cosine of the angle between them
	 * @param p the Point to get the dot product between
	 * @return the dot product of this Point and the given one
	 * @throws IllegalArgumentException if the Points have a different number of dimensions
	 */
	public double dot(Point p) {
		if(p.vals.length != this.vals.length) {
			throw new IllegalArgumentException("dot product requires the same number of dimensions between points");
		}
		double sum = 0;
		for(int i = 0; i < vals.length; i++) {
			sum += this.vals[i] * p.vals[i];
		}
		return sum;
	}

	/**
	 * returns the dot product between this Point and another one,
	 * using only the first two dimensions, which is equivalent to .dot(p)
	 * if the Point have two dimensions to begin with
	 * @param p the Point to get the dot product between
	 * @return the dot product of the first two dimensions of this Point and the given one
	 */
	public double dot2d(Point p) {
		double sum = 0;
		for(int i = 0; i < 2; i++) {
			sum += this.vals[i] * p.vals[i];
		}
		return sum;
	}

	/**
	 * sets all the dimensions of this Point to the absolute value of those dimensions,
	 * then returns this Point
	 * @return this Point
	 */
	public Point abs() {
		for(int i = 0; i < vals.length; i++) {
			vals[i] = Math.abs(vals[i]);
		}
		return this;
	}

	/**
	 * add the given Point to this one, then returns this Point.
	 * the Point to be added must have the same number or less dimensions as this Point
	 * @param p the Point to be added to this one
	 * @return this Point
	 * @throws IllegalArgumentException if the given Point has more dimensions than this Point
	 */
	public Point add(Point p) {
		if(p.vals.length > this.vals.length) {
			throw new IllegalArgumentException("points to add must have the same number of dimensions or less");
		}
		for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
			this.vals[i] += p.vals[i];
		}
		return this;
	}

	/**
	 * adds the given x and y values to the corresponding dimensions of this Point,
	 * then returns this Point
	 * @param x the x value to add to this Point
	 * @param y the y value to add to this Point
	 * @return this Point
	 */
	public Point add(double x, double y) {
		vals[0] += x;
		vals[1] += y;
		return this;
	}

	/**
	 * applies a 2d matrix transformation to this Point, based on the given Points,
	 * then returns this Point.
	 * I don't really want to describe what specifically this does,
	 * so I'll leave it at "applying a space transformation"
	 * @param iHatLoc the relative new location of the iHat vector
	 * @param jHatLoc the relative new location of the jHat vector
	 * @return this Point
	 */
	public Point matrixTransform(Point iHatLoc, Point jHatLoc) {
		double newX = Y() * jHatLoc.X() + X() * iHatLoc.X();
		double newY = Y() * jHatLoc.Y() + X() * iHatLoc.Y();
		setX(newX);
		setY(newY);
		return this;
	}

	/**
	 * multiplies this Point by the given Point as if they're complex numbers in the form
	 * x + yi, then returns this Point.
	 * both Points must have exactly two dimensions
	 * @param o the Point to multiply this one by
	 * @return this Point
	 * @throws IllegalArgumentException if either Point has more than two dimensions
	 */
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

	/**
	 * divides this Point by the given Point, using only the first two dimensions,
	 * as if they're complex numbers in the form
	 * x + yi, then returns this Point.
	 * @param o the Point to divide this one by
	 * @return this Point
	 */
	public Point cDiv(Point o) {
		double oSqrd = o.X() * o.X() + o.Y() * o.Y();
		double newX = (X() * o.X() + Y() * o.Y()) / oSqrd;
		double newY = (Y() * o.X() - X() * o.Y()) / oSqrd;
		setX(newX);
		setY(newY);
		return this;
	}

	/**
	 * returns a new Point which is perpendicular to this one, rotated 90 degrees clockwise
	 * in screen space. (remember that positive y is down)
	 * @return a clockwise perpendicular Point to this one
	 */
	public Point getPerpendicular() {
		return new Point(-Y(), X());
	}

	/**
	 * rotates this Point counter-clockwise around the origin, using only the first two dimensions,
	 * by the given angle in radians, the returns this Point.
	 * why does getPerpendicular() go clockwise, and rot() go counter-clockwise?
	 * arbitrary decisions I should probably change later
	 * @param rot the number of radians to rotate by
	 * @return this Point
	 */
	public Point rot(double rot) {
		double oldX = X();
		double oldY = Y();
		setX(Math.cos(rot) * oldX + Math.sin(rot) * oldY);
		setY(Math.cos(rot) * oldY - Math.sin(rot) * oldX);
		return this;
	}

	/**
	 * clamps the dimensions of this Point to the given minimum and maximum values,
	 * setting each dimension to the value of min or max, if it's below min or above max
	 * respectively, then returns this Point.
	 * min and max must have the same number of dimensions
	 * @param min the minimum value for each dimension
	 * @param max the maximum value for each dimension
	 * @return this Point
	 */
	public Point clamp(Point min, Point max) {
		if(min.dims() != max.dims()) {
			throw new IllegalArgumentException("arguments must have the same number of dimensions");
		}
		for(int i = 0; i < dims() && i < min.dims(); i++) {
			if(vals[i] < min.vals[i]) vals[i] = min.vals[i];
			if(vals[i] > max.vals[i]) vals[i] = max.vals[i];
		}
		return this;
	}

	/**
	 * transforms the Point into the relative space of the given Point,
	 * then returns the Point. this is the same as .matrixTransform(space, space.getPerpendicular())
	 * @param space the Point to transform into the space of
	 * @return this Point
	 */
	public Point toSpace(Point space) {
		double oldX = X();
		double oldY = Y();
		setX(oldX * space.X() + oldY * space.Y());
		setY(oldY * space.X() - oldX * space.Y());
		return this;
	}

	/**
	 * returns whether this Point is equal to the given Point, meaning that they
	 * have the same number of dimensions, and the value of each dimension is exactly
	 * the same
	 * @param p the Point to compare to
	 * @return whether this Point is equal to the given Point
	 */
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

	/**
	 * returns the clockwise angle of this Point from (1, 0)
	 * @return the angle of this Point
	 */
	public double ang() {
		return Math.atan2(-Y(), X());
	}

	/**
	 * applies the given Function to each dimension of this Point, setting
	 * the value of that dimension to the value the function gives when given
	 * the old value of that dimension, the returns this Point
	 * @param f the Function to apply to the dimensions of this Point
	 * @return this Point
	 */
	public Point forEachDim(Function<Double, Double> f) {
		for(int i = 0; i < vals.length; i++) {
			vals[i] = f.apply(vals[i]);
		}
		return this;
	}

	/**
	 * applies the given function to each shared dimension of this Point and the given Point,
	 * and sets the corresponding dimension of this Point to the output,
	 * then returns this Point
	 * @param f the Function to apply to each pair of dimensions
	 * @param p the Point to get the second input of the Function for each dimension from
	 * @return this Point
	 */
	public Point forEachDims(BiFunction<Double, Double, Double> f, Point p) {
		for(int i = 0; i < Math.min(this.vals.length,p.vals.length); i++) {
			this.vals[i] = f.apply(this.vals[i], p.vals[i]);
		}
		return this;
	}

	/**
	 * returns whether this Point is within the given bounds, such that x and y dimensions of this Point
	 * are >= the corresponding dimension of boundsMin, and are <= the corresponding dimension of boundsMax
	 * @param boundsMin the minimum values of the bounds to check
	 * @param boundsMax the maximum value of the bounds to check
	 * @return whether this Point is within the given bounds
	 */
	public boolean isWithinBounds(Point boundsMin, Point boundsMax) {
		double minX;
		double maxX;
		double minY;
		double maxY;
		
		if(boundsMin.X() < boundsMax.X()) {
			minX = boundsMin.X();
			maxX = boundsMax.X();
		} else {
			maxX = boundsMin.X();
			minX = boundsMax.X();
		}
		
		if(boundsMin.Y() < boundsMax.Y()) {
			minY = boundsMin.Y();
			maxY = boundsMax.Y();
		} else {
			maxY = boundsMin.Y();
			minY = boundsMax.Y();
		}
		
		return minX <= X() && X() <= maxX && minY <= Y() && Y() <= maxY;
	}
}