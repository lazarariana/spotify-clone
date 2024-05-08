package commands.constants;

import lombok.Getter;

public class PlaylistEnums {

    @Getter
    public enum CreatePlaylistEnum {
        ALREADY_EXISTS("A playlist with the same name already exists."),
        SUCCESS_CREATE("Playlist created successfully.");
        private final String name;

        CreatePlaylistEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum SwitchVisibilityEnum {
        TOO_HIGH_ID("The specified playlist ID is too high."),
        SUCCESS_SWITCH("Visibility status updated successfully to ");
        private final String name;

        SwitchVisibilityEnum(final String name) {
            this.name = name;
        }
    }
}
