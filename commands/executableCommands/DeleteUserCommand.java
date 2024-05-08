package commands.executableCommands;

import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_DELETE;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.SUCCESS_DELETE;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.updateAudioTrackbar;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.AlbumInput;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

public final class DeleteUserCommand implements Executable {

  /**
   * The method deletes a user, artist, or host from the library. The command should contain the
   * username of the account to delete. The method first checks if the username exists and if so,
   * depeding of the type of users, it calls a different method. If the user is an artist, it calls
   * the `deleteArtist` method to delete the artist. If the user is a host, it calls the
   * `deleteHost` method to delete the host. If the user is a normal user, it calls the
   * `deleteNormalUser` method to delete the user.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the user, artist, or host to delete.
   * @return A BaseOutput object containing the result of the command. If the command was
   *     successful, this will be a success message. If the command failed, this will be an error
   *     message.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    String username = command.getUsername();
    UserInput normalUser = LibraryInput.getInstance().getUserByName(username);
    ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());
    HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());

    if (normalUser == null && artist == null && host == null) {
      message = USERNAME + username + NO_EXIST.getName();
      return new Output(command, message);
    }

    if (artist != null) {
      Output outputArtist = deleteArtist(command);
      if (outputArtist != null) {
        return outputArtist;
      }
    }

    if (host != null) {
      Output outputHost = deleteHost(command, host, username);
      if (outputHost != null) {
        return outputHost;
      }
    }

    if (normalUser != null && !normalUser.isPremium()) {
      Output outputNormalUser = deleteNormalUser(command, normalUser, username);
      if (outputNormalUser != null) {
        return outputNormalUser;
      }
    }

    message = username + SUCCESS_DELETE.getName();

    return new Output(command, message);
  }

  /**
   * The method deletes a normal user from the library and all connections and files related to him.
   * It first decreases the followers count of all playlists followed by the user, and the likes
   * count of all songs liked by the user. Then it removes all playlists created by the user from
   * all users' followed playlists and from the library. Finally, it removes the user from the
   * library.
   *
   * @param normalUser The normal user to delete. This should be an instance of UserInput.
   */
  private static void safeDeleteDataNormalUser(final UserInput normalUser) {
    for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
      if (normalUser.getFollowedPlaylists().contains(playlist)) {
        playlist.setFollowers(playlist.getFollowers() - 1);
      }
    }

    for (SongInput song : LibraryInput.getInstance().getSongs()) {
      if (normalUser.getLikedSongs().contains(song)) {
        song.setLikes(song.getLikes() - 1);
      }
    }

    for (PlaylistInput playlist : normalUser.getCreatedPlaylists()) {
      for (UserInput user : LibraryInput.getInstance().getUsers()) {
        user.getFollowedPlaylists().remove(playlist);
      }

      LibraryInput.getInstance().getPlaylists().remove(playlist);
    }

