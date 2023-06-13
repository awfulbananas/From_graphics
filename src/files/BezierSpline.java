package files;

import java.util.Arrays;

import fromics.Point;

public class BezierSpline {
	public static final double DIST_CALC_STEP = 0.001;
	public static final int DIST_CALC_DETAIL = 1000;
	private final Point[] coefficients;
	private final Point[] controlPoints;
	private double[] dists;
	private double[] distTPairs;
	private double totalDist;
	private boolean distancesCalculated;
	private int dims;
	
	protected boolean isShown;
	
	public BezierSpline(Point[] controlPoints) {
		this.controlPoints = controlPoints;
		dims = controlPoints[0].dims();
		for(int i = 1; i < controlPoints.length; i++) {
			if(controlPoints[i].dims() < dims) dims = controlPoints[i].dims();
		}
		coefficients = calculatePointCoefficients(controlPoints.length);
		isShown = true;
		distancesCalculated = false;
	}
	
	public BezierSpline(double[] values, Point origin) {
		controlPoints = new Point[values.length / 2];
		for(int i = 0; i < controlPoints.length; i++) {
			controlPoints[i] = new Point(values[2 * i] + origin.X(), values[2 * i + 1] + origin.Y());
		}
		dims = 2;
		coefficients = calculatePointCoefficients(controlPoints.length);
		isShown = true;
		distancesCalculated = false;
	}
	
	private Point[] calculatePointCoefficients(int points) {
		int[][] mat = new int[points][points];
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
		
		
		for(int[] arr : mat) {
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
	
	public Point getAtDist(double dist) {
		double t = getTAtDist(dist);
		Point loc = get(t);
		return loc;
	}
	
	public Point getStart() {
		return controlPoints[0].copy();
	}
	
	public Point getEnd() {
		return controlPoints[controlPoints.length - 1].copy();
	}
	
	public Point get(int n) {
		return controlPoints[n];
	}
	
	public String toString() {
		return "start: " + getStart() + "\nend:   " + getEnd();
	}
}
