package fromics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class NumberField extends InputField<Integer> {
	private int maxLength;
	private String data;
	private boolean keypressFuncAdded;

	public NumberField(double x, double y, int maxLength) {
		super(x, y);
		this.maxLength = maxLength;
		data = "";
		keypressFuncAdded = false;
	}
	
	public Integer getData() {
		if(data.length() > 0) {
			return Integer.parseInt(data);
		} else {
			return 0;
		}
	}
	
	@Override
	protected void onLink() {
		if(!keypressFuncAdded)
		addKeystrokeFunction((int e) -> {
			if(data.length() < maxLength) {
				char c = (char)e;
				if(Character.isDigit(c)) {
						data += c;
				}
			}
			if(!data.isEmpty()) {
				if(e == KeyEvent.VK_BACK_SPACE) {
					data = data.substring(0, data.length() - 1);
				}
			}
		});
		keypressFuncAdded = true;
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		g.setFont(DEF_FONT);
		g.drawString(data, (int)(xOff + X()), (int)(yOff + Y()));
		g.drawRect((int)(X() + xOff), (int)(Y() + yOff - 75), 230, 90);
	}

}
