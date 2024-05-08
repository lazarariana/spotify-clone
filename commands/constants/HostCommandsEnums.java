package commands.constants;

import lombok.Getter;

public class HostCommandsEnums {
    @Getter
    public enum AddAPodcastMessagesEnum {
        NOT_HOST(" is not a host."),
        ALREADY_EXISTS(" has another podcast with the same name."),
        TWICE_EPISODE(" has the same episode in this podcast."),
        SUCCESS(" has added new podcast successfully.");

        private final String name;
        AddAPodcastMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum RemovePodcastMessagesEnum {
        NO_PODCAST(" doesn't have a podcast with the given name."),
        NO_DELETE(" can't delete this podcast."),
        SUCCESS_DELETE_PODCAST(" deleted the podcast successfully.");

        private final String name;
        RemovePodcastMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum AddAnnouncementMessagesEnum {
        ALREADY_EXISTS_ANNOUNCEMENT("  has already added an announcement with this name."),
        SUCCESS_ADD_ANNOUNCEMENT(" has successfully added new announcement.");

        private final String name;
        AddAnnouncementMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum RemoveAnnouncementMessagesEnum {
        NO_ANNOUNCEMENT(" has no announcement with the given name."),
        SUCCESS_DELETE_ANNOUNCEMENT(" has successfully deleted the announcement.");

        private final String name;
        RemoveAnnouncementMessagesEnum(final String name) {
            this.name = name;
        }
    }
}
