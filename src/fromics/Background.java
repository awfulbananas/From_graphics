package fromics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

//represents the background of something, if you want to have multiple different screens,
//a good way to do it is to have each screen be a class extending Background
public class Background extends Linkable {
	
	//the Frindow which this background is displayed on
	protected Frindow observer;  
	
	//constructs a new Background with the given observer
	public Background(Frindow observer) {
		super(observer.getWidth() / 2, observer.getHeight() / 2);
		this.observer = observer;
		setKeysSet(observer.getKeys().codes);
	}
	
	//draws this Backgound and all of it's children
	//relative to this background
	public void drawAll(Graphics g) {
		setDefColor(g);
		draw(g, 0, 0, 0);
		for(int i = 0; i < linked.size(); i++) {
			linked.get(i).drawAll(g);
		}
	}
	
	//should return true when the next screen should be shown
	public boolean nextScreen() {
		return false;
	}
	
	protected boolean hasAlpha() {
		int colorType = observer.getColorType();
		return colorType == BufferedImage.TYPE_INT_ARGB || colorType == BufferedImage.TYPE_4BYTE_ABGR;
	}
	
	//called whenever this screen stops being shown
	public void close() {}
	
	//returns a Point representing the lower-right corner of the bounds of the screen
	@Override
	public Point getMaxBounds() {
		return new Point(observer.getWidth() / 2, observer.getHeight() / 2);
	}
	
	//returns a Point representing the upper-left corner of the screen
	@Override
	public Point getMinBounds() {
		return new Point(observer.getWidth() / -2, observer.getHeight() / -2);
	}
	
	@Override
	public int getScreenWidth() {
		return observer.getWidth();
	}
	
	public int getScreenHeight() {
		return observer.getHeight();
	}

	//override this if you want the background to draw something
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {}
}
