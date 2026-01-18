package fromics.events;

@FunctionalInterface
public interface EventAction {
    public boolean act(double completion);
}
