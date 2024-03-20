package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ConcurrentModificationException;
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
	//the relative scale of this Linkable compared to its parent
	protected Point scale;
	//the children of this Linkable, with locations relative to this one,
	//which are drawn after the parent
	protected List<Linkable> linked;
	//the parent of this Linkable, null if it has none
	protected Linkable parent;
	//whether this Linkable is updating, used to manage
	//when new Linkables are Linked
	protected boolean updating;
	//only used if a child tries to link a new Linkable while this one is updating
	protected Queue<Linkable> linkQueue;
	//only used if a child tries to unlink a Linkable while this one is updating
	protected Queue<Linkable> unlinkQueue;
	//a Set containing all of the keyboard key currently pressed
	protected Set<Integer> keysPressed;
	//the default color for this Linkable to be drawn as
	private Color color;
	//whether this Linkable is fading in after being linked
	private boolean fadingIn;
	//whether this Linkable is fading out before being unlinked
	private boolean fadingOut;
	//the amount of frames left in the fade when fading in or out
	private double fadeTimer;
	//the total amount of frames in a fade when fading in or out
	private double initialFadeTime;
	//whether or not this Linkable has ever been linked, used to only call onFirstLink once
	protected boolean hasLinked;
	
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
	
	//sets the default drawing color of this Linkable
	public void setColor(Color c) {
		this.color = c;
	}
	
	//returns the current default drawing color of this Linkable
	public Color getColor() {
		return this.color;
	}
	
	//initializes various values used by this Linkable
	private void init() {
		updating = false;
		hasLinked = false;
		parent = null;
		linkQueue = new LinkedList<>();
		unlinkQueue = new LinkedList<>();
		linked = new LinkedList<>();
		ang = 0;
		scale = new Point(dims());
		for(int i = 0; i < dims(); i++) scale.set(i, 1);
		color = Color.WHITE;
	}
	
	protected Mouse getMouse() {
		return parent.getMouse();
	}
	
	//called directly after this Linkable is linked to another Linkable
	protected void onLink() {}
	
	private void onFirstLinks() {
		onFirstLink();
		hasLinked = true;
		for(Linkable l : linked) {
			l.onFirstLinks();
		}
	}
	
	//called directly after the first time this Linkable is linked to another Linkable, unless the link chain doesn't reach
	//a Background, in which case onFirstLink is delayed until then
	protected void onFirstLink() {}
	
	//sets the angle of this Linkable
	public void setAng(double ang) {
		this.ang = ang;
	}
	
	//links this Linkable to the given parent, and causes it to visually fade in for
	//fadeTime frames
	public void fadeIn(Linkable parent, double fadeTimeSeconds) {
		parent.link(this);
		fadingIn = true;
		fadeTimer = fadeTimeSeconds;
		initialFadeTime = fadeTimeSeconds;
	}
	
	//links this Linkable to the given parent, and causes it to visually fade in for
	//fadeTime frames
	public void fadeOut(double fadeTimeSeconds) {
		fadingOut = true;
		fadeTimer = fadeTimeSeconds;
		initialFadeTime = fadeTimeSeconds;
	}
	
	//updates this Linkable and all of it's children
	public synchronized boolean updateAll() {
		updating = true;
		Iterator<Linkable> lItr = linked.iterator();
		while(lItr.hasNext()) {
			Linkable next = lItr.next();
			if(next.updateAll()) lItr.remove();
		}
		boolean updateVal = update();
		if(fadingIn || fadingOut) {
			fadeTimer = fadeTimer - 0.000001 * dt();
			if(fadeTimer < 0) {
				fadingIn = false;
			}
		}
		updating = false;
		if(fadingOut && fadeTimer <= 0) {
			parent.unlink(this);
			fadingOut = false;
		}
		if(!linkQueue.isEmpty()) {
			link(linkQueue.remove());
		}
		if(!unlinkQueue.isEmpty()) {
			unlink(unlinkQueue.remove());
		}
		return updateVal;
	}
	
	//optional method, if implemented, should run any update functionality, 
	//and should return whether it should be unlinked from it's parent
	public boolean update() {return false;}
	
	//adds a consumer to be called whenever a key is pressed which is passed
	//a KeyEvent corresponding to the key press
	protected void addKeystrokeFunction(KeypressFunction func) {
		parent.addKeystrokeFunction(func);
	}
	
	//returns a Point representing the lower-right corner of the bounds of the screen (lower-right bc. it's positive x & y), 
	//these bounds aren't enforced by default, but this method can be used for something like screen-looping
	public Point getMaxBounds() {
		return parent.getMaxBounds();
	}
	
	//like getMaxBounds(), but returns the point representing the upper left corner of the bounds of the screen
	public Point getMinBounds() {
		return parent.getMinBounds();
	}
	
	//returns the current width of the window
	public int getScreenWidth() {
		return parent.getScreenWidth();
	}
	
	//returns the current height of the window
	public int getScreenHeight() {
		return parent.getScreenHeight();
	}
	
	//returns the location of this Linkable in global space
	public Point getAbsLoc() {
		return new Point(getAbsX(), getAbsY());
	}
	
	//returns the z-value of this Linkable if it has one, or 0 otherwise
	public double getZ() {return get(2);}
	
	//returns the x value of this Linkable in global space
	public double getAbsX() {
		if(parent == null) {
			return X();
		} else {
			return ((Math.cos(parent.ang) * X()) - (Math.sin(parent.ang) * Y()))*getAbsScale().X() + parent.getAbsX();
		}
	}
	
	//returns the y value of this Linkable in global space
	public double getAbsY() {
		if(parent == null) {
			return Y();
		} else {
			return ((Math.cos(parent.ang) * Y()) - (Math.sin(parent.ang) * X()))*getAbsScale().X() + parent.getAbsY();
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
	
	public Point getScale() {
		return scale.copy();
	}
	
	public Point getAbsScale() {
		if(parent == null) {
			return scale.copy();
		} else {
			Point pScale = parent.getAbsScale();
			return new Point(scale.X() * pScale.X(), scale.Y() * pScale.Y());
		}
	}
	
	//sets the set used to detect key presses
	//maybe don't use this one unless you need to
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
			child.onLink();
			if(!child.hasLinked && this.hasLinked) {
				child.onFirstLinks();
			}
		}
	}
	
	//unlinks a Linkable from this one, usually called if a Linkable should stop being drawn and updated
	public void unlink(Linkable child) {
		if(updating) {
			unlinkQueue.add(child);
		} else {
			child.parent = null;
			linked.remove(child);
		}
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
	public int numLinked() {
		return linked.size();
	}
	
	//returns the angle of this Linkable relative to it's parent
	public double getAng() {return ang;}
	
	//returns a list of all children of this Linkable
	public List<Linkable> getLinked() {return linked;}
	
	//returns the parent of this Linkable
	public Linkable parent() {return parent;}
	
	//returns whether the current color model being used has an
	//alpha component
	protected boolean hasAlpha() {
		return parent.hasAlpha();
	}
	
	//sets the drawing color to the default drawing color for this Linkable,
	//accounting for any fade currently in effect
	protected void setDefColor(Graphics g) {
		if(!(fadingIn || fadingOut)) {
			g.setColor(color);
		} else {
			double fadeMult = ((double)fadeTimer / (double)initialFadeTime);
			if(fadingIn) {
				fadeMult = 1.0 - fadeMult;
			}
			float[] hsbComps = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			hsbComps[2] *= fadeMult;
			g.setColor(Color.getHSBColor(hsbComps[0], hsbComps[1], hsbComps[2]));
		}
		if(parent != null && (parent.fadingIn || parent.fadingOut)) {
			double fadeMult = ((double)parent.fadeTimer / (double)parent.initialFadeTime);
			if(parent.fadingIn) {
				fadeMult = 1.0 - fadeMult;
			}
			Color currentColor = g.getColor();
			float[] hsbComps = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
			hsbComps[2] *= fadeMult;
			g.setColor(Color.getHSBColor(hsbComps[0], hsbComps[1], hsbComps[2]));
		}
	}
	
	//draws this Linkable, and all its children relative to it's parent
	public void drawAll(Graphics g) {
		setDefColor(g);
		try {
			draw(g, parent.getAbsX(), parent.getAbsY(), parent.getAbsAng());
		} catch(NullPointerException e) {
			e.printStackTrace();
			return;
		}

		try {
			for(Linkable l : linked) {
				l.drawAll(g);
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}
	
	//returns an Iterator for all of the children of this Linkable
	public Iterator<Linkable> getLinkedIterator() {
		return linked.iterator();
	}
	
	//returns the change in time between the previous update and this one
	//in thousands of nanoseconds
	public int dt() {
		return parent.dt();
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
		
		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((relativeX[i] * size) + totalX);
			yLocs[i] = (int)((relativeY[i] * size) + totalX);
		}
		
		g.drawPolygon(xLocs, yLocs, xLocs.length);
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

	protected static void simpleDrawPoints(Graphics g, Point[] pts) {
		simpleDrawPoints(g, pts, true);
	}

	protected static void simpleDrawPoints(Graphics g, Point[] pts, boolean closed) {
		int[] xLocs = new int[pts.length];
		int[] yLocs = new int[pts.length];

		for(int i = 0; i < pts.length; i++) {
			xLocs[i] = (int)pts[i].X();
			yLocs[i] = (int)pts[i].Y();
		}

		if(closed) {
			g.drawPolygon(xLocs, yLocs, xLocs.length);
		} else {
			for(int i = 0; i < xLocs.length - 1; i++) {
				g.drawLine(xLocs[i], yLocs[i], xLocs[i + 1], yLocs[i + 1]);
			}
		}
	}
	
	//draws a polygon from the Points points, with location offset in the x-axis by totalX, and in the y-axis by totalY/
	//and rotated around the offset location by titalAng radians, scaled by size, using Graphics g
	//closed determines whether the the first and last Points should be connected
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points, boolean closed) {
		Point[] newPoints = new Point[points.length];
		Point newXLoc = (new Point(-1, 0)).rot(totalAng).mult(size);
		Point newYLoc = newXLoc.getPerpendicular();
		for(int i = 0; i < points.length; i++) {
			newPoints[i] = points[i].copy().matrixTransform(newXLoc, newYLoc).add(totalX, totalY);
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
