package fromics;

import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

//a class to represent a manager for different screens of a game or application
//it's linkable so that you could have different managers for sub-menus or something similar
//I usually put my main method in here
public abstract class Manager extends Background {
	public static final int DEF_FRAME_DELAY = 15;
	public static final int START_DELAY = 20;
	protected Background[] screens;
	protected int screen;
	private final int frameDelay;
	private boolean updated;
	private long dt;
	
	//constructs a new Manager with the given Frindow
	//managers are constructed at (0, 0) by default, 
	//so nesting them won't create a weird offset
	public Manager(Frindow observer) {
		this(observer, DEF_FRAME_DELAY);
	}
	
	public Background currentScreen() {
		return screens[screen];
	}
	
	public Manager(Frindow observer, int frameDelayMillis) {
		super(observer);
		frameDelay = frameDelayMillis;
		dt = frameDelayMillis * 1000000;
		updated = true;
		setX(0);
		setY(0);
	}
	
	//updates the current screen of the Manager,
	//and switches to the next one if it needs to
	//feel free to override this if you want to change
	//how screens are swapped between
	@Override
	public boolean updateAll() {
		boolean updateVal = update();
		if(screens[screen].nextScreen()) {
			screens[screen].close();
			initScreen((screen + 1) % screens.length);
			screen = (screen + 1) % screens.length;
		}
		screens[screen].updateAll();
		updated = true;
		return updateVal;
	}
	
	//draws the current Screen of this Manager and all of it's children
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
		screens[screen].drawAll(g);
	}
	
	//this method should initialize screen n in screens
	
	//for example, if screen 0 is a title screen, and screen 1 is the game,
	//the if this method is passed 0, it should initialize the title screen at index
	//0 of screens, and if it's passed 1, then it should
	protected abstract void initScreen(int n);
	
	public void startLoop() {
		Timer runner = new Timer();
		runner.schedule(this.new Run(), START_DELAY, frameDelay);
	}
	
	public void startVariableLoop() {
		Thread runner = new Thread(() -> {
			boolean running = true;
			Run r = this.new Run();
			long time = System.nanoTime();
			while(running) {
				long newTime = System.nanoTime();
				dt = (int)(newTime - time);
				r.run();
				int elapsedTime = (int)(System.nanoTime() - newTime);
				while(elapsedTime < frameDelay * 1000000) {
					elapsedTime = (int)(System.nanoTime() - newTime);
				}
				time = newTime;
			}
		});
		runner.start();
	}
	
	public int dt() {
		return (int) (dt / 1000l);
	}

	private class Run extends TimerTask {
		public void run() {
			if(updated) {
				updated = false;
				updateAll();
			}
			observer.defPaint();
		}
	}
	
	

}