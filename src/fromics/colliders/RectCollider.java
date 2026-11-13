package fromics.colliders;

import fromics.Point;

public class RectCollider extends PolygonCollider{
    private final double width, height;

    public RectCollider(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
        Point[] corners = new Point[4];
        corners[0] = new Point(width/2, height/2);
        corners[1] = new Point(width/2, -height/2);
        corners[2] = new Point(-width/2, -height/2);
        corners[3] = new Point(-width/2, height/2);
        init(corners, 1);
    }

    @Override
    public CollisionType getCollisionType() {
        return CollisionType.RECT;
    }

    @Override
    public boolean shapeContains(Point p) {
        Point rel = p.copy().rot(-ang).add(width / 2, height / 2);
        return (rel.X() >= 0 && rel.X() <= width && rel.Y() >= 0 && rel.Y() <= height);
    }

    @Override
    public boolean check(Collidable other) {
        return switch(other.getCollisionType()) {
            case POLYGON, RECT -> checkPolygon((PolygonCollider) other);
            case POINT -> shapeContains(other.copy().sub(this));
            case OVAL -> super.check(other);
            default -> false;
        };
    }

}
