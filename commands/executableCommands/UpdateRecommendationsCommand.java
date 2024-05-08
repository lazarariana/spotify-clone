package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.History;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static commands.constants.Constants.FANS_PLAYLIST;
import static commands.constants.Constants.MIN_PASSED_TIME;
import static commands.constants.Constants.RANDOM_PLAYLIST;
import static commands.constants.Constants.RANDOM_SONG;
import static commands.constants.Constants.TOP2;
import static commands.constants.Constants.TOP3;
import static commands.constants.Constants.TOP5;
import static commands.constants.Constants.TOP_GENRE;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.FAN_CLUB;
import static commands.constants.UserCommandsEnums.S_RECOMMENDATIONS;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.NOT_NORMAL;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.constants.UserCommandsEnums.UpdateRecommendationsMessagesEnum.NO_RECOMMENDATIONS;
import static commands.constants.UserCommandsEnums.UpdateRecommendationsMessagesEnum.RECOMMENDATIONS;
import static commands.constants.UserCommandsEnums.UpdateRecommendationsMessagesEnum.UPDATED_SUCCESSFULLY;
import static commands.player.TimestampTrack.getSongRemainingTime;

public class UpdateRecommendationsCommand implements Executable {
    /**
     * This method takes a Command object as input and retrieves the user, artist, and host
     * associated with the username in the command.
     * If the user, artist, and host are all null, it returns an error message.
     * If the user is an artist or host, it returns an error message.
     * Depending on the recommendation type in the command, it calls the appropriate method to
     * generate a song or playlist recommendation, or a fans playlist.
     * It then sets the last recommendation type for the user and returns the result of the command
     * execution.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object containing the result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);
        ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());
        HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());

        if (user == null && artist == null && host == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        if (artist != null || host != null) {
            message = USERNAME + username + NOT_NORMAL.getName();
            return new Output(command, message);
        }

        if (command.getRecommendationType().equals(RANDOM_SONG)) {
            user.setLastRecommendationType(RANDOM_SONG);
            message = randomSong(user);
            return new Output(command, message);
        }

        if (command.getRecommendationType().equals(RANDOM_PLAYLIST)) {
            user.setLastRecommendationType(RANDOM_PLAYLIST);
            message = randomPlaylist(command, user);
            return new Output(command, message);
        }

        if (command.getRecommendationType().equals(FANS_PLAYLIST)) {
            user.setLastRecommendationType(FANS_PLAYLIST);
            PlaylistInput loadedPlaylist = user.getPlayer().getLoadedPlaylist();
            SongInput loadedSong = user.getPlayer().getLoadedSong();
            String songArtist = null;

            if (loadedPlaylist != null) {
                SongInput currentSong = loadedPlaylist
                        .getShuffleCurrentPlayingSong(command.getTimestamp(),
                                user.getPlayer().getCurrentShuffleArray());

                if (currentSong != null) {
                    songArtist = currentSong.getArtist();
                }
            } else if (loadedSong != null) {
                songArtist = loadedSong.getArtist();
            }

            message = fansPlaylist(command, songArtist);
            return new Output(command, message);
        }
        return null;
    }

    /**
     * Recommends a random song from the same genre as the currently loaded song for a given user.
     * <p>
     * This method takes a UserInput object as input and checks the passed time of the currently
     * loaded song.
     * If the passed time is less than a minimum threshold, it returns a message indicating that
     * no recommendations can be made.
     * Otherwise, it gets a list of all songs in the same genre as the currently loaded song,
     * generates a random index based on the passed time, and selects the song at that index.
     * It then adds the selected song to the user's song recommendations and returns a success
     * message.
     *
     * @param user The user for whom the song recommendation is to be made.
     * @return A string indicating whether the recommendation was successful or not.
     */
    public String randomSong(final UserInput user) {
        SongInput song = user.getPlayer().getLoadedSong();
        int passedTime = song.getDuration() - getSongRemainingTime(user.getPlayer());

        if (passedTime < MIN_PASSED_TIME) {
            return NO_RECOMMENDATIONS.getName();
        }

        List<String> genreSongs = LibraryInput.getInstance().getSongsByGenre(song.getGenre());
        Random random = new Random(passedTime);
        int randomIndex = random.nextInt(genreSongs.size());
        String randomSong = genreSongs.get(randomIndex);
        user.getSongRecommendations().add(randomSong);

        return RECOMMENDATIONS.getName() + user.getUsername() + UPDATED_SUCCESSFULLY.getName();
    }

