package commands.constants;

import lombok.Getter;

public final class SearchBarEnums {
    private SearchBarEnums() {
    }

    @Getter
    public enum SelectMessagesEnum {
        NO_PREV_SEARCH("Please conduct a search before making a selection."),
        TOO_HIGH_ID("The selected ID is too high."),
        SUCCESS_SELECT("Successfully selected ");

        private final String name;
        SelectMessagesEnum(final String name) {
            this.name = name;
        }

    }

    @Getter
    public enum FiltersEnum {
        OWNER("owner"),
        ALBUM("album"),
        LYRICS("lyrics"),
        RELEASE_YEAR("releaseYear"),
        GENRE("genre"),
        ARTIST("artist"),
        TAGS("tags"),
        DESCRIPTION("description");

        private final String name;
        FiltersEnum(final String name) {
            this.name = name;
        }
    }

    public static final String SEARCH_RETURN = "Search returned ";
    public static final String SEARCH_RESULT = " results";
    public static final String INVALID_SEARCH = "Invalid search type";
}
