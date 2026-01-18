package fromics.events;

import fromics.Point;

public class MoveEvent extends TimedEvent{
    private final Point obj;
    private Point originalLoc;

    public MoveEvent(Point obj, Point target, int durationMillis) {
        super(durationMillis);
        this.obj = obj;
        setAction((double completion) -> {
            this.obj.setX((target.X() - originalLoc.X()) * completion + originalLoc.X());
            this.obj.setY((target.Y() - originalLoc.Y()) * completion + originalLoc.Y());
            return false;
        });
    }

    public void start() {
        super.start();
        originalLoc = obj.copy();
    }

    public MoveEvent(Point obj, Point target, double durationSeconds) {
        this(obj, target, (int)(durationSeconds * 1000));
    }
}
