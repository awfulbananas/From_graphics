package fromics;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * represents a background of the program, managing the current screen.
 * this is what is swapped between by a Screens object.
 * as an example, in a given program, you might have the backgrounds MainMenu, Settings, and GameScreen
 */
public abstract class Background extends Linkable {

	/**
	 * constructs a new Background at the given location
	 * @param x the x value to construct the Background at
	 * @param y the y value to construct the Background at
	 */
	public Background(double x, double y) {
		super(x, y);
	}

	/**
	 * constructs a new Background at (0, 0)
	 */
	public Background() {
		super(0, 0);
	}

	/**
	 * draws this Background and all of its children relative to the screen
	 * @param g the Graphics object to draw with
	 * @param img the image to draw on
	 */
	public void drawAll(Graphics g, BufferedImage img) {
		setToDefColor(g);
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

	/**
	 * returns whether the parent Screens object should change to the next screen.
	 * returns false if not overridden
	 * @return whether to change to the next screen
	 */
	public boolean nextScreen() {
		return false;
	}

	/**
	 * returns the index to the next screen to change to when changing screens, or -1 if the next index should be used.
	 * returns -1 if not overridden
	 * @return the screen to switch to when switching screens
	 */
	public int getNextScreen() {
		return -1;
	}

	/**
	 * this has no base functionality, but is called when a background is switched away from by a Screens object
	 */
	public void close() {}

	/**
	 * see the draw method of Linkable.
	 * this is implemented since not all Backgrounds have to be drawn
	 */
	@Override
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {}

	/**
	 * draws the UI associated with this Background.
	 * this is called after all this Backgrounds children have been drawn, unlike draw() which is called
	 * before children are drawn, so this method will always draw on top
	 * @param g the Graphics object to draw with
	 * @param img the image to draw on
	 */
	protected void drawUI(Graphics g, BufferedImage img) {}
}
