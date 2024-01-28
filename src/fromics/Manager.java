package fromics;

import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

//a class to represent a manager for different screens of a game or application
//it's linkable so that you could have different managers for sub-menus or something similar
//I usually put my main method in here
public abstract class Manager extends Background {
	public static final int DEF_DRAW_DELAY = 15;
	public static final int DEF_UPDATE_DELAY = 15;
	public static final int START_DELAY = 20;
	protected Background[] screens;
	protected int screen;
	private final int drawDelay;
	private final int updateDelay;
	protected boolean updated;
	private long dt;
	
	//TODO: add a constructor for a Manager to be used as a sub-menu or other non-main screens
	//or else create a different class for that purpose
	
	//constructs a new Manager with the given Frindow
	//managers are constructed at (0, 0) by default, 
	//so nesting them won't create a weird offset
	public Manager(Frindow observer) {
		this(observer, DEF_DRAW_DELAY, DEF_UPDATE_DELAY);
	}
	
	//constructs a new Manager with the given Frindow, draw delay, and update delay
		public Manager(Frindow observer, int drawDelayMillis, int updateDelayMillis) {
			super(observer);
			updateDelay = updateDelayMillis;
			drawDelay = drawDelayMillis;
			dt = updateDelayMillis * 1000000;
			updated = true;
			setX(0);
			setY(0);
			hasLinked = true;
		}
	
	//returns the currently displayed screen of this Manager
	public Background currentScreen() {
		return screens[screen];
	}
	
	//updates the current screen of the Manager,
	//and switches to the next one if it needs to
	//feel free to override this if you want to change
	//how screens are swapped between
	@Override
	public boolean updateAll() {
		boolean updateVal = update();
		observer.update();
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
	
	//draws the current Screen of this Manager and all of it's children
	public void drawAll(Graphics g) {
		draw(g, 0, 0, 0);
		if(screens[screen] == null) return;
		screens[screen].drawAll(g);
	}
	
	//this method should initialize screen n in screens
	
	//for example, if screen 0 is a title screen, and screen 1 is the game,
	//the if this method is passed 0, it should initialize the title screen at index
	//0 of screens, and if it's passed 1, then it should
	protected abstract void initScreen(int n);
	
	//begins the loop of updating Linkables and drawing frames, using java Timers to run the
	//tasks as close to the given delays as possible
	public void startLoop() {
		Timer updateRunner = new Timer();
		updateRunner.schedule(this.new RunUpdate(), START_DELAY, updateDelay);
		Timer drawRunner = new Timer();
		drawRunner.schedule(this.new RunDraw(), START_DELAY, drawDelay);
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
	
	

}