package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Line3d extends Shape3d {
		private Point to;
		
		public Line3d(Point a, Point b) {
			super(a.x, a.y, a.z);
			to = b;
		}

		@Override
		public List<Point> getPoints() {
			List<Point> pList = new ArrayList<>(2);
			pList.add(this);
			pList.add(to);
			return pList;
		}

		@Override
		public void drawTransformedPoints(Graphics g, List<Point> tPoints) {
			g.setColor(Color.WHITE);
			double totalAng = getAbsAng();
			double totalX = getAbsX();
			double totalY = getAbsY();
			int x1 = (int)((Math.cos(-totalAng) * tPoints.get(0).x + Math.sin(-totalAng) * tPoints.get(0).y) + totalX);
			int x2 = (int)((Math.cos(-totalAng) * tPoints.get(1).x + Math.sin(-totalAng) * tPoints.get(1).y) + totalX);
			int y1 = (int)((Math.sin(totalAng) * tPoints.get(0).x + Math.cos(totalAng) * tPoints.get(0).y) + totalY);
			int y2 = (int)((Math.sin(totalAng) * tPoints.get(1).x + Math.cos(totalAng) * tPoints.get(1).y) + totalY);
			
			g.drawLine(x1, y1, x2, y2);
		}

		@Override
		protected void draw(Graphics g, double xOff, double yOff, double angOff) {}
		
		@Override
		public void transform(Consumer<Point> c) {
			c.accept(this);
			c.accept(to);
		}

}
