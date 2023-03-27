package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

//a class representing an object with a location and angle in space, usually relative to a parent Linkable, with the ability to be drawn on screen
//with the exception of Background objects, Linkable objects should always be passed as the argument of .link() or .linkE() to another Linkable before use
//the z-value can optionally be used for drawing order if linked is sorted
public abstract class Linkable extends Point implements Comparable<Linkable> {
	//the angle of this Linkable relative to its parent
	protected double ang;
	//the children of this Linkable, with locations relative to this one,
	//which are drawn after the parent
	protected List<Linkable> linked;
	//the parent of this Linkable, null if it has none
	protected Linkable parent;
	//whether this Linkable is updating, used to manage
	//when new Linkables are Linked
	protected boolean updating;
	//only used if a child tries to Link a new Linkable while this one is updating
	protected Queue<Linkable> linkQueue;
	//a Set containing all of the keyboard key currently pressed
	private Set<Integer> keysPressed;
	
	//constructs a new Linkable at (x, y) in 2d space
	public Linkable(double x, double y) {
		super(x, y);
		init();
	}
	
	//constructs a new Linkable at (x, y, z) in 3d space
	public Linkable(double x, double y, double z) {
		super(x, y, z);
		init();
	}
	
	//initializes various values used by this Linkable
	private void init() {
		updating = false;
		parent = null;
		linkQueue = new LinkedList<>();
		linked = new LinkedList<>();
		ang = 0;
	}
	
	//updates this Linkable and all of it's children
	public boolean updateAll() {
		updating = true;
		Iterator<Linkable> lItr = linked.iterator();
		while(lItr.hasNext()) {
			Linkable next = lItr.next();
			if(next.update()) lItr.remove();
			next.updateAll();
		}
		boolean updateVal = update();
		updating = false;
		while(!linkQueue.isEmpty()) {
			link(linkQueue.remove());
		}
		return updateVal;
	}
	
	//optional method, if implemented, should run any update functionality, 
	//and should return whether it should be unlinked from it's parent
	public boolean update() {return false;}
	
	//returns a Point representing the lower-right corner of the bounds of the screen (lower-right bc. it's positive x & y), 
	//these bounds aren't enforced by default, but this method can be used for something like screen-looping
	public Point getMaxBounds() {
		return parent.getMaxBounds();
	}
	
	//like getMaxBounds(), but returns the point representing the upper left corner of the bounds of the screen
	public Point getMinBounds() {
		return parent.getMinBounds();
	}
	
	//returns the location of this Linkable in global space
	public Point gatAbsLoc() {
		return new Point(getAbsX(), getAbsY());
	}
	
	//returns the z-value of this Linkable if it has one, or 0 otherwise
	public double getZ() {return get(2);}
	
	//returns the x value of this Linkable in global space
	public double getAbsX() {
		if(parent == null) {
			return X();
		} else {
			return (Math.cos(parent.ang) * X()) + (Math.sin(parent.ang) * Y()) + parent.getAbsX();
		}
	}
	
	//returns the y value of this Linkable in global space
	public double getAbsY() {
		if(parent == null) {
			return Y();
		} else {
			return (Math.cos(parent.ang) * Y()) + (Math.sin(parent.ang) * X()) + parent.getAbsY();
		}
	}
	
	//returns the angle of this Linkable in global space
	public double getAbsAng() {
		if(parent == null) {
			return ang;
		} else {
			return ang + parent.getAbsAng();
		}
	}
	
	protected void setKeysSet(Set<Integer> keysPressed) {
		this.keysPressed = keysPressed;
	}
	
	//links a Linkable to this one, so that it follows it,
	//every Linkable except a Background should be linked to another
	public void link(Linkable child) {
		child.keysPressed = this.keysPressed;
		if(updating) {
			linkQueue.add(child);
		} else {
			child.parent = this;
			linked.add(child);
			linked.sort(null);
		}
	}
	
	//unlinks a Linkable from this one, usually called if a Linkable should stop being drawn and updated
	public void unlink(Linkable child) {
		child.parent = null;
		linked.remove(child);
	}
	
	//compares the z values of two Linkables, altering them to resolve any conflicts,
	//so that sorting a list will order then based on drawing order
	public int compareTo(Linkable o) {
		switch((int)Math.copySign(1, Double.compare(getZ(), o.getZ()))) {
			case -1:
				return -1;
			case 1:
				return 1;
			case 0:
				if(vals.length > 2) {
					add(new Point(0, 0, 0.001));
				} else {
					double[] oldVals = vals;
					vals = new double[3];
					vals[0] = oldVals[0];
					vals[1] = oldVals[1];
					vals[2] = 0.001;
				}
				return 1;
			default:
				System.out.println("problem");
				return 1;
		}
		
	}
	
	//returns whether the given key is presses
	protected boolean getKey(int key) {
		return keysPressed.contains(key);
	}
	
	//returns the number of children of this Linkable
	public int size() {
		return linked.size();
	}
	
	//returns the angle of this Linkable relative to it's parent
	public double getRot() {return ang;}
	
	//returns a list of all children of this Linkable
	public List<Linkable> getLinked() {return linked;}
	
	//returns the parent of this Linkable
	public Linkable parent() {return parent;}
	
	//draws this Linkable, and all its children relative to it's parent
	public void drawAll(Graphics g) {
		draw(g, parent.getAbsX(), parent.getAbsY(), parent.getAbsAng());
		for(Linkable l : linked) {
			l.drawAll(g);
		}
	}
	
	//returns an Iterator for all of the children of this Linkable
	public Iterator<Linkable> getLinkedIterator() {
		return linked.iterator();
	}
	
