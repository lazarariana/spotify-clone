package fileio.input;

import commands.jsonReader.Command;
import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.constants.StatusEnums;

import lombok.Getter;
import lombok.Setter;
import notifications.BalanceObserver;
import notifications.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PlaylistInput implements Subject {
    private String name;
    private List<SongInput> songs;
    private String visibility;
    private Integer followers;
    private List<BalanceObserver> followersList = new ArrayList<>();

    @JsonIgnore
    private String owner;
    @JsonIgnore
    private Integer dateCreated;

    public PlaylistInput(final Command command) {
        this.visibility = StatusEnums.VisibilityEnum.PUBLIC.getName();
        this.songs = new ArrayList<>();
        this.name = command.getPlaylistName();
        this.owner = command.getUsername();
        this.dateCreated = command.getTimestamp();
    }

    public PlaylistInput(final Command command, final String name) {
        this.visibility = StatusEnums.VisibilityEnum.PUBLIC.getName();
        this.songs = new ArrayList<>();
        this.name = name;
        this.owner = command.getUsername();
        this.dateCreated = command.getTimestamp();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * <p>The equals method implements an equivalence relation on non-null object references. It
     * checks if the given object is "equal to" this one based on the owner and name fields.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the o argument; false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaylistInput that = (PlaylistInput) o;

        return Objects.equals(owner, that.owner) && Objects.equals(name, that.name);
    }

    /**
     * Returns a hash code value for the object.
     *
     * <p>This method is supported for the benefit of hash tables such as those provided by HashMap.
     * The hash code is calculated based on the name, songs, visibility, followers, owner, and
     * dateCreated fields.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, songs, visibility, followers, owner, dateCreated);
    }

    /**
     * The method iterates over all playlists in the library. If it finds a playlist with the same
     * name as the one provided, it returns the playlist. If it does not find a matching playlist,
     * it returns null.
     *
     * @param name The name of the playlist to be retrieved.
     * @return A PlaylistInput object representing the retrieved playlist, or null if no matching
     * playlist is found.
     */
    public static PlaylistInput getPlaylistByName(final String name) {
        for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }

        return null;
    }

    /**
     * The method iterates over all songs in the playlist. For each song, it adds the duration of
     * the song to a running total. Finally, it returns the total duration.
     *
     * @return An Integer representing the total duration of all songs in the playlist.
     */
    public Integer getSummedDurationsPlaylist() {
        Integer durationSum = 0;
        for (SongInput song : this.getSongs()) {
            durationSum += song.getDuration();
        }

        return durationSum;
    }

    /**
     * The method iterates over the currentShuffleArray. For each index in the array, it retrieves
     * the corresponding song from the playlist and adds its duration to a running total. Finally,
     * it returns the total duration.
     *
     * @param currentShuffleArray An ArrayList of Integers representing the order of songs in the
     *                            shuffled playlist.
     * @return An Integer representing the total duration of all songs in the shuffled playlist.
     */
    public Integer getShuffleSummedDurationsPlaylist(final List<Integer> currentShuffleArray) {
        Integer durationSum = 0;

        for (Integer integer : currentShuffleArray) {
            durationSum += this.getSongs().get(integer).getDuration();
        }

        return durationSum;
    }

    /**
     * The method first calculates the total duration of the playlist. It then iterates over the
     * currentShuffleArray. For each index in the array, it retrieves the corresponding song from
     * the playlist. If the sum of the durations of the songs so far and the duration of the
     * current song is greater than the remainder of the current timestamp divided by the total
     * duration of the playlist, it returns the current song. If it does not find a currently
     * playing song, it returns null.
     *
     * @param currentTimestamp    The current timestamp.
     * @param currentShuffleArray An ArrayList of Integers representing the order of songs in the
     *                            shuffled playlist.
     * @return A SongInput object representing the currently playing song, or null if no song is
     * playing.
     */
    public SongInput getShuffleCurrentPlayingSong(
            final Integer currentTimestamp, final List<Integer> currentShuffleArray) {
        int durationSum = 0;
        Integer playlistDimension = this.getSummedDurationsPlaylist();

        for (Integer integer : currentShuffleArray) {
            SongInput song = this.getSongs().get(integer);

            if (durationSum + song.getDuration() > currentTimestamp % playlistDimension) {
                return song;
            }

            durationSum += song.getDuration();
        }

        return null;
    }

    /**
     * The method first calculates the remainder of the current timestamp divided by the total
     * duration of the playlist. It then iterates over the currentShuffleArray. For each index in
     * the array, it retrieves the corresponding song from the playlist. If the sum of the
     * durations of the songs so far and the duration of the current song is greater than the
     * calculated remainder, it returns the sum of the durations of the songs so far. If it does
     * not find any played songs, it returns 0.
     *
     * @param currentTimestamp    The current timestamp.
     * @param currentShuffleArray An ArrayList of Integers representing the order of songs in the
     *                            shuffled playlist.
     * @return An Integer representing the total duration of all songs that have been played.
     */
    public Integer getShufflePlayedSongsTotalDuration(
            final Integer currentTimestamp, final List<Integer> currentShuffleArray) {

        Integer durationSum = 0;

        if (this.getSummedDurationsPlaylist() == 0) {
            return 0;
        }

        int time = currentTimestamp % this.getSummedDurationsPlaylist();

        for (Integer integer : currentShuffleArray) {
            SongInput song = this.getSongs().get(integer);
            if (durationSum + song.getDuration() > time) {
                return durationSum;
            }
            durationSum += song.getDuration();
        }

        return 0;
    }

    /**
     * The method calculates and returns the total number of likes for all songs in the playlist. It
     * iterates over all songs in the playlist, adds up their likes, and returns the total.
     *
     * @return The total number of likes for all songs in the playlist.
     */
    public Integer getTotalLikesPlaylist() {
        int totalLikes = 0;

        for (SongInput song : this.getSongs()) {
            totalLikes += song.getLikes();
        }

        return totalLikes;
    }

    /**
     * Returns a string representation of the PageInput object.
     *
     * @return a string representation of the PageInput object.
     */
    @Override
    public String toString() {
        return "PlaylistInput{"
                + "name='"
                + name
                + '\''
                + ", songs="
                + songs
                + ", visibility="
                + visibility
                + ", followers="
                + followers
                + ", owner='"
                + owner
                + '\''
                + ", dateCreated="
                + dateCreated
                + '}';
    }

    /**
     * Adds an observer to the list of subscribers.
     * <p>
     * This method takes a BalanceObserver object as input and adds it to the list of subscribers.
     *
     * @param observer The observer to be added to the list of subscribers.
     */
    @Override
    public void subscribe(final BalanceObserver observer) {
        followersList.add(observer);
    }

    /**
     * Removes an observer from the list of subscribers.
     * <p>
     * This method takes a BalanceObserver object as input and removes it from the list of
     * subscribers.
     *
     * @param observer The observer to be removed from the list of subscribers.
     */
    @Override
    public void unsubscribe(final BalanceObserver observer) {
        followersList.remove(observer);
    }

    /**
     * Notifies all observers about a change in balance.
     * <p>
     * This method iterates over all observers (subscribers) and calls their update method with the
     * provided update message.
     *
     * @param update The update message to be sent to the observers.
     */
    @Override
    public void notifyObservers(final String update) {
        for (BalanceObserver observer : followersList) {
            observer.update(update);
        }
    }

    /**
     * Retrieves the song that is currently playing based on the current timestamp and the shuffle
     * array.
     *
     * This method iterates over the shuffle array and for each song, it checks if the sum of the
     * durations of the songs so far plus the duration of the current song is greater than the
     * current timestamp. If it is, it returns the current song. If it iterates over all the songs
     * and does not find a song that meets this condition, it returns null.
     *
     * @param currentTimestamp The current timestamp.
     * @param currentShuffleArray The shuffle array.
     * @return The song that is currently playing, or null if no song is currently playing.
     */
    public SongInput getCurrentPlayingSong(
            final Integer currentTimestamp, final List<Integer> currentShuffleArray) {
        int durationSum = 0;
        Integer playlistDimension = this.getSummedDurationsPlaylist();

        for (Integer integer : currentShuffleArray) {
            SongInput song = this.getSongs().get(integer);

            if (durationSum + song.getDuration() > currentTimestamp) {
                return song;
            }

            durationSum += song.getDuration();
        }

        return null;
    }
}
