package fromics;

import fromics.events.Event;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * this class represents a Manager to handle running the application, getting values from the Frindow,
 * and swapping between the main screens of an application/game
 * I usually put my main method here
 * @author awfulbananas
 */
public abstract class Manager extends Screens {
	/**
	 * the default minimum delay between frames
	 */
	public static final int DEF_DRAW_DELAY = 15;
	/**
	 * the default minimum delay between update ticks
	 */
	public static final int DEF_UPDATE_DELAY = 15;
	/**
	 * the delay before starting the update and draw loops when using startLoop()
	 */
	public static final int START_DELAY = 20;

	public static final boolean HSYNzc = true;
	/**
	 * the minimum delay between drawing frames.
	 * this should generally be at least 1
	 */
	private final int drawDelay;
	/**
	 * the minimum delay between update ticks
	 */
	private final int updateDelay;
	/**
	 * the queue of Events to execute in order
	 */
	private final Queue<Event> eventQueue;
	/**
	 * a list of all the currently executing Events
	 */
	private final List<Event> activeEvents;

	/**
	 * the time between the start of the current update tick and the previous one
	 */
	private long dt;
	/**
	 * whether the previous update tick has finished.
	 * used to avoid overlapping update ticks, which would be a pain to program around
	 */
	private boolean updated;
	/**
	 * the Frindow associated with this Manager
	 */
	protected Frindow observer;

	/**
	 * constructs a new Manager with the given Frindow
	 * @param observer the Frindow to construct this manager with
	 */
	public Manager(Frindow observer) {
		this(observer, DEF_DRAW_DELAY, DEF_UPDATE_DELAY);
	}

	/**
	 * constructs a new Manager with the given Frindow and draw/update delays
	 * @param observer the Frindow to construct this Manager with
	 * @param drawDelayMillis the minimum delay between drawing frames to use
	 * @param updateDelayMillis the minimum delay between update ticks to use
	 */
	public Manager(Frindow observer, int drawDelayMillis, int updateDelayMillis) {
		super();
		updateDelay = updateDelayMillis;
		drawDelay = drawDelayMillis;
		dt = updateDelayMillis * 1000000L;
		eventQueue = new LinkedList<>();
		activeEvents = new ArrayList<>();
		this.observer = observer;
		setX(0);
		setY(0);
		hasLinked = true;
		updated = true;
	}

	/**
	 * starts the draw and update loops, using java Timer objects
	 * I honestly barely ever use this option, despite it being ostensibly the default
	 */
	public void startLoop() {
		resolvePreLinks();
		Timer updateRunner = new Timer();
		updateRunner.schedule(this.new RunUpdate(), START_DELAY, updateDelay);
		Timer drawRunner = new Timer();
		drawRunner.schedule(this.new RunDraw(), START_DELAY, drawDelay);
	}

	/**
	 * updates this Manager, the associated Frindow, active events, and the currently active Background
	 * @return whether this Manager should be unlinked from its parent (almost never relevant, since Managers almost never have a parent)
	 */
	@Override
	public boolean updateAll() {
		observer.update();
		boolean updateVal = super.updateAll();
		manageEvents();
		updated = true;
		return updateVal;
	}

	/**
	 * manages running active events and starting new events once the active ones finish
	 */
	private void manageEvents() {
		if(activeEvents.isEmpty() && !eventQueue.isEmpty()) {
			Event newEvent = eventQueue.remove();
			activeEvents.add(newEvent);
			newEvent.start();
		}
		if(!activeEvents.isEmpty()) {
			boolean runningConsecutiveEvents = true;
			while (runningConsecutiveEvents) {
				//this is a bit silly, but since eventItr isn't needed after this chunk, I decided to enclose it
				{
					for (int i = 0; i < activeEvents.size(); i++) {
						Event curEvent = activeEvents.get(i);
						curEvent.act();
						if (curEvent.isFinished()) {
							activeEvents.remove(i);
							i--;
						}
					}
				}
				if(activeEvents.isEmpty() && !eventQueue.isEmpty()) {
					activeEvents.add(eventQueue.remove());
				} else {
					runningConsecutiveEvents = false;
				}

			}
		}
	}

	/**
	 * starts the update and draw loops separately, keeping track of dt
	 */
	public void startVariableLoop() {
		resolvePreLinks();
		Thread updateRunner = new Thread(() -> {
			boolean running = true;
			RunUpdate r = this.new RunUpdate();
			long time = System.nanoTime();
			while(running) {
				long newTime = System.nanoTime();
				dt = (newTime - time);
				r.run();
				int elapsedTime = (int)(System.nanoTime() - newTime);
				while(elapsedTime < updateDelay * 1000000 && updateDelay > 0) {
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
				while(elapsedTime < drawDelay * 1000000 && drawDelay > 0) {
					elapsedTime = (int)(System.nanoTime() - newTime);
				}
			}
		});
		updateRunner.start();
		drawRunner.start();
	}

	public void startSynchronizedLoop() {
		resolvePreLinks();
		Thread runner = new Thread(() -> {
			boolean running = true;
			RunUpdate update = this.new RunUpdate();
			RunDraw draw = this.new RunDraw();
			long prevTime = System.nanoTime();
			while(running) {
				updateAll();
				observer.syncDefPaint();
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
		return (int) (dt / 1000);
	}

	public long dtNanos() {
		return dt;
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

	//returns whether the Frindow this manager uses is displayed on uses a colour model with an alpha channel
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

	public void removeKeystrokeFunction(KeypressFunction func) {
		observer.removeKeystrokeFunction(func);
	}

	@Override
	public void addMouseEventFunction(MouseEventFunction func) {
		observer.addMouseEventFunction(func);
	}

	public void removeMouseEventFunction(MouseEventFunction func) {
		observer.removeMouseEventFunction(func);
	}

	/**
	 * Returns the Frindow (this libraries window object) associated with this Manager.
	 * @return the Frindow associated with this Manager
	 */
	public Frindow getFrindow() {
		return observer;
	}

	@Override
	public void queueEvent(Event e) {
		eventQueue.add(e);
	}

	@Override
	public void addEvent(Event e) {
		activeEvents.add(e);
		e.start();
	}
}