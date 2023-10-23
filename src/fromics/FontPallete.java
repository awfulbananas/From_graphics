package fromics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;
//a class which when run, displays a window with a sample of every default font option,
//which is useful for picking fonts

//doesn't use normal fromics updating because it doesn't ever need to update,
//but I should probably change that to make it more in line with my general code
public class FontPallete extends Manager {
	//the array of all the fonts
	String[] allFonts;
	
	//constructs a new FontPallete with the given Frindow
	public FontPallete(Frindow observer) {
		super(observer);
		allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}
	
	//creates and displays a new FontPallete
	public static void main(String[] args) throws InterruptedException {
		Frindow win = new Frindow();
		FontPallete pallete = new FontPallete(win);
		win.init(1, pallete);
		Timer t = new Timer();
		Thread.sleep(1000);
		t.schedule(pallete.getDrawer(), 10l, 10l);
	}
	
	//the task to be run on a regular basis which draw the FontPallete to the screen
	private class DrawTask extends TimerTask {
		public void run() {
			observer.defPaint();
		}
	}
	
	//called by the Frindow to draw this FontPallete to the screen
	@Override
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
	}
	
	//draws this FontPallete with the given offsets and Graphics
	@Override
	public void draw(Graphics g, double xOff, double yOff, double angOff) {
		for (int i = 0; i < allFonts.length; i++) {
			g.setFont(new Font(allFonts[i], Font.PLAIN, 20));
			g.drawString(allFonts[i].substring(0, Math.min(allFonts[i].length(), 15)), 20  + 185 * ((i * 20 + 20) / (observer.getHeight() - 20)), (i * 20 + 20) % (observer.getHeight() - 20));
		}
	}
	
	//returns the drawer object for this FontPallete
	public DrawTask getDrawer() {
		return new DrawTask();
	}
	
	//a method which needs to be implemented but isn't used here
	@Override
	protected void initScreen(int n) {}
}
