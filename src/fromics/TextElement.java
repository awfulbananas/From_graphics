package fromics;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextElement extends Linkable {
	
	private class TextLine extends Linkable{
		String text;
		Font f;

		public TextLine(double x, double y, String str, Font f) {
			super(x, y);
			this.text = str;
			this.f = f;
		}

		@Override
		protected void draw(Graphics g, double xOff, double yOff, double angOff) {
			g.setFont(f);
			g.drawString(text, (int)(xOff + X()), (int)(yOff + Y()));
		}
		
	}

	public TextElement(double x, double y) {
		super(x, y);
	}
	
	public TextElement(double x, double y, String textFileName) {
		super(x, y);
		File txtFile = new File(textFileName);
		Scanner txtIn;
		try {
			txtIn = new Scanner(txtFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		String fontName = txtIn.nextLine().split(":")[1];
		while(txtIn.hasNext()) {
			String next = txtIn.nextLine();
			if(next.equals("end")) break;
			String nextTxt = next.split(":")[1];
			double xVal = Double.parseDouble(nextData(txtIn));
			double yVal = Double.parseDouble(nextData(txtIn));
			int fontSize = Integer.parseInt(nextData(txtIn));
			Font nextFont = new Font(fontName, Font.PLAIN, fontSize);
			
			nextTxt = nextTxt.replaceAll("\\\\n", "\n");
			
			addText(nextTxt, xVal, yVal, nextFont);
		}
	}
	
	private String nextData(Scanner in) {
		return in.nextLine().split(":")[1];
	}
	
	public void addText(String str, double xOff, double yOff, Font f) {
		String[] strs = str.split("\n");
		double size = f.getSize2D();
		double loc = 0;
		for(String s : strs) {
			link(new TextLine(xOff, yOff + loc, s, f));
			loc += size;
		}
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
	}

}
