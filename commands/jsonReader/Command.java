package commands.jsonReader;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public final class Command {

    private String command;
    private String username;
    private int age;
    private String city;
    private Integer timestamp;
    private String type;
    private int duration;

    private HashMap<String, Object> filters;

    private Integer itemNumber;
    private String name;
    private String playlistName;
    private Integer playlistId;
    private Integer seed;

    private String description;
    private ArrayList<SongInput> songs;
    private ArrayList<EpisodeInput> episodes;
    private String date;
    private Integer price;

    private String nextPage;
    private String recommendationType;

    public Command() {

    }

    public Command(final String command, final String username, final Integer timestamp,
                   final String type, final HashMap<String, Object> filtersMap) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
        this.filters = filtersMap;
    }

    @Override
    public String toString() {
        return "Command{"
                + "command='" + command + '\''
                + ", username='" + username + '\''
                + ", timestamp=" + timestamp
                + ", type='" + type + '\''
                + ", filtersMap=" + filters
                + '}';
    }
}
