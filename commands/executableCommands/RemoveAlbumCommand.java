package commands.executableCommands;

import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.NOT_ARTIST;
import static commands.constants.ArtistCommandsEnums.DeleteAlbumMessagesEnum.NO_ALBUM;
import static commands.constants.ArtistCommandsEnums.DeleteAlbumMessagesEnum.NO_DELETE;
import static commands.constants.ArtistCommandsEnums.DeleteAlbumMessagesEnum.SUCCESS_DELETE_ALBUM;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
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

public final class RemoveAlbumCommand implements Executable {

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

    if (artist == null) {
      message = username + NOT_ARTIST.getName();
      return new Output(command, message);
    }

    AlbumInput album = LibraryInput.getInstance().getAlbumByName(command.getName());

    if (album == null) {
      message = username + NO_ALBUM.getName();
      return new Output(command, message);
    }

    for (UserInput libraryUser : LibraryInput.getInstance().getUsers()) {
      Player player = libraryUser.getPlayer();
      if (libraryUser.isOnline()) {
        updateAudioTrackbar(command, player, libraryUser.getUsername());
      } else {
        player.setLastCommandTimestamp(command.getTimestamp());
      }

      Output interactionOutput = verifyInteractionWithAlbum(command, player, artist, album);
      if (interactionOutput != null) {
        return interactionOutput;
      }
    }

    for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
      for (SongInput song : playlist.getSongs()) {
        if (album.getSongs().contains(song)) {
          message = username + NO_DELETE.getName();
          return new Output(command, message);
        }
      }
    }

    safeRemoveDataAlbum(album, artist);

    message = username + SUCCESS_DELETE_ALBUM.getName();
    return new Output(command, message);
  }

  /**
   * Safely removes an album and its songs from the library and the artist's list of albums.
   *
   * <p>The method first iterates over all songs on the album and removes them from the library's
   * list of songs. It then removes the album from the artist's list of albums and the library's
   * list of albums.
   *
   * @param album The album to remove. This should be an instance of AlbumInput.
   * @param artist The artist who owns the album. This should be an instance of ArtistInput.
   */
  private static Output verifyInteractionWithAlbum(
      final Command command,
      final Player player,
      final ArtistInput artist,
      final AlbumInput album) {

    String message;

    if (player.getLoadedAlbum() != null
        && player.getLoadedAlbum().getOwner().equals(artist.getUsername())) {
      message = command.getUsername() + NO_DELETE.getName();
      return new Output(command, message);
    }

    if (player.getLoadedSong() != null) {
      SongInput loadedSong = player.getLoadedSong();
      if (album.getSongs().contains(loadedSong)) {
        message = command.getUsername() + NO_DELETE.getName();
        return new Output(command, message);
      }
    }

    if (player.getLoadedPlaylist() != null) {
      SongInput playingSong =
          player
              .getLoadedPlaylist()
              .getShuffleCurrentPlayingSong(
                  player.getTotalTimestamp(), player.getCurrentShuffleArray());

      if (album.getSongs().contains(playingSong)) {
        message = command.getUsername() + NO_DELETE.getName();
        return new Output(command, message);
      }
    }

    return null;
  }

  /**
   * Safely removes an album and its songs from the library and the artist's list of albums.
   *
   * <p>The method first iterates over all songs on the album and removes them from the library's
   * list of songs. It then removes the album from the artist's list of albums and the library's
   * list of albums.
   *
   * @param album The album to remove. This should be an instance of AlbumInput.
   * @param artist The artist who owns the album. This should be an instance of ArtistInput.
   */
  private static void safeRemoveDataAlbum(final AlbumInput album, final ArtistInput artist) {
    for (SongInput song : album.getSongs()) {
      LibraryInput.getInstance().getSongs().remove(song);
    }

    artist.getArtistAlbums().remove(album);
    LibraryInput.getInstance().getAlbums().remove(album);
  }
}
