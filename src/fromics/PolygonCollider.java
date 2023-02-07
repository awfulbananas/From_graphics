package fromics;

import java.awt.Graphics;

public abstract class PolygonCollider extends Collidable {
	private Point[] shape;
	Point maxBounds;
	Point minBounds;
	protected double size;
	
	protected PolygonCollider(double x, double y) {
		super(x, y);
	}
	
	protected void init(double[] xVals, double[] yVals, double size) {
		this.size = size;
		shape = new Point[xVals.length];
		for(int i = 0; i < xVals.length; i++) {
			shape[i] = new Point(xVals[i] * size, yVals[i] * size);
		}
//		System.out.println(Arrays.toString(shape));
		maxBounds = shape[0].copy();
		minBounds = shape[0].copy();
		for(int i = 0; i < shape.length; i++) {
			Point cur = shape[i];
			if(cur.X() > maxBounds.X()) {
				maxBounds.setX(cur.X());
			}
			if(cur.X() < minBounds.X()) {
				minBounds.setX(cur.X());
			}
			if(cur.Y() > maxBounds.Y()) {
				maxBounds.setY(cur.Y());
			}
			if(cur.Y() < minBounds.Y()) {
				minBounds.setY(cur.Y());
			}
		}
	}
	
	public PolygonCollider(int x, int y, double[] xVals, double[] yVals, double size) {
		super(x, y);
		init(xVals, yVals, size);
	}
	
	public boolean shapeContains(Point p) {
		if(!Linkable.boundsContain(minBounds, maxBounds, p)) return false;
		
		int hits = 0;
		
		Point last = shape[shape.length - 1];
		Point cur;
		
		for (int i = 0; i < shape.length; last = cur, i++) {
            cur = shape[i];

            if (cur.Y() == last.Y()) {
                continue;
            }

            double leftx;
            if (cur.X() < last.X()) {
                if (p.X() >= last.X()) {
                    continue;
                }
                leftx = cur.X();
            } else {
                if (p.X() >= cur.X()) {
                    continue;
                }
                leftx = last.X();
            }

            Point test;
            if (cur.Y() < last.Y()) {
                if (p.Y() < cur.Y() || Y() >= last.Y()) {
                    continue;
                }
                if (p.X() < leftx) {
                    hits++;
                    continue;
                }
                test = p.copy().sub(cur);
            } else {
                if (p.Y() < last.Y() || p.Y() >= cur.Y()) {
                    continue;
                }
                if (X() < leftx) {
                    hits++;
                    continue;
                }
                test = p.copy().sub(last);
            }

            if (test.X() < (test.Y() / (last.Y() - cur.Y()) * (last.X() - cur.X()))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
	}

	@Override
	public int getCollisionType() {
		return 5;
	}
	
	//TODO: add other types of collision
	@Override
	public boolean check(Collidable other) {
		
		switch(other.getCollisionType()) {
			case 4:
				return shapeContains(other.copy().sub(this));
			case 5:
//				System.out.println("derp");
				for(Point p : ((PolygonCollider)other).absPoints()) {
					if(shapeContains(p.copy().sub(this))) return true;
				}
				for(Point p : this.absPoints()) {
					if(((PolygonCollider)other).shapeContains(p.copy().sub(other))) return true;
				}
				return false;
			default:
				return false;
		}
	}
	
	public Point[] absPoints() {
		Point[] absPoints = new Point[shape.length];
		for(int i = 0; i < shape.length; i++) {
			absPoints[i] = new Point(((Math.cos(-ang) * shape[i].X() + Math.sin(-ang)*shape[i].Y()) + X())
			,((Math.sin(ang) * shape[i].X() + Math.cos(ang)*shape[i].Y()) + Y()));
		}
		return absPoints;
	}

	@Override
	public double getWidth() {
		return maxBounds.X() - minBounds.X();
	}

	@Override
	public double getHeight() {
		return maxBounds.Y() - minBounds.Y();
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		
	}
	
	protected void drawCollider(Graphics g, double xOff, double yOff, double angOff) {
		drawPoints(g, xOff, yOff, angOff, 1, shape);
	}

}
 