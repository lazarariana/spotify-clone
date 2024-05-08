package commands.constants;

import lombok.Getter;

public final class StatusEnums {
    private StatusEnums() {
    }

    @Getter
    public enum VisibilityEnum {
        PUBLIC("public"),
        PRIVATE("private");
        private final String name;

        VisibilityEnum(final String name) {
            this.name = name;
        }
    }

    public enum PlayPauseEnum {
        PAUSE,
        PLAY
    }

    @Getter
    public enum RepeatPlaylistEnum {
        NO_REPEAT("No Repeat"),
        REPEAT_ALL("Repeat All"),
        REPEAT_CURRENT_SONG("Repeat Current Song");
        public static final Integer REPEAT_MODES_LENGTH = 3;
        private final String name;

        RepeatPlaylistEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum RepeatAudioCollectionEnum {
        NO_REPEAT("No Repeat"),
        REPEAT_ONCE("Repeat Once"),
        REPEAT_INFINITE("Repeat Infinite");
        public static final Integer REPEAT_MODES_LENGTH = 3;
        private final String name;
        RepeatAudioCollectionEnum(final String name) {
            this.name = name;
        }

    }

    public enum LoadEnum {
        NOT_LOADED,
        IS_LOADED
    }

    public enum ShuffleEnum {
        NOT_SHUFFLED,
        IS_SHUFFLED
    }
}
