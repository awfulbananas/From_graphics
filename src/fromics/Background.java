package fromics;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

//represents the background of something, if you want to have multiple different screens,
//a good way to do it is to have each screen be a class extending Background
public class Background extends Linkable {

	//constructs a new Background with the given observer
	public Background(double x, double y) {
		super(x, y);
	}

	public Background() {
		super(0, 0);
	}

	//draws this Background and all of its children
	//relative to this background
	public void drawAll(Graphics g, BufferedImage img) {
		setDefColor(g);
		draw(g, img, 0, 0, 0);
		for(int i = 0; i < linked.size(); i++) {
			try {
				linked.get(i).drawAll(g, img);
			} catch(NullPointerException e) {
				continue;
			}
		}
		drawUI(g, img);
	}
	
	//should return true when the next screen should be shown
	public boolean nextScreen() {
		return false;
	}
	
	//should return the index of the next screen to be loaded after nextScreen return true,
	//or -1 to go to the screen with the next index
	public int getNextScreen() {
		return -1;
	}
	
	//called whenever this screen stops being shown
	public void close() {}

	//override this if you want the background to draw something
	@Override
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {}

	//override this to draw things over everything else (unlike under it with the draw() method
	protected void drawUI(Graphics g, BufferedImage img) {}
}
