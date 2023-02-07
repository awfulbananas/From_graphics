package fromics;

import java.awt.Component;
import java.awt.Graphics;

public class Background extends Linkable {

	protected Component observer;
	
	public Background(Component observer) {
		super(observer.getWidth() / 2, observer.getHeight() / 2);
		this.observer = observer;
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		for(Linkable l : linked) {
			l.drawAll(g);
		}
	}
	
	//draws this Linkable, and all its children relative to it's parent
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
		for(Linkable l : linked) {
			l.drawAll(g);
		}
	}
	
	@Override
	public Point getMaxBounds() {
		return new Point(observer.getWidth(), observer.getHeight());
	}
	
	@Override
	public Point getMinBounds() {
		return new Point(observer.getWidth() * -1, observer.getHeight() * -1);
	}
}
