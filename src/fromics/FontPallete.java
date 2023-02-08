package fromics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
//a class which when run, displays a window with a sample of every default font option,
//which is useful for picking fonts

public class FontPallete {
	public static void main(String[] args) {
		Frindow win = new Frindow();
		Graphics winG = win.init(0);
		String[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Graphics f1 = win.getNewFrame();
		for (int i = 0; i < allFonts.length; i++) {
			f1.setFont(new Font(allFonts[i], Font.PLAIN, 20));
			f1.drawString(allFonts[i].substring(0, Math.min(allFonts[i].length(), 15)), 20  + 175 * ((i * 20 + 20) / (win.getHeight() - 20)), (i * 20 + 20) % (win.getHeight() - 20));
		}
		win.paint(winG);
	}
}