    /**
     * Recommends a random playlist for a given user based on their genre preferences.
     * <p>
     * This method takes a Command and UserInput object as input and calculates the frequency of
     * each genre in the user's listen history.
     * It then selects the top 3 genres and gets all songs in these genres.
     * It selects the top 5 songs from the first genre, the top 3 songs from the second genre, and
     * the top 2 songs from the third genre to create a playlist.
     * If the playlist is empty, it returns a message indicating that no recommendations can be
     * made.
     * Otherwise, it adds the playlist to the user's playlist recommendations and returns a success
     * message.
     *
     * @param command The command to be executed.
     * @param user    The user for whom the playlist recommendation is to be made.
     * @return A string indicating whether the recommendation was successful or not.
     */
    public String randomPlaylist(final Command command, final UserInput user) {

        Map<String, Integer> genreFrequencies = getGenreFrequency(user);
        List<String> topGenres = getTopGenres(genreFrequencies);
        List<SongInput> songsTop3Genre = createTop3GenreSongs(topGenres);
        List<SongInput> top5FirstGenreSongs = getGenreSongs(songsTop3Genre, topGenres, TOP5, 0);
        List<SongInput> top3SecondGenreSongs = getGenreSongs(songsTop3Genre, topGenres, TOP3, 1);
        List<SongInput> top2ThirdGenreSongs = getGenreSongs(songsTop3Genre, topGenres, TOP2, 2);
        PlaylistInput randomPlaylist = createRandomPlaylist(command, top5FirstGenreSongs,
                top3SecondGenreSongs, top2ThirdGenreSongs);

        if (randomPlaylist.getSongs().isEmpty()) {
            return NO_RECOMMENDATIONS.getName();
        }

        user.getPlaylistRecommendations().add(randomPlaylist);
        return RECOMMENDATIONS.getName() + user.getUsername() + UPDATED_SUCCESSFULLY.getName();
    }

    /**
     * Generates a personalized playlist recommendation for a given artist based on the top 5 fans'
     * liked songs.
     * The recommendation is created for the user associated with the given command.
     *
     * @param command The command containing user information and preferences.
     * @param artist  The artist for whom the playlist recommendation is generated.
     * @return A message indicating the status of the playlist generation.
     * - If the playlist is successfully generated, the message includes the updated username and
     * success status.
     * - If no recommendations are available, the message indicates the absence of recommendations.
     */
    public String fansPlaylist(final Command command, final String artist) {
        UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
        List<String> top5Fans = getTop5Fans(artist);
        List<List<SongInput>> top5LikedSongsAllFans = new ArrayList<>();

        for (String fan : top5Fans) {
            List<SongInput> top5LikedSongs = getTop5LikedSongs(fan);
            top5LikedSongsAllFans.add(top5LikedSongs);
        }

        PlaylistInput randomPlaylist = createFansPlaylist(command, artist, top5LikedSongsAllFans);

        if (randomPlaylist.getSongs().isEmpty()) {
            return NO_RECOMMENDATIONS.getName();
        }

        user.getPlaylistRecommendations().add(randomPlaylist);
        return RECOMMENDATIONS.getName() + user.getUsername() + UPDATED_SUCCESSFULLY.getName();
    }

    /**
     * Calculates the frequency of each genre in a user's liked songs, created playlists, and
     * followed playlists.
     * <p>
     * This method takes a UserInput object as input and retrieves the user's liked songs, created
     * playlists, and followed playlists.
     * It then iterates over each song in these collections and increments the count for the song's
     * genre in a HashMap.
     * The HashMap is then returned, with each key being a genre and each value being the frequency
     * of that genre.
     *
     * @param user The user for whom the genre frequency is to be calculated.
     * @return A HashMap where each key is a genre and each value is the frequency of that genre.
     */
    public Map<String, Integer> getGenreFrequency(final UserInput user) {
        List<SongInput> likedSongs = user.getLikedSongs();
        List<PlaylistInput> createdPlaylists = user.getCreatedPlaylists();
        List<PlaylistInput> followedPlaylists = user.getFollowedPlaylists();
        Map<String, Integer> genreFrequency = new HashMap<>();

        for (SongInput song : likedSongs) {
            genreFrequency.put(song.getGenre(), genreFrequency
                    .getOrDefault(song.getGenre(), 0) + 1);
        }

        for (PlaylistInput playlist : createdPlaylists) {
            for (SongInput song : playlist.getSongs()) {
                genreFrequency.put(song.getGenre(), genreFrequency
                        .getOrDefault(song.getGenre(), 0) + 1);
            }
        }

        for (PlaylistInput playlist : followedPlaylists) {
            for (SongInput song : playlist.getSongs()) {
                genreFrequency.put(song.getGenre(), genreFrequency
                        .getOrDefault(song.getGenre(), 0) + 1);
            }
        }

        return genreFrequency;
    }

