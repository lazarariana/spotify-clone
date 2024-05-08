package notifications;

public interface Subject {

    /**
     * Adds an observer to the list of subscribers.
     *
     * This method takes a BalanceObserver object as input and adds it to the list of subscribers.
     *
     * @param observer The observer to be added to the list of subscribers.
     */
    void subscribe(BalanceObserver observer);

    /**
     * Removes an observer from the list of subscribers.
     *
     * This method takes a BalanceObserver object as input and removes it from the list of
     * subscribers.
     *
     * @param observer The observer to be removed from the list of subscribers.
     */
    void unsubscribe(BalanceObserver observer);

    /**
     * Notifies all observers about a change in balance.
     *
     * This method iterates over all observers (subscribers) and calls their update method with the
     * provided update message.
     *
     * @param update The update message to be sent to the observers.
     */
    void notifyObservers(String update);
}
