package commands.constants;

import lombok.Getter;

public final class UserCommandsEnums {
    public static final String USERNAME = "The username ";
    public static final String USER = "The user ";
    public static final String S_RECOMMENDATIONS = "'s recommendations";
    public static final String FAN_CLUB = " Fan Club recommendations";
    public static final Double CREDIT = 1000000.0;
    public static final Double ROUND = 100.0;

    private UserCommandsEnums() {
    }

    @Getter
    public enum AddUserMessagesEnum {
        TAKEN(" is already taken."),
        SUCCESS_ADD(" has been added successfully.");

        private final String name;
        AddUserMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum DeleteUserMessagesEnum {
        NO_EXIST(" doesn't exist."),
        NO_DELETE(" can't be deleted."),
        SUCCESS_DELETE(" was successfully deleted.");

        private final String name;
        DeleteUserMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum SwitchConnectionStatusMessagesEnum {
        NOT_NORMAL(" is not a normal user."),
        SUCCESS_STATUS(" has changed status successfully."),
        IS_OFFLINE(" is offline.");

        private final String name;
        SwitchConnectionStatusMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum BuyPremiumMessagesEnum {
        ALREADY_PREMIUM(" is already a premium user."),
        SUCCESS_BUY_PREMIUM(" bought the subscription successfully.");

        private final String name;
        BuyPremiumMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum CancelPremiumMessagesEnum {
        NOT_PREMIUM(" is not a premium user."),
        SUCCESS_CANCEL_PREMIUM(" cancelled the subscription successfully.");

        private final String name;
        CancelPremiumMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum AdBreakMessagesEnum {
        NOT_MUSIC(" is not playing any music."),
        SUCCESS_AD("Ad inserted successfully.");

        private final String name;
        AdBreakMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum SubscribeMessagesEnum {
        NOT_ON_PAGE("To subscribe you need to be on the page of an artist or host."),
        SUCCESS_SUBSCRIBE(" subscribed to "),
        SUCCESS_UNSUBSCRIBE(" unsubscribed from "),
        SUCCESSFULLY(" successfully.");

        private final String name;
        SubscribeMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum UpdateRecommendationsMessagesEnum {
        NO_RECOMMENDATIONS("No new recommendations were found"),
        SUCCESS_SUBSCRIBE(" subscribed to "),
        RECOMMENDATIONS("The recommendations for user "),
        UPDATED_SUCCESSFULLY(" have been updated successfully.");

        private final String name;
        UpdateRecommendationsMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum LoadRecommendationsMessagesEnum {
        NOT_AVAILABLE("No recommendations available.");

        private final String name;
        LoadRecommendationsMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum PageNavigationMessagesEnum {
        NO_PAGES_FORWARD("There are no pages left to go forward."),
        NO_PAGES_BACKWARD("There are no pages left to go back."),
        SUCCESS_FORWARD(" has navigated successfully to the next page."),
        SUCCESS_BACKWARD(" has navigated successfully to the previous page.");

        private final String name;
        PageNavigationMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum WrappedMessagesEnum {
        NO_LISTENS_USER("No data to show for user "),
        NO_LISTENS_ARTIST("No data to show for artist "),
        NO_LISTENS_HOST("No data to show for song ");

        private final String name;
        WrappedMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum PaymentTypeEnum {
        FREE("free"),
        PREMIUM("premium");

        private final String name;
        PaymentTypeEnum(final String name) {
            this.name = name;
        }

    }

}
