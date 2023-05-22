package files;

import fromics.Point;

public class BezierSpline {
	public static final int RESOLUTION = 1000;
	private final Point[] coefficients;
	private final Point[] controlPoints;
	
	public BezierSpline(Point[] controlPoints) {
		this.controlPoints = controlPoints;
		coefficients = calculatePointCoefficients(controlPoints.length);
	}
	
	public BezierSpline(double[] values, Point origin) {
		controlPoints = new Point[values.length / 2];
		for(int i = 0; i < controlPoints.length; i++) {
			controlPoints[i] = new Point(values[2 * i] + origin.X(), values[2 * i + 1] + origin.Y());
		}
		
		coefficients = calculatePointCoefficients(controlPoints.length);
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
		
		for(int i = 0; i < points; i++) {
			Point coefficient = new Point(0, 0);
			for(int j = 0; j <= i; j++) {
				coefficient.add(controlPoints[j].copy().mult(mat[i][j]));
			}
			coefficients[i] = coefficient;
		}
		return coefficients;
	}
	
	public Point get(double t) {
		if(t == 0){
			return controlPoints[0];
		} else if(t == 1) {
			return controlPoints[controlPoints.length - 1];
		} else {
			Point loc = new Point(0, 0);
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
	
	public Point getStart() {
		return controlPoints[0].copy();
	}
	
	public Point getEnd() {
		return controlPoints[controlPoints.length - 1].copy();
	}
	
	public Point get(int n) {
		return controlPoints[n];
	}
}
