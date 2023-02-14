package fromics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
//a class which when run, displays a window with a sample of every default font option,
//which is useful for picking fonts

//needs updated, won't work right now

public class FontPallete extends Manager {
	public FontPallete(Frindow observer) {
		super(observer);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Frindow win = new Frindow();
		Graphics winG = win.init(0, null);
		String[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Graphics f1 = win.getNewFrame();
		for (int i = 0; i < allFonts.length; i++) {
			f1.setFont(new Font(allFonts[i], Font.PLAIN, 20));
			f1.drawString(allFonts[i].substring(0, Math.min(allFonts[i].length(), 15)), 20  + 175 * ((i * 20 + 20) / (win.getHeight() - 20)), (i * 20 + 20) % (win.getHeight() - 20));
		}
		win.paint(winG);
	}
	
	@Override
	public void draw(Graphics g, double xOff, double yOff, double angOff) {
		
	}
	
	@Override
	protected void initScreen(int n) {
		// TODO Auto-generated method stub
		
	}
}
