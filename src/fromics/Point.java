package fromics;

import java.util.function.Consumer;

public class Point {
		private double[] vals;
		protected double x;
		protected double y;
		protected double z;
		
		public Point() {
			this(0, 0);
		}
		
		public Point(double x, double y) {
			vals = new double[2];
			vals[0] = x;
			vals[1] = y;
			cal();
		}
		
		public Point(double x, double y, double z) {
			vals = new double[3];
			vals[0] = x;
			vals[1] = y;
			vals[2] = z;
			cal();
		}
		
		public Point(double[] vals) {
			if(vals.length < 2) {
				throw new IllegalArgumentException("dimension counts less than 2 not supported");
			}
			this.vals = vals;
			cal();
		}
		
		public Point(int dimensions) {
			if(dimensions < 2) {
				throw new IllegalArgumentException("dimension counts less than 2 not supported");
			}
			vals = new double[dimensions];
		}
		
		public Point copy() {
			return new Point(vals);
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
		
		public double getX() {
			return x;
		}
		
		public void setX(double n) {
			x = n;
			vals[0] = n;
		}
		
		public double getY() {
			return y;
		}
		
		public void setY(double n) {
			y = n;
			vals[1] = n;
		}
		
		public double getZ() {
			return z;
		}
		
		public void setZ(double n) {
			z = n;
			vals[2] = n;
		}
		
		public double get(int dim) {
			return vals[dim];
		}
		
		public void set(int dim, double n) {
			vals[dim] = n;
			cal();
		}
		
		public double dist(Point p) {
			if(p.vals.length != this.vals.length) {
				throw new IllegalArgumentException("points to compare must have the same number of dimensions");
			}
			return p.copy().sub(this).mag();
		}
		
		public Point normalize() {
			div(mag());
			cal();
			return this;
		}
		
		public Point div(double d) {
			for(int i = 0; i < vals.length; i++) {
				vals[i] /= d;
			}
			cal();
			return this;
		}
		
		public Point mult(double d) {
			for(int i = 0; i < vals.length; i++) {
				vals[i] *= d;
			}
			cal();
			return this;
		}
		
		private void cal() {
			x = vals[0];
			y = vals[1];
			if(vals.length > 2) {
				z = vals[2];
			}
		}
		
		public Point sub(Point p) {
			if(p.vals.length != this.vals.length) {
				throw new IllegalArgumentException("points to compare must have the same number of dimensions");
			}
			for(int i = 0; i < vals.length; i++) {
				this.vals[i] -= p.vals[i];
			}
			cal();
			return this;
		}
		
		public double dot(Point p) {
			if(p.vals.length > this.vals.length) {
				throw new IllegalArgumentException("points to subtract must have the same number of dimensions or less");
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
			cal();
			return this;
		}
		
		public Point add(Point p) {
			if(p.vals.length > this.vals.length) {
				throw new IllegalArgumentException("points to add must have the same number of dimensions or less");
			}
			for(int i = 0; i < vals.length; i++) {
				this.vals[i] += p.vals[i];
			}
			cal();
			return this;
		}
		
		public Point add(double x, double y) {
			vals[0] += x;
			vals[1] += y;
			cal();
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