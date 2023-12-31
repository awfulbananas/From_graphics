package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class TextField extends InputField<String> {
	public static final double HOLD_TIME_BEFORE_SPAM = 1;
	public static final double KEY_SPAM_DELAY = 0.5;
	private final FontRenderContext fContext = new FontRenderContext(null, false, false);
	
	private String data;
	private int maxLength;
	private int cursorInd;
	
	public TextField() {
		this(0, 0, -1);
	}
	
	public TextField(double x, double y) {
		this(x, y, -1);
	}
	
	public TextField(double x, double y, int maxLength) {
		super(x, y);
		this.maxLength = maxLength;
		data = "";
		cursorInd = 0;
	}

	@Override
	public String getData() {
		return data;
	}
	
	@Override
	public boolean update() {
		//TODO:  code for holding delete here
		return false;
	}
	
	@Override
	public void onFirstLink() {
		addKeystrokeFunction((KeyEvent e) -> {
			int num = e.getKeyCode();
			char c = e.getKeyChar();
			System.out.println(c);
			if(!(Character.isIdentifierIgnorable(c) || num == KeyEvent.VK_SHIFT || (num > 36 && num <= 40))) {
				if(cursorInd == 0) {
					data = c + data;
				} else if(cursorInd == data.length()){
					data = data + c;
				} else {
					data = data.substring(0, cursorInd + 1) + c + data.substring(cursorInd + 1);
				}
				cursorInd++;
			}
			if(!data.isEmpty()) {
				switch(num) {
					case KeyEvent.VK_BACK_SPACE:
						if(cursorInd > 0) {
							if(cursorInd < data.length()) {
								data = data.substring(0, cursorInd - 1) + data.substring(cursorInd);
							} else {
								data = data.substring(0, data.length() - 1);
							}
							cursorInd--;
						}
						break;
					case KeyEvent.VK_DELETE:
						if(cursorInd < data.length()) {
							data = data.substring(0, cursorInd) + data.substring(cursorInd + 1);
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(cursorInd < data.length()) {
							cursorInd++;
						}
						break;
					case KeyEvent.VK_LEFT:
						if(cursorInd > 0) {
							cursorInd--;
						}
						break;
				}
			}
		});
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		g.setFont(font);
		g.drawString(data, (int)(xOff + X()), (int)(yOff + Y()));
		Rectangle2D dataBounds;
		if(maxLength >= 0) {
			dataBounds = font.getStringBounds("W".repeat(maxLength), fContext);
		} else {
			dataBounds = font.getStringBounds(data, fContext);
		}
		double dataWidth = dataBounds.getWidth();
		double dataHeight = dataBounds.getHeight();
		g.drawRect((int)(X() + xOff), (int)(Y() + yOff - dataHeight), (int)dataWidth + 10, (int)dataHeight + 10);
		g.setColor(Color.BLUE);
		double cursorX = font.getStringBounds(data.substring(0, cursorInd), fContext).getWidth();
		g.drawLine((int)(X() + xOff + cursorX), (int)(Y() + yOff - dataHeight + 10), (int)(X() + xOff + cursorX), (int)(Y() + yOff));
	}

}
