package fromics;

import fromics.events.Event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/*TODO: * add an "IOEvent" class to unify MouseEvent and KeyEvent
		* also unify KeypressFunction and MouseEventFunction while doing above
		* make said functions register using a name/id so I can un-register them even without a direct object reference
		* consider adding an alternate system where events are sent to methods in linkables/backgrounds instead of them registering events
		*
		* 3d stuff:
			* custom Graphics class
			* projection
			* texture mapping
			* rendering
			* add multiple options
			* allow 2d & 3d simultaneously
 */

/**
 * Linkable is a class which represents an object in the hierarchy of a program, with a position, rotation, and scale
 * relative to its parent. Linkables each have one parent and any number of children, and can represent anything in a program;
 * for example in a drawing program, you might have a Background with the children DrawFrame and Menu, with Menu having
 * further children itself.
 * Linkable extends Point, and the location of that Point represents the location of this Linkable relative to it's parent
 * @author awfulbananas
 */
public abstract class Linkable extends Point {
	/**
	 * ang represents the angle of this Linkable relative to its parent
	 */
	protected double ang;
	/**
	 * scale represents the scale of this Linkable relative to its parent.
	 * this isn't often used, and things like PolygonCollider don't currently take this into account
	 */
	protected Point scale;
	/**
	 * the children of this Linkable
	 */
	protected List<Linkable> linked;
	/**
	 * the parent of this Linkable, or null if it has none
	 */
	protected Linkable parent;
	/**
	 * whether this Linkable is currently updating, which is used to manage when new Linkables are linked to this one
	 */
	protected boolean updating;
	/**
	 * the queue of Linkables to link to this Linkable after this Linkable and it's children finish updating
	 */
	protected Queue<Linkable> linkQueue;
	/**
	 * the queue of Linkables to unlink from this Linkable after this Linkable and it's children finish updating
	 */
	protected Queue<Linkable> unlinkQueue;
	/**
	 * a set containing all the currently pressed keys, following the KeyEvent constants
	 */
	protected Set<Integer> keysPressed;
	/**
	 * the default color for this Linkable to be drawn as.
	 */
	private Color color;
	/**
	 * whether this Linkable has been linked, used to only execute onFirstLink() once
	 */
	protected boolean hasLinked;

	/**
	 * constructs a new Linkable at (x, Y) in 2d
	 * @param x the x location to construct this Linkable at
	 * @param y the y location to construct this Linkable at
	 */
	public Linkable(double x, double y) {
		super(x, y);
		init();
	}

	/**
	 * constructs a new Linkable at (x, y, z) in 2d
	 * @param x the x location to construct this Linkable at
	 * @param y the y location to construct this Linkable at
	 * @param z the x location to construct this Linkable at
	 */
	public Linkable(double x, double y, double z) {
		super(x, y, z);
		init();
	}

	/**
	 * sets the default Color for this Linkable to be drawn in
	 * @param c the color to set the default to
	 */
	public void setColor(Color c) {
		this.color = c;
	}

