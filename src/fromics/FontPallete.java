package fromics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;
//a class which when run, displays a window with a sample of every default font option,
//which is useful for picking fonts
public class FontPallete extends Manager {
	String[] allFonts;
	
	public FontPallete(Frindow observer) {
		super(observer);
		allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}

	public static void main(String[] args) throws InterruptedException {
		Frindow win = new Frindow();
		FontPallete pallete = new FontPallete(win);
		win.init(1, pallete);
		Timer t = new Timer();
		Thread.sleep(1000);
		t.schedule(pallete.getDrawer(), 10l, 10l);
	}
	
	private class DrawTask extends TimerTask {
		public void run() {
			observer.defPaint();
		}
	}
	
	@Override
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
	}
	
	@Override
	public void draw(Graphics g, double xOff, double yOff, double angOff) {
		for (int i = 0; i < allFonts.length; i++) {
			g.setFont(new Font(allFonts[i], Font.PLAIN, 20));
			g.drawString(allFonts[i].substring(0, Math.min(allFonts[i].length(), 15)), 20  + 185 * ((i * 20 + 20) / (observer.getHeight() - 20)), (i * 20 + 20) % (observer.getHeight() - 20));
		}
	}
	
	public DrawTask getDrawer() {
		return new DrawTask();
	}
	
	@Override
	protected void initScreen(int n) {
		
	}
}