	//the function which draws this Linkable, given a total x location, y location and angle
	protected abstract void draw(Graphics g, double xOff, double yOff, double angOff);
	
	//draws a polygon from points (relativeX, relativeY), with location offset in the x-axis by totalX, and in the y-axis by totalY/
	//and rotated around the offset location by titalAng radians, scaled by size, using Graphics g
	//doesn't work right now
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, int size, double[] relativeX, double[] relativeY) {
		g.setColor(Color.WHITE);
		int[] xLocs = new int[relativeX.length];
		int[] yLocs = new int[relativeX.length];
		
		Point newXLoc = (new Point(Math.cos(totalAng), Math.sin(totalAng)));
		Point newYLoc = newXLoc.getPerpendicular();
		
		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((relativeX[i] * size) + totalX);
			yLocs[i] = (int)((relativeY[i] * size) + totalX);
		}
		
//		g.drawOval((int)totalX - 5, (int)totalY - 5, 10, 10);
		g.drawPolygon(xLocs, yLocs, xLocs.length);
	}
	
	//returns whether Point test is within the bounds given by minBounds and maxBounds
	//maybe I should move this to Point
	
	//TODO: fix this
	public static boolean boundsContain(Point minBounds, Point maxBounds, Point origin, double ang,  Point test) {
		test = test.copy().sub(origin).rot(ang);
		return test.X() < maxBounds.X() && test.X() > minBounds.X() &&
				test.Y() < maxBounds.Y() && test.Y() > minBounds.Y();
	}
	public static boolean boundsContain(Point minBounds, Point maxBounds, Point test) {
		return test.X() < maxBounds.X() && test.X() > minBounds.X() &&
				test.Y() < maxBounds.Y() && test.Y() > minBounds.Y();
	}
	
	//draws a polygon from the points (relativeX, relativeY), with location offset in the x-axis by totalX, and in the y-axis by totalY/
	//and rotated around the offset location by titalAng radians, scaled by size, using Graphics g
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, int size, int[] relativeX, int[] relativeY) {
		int[] xLocs = new int[relativeX.length];
		int[] yLocs = new int[relativeX.length];
		
		Point newXLoc = (new Point(1, 0)).rot(totalAng);
		Point newYLoc = newXLoc.getPerpendicular();
		
		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((newXLoc.X() * relativeX[i] + newYLoc.X() * relativeY[i]) * size + totalX);
			yLocs[i] = (int)((newXLoc.Y() * relativeX[i] + newYLoc.Y() * relativeY[i]) * size + totalX);
		}
		
		g.drawPolygon(xLocs, yLocs, xLocs.length);
	}
	
	//draws a polygon from the Points points, with location offset in the x-axis by totalX, and in the y-axis by totalY/
	//and rotated around the offset location by titalAng radians, scaled by size, using Graphics g
	//closed determines whether the the first and last Points should be connected
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points, boolean closed) {
		Point[] newPoints = new Point[points.length];
		Point newXLoc = (new Point(-1, 0)).rot(totalAng);
		Point newYLoc = newXLoc.getPerpendicular();
		for(int i = 0; i < points.length; i++) {
			newPoints[i] = points[i].copy().matrixTransform(newXLoc, newYLoc).add(totalX, totalY);
//			System.out.println(newPoints[i]);
		}
		int[] xLocs = new int[points.length];
		int[] yLocs = new int[points.length];
		
		for(int i = 0; i < points.length; i++) {
			xLocs[i] = (int)newPoints[i].X();
			yLocs[i] = (int)newPoints[i].Y();
		}
		
		if(closed) {
			g.drawPolygon(xLocs, yLocs, xLocs.length);
		} else {
			for(int i = 0; i < xLocs.length - 1; i++) {
				g.drawLine(xLocs[i], yLocs[i], xLocs[i + 1], yLocs[i + 1]);
			}
		}
	}
	
	//closed determines whether the the first and last Points should be connected
	protected static void fillPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points) {
		int[] xLocs = new int[points.length];
		int[] yLocs = new int[points.length];
		
		for(int i = 0; i < points.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng) * points[i].X() + Math.sin(-totalAng) * points[i].Y()) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng) * points[i].X() + Math.cos(totalAng) * points[i].Y()) * size + totalY);
		}
		
		g.fillPolygon(xLocs, yLocs, xLocs.length);
	}
	
	//closed determines whether the the first and last Points should be connected
	protected static void fillPoints(Graphics g, double totalX, double totalY, double totalAng, double size, double[] xVals, double[] yVals) {
		int[] xLocs = new int[xVals.length];
		int[] yLocs = new int[yVals.length];
		
		for(int i = 0; i < xVals.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng) * xVals[i] + Math.sin(-totalAng) * yVals[i]) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng) * xVals[i] + Math.cos(totalAng) * yVals[i]) * size + totalY);
		}
		
		g.fillPolygon(xLocs, yLocs, xLocs.length);
	}
	
	//draws a polygon from points (relativeX, relativeY), with location offset in the x-axis by totalX, and in the y-axis by totalY/
	//and rotated around the offset location by titalAng radians, scaled by size, using Graphics g
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points) {
		drawPoints(g, totalX, totalY, totalAng, size, points, true);
	}
	
	//should be used for looping around the edge of the screen, given a maxX and maxY value, 
	//assuming the origin is in the center of the screen
	protected void loop(int maxX, int maxY) {
		if(Math.abs(X()) > maxX) {
			vals[0] *= -0.99;
		}
		if(Math.abs(Y()) > maxY) {
			vals[1] *= -0.99;
		}
	}
	
}
