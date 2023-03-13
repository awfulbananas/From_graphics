package effects;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fromics.Linkable;
import fromics.Point;
//a class representing a spray of particles from a source, which fade over time
//I use this for explosion effects
public class ParticleEffect extends Linkable {
	private List<Particle> particles;
	private float pLife;
	
	//constructs a new ParticleEffect at (x, y) with particles particles, moving at pSpeed
	public ParticleEffect(double x, double y, int particles, int pSpeed, int pLife) {
		super(x, y);
		this.pLife = pLife;
		this.particles = new LinkedList<>();
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
//			System.out.println(particles.size());
			if(next.timer <= 0) {
				pItr.remove();
			}
		}
		if(particles.size() == 0) {
			return true;
		}
		return false;
	}
	
	//draws this ParticleEffect, with each particle being a pixel which fades over time
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		for(Particle p : particles) {
			g.setColor(p.getColor());
			g.drawRect((int)(p.X() + xOff), (int)(p.Y() + yOff), 1, 1);
		}
	}
	
	private class Particle extends Point {
		public Point vec;
		public int timer;
		private Color c;
		
		public void update() {
			add(vec);
			timer--;
			float val = 1f - (float)(pLife - timer) / pLife;
			c = new Color(val, val, val);
		}
		
		public Color getColor() {
			return c;
		}
		
		public Particle(double x, double y, double ang, double speed, int timer) {
			super(x, y);
			this.timer = timer;
			vec = new Point(Math.cos(ang) * speed, Math.sin(ang) * speed);
		}
	}
}
