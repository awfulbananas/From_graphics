package fromics;

import java.awt.Font;
import java.awt.Graphics;

public abstract class InputField<E> extends Linkable {
	public static final Font DEF_FONT = new Font("Cambria Math", Font.PLAIN, 100);
	
	public abstract E getData();

	public InputField(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		// TODO Auto-generated method stub

	}

}
