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
			if(cur.getX() > maxBounds.getX()) {
				maxBounds.setX(cur.getX());
			}
			if(cur.getX() < minBounds.getX()) {
				minBounds.setX(cur.getX());
			}
			if(cur.getY() > maxBounds.getY()) {
				maxBounds.setY(cur.getY());
			}
			if(cur.getY() < minBounds.getY()) {
				minBounds.setY(cur.getY());
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

            if (cur.getY() == last.getY()) {
                continue;
            }

            double leftx;
            if (cur.getX() < last.getX()) {
                if (p.x >= last.getX()) {
                    continue;
                }
                leftx = cur.getX();
            } else {
                if (p.x >= cur.getX()) {
                    continue;
                }
                leftx = last.getX();
            }

            Point test;
            if (cur.getY() < last.getY()) {
                if (p.y < cur.getY() || y >= last.getY()) {
                    continue;
                }
                if (p.x < leftx) {
                    hits++;
                    continue;
                }
                test = p.copy().sub(cur);
            } else {
                if (p.y < last.y || p.y >= cur.y) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test = p.copy().sub(last);
            }

            if (test.x < (test.y / (last.y - cur.y) * (last.x - cur.x))) {
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
			absPoints[i] = new Point(((Math.cos(-ang) * shape[i].x + Math.sin(-ang)*shape[i].y) + x)
			,((Math.sin(ang) * shape[i].x + Math.cos(ang)*shape[i].y) + y));
		}
		return absPoints;
	}

	@Override
	public double getWidth() {
		return maxBounds.getX() - minBounds.getX();
	}

	@Override
	public double getHeight() {
		return maxBounds.getY() - minBounds.getY();
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		
	}
	
	protected void drawCollider(Graphics g, double xOff, double yOff, double angOff) {
		drawPoints(g, xOff, yOff, angOff, 1, shape);
	}

}
 