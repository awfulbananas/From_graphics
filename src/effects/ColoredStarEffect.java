package effects;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fromics.Linkable;
import fromics.Point;

//this class represents an effect of somewhat naturally colored stars fading in and out in a specified area
public class ColoredStarEffect extends Linkable {
	private final int starLifetime;
	private final int starAddTime;
	private final int starSize;
	private final int starFadeTime;
	
	protected int starTimer;
	protected List<Star> stars;
	protected Point bounds;
	protected Random r;
	
	public ColoredStarEffect(int width, int height, int startStarCount, int starLifetime, int starAddTime, int starSize, int starFadeTime) {
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
		public Color c;

		public Star(int x, int y, int lifetime) {
			super(x, y); 
			this.timer = lifetime;
			double colorHue;
			if(Math.random() < 0.5) {
				colorHue = (Math.random() * 30 + 100) / 360;
			} else {
				colorHue = (Math.random() * 34 + 1) / 360;
			}
			c = Color.getHSBColor((float)colorHue, (float)(Math.random() * 0.88), 1f);
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
			float mag = Math.min(1f, Math.min((float)next.timer / (float)starFadeTime, (float)(starLifetime - next.timer) / (float)starFadeTime));
			Color c = next.c;
			g.setColor(new Color(((float)c.getRed() / 255) * mag, ((float)c.getGreen() / 255) * mag, ((float)c.getBlue() / 255) * mag));
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
