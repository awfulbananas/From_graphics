package effects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fromics.Linkable;
import fromics.Point;
//a class representing a spray of particles from a source, which fade over time
//I use this for explosion effects
public class ParticleEffect extends Linkable {
	private List<Particle> particles;
	private boolean remOnEmpty;

	public ParticleEffect() {
		super(0,0);
		this.particles = new LinkedList<>();
		this.remOnEmpty = false;
	}
	//constructs a new ParticleEffect at (x, y) with particles particles, moving at pSpeed
	public ParticleEffect(double x, double y, int particles, double pSpeed, int pLife, boolean remOnEmpty) {
		super(x, y);
		this.remOnEmpty = remOnEmpty;
		for(int i = 0; i < particles; i++) {
			this.particles.add(new Particle(x, y, Math.random() * Math.PI * 2, (Math.random() + 0.2) * pSpeed, pLife));
		}
	}

	public void addParticles(double x, double y, int particles, double pSpeed, int pLife) {
		for(int i = 0; i < particles; i++) {
			this.particles.add(new Particle(x, y, Math.random() * Math.PI * 2, (Math.random() + 0.2) * pSpeed, pLife));
		}
	}
	
	//updates this ParticleEffect, moving all the particles outward from the source
	@Override
	public boolean update() {
		Iterator<Particle> pItr = particles.iterator();
		while(pItr.hasNext()) {
			Particle next = pItr.next();
			next.update();
			if(next.timer <= 0) {
				pItr.remove();
			}
		}
		if(remOnEmpty) {
			return particles.isEmpty();
		}
		return false;
	}
	
	//draws this ParticleEffect, with each particle being a pixel which fades over time
	@Override
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
		for(int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			g.setColor(p.getColor());
			g.drawRect((int)(p.X() + xOff), (int)(p.Y() + yOff), 1, 1);
		}
	}
	
	private static class Particle extends Point {
		public Point vec;
		public int timer;
		public int life;
		private Color c;
		
		public void update() {
			add(vec);
			timer--;
			float val = 1f - (float)(life - timer) / life;
			c = new Color(val, val, val);
		}
		
		public Color getColor() {
			return c;
		}
		
		public Particle(double x, double y, double ang, double speed, int timer) {
			super(x, y);
			this.timer = timer;
			this.life = timer;
			vec = new Point(Math.cos(ang) * speed, Math.sin(ang) * speed);
		}
	}
}
