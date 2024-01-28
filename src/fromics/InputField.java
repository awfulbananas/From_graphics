package fromics;

import java.awt.Font;
import java.awt.Graphics;

//a class representing a field for the input of some value through typing
public abstract class InputField<E> extends Linkable {
	//the default font size to draw with
	public static final int DEF_FONT_SIZE = 50;
	//the default font to draw with
	public static final Font DEF_FONT = new Font("Cambria Math", Font.PLAIN, DEF_FONT_SIZE);
	
	//whether or not this field is selected for input
	protected boolean selected;
	
	//the font used to draw this InputField to the screen
	protected Font font;
	
	//should return the inputed data
	public abstract E getData();
	
	public void setSelected(boolean s) {
		this.selected = s;
	}

	//constructs a new InputField at the given location
	public InputField(double x, double y) {
		this(x, y, DEF_FONT);
	}
	
	//constructs a new InputField at the given location and with the given Font
	public InputField(double x, double y, Font f) {
		super(x, y);
		selected = true;
		font = f;
	}

	//draws this InputField to the screen, usually displaying the currently inputed data in some way
	@Override
	protected abstract void draw(Graphics g, double xOff, double yOff, double angOff);

}
