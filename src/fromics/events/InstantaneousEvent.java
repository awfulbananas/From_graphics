package fromics.events;

public class InstantaneousEvent implements Event {
    private final EventAction act;
    private boolean hasActed;

    public InstantaneousEvent(InstantAction action) {
        this((double n) -> {
            action.act();
            return true;
        });
    }

    public InstantaneousEvent(EventAction action) {
        this.act = action;
        hasActed = false;
    }

    @Override
    public boolean isFinished() {
        return hasActed;
    }

    @Override
    public void start() {}

    @Override
    public void reset() {
        hasActed = false;
    }

    @Override
    public void act() {
        act.act(0);
        hasActed = true;
    }
}
