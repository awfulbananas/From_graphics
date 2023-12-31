package fromics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

//a class representing a field for inputing a number from the keyboard
public class NumberField extends InputField<Integer> {
	//the maximum length of
	private int maxLength;
	private String data;

	public NumberField(double x, double y, int maxLength) {
		this(x, y, maxLength, DEF_FONT);
	}
	
	public NumberField(double x, double y, int maxLength, Font f) {
		super(x, y, f);
		this.maxLength = maxLength;
		data = "";
	}
	
	public Integer getData() {
		if(data.length() > 0) {
			return Integer.parseInt(data);
		} else {
			return 0;
		}
	}
	
	@Override
	protected void onFirstLink() {
		addKeystrokeFunction((KeyEvent e) -> {
			if(data.length() < maxLength || maxLength == -1) {
				char c = e.getKeyChar();
				if(Character.isDigit(c)) {
						data += c;
				}
			}
			if(!data.isEmpty()) {
				if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					data = data.substring(0, data.length() - 1);
				}
			}
		});
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		g.setFont(font);
		g.drawString(data, (int)(xOff + X()), (int)(yOff + Y()));
		g.drawRect((int)(X() + xOff), (int)(Y() + yOff - 75), 230, 90);
	}

}
