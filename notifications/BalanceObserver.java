package notifications;

public interface BalanceObserver {
    /**
     * Updates the observer with the provided update message.
     *
     * This method is called when the subject's state changes and needs to notify its observers.
     *
     * @param update The update message to be sent to the observer.
     */
    void update(String update);
}

