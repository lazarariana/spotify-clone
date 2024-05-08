package monetization;

import fileio.input.UserInput;

public interface MonetizationStrategy {
    /**
     * Executes the payment process for a user.
     *
     * This method is called when a user needs to make a payment. The specific payment process
     * depends on the implementation of the MonetizationStrategy interface.
     *
     * @param user The user who needs to make a payment.
     */
    void monetize(UserInput user);
}
