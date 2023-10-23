package files;

import java.util.Arrays;

import fromics.Point;

//a class representing a bezier spline using a set of control points which are 
//generally assumed to remain static
public class BezierSpline {
	//the step used when calculating the distances along the spline
	public static final double DIST_CALC_STEP = 0.001;
	//the detail used when calculating the distances along the spline,
	//which is just the inverse of the above constant precalculated for more precision
	public static final int DIST_CALC_DETAIL = 1000;
	//the coefficients for each control point to be multiplied by when calculating a point on the spline
	private final Point[] coefficients;
	//the control points used to define the spline
	private final Point[] controlPoints;
	//a list of distances along the spline, used to calculate points on the spline
	//which are a specific distance along the curve
	private double[] dists;
	//the t values for the distances along the spline
	private double[] distTPairs;
	//the total distance of the spline
	private double totalDist;
	//whether or not the distances along the spline has been calculated,
	//the distances are not calculated unless needed for performance
	private boolean distancesCalculated;
	//the number of dimensions of the spline
	//(generalization, yay)
	private int dims;
	
	//constructs a new BezierSpline with the given control points
	public BezierSpline(Point[] controlPoints) {
		this.controlPoints = controlPoints;
		dims = controlPoints[0].dims();
		for(int i = 1; i < controlPoints.length; i++) {
			if(controlPoints[i].dims() < dims) dims = controlPoints[i].dims();
		}
		coefficients = calculatePointCoefficients(controlPoints.length);
		distancesCalculated = false;
	}
	
	//constructs a new BezierSpline with the given origin and
	//array of pairs of x and y offsets from that origin
	public BezierSpline(double[] values, Point origin) {
		controlPoints = new Point[values.length / 2];
		for(int i = 0; i < controlPoints.length; i++) {
			controlPoints[i] = new Point(values[2 * i] + origin.X(), values[2 * i + 1] + origin.Y());
		}
		dims = 2;
		coefficients = calculatePointCoefficients(controlPoints.length);
		distancesCalculated = false;
	}
	
	//returns the coefficients for a bezier spline with the given number of control points
	//this could be precalculated, but it's not that slow like this
	private Point[] calculatePointCoefficients(int points) {
		long[][] mat = new long[points][points];
		Point[] coefficients = new Point[points];
		
		for(int i = 0; i < points; i++) {
			mat[i][0] = 1;
			mat[i][i] = 1;
			for(int j = 1; j < i; j++) {
				mat[i][j] = mat[i-1][j-1] + mat[i-1][j];
			}
		}
		
		for(int i = 0; i < points; i++) {
			for(int j = 0; j <= i; j++) {
				mat[i][j] *= mat[points - 1][i];
				if((i + j) % 2 == 1) mat[i][j] *= -1;
			}
		}
		
		
		for(long[] arr : mat) {
			System.out.println(Arrays.toString(arr));
		}
		
		
		for(int i = 0; i < points; i++) {
			Point coefficient = new Point(dims);
			for(int j = 0; j <= i; j++) {
				coefficient.add(controlPoints[j].copy().mult(mat[i][j]));
			}
			coefficients[i] = coefficient;
		}
		return coefficients;
	}
	
	//returns the point along the spline for the given value of t,
	//where t=0 is the start of the spline, and t=1 is the end.
	//t can be outside of that range, but the spline often approaches extreme values
	public Point get(double t) {
		if(t == 0){
			return controlPoints[0].copy();
		} else if(t == 1) {
			return controlPoints[controlPoints.length - 1].copy();
		} else {
			Point loc = new Point(dims);
			for(int i = 0; i < coefficients.length; i++) {
				Point coefficient = coefficients[i].copy();
				for(int j = 0; j < i; j++) {
					coefficient.mult(t);
				}
				loc.add(coefficient);
			}
			return loc;
		}
	}
	
	//calculates approximate distances along the spline from the start of the spline, 
	//and their corresponding t values
	public double calculateDistances() {
		if(!distancesCalculated) {
			Point prev = getStart();
			distTPairs = new double[DIST_CALC_DETAIL + 1];
			dists = new double[DIST_CALC_DETAIL + 1];
			dists[0] = 0;
			distTPairs[0] = 0;
			double distSum = 0;
			for(int i = 1; i <= DIST_CALC_DETAIL; i++ ) {
				double currentT = ((double)i) * DIST_CALC_STEP;
				Point cur = get(currentT);
				distSum += cur.copy().sub(prev).mag();
				dists[i] = distSum;
				distTPairs[i] = currentT;
				prev = cur;
			}
			totalDist = distSum;
			distancesCalculated = true;
		}
		
		System.out.println(totalDist);
		
		return totalDist;
	}
	
	//returns the t value approximately at the given distance along the spline
	public double getTAtDist(double dist) {
		if(!distancesCalculated) calculateDistances();
		double pivot = ((double)dists.length) / 2.0;
		double change = pivot / 2;
		while(true) {
			int lowerIndex = (int)Math.floor(pivot);
			int upperIndex = (int)Math.ceil(pivot);
			double lowerCheck = dists[lowerIndex];
			double upperCheck = dists[upperIndex];
			if(upperCheck == lowerCheck) return pivot;
			if(dist < lowerCheck) {
				pivot -= change;
				change /= 2;
			} else if(dist > upperCheck) {
				pivot += change;
				change /= 2;
			} else {
				double lowerT = distTPairs[lowerIndex];
				double upperT = distTPairs[upperIndex];
				double finalT = ((upperT - lowerT)/(upperCheck - lowerCheck))*(dist - lowerCheck) + lowerT;
				return finalT;
			}
		}
	}
	
	//returns the point at the given distance along the spline
	public Point getAtDist(double dist) {
		double t = getTAtDist(dist);
		Point loc = get(t);
		return loc;
	}
	
	//returns the starting location of the spline
	public Point getStart() {
		return controlPoints[0].copy();
	}
	
	//returns the ending location of the spline
	public Point getEnd() {
		return controlPoints[controlPoints.length - 1].copy();
	}
	
	//returns the nth control point of the spline
	public Point get(int n) {
		return controlPoints[n].copy();
	}
	
	//returns a String representation of the spline as the start and end locations
	public String toString() {
		return "start: " + getStart() + "\nend:   " + getEnd();
	}
}
