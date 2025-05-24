package fromics.events;

public interface Event {
    boolean isFinished();
    void start();
    void reset();
    void act();

    default Event andThen(Event next) {
        return new SequentialEvent(this, next);
    }

    default Event andThen(InstantAction action) {
        return andThen(new InstantaneousEvent(action));
    }

    default Event andThen(EventAction action, double seconds) {
        return andThen(new TimedEvent(action, seconds));
    }

    default Event andThen(EventAction action, int millis) {
        return andThen(new TimedEvent(action, millis));
    }

    default Event doAfter(Event before) {
        return new SequentialEvent(before, this);
    }

    default Event doAfter(InstantAction action) {
        return doAfter(new InstantaneousEvent(action));
    }

    default Event doAfter(EventAction action, double seconds) {
        return doAfter(new TimedEvent(action, seconds));
    }

    default Event doAfter(EventAction action, int millis) {
        return doAfter(new TimedEvent(action, millis));
    }

    default Event doWith(Event other) {
        return new ParallelEvent(this, other);
    }

    default Event doWith(EventAction action, double seconds) {
        return doWith(new TimedEvent(action, seconds));
    }

    default Event doWith(EventAction action, int millis) {
        return doWith(new TimedEvent(action, millis));
    }

    default Event delay(int millis) {
        return new SequentialEvent(new DelayEvent(millis), this);
    }

    default Event delay(double seconds) {
        return new SequentialEvent(new DelayEvent(seconds), this);
    }
}
