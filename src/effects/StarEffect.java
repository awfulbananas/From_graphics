package effects;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fromics.Linkable;
import fromics.Point;

//this class represents an effect of stars fading in and out in a specified area
//I use this for a background in space, or for a night sky
public class StarEffect extends Linkable {
	private final int starLifetime;
	private final int starAddTime;
	private final int starSize;
	private final int starFadeTime;
	
	protected int starTimer;
	protected List<Star> stars;
	protected Point bounds;
	protected Random r;
	
	public StarEffect(int width, int height, int startStarCount, int starLifetime, int starAddTime, int starSize, int starFadeTime) {
		super(0, 0);
		this.starLifetime = starLifetime;
		this.starAddTime = starAddTime;
		this.starSize = starSize;
		this.starFadeTime = starFadeTime;
		bounds = new Point(width, height);
		starTimer = starAddTime;
		stars = new ArrayList<>();
		r = new Random();
		for(int i = 0; i < startStarCount; i++) {
			stars.add(new Star(r.nextInt(width), r.nextInt(height), r.nextInt(starLifetime)));
		}
	}
	
	protected class Star extends Point {
		public int timer;

		public Star(int x, int y, int lifetime) {
			super(x, y); 
			this.timer = lifetime;
		}
		
		public boolean isDone() {
			timer--;
			return timer < 0;
		}
	}
	
	@Override
	protected void draw(Graphics g, double x, double y, double ang) { 
		if(starTimer < 0) {
			stars.add(new Star(r.nextInt((int)bounds.X()), r.nextInt((int)bounds.Y()), starLifetime));
			starTimer = starAddTime;
		} else {
			starTimer--;
		}
		for(int i = 0; i < stars.size(); i++) {
			Star next = stars.get(i);	
			float mag = Math.max(Math.min(1f, Math.min((float)next.timer / (float)starFadeTime, (float)(starLifetime - next.timer) / (float)starFadeTime)), 0f);
			g.setColor(new Color(mag, mag, mag));
			g.drawRect((int)next.X(), (int)next.Y(), starSize, starSize);		
		}
	}
	
	@Override
	public boolean update() {
		if(starTimer < 0) {
			stars.add(new Star(r.nextInt((int)bounds.X()), r.nextInt((int)bounds.Y()), starLifetime));
			starTimer = starAddTime;
		} else {
			starTimer--;
		}
		Iterator<Star> sts = stars.iterator();
		while(sts.hasNext()) {
			Star next = sts.next();
			if(next.isDone()) {
				sts.remove();
			}
		}
		return false;
	}
}