	/**
	 * returns the current default color this Linkable is being drawn with
	 * @return the color this Linkable is being drawn with by default
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * initializes a bunch of variables, this is only used in constructors for convenience
	 */
	private void init() {
		updating = true;
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

	/**
	 * returns the mouse object of the associated with the Frindow of the Manager,
	 * or throws an IllegalStateException if the chain of parents doesn't reach a Manager
	 * @return the Mouse object associated with the current Frindow
	 */
	protected Mouse getMouse() {
		try {
			return parent.getMouse();
		} catch(NullPointerException e) {
			throw new IllegalStateException("this Linkable has no parent to get Mouse from");
		}
	}

	/**
	 * this is called directly before a Linkable is unlinked from another Linkable.
	 * this has not default functionality, but it can be useful for removing MouseEventFunctions
	 * and KeypressFunctions when a Linkable should no longer receive that input, and for otherwise
	 * preventing memory leaks where relevant
	 */
	protected void beforeUnlink() {}

	/**
	 * this is called whenever this Linkable is linked to another Linkable
	 * it has no functionality on its own, and it's there to be extended if needed
	 */
	protected void onLink() {}

	/**
	 * called when a Linkable is linked for the first time, is calls onFirstLink on itself
	 * and all of its children
	 */
	private void onFirstLinks() {
		onFirstLink();
		hasLinked = true;
		for(int i = 0; i < linked.size(); i++) {
			linked.get(i).onFirstLinks();
		}
	}

	/**
	 * called after this Linkable is linked for the first time, this has no functionality
	 * and is meant to be extended if needed
	 */
	protected void onFirstLink() {}

	/**
	 * sets the angle of this Linkable relative to its parent
	 * @param ang the angle to set this Linkable to relative to its parent
	 */
	public void setAng(double ang) {
		this.ang = ang;
	}

	/**
	 * updates this Linkable and all of its children.
	 * while this method is public, you generally shouldn't call this in your
	 * own code unless you have a very specific reason to do so, or are overriding this method.
	 * @return whether this Linkable should be unlinked from its parent
	 */
	public synchronized boolean updateAll() {
		updating = true;
		Iterator<Linkable> lItr = linked.iterator();
		while(lItr.hasNext()) {
			Linkable next = lItr.next();
			if(next.updateAll()) lItr.remove();
		}
		boolean updateVal = update();
		updating = false;
		while(!linkQueue.isEmpty()) {
			link(linkQueue.remove());
		}
		while(!unlinkQueue.isEmpty()) {
			unlink(unlinkQueue.remove());
		}
		return updateVal;
	}

	/**
	 * called every tick of the update loop, and returns whether this Linkable should be unlinked from its parent
	 * this method is meant to be extended is needed
	 * @return whether this Linkable should be unlinked from its parent
	 */
	public synchronized boolean update() {return false;}

	/**
	 * adds a new KeypressFunction to the Keys object associated with the current Frindow is this Linkable has a parent.
	 * a KeypressFunction is a Consumer<<KeyEvent>> which is usually defined inline as a functional interface, and performs
	 * some functionality when a key is pressed
	 * you should probably be careful to not add KeypressFunctions for any Linkables which won't stay around too long, or
	 * to add KeypressFunctions periodically when a given Linkable will be removed, since they will technically store a
	 * reference to where they were defined, potentially causing a memory leak.
	 * (I'll add a removeKeystrokeFunction at some point to fix this)
	 * @param func the KeypressFunction to add to Keys
	 */
	protected void addKeystrokeFunction(KeypressFunction func) {
		parent.addKeystrokeFunction(func);
	}

	/**
	 * Removes the given KeypressFunction from the associated Frindow, undoing a call of addKeystrokeFunction.
	 * If many Linkables with KeystrokeFunctions are being unlinked, they should use this to prevent a
	 * memory leak (the reference to the KeypressFunction in Frindow likely prevents the garbage collector
	 * from disposing of an otherwise unused Linkable, potentially causing a memory leak and/or bugs)
	 * @param func the KeypressFunction to remove
	 */
	public void removeKeystrokeFunction(KeypressFunction func) {
		parent.removeKeystrokeFunction(func);
	}

	/**
	 * adds a new MouseEventFunction to the Mouse object associated with the current Frindow if this Linkable has a parent.
	 * a MouseEventFunction is a Consumer<<MouseEvent>> which is usually defined inline as a functional interface, and performs
	 * some functionality whenever a MouseEvent is detected. notably, this triggers for every MouseEvent, unlike a KeypressFunction
	 * which only triggers when a key is pressed, so you usually need to add some more conditions to distinguish between event types.
	 * this has similar things you should be careful about as addKeystrokeFunction, to see that method for mroe info
	 * @param func the MouseEventFunction to add to Mouse
	 */
	public void addMouseEventFunction(MouseEventFunction func) {
		parent.addMouseEventFunction(func);
	}

	/**
	 * Removes the given MouseEventFunction from the associated Frindow, undoing a call of addMouseEventFunction.
	 * Similar to KeypressFunctions, if many Linkables with MouseEventFunctions are being unlinked, they should use this to prevent a
	 * memory leak (the reference to the MouseEventFunction in Frindow likely prevents the garbage collector
	 * from disposing of an otherwise unused Linkable, potentially causing a memory leak and/or bugs)
	 * @param func the MouseEventFunction to remove
	 */
	public void removeMouseEventFunction(MouseEventFunction func) {
		parent.removeMouseEventFunction(func);
	}

	/**
	 * returns the position of the mouse in the given MouseEvent as a Point
	 * @param e the MouseEvent to get the position of
	 * @return the position of the given MouseEvent
	 */
	public Point getMousePos(MouseEvent e) {
		return new Point(e.getX(), e.getY());
	}

	/**
	 * adds the given Event to the event queue, which will be executed when all currently running Events
	 * and previously queued Events have finished.
	 * only works if this Linkable has a parent.
	 * @param e the Event to add to the event queue
	 */
	public void queueEvent(Event e) {
		parent.queueEvent(e);
	}

	/**
	 * add the given Event to the currently running Events, starting its execution immediately
	 * @param e the Event to start
	 */
	public void addEvent(Event e) {
		parent.addEvent(e);
	}

	/**
	 * returns a Point representing the maximum bounds of the current Frindow, where the x value of the Point
	 * is the max x and the y value of the Point is the max y
	 * @return the maximum bounds of the current Frindow
	 */
	public Point getMaxBounds() {
		return parent.getMaxBounds();
	}

	/**
	 * returns a Point representing the minimum bounds of the current Frindow.
	 * unless overridden in a Linkable or one of its parents, this will always be (0, 0)
	 * @return the minimum bounds of the current Frindow
	 */
	public Point getMinBounds() {
		return parent.getMinBounds();
	}

	/**
	 * returns the current width of the window
	 * @return the width of the window
	 */
	public int getScreenWidth() {
		return parent.getScreenWidth();
	}

	/**
	 * returns the current height of the window
	 * @return the height of the window
	 */
	public int getScreenHeight() {
		return parent.getScreenHeight();
	}

	/**
	 * returns the location of this Linkable in global space (still relative to the window)
	 * @return the global location of this Linkable
	 */
	public Point getAbsLoc() {
		return new Point(getAbsX(), getAbsY());
	}

	/**
	 * returns the x value of this Linkabls in global space
	 * @return the global x location of this Linkable
	 */
	public double getAbsX() {
		if(parent == null) {
			return X();
		} else {
			return ((Math.cos(parent.ang) * X()) - (Math.sin(parent.ang) * Y()))*getAbsScale().X() + parent.getAbsX();
		}
	}

	/**
	 * returns the y value of this Linkable in global space
	 * @return the global y location of this Linkable
	 */
	public double getAbsY() {
		if(parent == null) {
			return Y();
		} else {
			return ((Math.cos(parent.ang) * Y()) - (Math.sin(parent.ang) * X()))*getAbsScale().X() + parent.getAbsY();
		}
	}

	/**
	 * returns the angle of this Linkable in global space
	 * @return the absolute angle of this Linkable
	 */
	public double getAbsAng() {
		if(parent == null) {
			return ang;
		} else {
			return ang + parent.getAbsAng();
		}
	}

	/**
	 * returns the scale of this Linkable
	 * @return the scale of this Linkable
	 */
	public Point getScale() {
		return scale.copy();
	}

	/**
	 * returns the absolute scale if this Linkable
	 * @return the absolute scale of this Linkable
	 */
	public Point getAbsScale() {
		if(parent == null) {
			return scale.copy();
		} else {
			Point pScale = parent.getAbsScale();
			return new Point(scale.X() * pScale.X(), scale.Y() * pScale.Y());
		}
	}

	/**
	 * sets this Linkables x and y to the current center of the screen
	 */
	public void goToCenterScreen() {
		Point bounds = getMaxBounds();
		setX(bounds.X() / 2);
		setY(bounds.Y() / 2);
	}

	/**
	 * sets the KeysPressed set to the given set
	 * don't call this in your own code unless you have a specific reason to
	 * @param keysPressed the set to set KeysPressed to
	 */
	protected void setKeysSet(Set<Integer> keysPressed) {
		this.keysPressed = keysPressed;
	}

	/**
	 * if this Linkable is updating, adds the given Linkable to the linkQueue, otherwise links the given
	 * Linkable to this one, calling onFirstLinks
	 * @param child the Linkable to link to this one
	 */
	public void link(Linkable child) {
		if(!linked.contains(child)) {
			if (updating) {
				linkQueue.add(child);
			} else {
				if(child.parent != null) {
					child.parent.linked.remove(child);
				}
				child.keysPressed = this.keysPressed;
				child.parent = this;
				linked.add(child);
				child.onLink();
				if (!child.hasLinked && this.hasLinked) {
					child.onFirstLinks();
					child.resolvePreLinks();
				}
			}
		}
	}

	protected void resolvePreLinks() {
		updating = false;
		while(!linkQueue.isEmpty()) {
			link(linkQueue.remove());
		}
	}

	/**
	 * if this Linkable is updating, adds the given Linkable to the unlinkQueue, otherwise
	 * unlinks the given Linkable from this one
	 * @param child the Linkable to unlink from this one
	 */
	public void unlink(Linkable child) {
		if(updating) {
			unlinkQueue.add(child);
		} else {
			child.beforeUnlink();
			child.parent = null;
			linked.remove(child);
		}
	}

	/**
	 * returns whether the given key is pressed, using the KeyEvent constants
	 * @param key the key code to get whether the associated key is pressed
	 * @return whether the given key is pressed
	 */
	protected boolean getKey(int key) {
		return keysPressed.contains(key);
	}

	/**
	 * returns the number of children of this Linkable
	 * @return the number of children of this Linkable
	 */
	public int numLinked() {
		return linked.size();
	}

	/**
	 * returns the angle of this Linkable relative to its parent
	 * @return the angle of this Linkable
	 */
	public double getAng() {return ang;}

	/**
	 * returns the list of children of this Linkable
	 * @return the list of children of this Linkable
	 */
	public List<Linkable> getLinked() {return linked;}

	/**
	 * returns the parent of this Linkable
	 * @return the parent of this Linkable
	 */
	public Linkable parent() {return parent;}

	/**
	 * returns whether the current window has an alpha channel when drawing
	 * @return whether the current window has an alpha channel
	 */
	protected boolean hasAlpha() {
		return parent.hasAlpha();
	}

	/**
	 * sets the color of the given Graphics to the default color of this Linkable
	 * @param g the Graphics object to set the color of
	 */
	protected void setToDefColor(Graphics g) {
		g.setColor(color);
	}

	/**
	 * draws this Linkable and all of its children using the given Graphics object and image
	 * @param g the Graphics object to draw with
	 * @param img the image to draw on
	 */
	public void drawAll(Graphics g, BufferedImage img) {
		setToDefColor(g);
		try {
			draw(g, img, parent.getAbsX(), parent.getAbsY(), parent.getAbsAng());
		} catch(NullPointerException e) {
			//e.printStackTrace();
			return;
		}

		try {
			for(int i = 0; i < linked.size(); i++) {
				linked.get(i).drawAll(g, img);
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns an iterator over all the children of this Linkable
	 * @return an iterator over the children of this Linkable
	 */
	public Iterator<Linkable> getLinkedIterator() {
		return linked.iterator();
	}

	/**
	 * returns the amount of time between this update ant the previous one in milliseconds
	 * @return the dt in microseconds from the last update
	 */
	public int dt() {
		return parent.dt();
	}

	/**
	 * draw this Linkable using hte given Graphics and image, as well as the given offsets
	 * this should be overridden in most Linkables to draw it to the screen
	 * @param g the Graphics to draw with
	 * @param img the image to draw on
	 * @param xOff the x offset of this Linkable
	 * @param yOff the y offset of this Linkable
	 * @param angOff the angle offset of this Linkable
	 */
	protected abstract void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff);

	/**
	 * draws the given points as a closed polygon with the points (relativeX, relativeY) rotated by
	 * totalAng and scaled by size offset by the point (totalX, totalY) using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param totalX the x offset to draw the Points with
	 * @param totalY the y offset to draw th Points with
	 * @param totalAng the angle to rotate the polygon by around the x/y offsets
	 * @param size the size to scale the polygon by
	 * @param relativeX the x location to draw relative to
	 * @param relativeY the y location to draw relative to
	 */
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, int size, double[] relativeX, double[] relativeY) {
		g.setColor(Color.WHITE);
		int[] xLocs = new int[relativeX.length];
		int[] yLocs = new int[relativeX.length];

		Point newXLoc = (new Point(1, 0)).rot(totalAng);
		Point newYLoc = newXLoc.getPerpendicular();

		for(int i = 0; i < relativeX.length; i++) {
			xLocs[i] = (int)((newXLoc.X() * relativeX[i] + newYLoc.X() * relativeY[i]) * size + totalX);
			yLocs[i] = (int)((newXLoc.Y() * relativeX[i] + newYLoc.Y() * relativeY[i]) * size + totalY);
		}

		g.drawPolygon(xLocs, yLocs, xLocs.length);
	}

	/**
	 * draws the given Points as a closed polygon using the give Graphics object
	 * @param g the Graphics object to draw with
	 * @param pts the Points to draw
	 */
	protected static void simpleDrawPoints(Graphics g, Point[] pts) {
		simpleDrawPoints(g, pts, true);
	}

	/**
	 * draws the given Points using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param pts the Points to draw
	 * @param closed whether to close the drawn shape
	 */
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

	/**
	 * draws the given points (relativeX, relativeY) rotated by totalAng and
	 * scaled by size offset by the point (totalX, totalY) using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param totalX the x offset to draw from
	 * @param totalY the y offset to draw from
	 * @param totalAng the angle to rotate the Points by
	 * @param size the size to scale the points by
	 * @param points the Points to draw
	 * @param closed whether to close the drawn shape
	 */
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

	/**
	 * draws the given points as a filled closed polygon rotated by totalAng and
	 * scaled by size offset by the point (totalX, totalY) using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param totalX the x offset to draw from
	 * @param totalY the y offset to draw from
	 * @param totalAng the angle to rotate the Points by
	 * @param size the size to scale the points by
	 * @param points the Points to draw
	 */
	protected static void fillPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points) {
		int[] xLocs = new int[points.length];
		int[] yLocs = new int[points.length];
		
		for(int i = 0; i < points.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng) * points[i].X() + Math.sin(-totalAng) * points[i].Y()) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng) * points[i].X() + Math.cos(totalAng) * points[i].Y()) * size + totalY);
		}
		
		g.fillPolygon(xLocs, yLocs, xLocs.length);
	}

	/**
	 * draws the given points (xVals, yVals) as a closed polygon rotated by totalAng and
	 * scaled by size offset by the point (totalX, totalY) using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param totalX the x offset to draw from
	 * @param totalY the y offset to draw from
	 * @param totalAng the angle to rotate the Points by
	 * @param size the size to scale the points by
	 * @param xVals the x values of the points to draw
	 * @param yVals the y values of the points to draw
	 */
	protected static void fillPoints(Graphics g, double totalX, double totalY, double totalAng, double size, double[] xVals, double[] yVals) {
		int[] xLocs = new int[xVals.length];
		int[] yLocs = new int[yVals.length];
		
		for(int i = 0; i < xVals.length; i++) {
			xLocs[i] = (int)((Math.cos(-totalAng) * xVals[i] + Math.sin(-totalAng) * yVals[i]) * size + totalX);
			yLocs[i] = (int)((Math.sin(totalAng) * xVals[i] + Math.cos(totalAng) * yVals[i]) * size + totalY);
		}
		
		g.fillPolygon(xLocs, yLocs, xLocs.length);
	}

	/**
	 * draws the given points as a closed polygon rotated by totalAng and
	 * scaled by size offset by the point (totalX, totalY) using the given Graphics object
	 * @param g the Graphics object to draw with
	 * @param totalX the x offset to draw from
	 * @param totalY the y offset to draw from
	 * @param totalAng the angle to rotate the Points by
	 * @param size the size to scale the points by
	 * @param points the Points to draw
	 */
	protected static void drawPoints(Graphics g, double totalX, double totalY, double totalAng, double size, Point[] points) {
		drawPoints(g, totalX, totalY, totalAng, size, points, true);
	}

	/**
	 * flips this Linkables x/y coordinate if it's absolute value is beyond the given bounds
	 * @param maxX the x value to flip when moved past
	 * @param maxY the y value to flip when moved past
	 */
	protected void loop(int maxX, int maxY) {
		if(Math.abs(X()) > maxX) {
			vals[0] *= -1;
		}
		if(Math.abs(Y()) > maxY) {
			vals[1] *= -1;
		}
	}
	
}
