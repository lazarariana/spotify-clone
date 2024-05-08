package commands.constants;

import lombok.Getter;

public final class PlayerEnums {
    public static final Integer AD = 10;

    @Getter
    public enum LoadMessagesEnum {
        LOAD_MESSAGE("Playback loaded successfully."),
        EMPTY_AUDIO_MESSAGE("You can't load an empty audio collection!"),
        SELECT_ERROR_MESSAGE("Please select a source before attempting to load.");
        private final String name;

        LoadMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum PlayPauseMessagesEnum {
        PLAY_MESSAGE("Playback resumed successfully."),
        PAUSE_MESSAGE("Playback paused successfully."),
        LOAD_ERROR_MESSAGE("Please load a source before attempting to pause or resume playback.");
        private final String name;

        PlayPauseMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum RepeatMessagesEnum {
        CHANGE_REPEAT_MODE("Repeat mode changed to "),
        LOAD_ERROR_REPEAT("Please load a source before setting the repeat status.");
        private final String name;

        RepeatMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ShuffleMessagesEnum {
        DEACTIVATE("Shuffle function deactivated successfully."),
        ACTIVATE("Shuffle function activated successfully."),
        FILE_ERROR("The loaded source is not a playlist or an album."),
        LOAD_ERROR_SHUFFLE("Please load a source before using the shuffle function.");
        private final String name;

        ShuffleMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum ForwardMessagesEnum {
        SUCCESS_FORWARD("Skipped forward successfully."),
        FILE_ERROR("The loaded source is not a podcast."),
        LOAD_ERROR_FORWARD("Please load a source before attempting to forward.");
        private final String name;

        ForwardMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum BackwardMessagesEnum {
        SUCCESS_REWIND("Rewound successfully."),
        FILE_ERROR("The loaded source is not a podcast."),
        LOAD_ERROR_BACKWARD("Please select a source before rewinding.");
        private final String name;

        BackwardMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum LikeMessagesEnum {
        SUCCESS_LIKE("Like registered successfully."),
        SUCCESS_UNLIKE("Unlike registered successfully."),
        FILE_ERROR("Loaded source is not a song."),
        LOAD_ERROR_LIKE("Please load a source before liking or unliking.");
        private final String name;

        LikeMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum NextMessagesEnum {
        SUCCESS_NEXT("Skipped to next track successfully. The current track is "),
        LOAD_ERROR_NEXT("Please load a source before skipping to the next track.");
        private final String name;

        NextMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum PrevMessagesEnum {
        SUCCESS_PREV("Returned to previous track successfully. The current track is "),
        LOAD_ERROR_PREV("Please load a source before returning to the previous track.");
        private final String name;

        PrevMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum AddRemoveInPlaylistMessagesEnum {
        SUCCESS_ADD("Successfully added to playlist."),
        SUCCESS_REMOVE("Successfully removed from playlist."),
        WRONG_TYPE_FILE("The loaded source is not a song."),
        FILE_ERROR("The specified playlist does not exist."),
        LOAD_ERROR_ADD_REMOVE("Please load a source before adding "
                + "to or removing from the playlist.");
        private final String name;

        AddRemoveInPlaylistMessagesEnum(final String name) {
            this.name = name;
        }
    }

    @Getter
    public enum FollowEnum {
        FOLLOW("Playlist followed successfully."),
        UNFOLLOW("Playlist unfollowed successfully."),
        OWN_PLAYLIST("You cannot follow or unfollow your own playlist."),
        FILE_ERROR("The selected source is not a playlist."),
        NOTLOADED("Please select a source before following or unfollowing.");
        private final String name;

        FollowEnum(final String name) {
            this.name = name;
        }
    }

    private PlayerEnums() {
    }
}
