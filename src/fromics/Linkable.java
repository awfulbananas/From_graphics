package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Linkable extends Point implements Comparable<Linkable> {
	//the angle of this Linkable relative to its parent
	protected double ang;
	//the children of this Linkable, needs to be sorted to use z ordering
	protected List<Linkable> linked;
	//the parent of this Linkable, null if it has none
	protected Linkable parent;
	
	public Linkable(double x, double y) {
		super(x, y);
		init();
	}
	
	public Linkable(double x, double y, double z) {
		super(x, y, z);
		init();
	}
	
	private void init() {
		parent = null;
		linked = new LinkedList<>();
		ang = 0;
	}
	
	//use this for when something should be removed for any reason
	public boolean isValid() {return true;}
	
	//use this for the update function
	public void updateAll() {
		update();
		Iterator<Linkable> lItr = linked.iterator();
		while(lItr.hasNext()) {
			Linkable next = lItr.next();
			if(next.update()) lItr.remove();
			next.updateAll();
		}
	}
	
	//use this for the update function
	public boolean update() {return false;}
	
	public Point getMaxBounds() {
		return parent.getMaxBounds();
	}
	
	public Point getMinBounds() {
		return parent.getMinBounds();
	}
	
	public Point gatAbsLoc() {
		return new Point(getAbsX(), getAbsY());
	}
	
	//this represents a z value for layering, so that higher z values can get drawn on top
	public double getZ() {return get(2);}
	
	//returns the x value relative to the screen
	public double getAbsX() {
		if(parent == null) {
			return X();
		} else {
			return (Math.cos(parent.ang) * X()) + (Math.sin(parent.ang) * Y()) + parent.getAbsX();
		}
	}
	
	//returns the y value relative to the screen
	public double getAbsY() {
		if(parent == null) {
			return Y();
		} else {
			return (Math.cos(parent.ang) * Y()) + (Math.sin(parent.ang) * X()) + parent.getAbsY();
		}
	}
	
	//gets the angle relative to the screen
	public double getAbsAng() {
		if(parent == null) {
			return ang;
		} else {
			return ang + parent.getAbsAng();
		}
	}
	
	//links a Linkable to this one, so that it follows it
	public void link(Linkable child) {
		child.parent = this;
		linked.add(child);
	}
	
	//unlinks a Linkable from this Linkable
	public void unlink(Linkable child) {
		child.parent = null;
		linked.remove(child);
	}
	
	//compares z values, so that a sorted list will order then based on drawing order
	public int compareTo(Linkable o) {
		switch((int)Math.copySign(1, Double.compare(get(2), o.get(2)))) {
			case -1:
				return -1;
			case 1:
				return 1;
			case 0:
				add(new Point(0, 0, 0.001));
				return 1;
			default:
				System.out.println("problem");
				return 1;
		}
		
	}
	
	public int size() {
		return linked.size();
	}
	
	//returns the angle of this Linkable
	public double getRot() {return ang;}
	
	//returns all linked children in  List
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
	
	public Iterator<Linkable> getLinkedIterator() {
		return linked.iterator();
	}
	
	//the function which draws this Linkable, given a total x location, y location and angle
	protected abstract void draw(Graphics g, double xOff, double yOff, double angOff);
	
	//draws a polygon of given points, for convenience of drawing textures
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, int size, double[] relativeX, double[] relativeY) {
		g.setColor(Color.WHITE);
		int[] xLocs = new int[relativeX.length];
		int[] yLocs = new int[relativeX.length];
		
		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((Math.cos(totalAng)*relativeX[i] + Math.sin(totalAng)*relativeY[i]) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng)*relativeX[i] + Math.cos(totalAng)*relativeY[i]) * size + totalY);
		}
		
		g.drawPolygon(xLocs, yLocs, xLocs.length);
	}
	
	public static boolean boundsContain(Point minBounds, Point maxBounds, Point test) {
		return test.X() < maxBounds.X() && test.X() > minBounds.X() &&
				test.Y() < maxBounds.Y() && test.Y() > minBounds.Y();
	}
	
	//draws a polygon of given points, for convenience of drawing textures, but with ints
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, int size, int[] relativeX, int[] relativeY) {
		g.setColor(Color.WHITE);
		int[] xLocs = new int[relativeX.length];
		int[] yLocs = new int[relativeX.length];
		
		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng)*relativeX[i] + Math.sin(-totalAng)*relativeY[i]) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng)*relativeX[i] + Math.cos(totalAng)*relativeY[i]) * size + totalY);
		}
		
		g.drawPolygon(xLocs, yLocs, xLocs.length);
	}
	
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points, boolean closed) {
		g.setColor(Color.WHITE);
		int[] xLocs = new int[points.length];
		int[] yLocs = new int[points.length];
		
		for(int i = 0; i < points.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng) * points[i].X() + Math.sin(-totalAng) * points[i].Y()) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng) * points[i].X() + Math.cos(totalAng) * points[i].Y()) * size + totalY);
		}
		
//		xLocs[i] = ((Math.cos(-ang - angOff)*relativeX[i] + Math.sin(-ang - angOff)*relativeY[i]) * size + this.x + xOff);
//		yLocs[i] = ((Math.sin(ang + angOff)*relativeX[i] + Math.cos(ang + angOff)*relativeY[i]) * size + this.y + yOff);
//		System.out.println(Arrays.toString(xLocs));
		
		if(closed) {
			g.drawPolygon(xLocs, yLocs, xLocs.length);
		} else {
			for(int i = 0; i < xLocs.length - 1; i++) {
				g.drawLine(xLocs[i], yLocs[i], xLocs[i + 1], yLocs[i + 1]);
			}
		}
	}
	
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points) {
		drawPoints(g, totalX, totalY, totalAng, size, points, true);
	}
	
	protected void loop(int maxX, int maxY) {
		if(Math.abs(X()) + 10 > maxX) {
			vals[0] *= -0.99;
		}
		if(Math.abs(Y()) + 20 > maxY) {
			vals[1] *= -0.99;
		}
	}
	
}
