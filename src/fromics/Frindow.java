package fromics;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Frindow extends Panel implements ActionListener {
public static final Rectangle SCREEN_RECT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	private Frame frame;
	private Queue<BufferedImage> contentBuffer;
	private Mouse mouse;
	private Keys keys;
	private int colorType;
	
	private class WindowOperator extends WindowAdapter {
		Frame f;
		
		public WindowOperator(Frame parent) {
			f = parent;
			f.addWindowListener(this);
		}
		
		@Override
		public void windowClosing (WindowEvent e) {    
            f.dispose();  
            System.exit(0);
        }  
	}
	
	public Frindow(int colorType) {
		contentBuffer = new LinkedList<>();
		frame = new Frame("game");
		frame.add(this);
		mouse = new Mouse();
		keys = new Keys();
		this.colorType = colorType;
		frame.addMouseListener(mouse);
		frame.addKeyListener(keys);
		setBounds(0, 0, SCREEN_RECT.width, SCREEN_RECT.height);
		frame.setBounds(0, 0, SCREEN_RECT.width, SCREEN_RECT.height);
		System.out.println(SCREEN_RECT);
		System.out.println(getBounds());
		System.out.println(getWidth() + ", " + getHeight());
		setVisible(false);
		frame.setVisible(false);
		new WindowOperator(frame);
		frame.setResizable(false);
	}
	
	public Frindow() {
		this(BufferedImage.TYPE_INT_RGB);
	}
	
	public void setImageColorType(int colorType) {
		//TODO
	}
	
	public Graphics init(int bufferCount) {
		for(int i = 0; i < bufferCount; i++) contentBuffer.add(new BufferedImage(getWidth(), getHeight(), colorType));
		setVisible(true);
		frame.setVisible(true);
		return getGraphics();
	}
	
	@Override
	public void paint(Graphics g) {
		if(!contentBuffer.isEmpty()) {
			g.drawImage(contentBuffer.remove(), 0, 0, this);
		} else {
			super.paint(g);
		}
	}
	
	public void addFrame(BufferedImage img) {
		contentBuffer.add(img);
	}
	
	public Graphics getNewFrame() {
		BufferedImage next = new BufferedImage(getWidth(), getHeight(), colorType);
		contentBuffer.add(next);
		return next.getGraphics();
	}
	
	public BufferedImage getNewFrameImg() {
		BufferedImage next = new BufferedImage(getWidth(), getHeight(), colorType);
		contentBuffer.add(next);
		return next;
	}
	
	public Keys getKeys() {
		return keys;
	}
	
	public Mouse getMouse() {
		return mouse;
	}
	
	public int tX(int x) {
		return x - this.getLocationOnScreen().x;
	}
	
	public double tX(double x) {
		return x + this.getLocationOnScreen().x;
	}
	 
	public int tY(int y) {
		return y - this.getLocationOnScreen().y;
	}
	
	public double tY(double y) {
		return y + this.getLocationOnScreen().y;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public Point getMousePos() {
		int mouseX = MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x;
		int mouseY = MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y;
		return new Point(mouseX, mouseY);
	}

	public static int randomRange(int origin, int bound) {
		Random random = new Random();
		return random.nextInt(bound-origin)+origin;
	}
}
