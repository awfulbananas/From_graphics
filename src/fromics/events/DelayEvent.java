package fromics.events;

public class DelayEvent extends TimedEvent{
    public DelayEvent(int durationMillis) {
        super((double completion) -> false, durationMillis);
    }

    public DelayEvent(double durationSeconds) {
        this((int)(durationSeconds * 1000));
    }
}
