package fromics.events;

public class InstantaneousEvent implements Event {
    private final EventAction act;
    private boolean hasActed;

    public InstantaneousEvent(EventAction act) {
        this.act = act;
        hasActed = false;
    }

    @Override
    public boolean isFinished() {
        return hasActed;
    }

    @Override
    public void start() {}

    @Override
    public void act() {
        act.act(0);
        hasActed = true;
    }
}
