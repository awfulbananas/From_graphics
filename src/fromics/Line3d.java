package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//3d doesn't work yet, I'll fix it before adding comments to 3d classes
public class Line3d extends Shape3d {
		private Point to;
		
		public Line3d(Point a, Point b) {
			super(a.X(), a.Y(), a.Z());
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
			int x1 = (int)((Math.cos(-totalAng) * tPoints.get(0).X() + Math.sin(-totalAng) * tPoints.get(0).Y()) + totalX);
			int x2 = (int)((Math.cos(-totalAng) * tPoints.get(1).X() + Math.sin(-totalAng) * tPoints.get(1).Y()) + totalX);
			int y1 = (int)((Math.sin(totalAng) * tPoints.get(0).X() + Math.cos(totalAng) * tPoints.get(0).Y()) + totalY);
			int y2 = (int)((Math.sin(totalAng) * tPoints.get(1).X() + Math.cos(totalAng) * tPoints.get(1).Y()) + totalY);
			
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
