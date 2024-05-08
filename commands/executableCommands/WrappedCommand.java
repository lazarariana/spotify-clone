package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.jsonReader.WrappedOutput;
import commands.jsonReader.WrappedResultArtist;
import commands.jsonReader.WrappedResultHost;
import commands.jsonReader.WrappedResultUser;
import commands.player.History;
import commands.player.Player;
import fileio.input.ArtistInput;
import fileio.input.EpisodeInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static commands.constants.Constants.DOT;
import static commands.constants.Constants.TOP5;
import static commands.constants.UserCommandsEnums.WrappedMessagesEnum.NO_LISTENS_ARTIST;
import static commands.constants.UserCommandsEnums.WrappedMessagesEnum.NO_LISTENS_HOST;
import static commands.constants.UserCommandsEnums.WrappedMessagesEnum.NO_LISTENS_USER;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public class WrappedCommand implements Executable {
    /**
     * The method first checks if the user, artist, or host exists based on the username from the
     * command.
     * If the user exists, it updates the audio trackbar if the user is online, sets the last
     * command timestamp if the user is offline,
     * checks if the user has any listens, and if so, calculates and returns the user's statistics.
     * If the user is an artist, it calculates the artist's statistics and checks if the artist
     * has any top songs, albums, or fans.
     * If the artist has any of these, it returns the artist's statistics.
     * If the user is a host, it calculates the host's statistics and checks if the host has any
     * top episodes.
     * If the host has any top episodes, it returns the host's statistics.
     * If the user, artist, or host does not exist, it returns null.
     *
     * @param command The command to be executed.
     * @return An instance of BaseOutput which contains the command and a message indicating the
     * result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);
        ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());
        HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());

        if (user != null) {
            Player player = user.getPlayer();
            if (user.isOnline()) {
                updateAudioTrackbar(command, player, command.getUsername());
            } else {
                player.setLastCommandTimestamp(command.getTimestamp());
            }

            if (user.getListens().isEmpty()) {
                message = NO_LISTENS_USER.getName() + username + DOT;
                return new Output(command, message);
            }

            return new WrappedOutput(command, calculateUserStatistics(user));
        }

        if (artist != null) {
            WrappedResultArtist artistTops = calculateArtistStatistics(artist);

            if (artistTops.getTopSongs().isEmpty() && artistTops.getTopAlbums().isEmpty()
                    && artistTops.getTopFans().isEmpty()) {
                message = NO_LISTENS_ARTIST.getName() + username + DOT;
                return new Output(command, message);
            }

            return new WrappedOutput(command, artistTops);
        }

        if (host != null) {
            WrappedResultHost hostTops = calculateHostStatistics(host);

            if (hostTops.getTopEpisodes().isEmpty()) {
                message = NO_LISTENS_HOST.getName() + username + DOT;
                return new WrappedOutput(command, message);
            }

            return new WrappedOutput(command, hostTops);
        }
        return null;

    }

    /**
     * Calculates the statistics for a given user.
     * <p>
     * This method takes a UserInput object as input and calculates the user's top artists, genres,
     * songs, albums, and episodes based on their listen history.
     * It iterates over the user's song history and episode history, and for each song or episode,
     * it increments the count in the respective HashMap.
     * If the song or episode is not already in the HashMap, it adds it with a count of 1.
     * It then sorts each HashMap by value in descending order and sets the sorted HashMaps in the
     * WrappedResultUser object.
     *
     * @param user The user for whom the statistics are to be calculated.
     * @return A WrappedResultUser object containing the user's top artists, genres, songs, albums
     * and episodes.
     */
    public WrappedResultUser calculateUserStatistics(final UserInput user) {
        History listens = user.getListens();
        HashMap<String, Integer> topArtists = new HashMap<>();
        HashMap<String, Integer> topGenres = new HashMap<>();
        HashMap<String, Integer> topSongs = new HashMap<>();
        HashMap<String, Integer> topAlbums = new HashMap<>();
        HashMap<String, Integer> topEpisodes = new HashMap<>();
        WrappedResultUser result = new WrappedResultUser();

        for (SongInput song : listens.getSongHistory()) {

            if (topArtists.containsKey(song.getArtist())) {
                topArtists.put(song.getArtist(), topArtists.get(song.getArtist()) + 1);
            } else {
                topArtists.put(song.getArtist(), 1);
            }

            if (topGenres.containsKey(song.getGenre())) {
                topGenres.put(song.getGenre(), topGenres.get(song.getGenre()) + 1);
            } else {
                topGenres.put(song.getGenre(), 1);
            }

            if (!topSongs.containsKey(song.getName())) {
                topSongs.put(song.getName(), listens.numberOfListens(song));
            }

            if (!topAlbums.containsKey(song.getAlbum())) {
                topAlbums.put(song.getAlbum(), listens.numberOfListens(song.getAlbum()));
            }
        }

        for (EpisodeInput episode : listens.getEpisodeHistory()) {
            if (!topEpisodes.containsKey(episode.getName())) {
                topEpisodes.put(episode.getName(), listens.numberOfListens(episode));
            }
        }

        topArtists = sortHashMapTop(topArtists);
        topGenres = sortHashMapTop(topGenres);
        topSongs = sortHashMapTop(topSongs);
        topAlbums = sortHashMapTop(topAlbums);
        topEpisodes = sortHashMapTop(topEpisodes);

        result.setTopArtists(topArtists);
        result.setTopGenres(topGenres);
        result.setTopSongs(topSongs);
        result.setTopAlbums(topAlbums);
        result.setTopEpisodes(topEpisodes);

        return result;
    }

    /**
     * Calculates the statistics for a given artist.
     *
     * This method takes an ArtistInput object as input and calculates the artist's top songs,
     * albums, and fans based on the listen history of all users.
     * It iterates over all users and their song history, and for each song by the artist, it
     * increments the count in the respective HashMap.
     * If the song is not already in the HashMap, it adds it with a count of 1.
     * It also keeps track of the number of listeners for the artist.
     * It then sorts each HashMap by value in descending order and sets the sorted HashMaps in
     * the WrappedResultArtist object.
     *
     * @param artist The artist for whom the statistics are to be calculated.
     * @return A WrappedResultArtist object containing the artist's top songs, albums, and fans.
     */
    public WrappedResultArtist calculateArtistStatistics(final ArtistInput artist) {
        HashMap<String, Integer> topSongs = new HashMap<>();
        HashMap<String, Integer> topAlbums = new HashMap<>();
        HashMap<String, Integer> topFansHashMap = new HashMap<>();
        List<String> topFans;
        WrappedResultArtist result = new WrappedResultArtist();

        for (UserInput user : LibraryInput.getInstance().getUsers()) {
            History listens = user.getListens();

            for (SongInput song : listens.getSongHistory()) {
                if (song.getArtist().equals(artist.getUsername())) {
                    if (topSongs.containsKey(song.getName())) {
                        topSongs.put(song.getName(), topSongs.get(song.getName()) + 1);
                    } else {
                        topSongs.put(song.getName(), 1);
                    }

                    if (topAlbums.containsKey(song.getAlbum())) {
                        topAlbums.put(song.getAlbum(), topAlbums.get(song.getAlbum()) + 1);
                    } else {
                        topAlbums.put(song.getAlbum(), 1);
                    }

                    if (topFansHashMap.containsKey(user.getUsername())) {
                        topFansHashMap.put(user.getUsername(), topFansHashMap.get(user
                                .getUsername()) + 1);
                    } else {
                        topFansHashMap.put(user.getUsername(), 1);
                        result.setListeners(result.getListeners() + 1);
                    }
                }
            }
        }

        topFansHashMap = sortHashMapTop(topFansHashMap);

        topFans = new ArrayList<>(topFansHashMap.keySet());

        topSongs = sortHashMapTop(topSongs);
        topAlbums = sortHashMapTop(topAlbums);

        result.setTopSongs(topSongs);
        result.setTopAlbums(topAlbums);
        result.setTopFans(topFans);

        return result;
    }

    /**
     * Calculates the statistics for a given podcast host.
     *
     * This method takes a HostInput object as input and calculates the host's top episodes and the
     * number of listeners based on the listen history of all users.
     * It iterates over all users and their episode history, and for each episode by the host, it
     * increments the count in the respective HashMap.
     * If the episode is not already in the HashMap, it adds it with a count of 1.
     * It also keeps track of the number of listeners for the host.
     * It then sorts the HashMap by value in descending order and sets the sorted HashMap in the
     * WrappedResultHost object.
     *
     * @param host The podcast host for whom the statistics are to be calculated.
     * @return A WrappedResultHost object containing the host's top episodes and the number of
     * listeners.
     */
    public WrappedResultHost calculateHostStatistics(final HostInput host) {
        HashMap<String, Integer> topEpisodes = new HashMap<>();
        Set<String> listeners = new HashSet<>();
        WrappedResultHost result = new WrappedResultHost();

        for (UserInput user : LibraryInput.getInstance().getUsers()) {
            History listens = user.getListens();

            for (EpisodeInput episode : listens.getEpisodeHistory()) {
                if (episode.getPodcastOwner().equals(host.getUsername())) {
                    if (topEpisodes.containsKey(episode.getName())) {
                        topEpisodes.put(episode.getName(), topEpisodes.get(episode.getName()) + 1);
                    } else {
                        topEpisodes.put(episode.getName(), 1);
                    }

                    listeners.add(user.getUsername());
                }
            }
        }

        topEpisodes = sortHashMapTop(topEpisodes);
        result.setTopEpisodes(topEpisodes);
        result.setListeners(listeners.size());

        return result;
    }

    /**
     * Sorts a HashMap by value in descending order and then by key in ascending order, and limits
     * the size to the top 5 entries.
     *
     * This method takes a HashMap as input and sorts it first by value in descending order and
     * then by key in ascending order using a stream.
     * It then limits the size of the HashMap to the top 5 entries and collects the result into a
     * new LinkedHashMap, which maintains the order of the entries.
     *
     * @param topHashMap The HashMap to be sorted.
     * @return A LinkedHashMap containing the top 5 entries from the input HashMap, sorted by value
     * in descending order and then by key in ascending order.
     */
    private static HashMap<String, Integer> sortHashMapTop(HashMap<String, Integer> topHashMap) {
        topHashMap = topHashMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(TOP5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        return topHashMap;
    }
}