    /**
     * Returns the top genres based on their frequency.
     * <p>
     * This method takes a Map of genre frequencies as input, sorts the entries in descending order
     * of frequency,
     * limits the result to the top genres (defined by the constant TOP_GENRE), and collects the
     * genre names into a List.
     *
     * @param genreFrequency A Map where each key is a genre and each value is the frequency of
     *                       that genre.
     * @return A List of the top genres.
     */
    public static List<String> getTopGenres(final Map<String, Integer> genreFrequency) {
        return genreFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(TOP_GENRE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of songs that belong to the top 3 genres.
     * <p>
     * This method takes a list of top genres as input and retrieves all songs from the library
     * that belong to these genres.
     * It also retrieves all songs from the library's playlists that belong to these genres.
     * It then removes duplicates, sorts the songs in descending order of likes, and returns the
     * sorted list.
     *
     * @param topGenres A list of the top genres.
     * @return A list of songs that belong to the top 3 genres, sorted in descending order of likes.
     */
    public List<SongInput> createTop3GenreSongs(final List<String> topGenres) {
        List<SongInput> songsTop3Genres = new ArrayList<>();

        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if ((topGenres.size() > 0 && song.getGenre().equals(topGenres.get(0)))
                    || (topGenres.size() > 1 && song.getGenre().equals(topGenres.get(1)))
                    || (topGenres.size() > 2 && song.getGenre().equals(topGenres.get(2)))) {
                songsTop3Genres.add(song);
            }
        }

        for (PlaylistInput libraryPlaylist : LibraryInput.getInstance().getPlaylists()) {
            for (SongInput song : libraryPlaylist.getSongs()) {
                if (song.getGenre().equals(topGenres.get(0))
                        || song.getGenre().equals(topGenres.get(1))
                        || song.getGenre().equals(topGenres.get(2))) {
                    songsTop3Genres.add(song);
                }
            }
        }

        songsTop3Genres = songsTop3Genres.stream()
                .distinct()
                .sorted((s1, s2) -> s2.getLikes() - s1.getLikes())
                .collect(Collectors.toList());


        return songsTop3Genres;
    }

    /**
     * Returns a list of songs that belong to a specific genre.
     * <p>
     * This method takes a list of songs, a list of top genres, a top constant,and a genre index
     * as input.
     * It iterates over the list of songs and adds a song to the result list if it belongs to the
     * genre at the given index in the top genres list.
     * The method stops adding songs to the result list once its size reaches the top constant.
     *
     * @param songsTop3Genre A list of songs that belong to the top 3 genres.
     * @param topGenres      A list of the top genres.
     * @param topConstant    The maximum number of songs to be added to the result list.
     * @param genreIndex     The index of the genre in the top genres list.
     * @return A list of songs that belong to the genre at the given index in the top genres list.
     */
    public List<SongInput> getGenreSongs(final List<SongInput> songsTop3Genre,
                                         final List<String> topGenres,
                                         final int topConstant, final int genreIndex) {
        List<SongInput> topGenreSongs = new ArrayList<>();

        for (SongInput song : songsTop3Genre) {
            if (topGenreSongs.size() == topConstant) {
                break;
            }

            if (genreIndex < topGenres.size()
                    && song.getGenre().equals(topGenres.get(genreIndex))) {
                topGenreSongs.add(song);
            }
        }

        return topGenreSongs;
    }

    /**
     * Creates a random playlist from the top songs of the top 3 genres.
     * <p>
     * This method takes a Command object and lists of the top songs of the first, second, and
     * third genres as input.
     * It creates a new playlist with a name based on the command's username and adds the top songs
     * to the playlist.
     * It then removes duplicates from the playlist's songs and sets the playlist's songs to the
     * deduplicated list.
     *
     * @param command              The command to be executed.
     * @param top5FirstGenreSongs  A list of the top 5 songs of the first genre.
     * @param top3SecondGenreSongs A list of the top 3 songs of the second genre.
     * @param top2ThirdGenreSongs  A list of the top 2 songs of the third genre.
     * @return A PlaylistInput object representing the created playlist.
     */
    public PlaylistInput createRandomPlaylist(final Command command,
                                              final List<SongInput> top5FirstGenreSongs,
                                              final List<SongInput> top3SecondGenreSongs,
                                              final List<SongInput> top2ThirdGenreSongs) {
        String name = command.getUsername() + S_RECOMMENDATIONS;
        PlaylistInput randomPlaylist = new PlaylistInput(command, name);

        for (SongInput song : top5FirstGenreSongs) {
            randomPlaylist.getSongs().add(song);
        }

        for (SongInput song : top3SecondGenreSongs) {
            randomPlaylist.getSongs().add(song);
        }

        for (SongInput song : top2ThirdGenreSongs) {
            randomPlaylist.getSongs().add(song);
        }

        List<SongInput> randomPlaylistSongs = randomPlaylist.getSongs();

        randomPlaylistSongs = new ArrayList<>(new LinkedHashSet<>(randomPlaylistSongs));

        randomPlaylist.setSongs(randomPlaylistSongs);

        return randomPlaylist;
    }

    /**
     * Generates a personalized playlist recommendation for a given artist based on the top 5 fans'
     * liked songs.
     * The recommendation is created for the user associated with the given command.
     *
     * @param command The command containing user information and preferences.
     * @param artist  The artist for whom the playlist recommendation is generated.
     * @return A message indicating the status of the playlist generation.
     * - If the playlist is successfully generated, the message includes the updated
     * username and success status.
     * - If no recommendations are available, the message indicates the absence of
     * recommendations.
     */
    public PlaylistInput createFansPlaylist(final Command command, final String artist,
                                            final List<List<SongInput>> top5LikedSongsAllFans) {
        String name = artist + FAN_CLUB;
        PlaylistInput fansPlaylist = new PlaylistInput(command, name);

        for (List<SongInput> top5LikedSongs : top5LikedSongsAllFans) {
            for (SongInput song : top5LikedSongs) {
                fansPlaylist.getSongs().add(song);
            }
        }

        List<SongInput> randomPlaylistSongs = fansPlaylist.getSongs();

        randomPlaylistSongs = new ArrayList<>(new LinkedHashSet<>(randomPlaylistSongs));

        fansPlaylist.setSongs(randomPlaylistSongs);

        return fansPlaylist;
    }

    /**
     * Returns the top 5 fans of a given artist based on the number of times they have listened to
     * the artist's songs.
     * <p>
     * This method takes an artist name as input and retrieves all users from the library.
     * It then iterates over each user's song history and increments the count for the user in a
     * HashMap if the song's artist is the given artist.
     * The method then sorts the entries in the HashMap in descending order of count, limits the
     * result to the top 5 users, and collects the usernames into a List.
     *
     * @param artist The artist for whom the top 5 fans are to be found.
     * @return A List of the usernames of the top 5 fans of the given artist.
     */
    public List<String> getTop5Fans(final String artist) {
        HashMap<String, Integer> topFansHashMap = new HashMap<>();
        List<String> top5Fans = new ArrayList<>();

        for (UserInput user : LibraryInput.getInstance().getUsers()) {
            History listens = user.getListens();

            for (SongInput song : listens.getSongHistory()) {
                if (song.getArtist().equals(artist)) {
                    if (topFansHashMap.containsKey(user.getUsername())) {
                        topFansHashMap.put(user.getUsername(), topFansHashMap
                                .get(user.getUsername()) + 1);
                    } else {
                        topFansHashMap.put(user.getUsername(), 1);
                    }
                }
            }
        }

        top5Fans = topFansHashMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(TOP5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return top5Fans;
    }

    /**
     * Returns the top 5 liked songs of a given fan.
     * <p>
     * This method takes a fan's username as input and retrieves the fan's liked songs from the
     * library.
     * It then sorts the liked songs in descending order of likes, limits the result to the top
     * 5 songs, and collects the songs into a List.
     *
     * @param fan The username of the fan whose top 5 liked songs are to be found.
     * @return A List of the top 5 liked songs of the given fan.
     */
    public List<SongInput> getTop5LikedSongs(final String fan) {
        UserInput user = LibraryInput.getInstance().getUserByName(fan);

        List<SongInput> likedSongs = user.getLikedSongs();

        return likedSongs.stream()
                .sorted(Comparator.comparing(SongInput::getLikes).reversed())
                .limit(TOP5)
                .toList();
    }

}
