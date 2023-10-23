package fromics;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//a class representing text to be displayed on the screen
public class TextElement extends Linkable {
	
	//a class representing a single line of text within the TextElement
	private class TextLine extends Linkable{
		//the text contents of this TextLine
		String text;
		//the Font to draw this text with
		Font f;
		
		//constructs a new TextLine at the given location relative to the TextElement
		//and with the given Font and String
		public TextLine(double x, double y, String str, Font f) {
			super(x, y);
			this.text = str;
			this.f = f;
		}
		
		//draws this TextLine using the given Graphics and x and y offsets
		@Override
		protected void draw(Graphics g, double xOff, double yOff, double angOff) {
			g.setFont(f);
			g.drawString(text, (int)(xOff + X()), (int)(yOff + Y()));
		}
		
	}
	
	//constructs a new TextElement at the given location
	public TextElement(double x, double y) {
		super(x, y);
	}
	
	//constructs a new TextElement at the given location using the data from the given
	// text element file
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
	
	//returns the next piece of data from the text element file being scanned
	private String nextData(Scanner in) {
		return in.nextLine().split(":")[1];
	}
	
	//adds the given String with the given x and y offsets and font, and different
	//lines of the String automatically becoming different TextLines
	public void addText(String str, double xOff, double yOff, Font f) {
		String[] strs = str.split("\n");
		double size = f.getSize2D();
		double loc = 0;
		for(String s : strs) {
			link(new TextLine(xOff, yOff + loc, s, f));
			loc += size;
		}
	}
	
	//draws this TextElement to the screen, empty because all of the visuals are from the TextLines
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {}

}
