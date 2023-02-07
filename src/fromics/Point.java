package fromics;

import java.util.function.Consumer;

public class Point {
		protected double[] vals;
		
		public Point() {
			this(0, 0);
		}
		
		public Point(double x, double y) {
			vals = new double[2];
			vals[0] = x;
			vals[1] = y;
		}
		
		public Point(double x, double y, double z) {
			vals = new double[3];
			vals[0] = y;
			vals[1] = y;
			vals[2] = z;
		}
		
		public Point(double[] vals) {
			if(vals.length < 2) {
				throw new IllegalArgumentException("dimension counts less than 2 not supported");
			}
			this.vals = vals;
		}
		
		public Point(int dimensions) {
			if(dimensions < 2) {
				throw new IllegalArgumentException("dimension counts less than 2 not supported");
			}
			vals = new double[dimensions];
		}
		
		public Point copy() {
			return new Point(vals.clone());
		}
		
		@Override
		public String toString() {
			String s = "(" + vals[0];
			for(int i = 1; i < vals.length; i++) {
				s += ", " + vals[i];
			}
			return s + ")";
		}
		
		public double mag() {
			double n = 0;
			for(double d : vals) {
				n += d * d;
			}
			return Math.sqrt(n);
		}
		
		public double X() {
			return vals[0];
		}
		
		public void setX(double n) {
			vals[0] = n;
		}
		
		public double Y() {
			return vals[1];
		}
		
		public void setY(double n) {
			vals[1] = n;
		}
		
		public double Z() {
			if(vals.length < 2) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
			return vals[2];
		}
		
		public void setZ(double n) {
			if(vals.length < 2) throw new IllegalStateException("z-value requires a point with at least 3 dimensions");
			vals[2] = n;
		}
		
		public double get(int dim) {
			return vals[dim];
		}
		
		public void set(int dim, double n) {
			vals[dim] = n;
		}
		
		public double dist(Point p) {
			if(p.vals.length != this.vals.length) {
				throw new IllegalArgumentException("points to compare must have the same number of dimensions");
			}
			return p.copy().sub(this).mag();
		}
		
		public Point normalize() {
			div(mag());
			return this;
		}
		
		public Point div(double d) {
			for(int i = 0; i < vals.length; i++) {
				vals[i] /= d;
			}
			return this;
		}
		
		public Point mult(double d) {
			for(int i = 0; i < vals.length; i++) {
				vals[i] *= d;
			}
			return this;
		}
		
		public Point sub(Point p) {
			for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
				this.vals[i] -= p.vals[i];
			}
			return this;
		}
		
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
		
		public Point abs() {
			for(int i = 0; i < vals.length; i++) {
				vals[i] = Math.abs(vals[i]);
			}
			return this;
		}
		
		public Point add(Point p) {
			if(p.vals.length > this.vals.length) {
				throw new IllegalArgumentException("points to add must have the same number of dimensions or less");
			}
			for(int i = 0; i < Math.min(vals.length, p.vals.length); i++) {
				this.vals[i] += p.vals[i];
			}
			return this;
		}
		
		public Point add(double x, double y) {
			vals[0] += x;
			vals[1] += y;
			return this;
		}
		
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
		
		public void transform(Consumer<Point> c) {
			c.accept(this);
		}
	}