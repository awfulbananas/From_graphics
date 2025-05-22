package fromics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

//a class to represent a manager for different screens of a game or application
//it manages the update/draw loop(s), and extends Screens
//it's linkable so that you could have different managers for sub-menus or something similar
//I usually put my main method in here
public abstract class Manager extends Screens {
	public static final int DEF_DRAW_DELAY = 15;
	public static final int DEF_UPDATE_DELAY = 15;
	public static final int START_DELAY = 20;
	private final int drawDelay;
	private final int updateDelay;

	private long dt;
	private boolean updated;
	protected Frindow observer;
	
	//constructs a new Manager with the given Frindow
	//managers are constructed at (0, 0) by default, 
	//so nesting them won't create a weird offset
	public Manager(Frindow observer) {
		this(observer, DEF_DRAW_DELAY, DEF_UPDATE_DELAY);
	}
	
	//constructs a new Manager with the given Frindow, draw delay, and update delay
	public Manager(Frindow observer, int drawDelayMillis, int updateDelayMillis) {
		super();
		updateDelay = updateDelayMillis;
		drawDelay = drawDelayMillis;
		dt = updateDelayMillis * 1000000L;
		setX(0);
		setY(0);
		hasLinked = true;
		updated = true;
	}
	
	//begins the loop of updating Linkables and drawing frames, using java Timers to run the
	//tasks as close to the given delays as possible
	public void startLoop() {
		Timer updateRunner = new Timer();
		updateRunner.schedule(this.new RunUpdate(), START_DELAY, updateDelay);
		Timer drawRunner = new Timer();
		drawRunner.schedule(this.new RunDraw(), START_DELAY, drawDelay);
	}

	@Override
	public boolean updateAll() {
		observer.update();
		boolean updateVal = update();
		if(screens[screen].nextScreen()) {
			int nextScreen = screens[screen].getNextScreen();
			nextScreen = nextScreen==-1?(screen+1)%screens.length:nextScreen;
			screens[screen].close();
			initScreen(nextScreen);
			screen = nextScreen;
		}
		screens[screen].updateAll();
		updated = true;
		return updateVal;
	}
	
	//begins the loop of updating Linkables and drawing frames, but using a variable framerate method
	//which prevents updating or drawing things at the same time in slow programs, but removes the
	//consistency of framerate present in the non-variable loop
	public void startVariableLoop() {
		Thread updateRunner = new Thread(() -> {
			boolean running = true;
			RunUpdate r = this.new RunUpdate();
			long time = System.nanoTime();
			while(running) {
				long newTime = System.nanoTime();
				dt = (int)(newTime - time);
				r.run();
				int elapsedTime = (int)(System.nanoTime() - newTime);
				while(elapsedTime < updateDelay * 1000000) {
					elapsedTime = (int)(System.nanoTime() - newTime);
				}
				time = newTime;
			}
		});
		Thread drawRunner = new Thread(() -> {
			boolean running = true;
			RunDraw r = this.new RunDraw();
			while(running) {
				long newTime = System.nanoTime();
				r.run();
				int elapsedTime = (int)(System.nanoTime() - newTime);
				while(elapsedTime < drawDelay * 1000000) {
					elapsedTime = (int)(System.nanoTime() - newTime);
				}
			}
		});
		updateRunner.start();
		drawRunner.start();
	}

	public void startSynchronizedLoop() {
		Thread runner = new Thread(() -> {
			boolean running = true;
			RunUpdate update = this.new RunUpdate();
			RunDraw draw = this.new RunDraw();
			long prevTime = System.nanoTime();
			while(running) {
				update.run();
				draw.run();
				long newTime = System.nanoTime();
				long elapsedTime = newTime - prevTime;
				while(elapsedTime < (drawDelay + updateDelay) * 1000000L) {
					newTime = System.nanoTime();
					elapsedTime = newTime - prevTime;
				}
				dt = elapsedTime;
				prevTime = newTime;
			}
		});
		runner.start();
	}
	
	@Override
	//returns the amount of time that passed between the start of the previous frame and the start of this frame
	public int dt() {
		return (int) (dt / 1000l);
	}
	
	//a class representing a task for updating all linked Linkables at a regular interval using a java Timer
	private class RunUpdate extends TimerTask {
		public void run() {
			if(updated) {
				updated = false;
				updateAll();
			}
		}
	}
	
	//a class representing a task for drawing all linked Linkables at a regular interval using a java Timer
	private class RunDraw extends TimerTask{
		public void run() {
			observer.defPaint();
		}
	}

	//returns the Mouse object for the current Frindow
	public Mouse getMouse() {
		return observer.getMouse();
	}

	//returns whether the Frindow this background is displayed on uses a colour model with an alpha channel
	protected boolean hasAlpha() {
		int colorType = observer.getColorType();
		return colorType == BufferedImage.TYPE_INT_ARGB || colorType == BufferedImage.TYPE_4BYTE_ABGR;
	}

	//returns a Point representing the lower-right corner of the bounds of the screen
	@Override
	public Point getMaxBounds() {
		return new Point(observer.getWidth(), observer.getHeight());
	}

	//returns a Point representing the upper-left corner of the screen
	@Override
	public Point getMinBounds() {
		return new Point();
	}

	//returns the width of the window
	@Override
	public int getScreenWidth() {
		return observer.getWidth();
	}

	//returns the height of the window
	@Override
	public int getScreenHeight() {
		return observer.getHeight();
	}

	//adds a function to be run whenever a keystroke happens (including control keys like arrows, shift and ctrl)
	@Override
	protected void addKeystrokeFunction(KeypressFunction func) {
		observer.addKeystrokeFunction(func);
	}

	@Override
	public void addMouseEventFunction(MouseEventFunction func) {
		observer.addMouseEventFunction(func);
	}
}