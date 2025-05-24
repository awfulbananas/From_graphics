package fromics.events;

public class SequentialEvent implements Event{
    private final Event first;
    private final Event second;
    private boolean finishedFirst;

    public SequentialEvent(Event first, Event second) {
        this.first = first;
        this.second = second;
        finishedFirst = false;
    }

    @Override
    public boolean isFinished() {
        return finishedFirst && second.isFinished();
    }

    @Override
    public void start() {
        first.start();
    }

    @Override
    public void reset() {
        first.reset();
        second.reset();
        finishedFirst = false;
    }

    @Override
    public void act() {
        if(!finishedFirst) {
            first.act();
            if(first.isFinished()) {
                finishedFirst = true;
            }
        }
        if(finishedFirst) {
            second.act();
        }
    }
}
