package fromics;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Iterator;

//represents the background of something, if you want to have multiple different screens,
//a good way to do it is to have each screen be a class extending Background
public class Background extends Linkable {
	
	//the component, probably a Frindow, which this background is displayed on
	protected Component observer;
	
	//constructs a new Background with the given observer
	public Background(Component observer) {
		super(observer.getWidth() / 2, observer.getHeight() / 2);
		this.observer = observer;
	}
	
	//draws this Backgound and all of it's children
	//relative to this background
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
		Iterator<Linkable> lItr = linked.iterator();
		while(lItr.hasNext()) {
			Linkable next = lItr.next();
			next.drawAll(g);
		}
	}
	
	//returns a Point representing the lower-right corner of the bounds of the screen
	@Override
	public Point getMaxBounds() {
		return new Point(observer.getWidth(), observer.getHeight());
	}
	
	//returns a Point representing the upper-left corner of the screen
	@Override
	public Point getMinBounds() {
		return new Point(observer.getWidth() * -1, observer.getHeight() * -1);
	}

	//override this if you want the background to draw something
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {}
}
