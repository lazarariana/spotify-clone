package monetization;

public final class MonetizationFactory {
    private MonetizationFactory() {
    }

    /**
     * This method creates and returns an instance of a MonetizationStrategy based on the provided
     * user type.
     *
     * @param isPremium A boolean value indicating whether the user is a premium user. If true, a
     * PremiumMonetizationStrategy is created. If false, a FreeMonetizationStrategy is created.
     * @return An instance of either PremiumMonetizationStrategy or FreeMonetizationStrategy based
     * on the user type.
     */
    public static MonetizationStrategy createMonetizationStrategy(final boolean isPremium) {
        if (isPremium) {
            return new PremiumMonetizationStrategy();
        } else {
            return new FreeMonetizationStrategy();
        }
    }
}
