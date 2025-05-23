package fromics.events;

public class TimedEvent implements Event{
    private EventAction act;
    private final int durationMillis;
    private long startTime;
    private boolean isOverTime;

    public TimedEvent(EventAction act, int durationMillis) {
        this.act = act;
        this.durationMillis = durationMillis;
        isOverTime = false;
    }

    public TimedEvent(EventAction act, double durationSeconds) {
        this(act, (int)(durationSeconds * 1000));
    }

    protected TimedEvent(int durationMillis) {
        this.durationMillis = durationMillis;
    }

    protected void setAction(EventAction act) {
        this.act = act;
    }

    @Override
    public boolean isFinished() {
        return isOverTime;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void act() {
        long timePassedMillis = System.currentTimeMillis() - startTime;
        if(timePassedMillis > durationMillis) {
            isOverTime = true;
            timePassedMillis = durationMillis;
        }
        if(act.act(((double) timePassedMillis)/((double) durationMillis))) {
            isOverTime = true;
        }
    }
}
