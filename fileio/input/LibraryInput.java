package fileio.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pages.HomePage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public final class LibraryInput {
  private List<SongInput> songs;
  private List<PodcastInput> podcasts;
  private List<AlbumInput> albums;

  @JsonIgnore private static LibraryInput library;
  @JsonIgnore private ArrayList<PlaylistInput> playlists;

  private List<UserInput> users;
  private List<ArtistInput> artists;
  private List<HostInput> hosts;

  private LibraryInput() {
    playlists = new ArrayList<>();
  }

  /**
   * The method assigns the provided LibraryInput object to the library instance and initializes its
   * playlists list.
   *
   * @param userLibrary The LibraryInput object to be set as the instance.
   */
  public static void setInstance(final LibraryInput userLibrary) {
    LibraryInput.library = userLibrary;
    library.setPlaylists(new ArrayList<>());
    library.setAlbums(new ArrayList<>());
    library.setArtists(new ArrayList<>());
    library.setHosts(new ArrayList<>());

    for (UserInput user : library.getUsers()) {
      user.setPage(new HomePage(user));
    }
  }

  /**
   * If the library instance is null, the method initializes it.
   *
   * @return A LibraryInput object representing the instance of the library.
   */
  public static LibraryInput getInstance() {
    if (library == null) {
      library = new LibraryInput();
    }

    return LibraryInput.library;
  }

  /**
   * The method iterates over all users in the library. If it finds a user with the same username as
   * the one provided, it returns the user. If it does not find a matching user, it returns null.
   *
   * @param name The username of the user to be retrieved.
   * @return A UserInput object representing the retrieved user, or null if no matching user is
   *     found.
   */
  public UserInput getUserByName(final String name) {
    for (UserInput user : LibraryInput.getInstance().users) {
      if (user.getUsername().equals(name)) {
        return user;
      }
    }

    return null;
  }

  /**
   * The method iterates over all songs in the library. If it finds a song with the same name as the
   * one provided, it returns the song. If it does not find a matching song, it returns null.
   *
   * @param name The name of the song to be retrieved.
   * @return A SongInput object representing the retrieved song, or null if no matching song is
   *     found.
   */
  public SongInput getSongByName(final String name) {
    for (SongInput song : LibraryInput.getInstance().songs) {
      if (song.getName().equals(name)) {
        return song;
      }
    }

    return null;
  }

  /**
   * The method iterates over all the artists in the library and returns the one with the given
   * username. It returns the ArtistInput object with the given username or null if no artist with
   * the given username is found.
   *
   * @param name The username of the artist to get. This should be a String.
   * @return The ArtistInput object with the given username, or null if no such artist is found.
   */
  public ArtistInput getArtistByName(final String name) {
    for (ArtistInput artist : LibraryInput.getInstance().artists) {
      if (artist.getUsername().equals(name)) {
        return artist;
      }
    }

    return null;
  }

  /**
   * The method iterates over all the hosts in the library and returns the one with the given
   * username. It returns the HostInput object with the given username or null if no host with the
   * given username is found.
   *
   * @param name The username of the host to get. This should be a String.
   * @return The HostInput object with the given username, or null if no such host is found.
   */
  public HostInput getHostByName(final String name) {
    for (HostInput host : LibraryInput.getInstance().hosts) {
      if (host.getUsername().equals(name)) {
        return host;
      }
    }

    return null;
  }

  /**
   * The method checks if a list has unique elements.
   *
   * <p>It creates a set from the list, which removes any duplicate elements. It then checks if the
   * size of the set is equal to the size of the list. If the sizes are equal, it means that all
   * elements in the list were unique.
   *
   * @param list The list to check for unique elements. This should be an ArrayList of Strings.
   * @return true if the list has unique elements, false otherwise.
   */
  public static boolean hasUniqueElements(final List<String> list) {
    Set<String> set = new HashSet<>(list);
    return set.size() == list.size();
  }

  /**
   * The method iterates over all the albums in the library and returns the one with the given name.
   * It returns the AlbumInput object with the given name or null if no album with the given name is
   * found.
   *
   * @param name The name of the album to get. This should be a String.
   * @return The AlbumInput object with the given name, or null if no such album is found.
   */
  public AlbumInput getAlbumByName(final String name) {
    for (AlbumInput album : LibraryInput.getInstance().getAlbums()) {
      if (album.getName().equals(name)) {
        return album;
      }
    }

    return null;
  }

  /**
   * Retrieves the names of all songs in the library that belong to a specific genre.
   *
   * This method iterates over all songs in the library. For each song, it checks if the song's
   * genre matches the input genre. If it does, it adds the song's name to a list.
   *
   * @param genre The genre of the songs to retrieve.
   * @return A list of names of songs that belong to the input genre.
   */
  public List<String> getSongsByGenre(final String genre) {
    List<String> genreSongs = new ArrayList<>();

    for (SongInput song : LibraryInput.getInstance().getSongs()) {
      if (song.getGenre().equals(genre)) {
        genreSongs.add(song.getName());
      }
    }

    return genreSongs;
  }

  /**
   * Retrieves a playlist from the library by its name.
   *
   * This method iterates over all playlists in the library. For each playlist, it checks if the
   * playlist's name matches the input name. If it does, it returns the playlist.
   *
   * @param name The name of the playlist to retrieve.
   * @return The playlist with the input name. If no such playlist is found, it returns null.
   */
  public PlaylistInput getPlaylistByName(final String name) {
    for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
      if (playlist.getName().equals(name)) {
        return playlist;
      }
    }

    return null;
  }
}
