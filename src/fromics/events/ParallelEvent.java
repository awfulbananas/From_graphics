package fromics.events;

public class ParallelEvent implements Event{
    private final Event a;
    boolean runningA;
    private final Event b;
    boolean runningB;

    public ParallelEvent(Event a, Event b) {
        this.a = a;
        this.b = b;
        runningA = true;
        runningB = true;
    }


    @Override
    public boolean isFinished() {
        return a.isFinished() && b.isFinished();
    }

    @Override
    public void start() {
        a.start();
        b.start();
    }

    @Override
    public void reset() {
        a.reset();
        b.reset();
        runningA = true;
        runningB = true;
    }

    @Override
    public void act() {
        if(runningA) {
            a.act();
            runningA = !a.isFinished();
        }
        if(runningB) {
            b.act();
            runningB = !b.isFinished();
        }
    }
}