    LibraryInput.getInstance().getUsers().remove(normalUser);
  }

  /**
   * The method deletes a host by removing all podcasts owned by the host from the library and then
   * the host from the library.
   *
   * @param host The host to delete. This should be an instance of HostInput.
   */
  private static void safeDeleteDataHost(final HostInput host) {
    LibraryInput.getInstance()
        .getPodcasts()
        .removeIf(podcast -> podcast.getOwner().equals(host.getUsername()));

    LibraryInput.getInstance().getHosts().remove(host);
  }

  /**
   * The method deletes a normal user from the library. It checks if any online user is currently
   * playing a playlist owned by the user and if not it calls the `safeDeleteDataNormalUser` method
   * to safely delete the user from the library.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the user to delete.
   * @param normalUser The normal user to delete. This should be an instance of UserInput.
   * @param username The username of the user to delete.
   * @return An Output object containing the result of the command. If the command was successful,
   *     this will be null. If the command failed, this will be an error message.
   */
  private static Output deleteNormalUser(
      final Command command, final UserInput normalUser, final String username) {
    String message;
    for (UserInput user : LibraryInput.getInstance().getUsers()) {
      Player player = user.getPlayer();

      if (user.isOnline()) {
        updateAudioTrackbar(command, player, user.getUsername());
      } else {
        player.setLastCommandTimestamp(command.getTimestamp());
        message = user.getUsername() + IS_OFFLINE.getName();
        return new Output(command, message);
      }

      if (!player.isFinished()) {
        if (player.getLoadedPlaylist() != null
            && player.getLoadedPlaylist().getOwner().equals(normalUser.getUsername())) {
          message = username + NO_DELETE.getName();
          return new Output(command, message);
        }
      }
    }

    safeDeleteDataNormalUser(normalUser);
    return null;
  }

  /**
   * The method deletes a host from the library. It first checks if any online user is currently
   * playing a podcast owned by the host or is on a page owned by the host. If not, it calls the
   * `safeDeleteDataHost` method to safely delete the host from the library.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the host to delete.
   * @param host The host to delete. This should be an instance of HostInput.
   * @param username The username of the host to delete.
   * @return An Output object containing the result of the command. If the command was successful,
   *     this will be null. If the command failed, this will be an error message.
   */
  private static Output deleteHost(
      final Command command, final HostInput host, final String username) {
    for (UserInput user : LibraryInput.getInstance().getUsers()) {
      Player player = user.getPlayer();

      if (user.isOnline()) {
        updateAudioTrackbar(command, player, user.getUsername());
      } else {
        player.setLastCommandTimestamp(command.getTimestamp());
      }

      if (!player.isFinished()) {
        Output outputVerifyPodcast = verifyPodcast(command, host, username, player);
        if (outputVerifyPodcast != null) {
          return outputVerifyPodcast;
        }
      }

      Output outputVerifyPage = verifyPage(command, host, username, user);
      if (outputVerifyPage != null) {
        return outputVerifyPage;
      }
    }

    safeDeleteDataHost(host);
    return null;
  }

  /**
   * The method checks if the user's current page is owned by the host.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the host to delete.
   * @param host The host to delete. This should be an instance of HostInput.
   * @param username The username of the host to delete.
   * @param user The user to check. This should be an instance of UserInput.
   * @return An Output object containing the result of the verification. If the user is on a page
   *     owned by the host, this will be an error message. Otherwise, this will be null.
   */
  private static Output verifyPage(
      final Command command, final HostInput host, final String username, final UserInput user) {
    String message;
    if (user.getPage().getOwner().equals(host.getUsername())) {
      message = username + NO_DELETE.getName();
      return new Output(command, message);
    }
    return null;
  }

  /**
   * The method checks if the player's currently loaded podcast is owned by the host.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the host to delete.
   * @param host The host to delete. This should be an instance of HostInput.
   * @param username The username of the host to delete.
   * @param player The player to check. This should be an instance of Player.
   * @return An Output object containing the result of the verification. If the player is playing a
   *     podcast owned by the host, this will be an error message. Otherwise, this will be null.
   */
  private static Output verifyPodcast(
      final Command command, final HostInput host, final String username, final Player player) {
    String message;
    if (player.getLoadedPodcast() != null
        && player.getLoadedPodcast().getOwner().equals(host.getUsername())) {
      message = username + NO_DELETE.getName();
      return new Output(command, message);
    }
    return null;
  }

  /**
   * The method deletes an artist from the library and all his connections if all conditions are
   * met. If no user is playing an album or a playlist owned by the artist or is on a page owned by
   * the artist, it calls the `safeDeleteDataArtist` method to safely delete the artist from the
   * library.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the artist to delete.
   * @return An Output object containing the result of the command. If the command was successful,
   *     this will be null. If the command failed, this will be an error message.
   */
  private static Output deleteArtist(final Command command) {
    ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());

    for (UserInput user : LibraryInput.getInstance().getUsers()) {

      Player player = user.getPlayer();
      if (user.isOnline()) {
        updateAudioTrackbar(command, player, user.getUsername());
      } else {
        player.setLastCommandTimestamp(command.getTimestamp());
      }

      if (!player.isFinished()) {
        Output outputVerifyAlbum = verifyAlbum(command, player, artist);
        if (outputVerifyAlbum != null) {
          return outputVerifyAlbum;
        }

        if (player.getLoadedPlaylist() != null) {
          Output outputVerifyPlaylist = verifyPlaylist(command, player, artist);
          if (outputVerifyPlaylist != null) {
            return outputVerifyPlaylist;
          }
        }
      }

      Output outputVerifyPage = verifyPage(command, user, artist);
      if (outputVerifyPage != null) {
        return outputVerifyPage;
      }
    }

    safeDeleteDataArtist(artist);
    return null;
  }

  /**
   * The method safely deletes an artist from the library and all of his creations.
   *
   * <p>The method first removes all albums owned by the artist from the library. Then it removes
   * all songs by the artist from all playlists in the library. Then it removes all songs from the
   * artist's albums from the library. Then it removes all songs by the artist from the library.
   * Then it removes all songs by the artist from the liked songs of all users in the library.
   * Finally, it removes the artist from the library.
   *
   * @param artist The artist to delete. This should be an instance of ArtistInput.
   */
  private static void safeDeleteDataArtist(final ArtistInput artist) {
    LibraryInput.getInstance()
        .getAlbums()
        .removeIf(album -> album.getOwner().equals(artist.getUsername()));

    for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
      playlist.getSongs().removeIf(song -> song.getArtist().equals(artist.getUsername()));
    }

    for (AlbumInput album : artist.getArtistAlbums()) {
      LibraryInput.getInstance().getSongs().removeAll(album.getSongs());
    }

    LibraryInput.getInstance()
        .getSongs()
        .removeIf(song -> song.getArtist().equals(artist.getUsername()));

    for (UserInput nextUser : LibraryInput.getInstance().getUsers()) {
      nextUser.getLikedSongs().removeIf(song -> song.getArtist().equals(artist.getUsername()));
    }

    LibraryInput.getInstance().getArtists().remove(artist);
  }

  /**
   * The method checks if the user's current page is owned by the artist.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the artist to delete.
   * @param user The user to check. This should be an instance of UserInput.
   * @param artist The artist to delete. This should be an instance of ArtistInput.
   * @return An Output object containing the result of the verification. If the user is on a page
   *     owned by the artist, this will be an error message. Otherwise, this will be null.
   */
  private static Output verifyPage(
      final Command command, final UserInput user, final ArtistInput artist) {
    String message;
    if (user.getPage().getOwner().equals(artist.getUsername())) {
      message = command.getUsername() + NO_DELETE.getName();
      return new Output(command, message);
    }
    return null;
  }

  /**
   * The method checks if the song currently playing from the player's loaded playlist is owned by
   * the artist.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the artist to delete.
   * @param player The player to check. This should be an instance of Player.
   * @param artist The artist to delete. This should be an instance of ArtistInput.
   * @return An Output object containing the result of the verification. If the player is playing a
   *     song from a playlist owned by the artist, this will be an error message. Otherwise, this
   *     will be null.
   */
  private static Output verifyPlaylist(
      final Command command, final Player player, final ArtistInput artist) {
    String message;
    PlaylistInput playlist = player.getLoadedPlaylist();
    SongInput song =
        playlist.getShuffleCurrentPlayingSong(
            player.getTotalTimestamp(), player.getCurrentShuffleArray());

    if (song.getArtist().equals(artist.getUsername())) {
      message = command.getUsername() + NO_DELETE.getName();
      return new Output(command, message);
    }
    return null;
  }

  /**
   * The method checks if the player's currently loaded album or song is owned by the artist.
   *
   * @param command The command to execute. This should be an instance of Command with
   *                the username of the artist to delete.
   * @param player The player to check. This should be an instance of Player.
   * @param artist The artist to delete. This should be an instance of ArtistInput.
   * @return An Output object containing the result of the verification. If the player is
   *         playing an album or a song owned by the artist, this will be an error message.
   *         Otherwise, this will be null.
   */
  private static Output verifyAlbum(
      final Command command, final Player player, final ArtistInput artist) {
    String message;
    if (player.getLoadedAlbum() != null
            && player.getLoadedAlbum().getOwner().equals(artist.getUsername())
        || player.getLoadedSong() != null
            && player.getLoadedSong().getArtist().equals(artist.getUsername())) {

      message = command.getUsername() + NO_DELETE.getName();
      return new Output(command, message);
    }
    return null;
  }
}
