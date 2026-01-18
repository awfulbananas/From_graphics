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

/**
 * Frindow is a class representing a window on the screen which covers much
 * of the boilerplate otherwise necessary in a jav program, as well as facilitating
 * more convenient frame drawing and input management.
 * you shouldn't really interact with this class much when making a program besides
 * feeding it to constructors that want it and initializing one to begin with
 * @author Joseph Fromel
 */
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

	/**
	 * This class exists solely to make sure the window closes when you hit
	 * the close button or alt-f4, since java windows don't do that by default
	 */
	private class WindowOperator extends WindowAdapter {
		//the Frame for the WindowOperator
		Frame f;

		/**
		 * constructs a new WindowOperator for the given frame
		 * @param parent the frame this WindowOperator will be operating on
		 */
		public WindowOperator(Frame parent) {
			f = parent;
			f.addWindowListener(this);
		}

		/**
		 * closes the window, executes closing behavior,
		 * and exits the program when a window closing event is processes
		 * @param e the event to be processed
		 */
		@Override
		public void windowClosing (WindowEvent e) { 
			f.dispose();
			game.close();
            System.exit(0);
        }  
	}

	/**
	 * a component to fix a minor bug where the frame snapping to the edge of the screen would sometimes fail to resize the Frindow correctly
	 */
	private class FixResizeComponent extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			new Thread(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                setBounds(getX(), getY(), frame.getWidth(), frame.getHeight());
			}).start();
		}
	}


	/**
	 * returns the BufferedImage color type being used to draw frames
	 * @return the current color type of this Frindow
	 */
	public int getColorType() {
		return colorType;
	}

	/**
	 * called regularly to process input events
	 */
	public void update() {
		keys.process();
		mouse.process();
	}

	/**
	 * constructs a new Frindow with the given colorType for drawing frames,
	 * initial width and height of the window, and window name
	 * @param colorType the color space to be used when drawing frames
	 * @param width the initial width of the window (can be resized)
	 * @param height the initial height of the window (can be resized)
	 * @param name the name of the window
	 */
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
		addMouseWheelListener(mouse);
		frame.addComponentListener(new FixResizeComponent());
		setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		frame.setBounds(SCREEN_RECT.width / 2 - width / 2, SCREEN_RECT.height / 2 - height / 2, width, height);
		setVisible(false);
		frame.setVisible(false);
		new WindowOperator(frame);
		frame.setResizable(false);
		frame.setFocusable(true);
		setFocusable(true);
	}

	public void setResizable(boolean resizable) {
		frame.setResizable(resizable);
	}

	/**
	 * constructs a new Frindow with the given color type for drawing frames,
	 * initial width and height of the window, and a default window name of "game"
	 * @param colorType the color space to be used when drawing frames
	 * @param width the initial width of the window (can be resized)
	 * @param height the initial height of the window (can be resized)
	 */
	public Frindow(int colorType, int width, int height) {
		this(colorType, width, height, "game");
	}

	/**
	 * constructs a new Frindow with integer RGB color space for drawing frames,
	 * an initial width and height proportional to the device screen size,
	 * and a window name of "game"
	 */
	public Frindow() {
		this(BufferedImage.TYPE_INT_RGB, SCREEN_RECT.width, SCREEN_RECT.height);
	}

	/**
	 * initializes some functionality of the Frindow, including making the
	 * window visible, returning a useless Graphics object I forgot to get rid of(TODO:fix that)
	 * @param bufferCount the size of the frame buffer when drawing frames. a larger buffer creates
	 *                    more delay between updates and changes on screen while a smaller buffer can create
	 *                    screen tearing, so  find that 3 is usually a good midpoint
	 * @param game the Manager object which is used when closing the window to execute closing functionality
	 *             and when drawing frames to the screen
	 * @return a Graphics object which is mostly useless which I forgot to get rid of returning
	 */
	public void init(int bufferCount, Manager game) {
		this.game = game;
		game.keysPressed = keys.codes;
		for(int i = 0; i < bufferCount; i++) contentBuffer.add(new BufferedImage(getWidth(), getHeight(), colorType));
		targetFrameBufferSize = bufferCount;
		setVisible(true);
		frame.setVisible(true);
		initG = getGraphics();
	}

	/**
	 * starts a thread which draws the next frame in the frame buffer to the screen
	 * and uses the drawAll method of the Manager game to create the next frame of the buffer
	 */
	public void defPaint() {
		if(!painting) {
			painting = true;
			(new Thread(() -> paint(initG))).start();
		}
	}

	/**
	 * draws the next frame from the frame buffer to the screen and uses the drawAll method
	 * of the Manager game to create the next frame of the buffer, without using a separate thread like defPaint()
	 */
	public void syncDefPaint() {
		if(!painting) {
			painting = true;
			paint(initG);
		}
	}

	/**
	 * creates a new frame, adds it to the frame buffer, then uses the drawAll method
	 * from the Manger game to draw on that frame
	 * @param g doesn't actually matter, and I'm realizing I should have cleaned up this class a while ago
	 */
	@Override
	public void paint(Graphics g) {
		BufferedImage img = getNewFrameImg();
		game.drawAll(img.getGraphics(), img);
		while(contentBuffer.size() < targetFrameBufferSize) {
			img = getNewFrameImg();
			game.drawAll(img.getGraphics(), img);
		}
		g.drawImage(contentBuffer.remove(), 0, 0, this);
		painting = false;
	}

	/**
	 * creates a new BufferedImage and adds it to the frame buffer, returning that BufferedImage
	 * @return the new BufferedImage to be added to the frame buffer
	 */
	public BufferedImage getNewFrameImg() {
		BufferedImage next = new BufferedImage(getWidth(), getHeight(), colorType);
		contentBuffer.add(next);
		return next;
	}

	/**
	 * returns the Keys object for this Frindow, which helps manage input from the keyboard
	 * @return the Keys object of his Frindow
	 * @see Keys
	 */
	public Keys getKeys() {
		return keys;
	}

	/**
	 * calculates the position from the upper right corner of the window given a point
	 * in global screen space
	 * @param globalLoc a Point in global screen space
	 * @return a Point representing the location of the given Point relative to the window
	 */
	public Point locOnScreenFromGlobalLoc(Point globalLoc) {
		return globalLoc.copy().sub(this.getLocationOnScreen().x, this.getLocationOnScreen().y);
	}

	/**
	 * gets the position of the mouse on the window
	 * @return the position of the mouse pointer relative to the upper left corner of the window
	 */
	public Point getMousePos() {
		return mouse.getMouseLoc();
	}

	/**
	 * registers a KeypressFunction to be run whenever a key is pressed and released,
	 * giving the function the corresponding KeyEvent as it's argument
	 * @param func the KeypressFunction which will be run when a key is typed
	 */
	public void addKeystrokeFunction(KeypressFunction func) {
		keys.addKeypressFunction(func);
	}

	public void removeKeystrokeFunction(KeypressFunction func) {
		keys.removeKeystrokeFunction(func);
	}

	/**
	 * registers a mouse event function to be run whenever a mouse button from one to three is pressed and released,
	 * or the mouse wheel is scrolled, giving the function the corresponding MouseEvent as it's argument
	 * @param func the MouseEventFunction which will be run when the mouse is clicked or mouse wheel scrolled
	 */
	public void addMouseEventFunction(MouseEventFunction func) {
		mouse.addMouseEventFunction(func);
	}

	public void removeMouseEventFunction(MouseEventFunction func) {
		mouse.removeMouseEventFunction(func);
	}

	/**
	 * returns the Mouse object associated with this Frindow, which helps manage input from the mouse
	 * @return the Mouse object associated with this Frindow
	 */
	public Mouse getMouse() {
		return mouse;
	}
}
