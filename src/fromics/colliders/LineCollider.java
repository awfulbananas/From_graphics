package fromics.colliders;

import fromics.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LineCollider extends Collidable{
    private final Point o;

     public LineCollider(double x, double y, Point target) {
        super(x, y);
        o = target;
    }

    @Override
    public CollisionType getCollisionType() {
        return CollisionType.LINE;
    }

    @Override
    public boolean check(Collidable other) {
        switch(other.getCollisionType()) {
            case POINT:
                return checkPoint(other);
            case LINE:
                return intersectsLine(other, ((LineCollider)other).o);
            default:
                return false;
        }
    }

    public boolean intersectsLine(Point a, Point b) {
        Point diff = o.copy().sub(this);
        diff.setY(-diff.Y());
        Point ar = a.copy().sub(this).toSpace(diff);
        Point br = b.copy().sub(this).toSpace(diff);
        if(ar.Y() == br.Y()) {
            return ar.Y() == 0;
        }
        if(Math.min(ar.Y(), br.Y()) > 0 || Math.max(ar.Y(), br.Y()) < 0) {
            return false;
        }
        //I worked this out in desmos
        return (ar.Y() / (ar.Y() - br.Y())) * (br.X() - ar.X()) + ar.X() < diff.sMag();
    }

//    public Point closestPointTo(Point p) {
//
//    }

    //I'll implement this later, I'm undecided on whether to include a very small collision radius
    //or to require it to be exact, which would mean basically only the endpoints can reasonably collide
    @Override
    public boolean checkPoint(Point p) {
        return false;
    }

    @Override
    protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
        g.drawLine((int)(X() + xOff), (int)(Y() + yOff), (int)(o.X() + xOff), (int)(o.Y() + yOff));
    }
}
