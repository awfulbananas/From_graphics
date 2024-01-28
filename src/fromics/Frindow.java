package fromics;

import java.awt.Graphics;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

//a class representing a window for drawing on.
//doesn't actually need the rest of this library to be useful, besides Keys and Mouse
//maybe these three should go in a different package
@SuppressWarnings("serial")
public class Frindow extends Panel {
public static final Rectangle SCREEN_RECT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	//the Frame which goes on the screen
	private Frame frame;
	//the frame buffer, so that there isn't screen flickering with asynchronous operation
	private Queue<BufferedImage> contentBuffer;
	//the Keys object for managing KeyEvents
	private Keys keys;
	//the Mouse object for managing MouseEvents
	private Mouse mouse;
	//the color space to be used, TYPE_INT_RGP by default
	private final int colorType;
	//the target size for the frame buffer, if there's to few frames, the Frindow will draw more to correct,
	//and if there are to many, it will display more to the screen without drawing more to empty the queue
	private int targetFrameBufferSize;
	//the Manager for the program, used for drawing frames
	private Manager game;
	//the graphics created when initialized, which is stored because it seems to work best to always use
	//that one
	private Graphics initG;
	//whether or not a frame is currently being painted to the screen
	private boolean painting;
	
	//a class for managing frame closure, so that the window actually closes when you hit the X button
	private class WindowOperator extends WindowAdapter {
		//the Frame for the WindowOperator
		Frame f;
		
		//constructs a new WindowOperator for the given Frame
		public WindowOperator(Frame parent) {
			f = parent;
			f.addWindowListener(this);
		}
		
		//called when the x button of the window is clicked
		@Override
		public void windowClosing (WindowEvent e) { 
			f.dispose();
			game.close();
            System.exit(0);
        }  
	}
	
	
	//returns the current BufferedImage color model of this Frindow
	public int getColorType() {
		return colorType;
	}
	
	//called regularly to keep up with keystroke events
	public void update() {
		keys.process();
		mouse.processAll();
	}
	
	//constructs a new Frindow in the given color space of size (width, height)
	public Frindow(int colorType, int width, int height, String name) {
		contentBuffer = new LinkedList<>();
		painting = false;
		frame = new Frame(name);
		frame.add(this);
		keys = new Keys();
		mouse = new Mouse(this);
		this.colorType = colorType;
		addKeyListener(keys);
		addMouseListener(mouse);
		setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		frame.setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		setVisible(false);
		frame.setVisible(false);
		new WindowOperator(frame);
		frame.setResizable(true);
		frame.setFocusable(true);
		setFocusable(true);
	}
	//constructs a new Frindow in the given color space of size (width, height)
	public Frindow(int colorType, int width, int height) {
		this(colorType, width, height, "game");
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
		targetFrameBufferSize = bufferCount;
		setVisible(true);
		frame.setVisible(true);
		initG = getGraphics();
		return initG;
	}
	
	//paints the Frindow with the default Graphics
	public void defPaint() {
		if(!painting) {
			painting = true;
			(new Thread(() -> paint(initG))).start();
		}
	}
	
	//draws the next frame to the screen,
	//g should be the graphics returned by .init()
	@Override
	public void paint(Graphics g) {
		game.drawAll(getNewFrame());
		while(contentBuffer.size() < targetFrameBufferSize) {
			game.drawAll(getNewFrame());
		}
		g.drawImage(contentBuffer.remove(), 0, 0, this);
		painting = false;
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
	
	//returns the the location of a point in the window from a location in global space
	public Point locOnScreenFromGlobalLoc(Point globalLoc) {
		return globalLoc.copy().sub(this.getLocationOnScreen().x, this.getLocationOnScreen().y);
	}
	
	//returns a Point representing the location of the mouse on the frame
	public Point getMousePos() {
		return locOnScreenFromGlobalLoc(mouse.getMouseLoc());
	}
	
	//adds a function to be run whenever a keystroke is detected,
	//in this case, the keystroke is for any key rather than the usual
	//only alphanumeric and symbolic keys
	public void addKeystrokeFunction(KeypressFunction func) {
		keys.addKeypressFunction(func);
	}

	public Mouse getMouse() {
		return mouse;
	}
}
