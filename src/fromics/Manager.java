package fromics;

import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

//a class to represent a manager for different screens of a game or application
//it's linkable so that you could have different managers for sub-menus or something similar
//I usually put my main method in here
public abstract class Manager extends Background {
	public static final int FRAME_DELAY = 15;
	public static final int START_DELAY = 15;
	protected Background[] screens;
	private int screen;
	
	//constructs a new Manager with the given Frindow
	//managers are constructed at (0, 0) by default, 
	//so nesting them won't create a weird offset
	public Manager(Frindow observer) {
		super(observer);
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
		runner.schedule(this.new Run(), START_DELAY, FRAME_DELAY);
	}

	private class Run extends TimerTask {
		public void run() {
			updateAll();
//			Thread drawer = new Thread(() -> {
//				observer.defPaint();
//			});
//			drawer.start();
			observer.defPaint();
		}
	}

}