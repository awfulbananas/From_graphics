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

//a class representing a window for drawing on.
//doesn't actually need the rest of this library to be useful, besides Keys and Mouse
//maybe these three should go in a different package
@SuppressWarnings("serial")
public class Frindow extends Panel implements ActionListener {
public static final Rectangle SCREEN_RECT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	//the Frame which goes on the screen
	private Frame frame;
	//the frame buffer, so that there isn't screen flickering with asynchronous operation
	private Queue<BufferedImage> contentBuffer;
	//the Mouse object for Managing MouseEvents
	private Mouse mouse;
	//the Keys object for managing KeyEvents
	private Keys keys;
	//the color space to be used, TYPE_INT_RGP by default
	private final int colorType;
	
	private Manager game;
	
	private Graphics initG;
	
	//a class for managing frame closure, so that the window actually closes when you hit the X button
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
	
	//constructs a new Frindow in the given color space of size (width, height)
	public Frindow(int colorType, int width, int height) {
		contentBuffer = new LinkedList<>();
		frame = new Frame("game");
		frame.add(this);
		mouse = new Mouse();
		keys = new Keys();
		this.colorType = colorType;
		frame.addMouseListener(mouse);
		frame.addKeyListener(keys);
		setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		frame.setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		setVisible(false);
		frame.setVisible(false);
		new WindowOperator(frame);
		frame.setResizable(false);
		frame.setFocusable(true);
		setFocusable(true);
	}
	
	//constructs a new Frindow in TYPE_INT_RGB color space, with width and height based on screen size
	public Frindow() {
		this(BufferedImage.TYPE_INT_RGB, SCREEN_RECT.width, SCREEN_RECT.height);
	}
	
	//this should be called before this Frindow is drawn on, 
	//this makes the Frindow visible, and sets the number of buffer frames to bufferCount
	//returns the graphics which should be used when calling .paint()
	public Graphics init(int bufferCount, Manager game) {
		this.game = game;
		for(int i = 0; i < bufferCount; i++) contentBuffer.add(new BufferedImage(getWidth(), getHeight(), colorType));
		setVisible(true);
		frame.setVisible(true);
		initG = getGraphics();
		return initG;
	}
	
	//paints the Frindow with the default Graphics
	public void defPaint() {
		(new Thread(() -> paint(initG))).start();
	}
	
	//draws the next frame to the screen,
	//g should be the graphics returned by .init()
	@Override
	public void paint(Graphics g) {
		game.drawAll(getNewFrame());
		if(contentBuffer.isEmpty()) {
			game.drawAll(getNewFrame());
		}
		g.drawImage(contentBuffer.remove(), 0, 0, this);
	}
	
	//adds a new frame to the frame buffer, and returns a Graphics object for drawing on that frame
	private Graphics getNewFrame() {
		BufferedImage next = new BufferedImage(getWidth(), getHeight(), colorType);
		contentBuffer.add(next);
		return next.getGraphics();
	}
	
	//adds a new frame to the frame buffer, and returns that frame as a BufferedImage
	public BufferedImage getNewFrameImg() {
		BufferedImage next = new BufferedImage(getWidth(), getHeight(), colorType);
		contentBuffer.add(next);
		return next;
	}
	
	//returns the Keys object for this Frindow
	public Keys getKeys() {
		return keys;
	}
	
	//returns the Mouse object for this Frindow
	public Mouse getMouse() {
		return mouse;
	}
	
	//I don't actually remember what this and tY do
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
	
	//not sure what this is for, I think I needed to implement it for some reason
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	//returns a Point representing the location of the mouse on the frame
	public Point getMousePos() {
		int mouseX = MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x;
		int mouseY = MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y;
		return new Point(mouseX, mouseY);
	}
}
