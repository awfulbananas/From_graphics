package fromics;

import java.awt.Graphics;
import java.util.Arrays;

//a class representing a Collidable with polygon collision
public abstract class PolygonCollider extends Collidable {
	private Point[] shape;
	Point maxBounds;
	Point minBounds;
	protected double size;
	
	//constructs a new PolygonCollider at (x, y)
	//make sure to call .init() at some point if using this constructor
	protected PolygonCollider(double x, double y) {
		super(x, y);
	}
	
	//initializes this PolygonCollider with points (xVals, yVals), scaled by size
	protected void init(double[] xVals, double[] yVals, double size) {
		this.size = size;
		shape = new Point[xVals.length];
		for(int i = 0; i < xVals.length; i++) {
			shape[i] = new Point(xVals[i] * size, yVals[i] * size);
		}
		maxBounds = shape[0].copy();
		minBounds = shape[0].copy();
		for(int i = 0; i < shape.length; i++) {
			Point cur = shape[i];
			if(Double.isNaN(cur.X()) || Double.isNaN(cur.Y())) {
				continue;
			}
			if(cur.X() > maxBounds.X()) {
				maxBounds.setX(cur.X());
			}
			if(cur.X() < minBounds.X()) {
				minBounds.setX(cur.X());
			}
			if(cur.Y() > maxBounds.Y()) {
				maxBounds.setY(cur.Y());
			}
			if(cur.Y() < minBounds.Y()) {
				minBounds.setY(cur.Y());
			}
		}
	}
	
	//constructs a new PolygonCollider at (x, y) with points (xVals, yVals) scaled by size
	public PolygonCollider(int x, int y, double[] xVals, double[] yVals, double size) {
		super(x, y);
		init(xVals, yVals, size);
	}
	
	//returns whether the shape of this polygon contains Point p
	public boolean shapeContains(Point p) {
//		if(!Linkable.boundsContain(minBounds, maxBounds, this, ang, p)) return false;
		
		int hits = 0;
		
		Point prev = shape[shape.length - 1];
		Point cur;
		
		for (int i = 0; i < shape.length; prev = cur, i++) {
            cur = shape[i].copy();
            
            if(p.Y() > Math.max(cur.Y(), prev.Y())) {
            	continue;
            }
            
            double leftX;
            double rightX;
            if(cur.X() < prev.X()) {
            	leftX = cur.X();
            	rightX = prev.X();
            } else {
            	leftX = prev.X();
            	rightX = cur.X();
            }
            
            if(leftX == rightX) {
            	continue;
            }
            
            if(p.X() > rightX || p.X() < leftX) {
            	continue;
            }
            
            double yVal = (p.X() - cur.X()) * ((cur.Y() - prev.Y()) / (cur.X() - prev.X())) + cur.Y();
            
            if(p.Y() < yVal) {
            	hits++;
            }
        } 
		
        return ((hits & 1) != 0);
	}
	
	//returns the collision type of this Collidable
	//which is Collidable.TYPE_POLYGON
	@Override
	public int getCollisionType() {
		return 5;
	}
	
	//returns whether this Collidable is colliding with Collidable other
	@Override
	public boolean check(Collidable other) {
		switch(other.getCollisionType()) {
			case Collidable.TYPE_POINT:
				return shapeContains(other.copy().sub(this));
			case Collidable.TYPE_POLYGON:
				for(Point p : ((PolygonCollider)other).absPoints()) {
					if(shapeContains(p.copy().sub(this))) return true;
				}
				for(Point p : this.absPoints()) {
					if(((PolygonCollider)other).shapeContains(p.copy().sub(other))) return true;
				}
				return false;
			default:
				return false;
		}
	}
	
	//returns the Points of this PolygonCollider in world space
	public Point[] absPoints() {
		Point[] absPoints = new Point[shape.length];
		for(int i = 0; i < shape.length; i++) {
			absPoints[i] = shape[i].copy().rot(ang).add(this);
		}
		return absPoints;
	}
	
	//draw this PolygonCollider, draw the collision polygon by default
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		drawCollider(g, xOff, yOff, angOff);
	}
	
	//draws the Collider of this PolygonCollider using Graphics g
	protected void drawCollider(Graphics g, double xOff, double yOff, double angOff) {
		drawPoints(g, xOff, yOff, angOff, 1, shape);
	}

}
 