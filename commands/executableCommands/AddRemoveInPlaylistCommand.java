package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class AddRemoveInPlaylistCommand implements Executable {

  /**
   * The method retrieves the user and the specified playlist based on the command. If the playlist
   * is not found, it sets the message to FILE_ERROR. If the playlist is found, it checks the
   * loading status of the player. If the player has a loaded song, it calls the addRemoveSong
   * method. If the player does not have a loaded song, it sets the message to WRONG_TYPE_FILE. If
   * the player is not loaded, it sets the message to LOAD_ERROR_ADD_REMOVE.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return A BaseOutput object representing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    PlaylistInput specifiedPlaylist = null;
    assert user != null;

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
      message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    if (user.isOnline()) {
      if (command.getPlaylistId() <= user.getCreatedPlaylists().size()) {
        specifiedPlaylist = user.getCreatedPlaylists().get(command.getPlaylistId() - 1);
      }

      if (specifiedPlaylist == null) {
        message = PlayerEnums.AddRemoveInPlaylistMessagesEnum.FILE_ERROR.getName();
      } else {
        if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
          if (player.getLoadedSong() != null
              || player.getLoadedPlaylist() != null
              || player.getLoadedAlbum() != null) {
            message = addRemoveSong(player, specifiedPlaylist);
          } else {
            message = PlayerEnums.AddRemoveInPlaylistMessagesEnum.WRONG_TYPE_FILE.getName();
          }
        } else {
          message = PlayerEnums.AddRemoveInPlaylistMessagesEnum.LOAD_ERROR_ADD_REMOVE.getName();
        }
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * The method retrieves the loaded song from the player. If the song is already in the playlist,
   * it removes the song and sets the message to SUCCESS_REMOVE. If the song is not in the playlist,
   * it adds the song and sets the message to SUCCESS_ADD.
   *
   * @param player The Player object.
   * @param specifiedPlaylist The PlaylistInput object.
   * @return A String representing the result of the operation.
   */
  private static String addRemoveSong(final Player player, final PlaylistInput specifiedPlaylist) {
    String message;
    SongInput song;
    if (player.getLoadedSong() != null) {
      song = player.getLoadedSong();
    } else if (player.getLoadedPlaylist() != null) {
      song =
          player
              .getLoadedPlaylist()
              .getShuffleCurrentPlayingSong(
                  player.getTotalTimestamp(), player.getCurrentShuffleArray());
    } else {
      song =
          player
              .getLoadedAlbum()
              .getShuffleCurrentPlayingSong(
                  player.getTotalTimestamp(), player.getCurrentShuffleArray());
    }

    if (specifiedPlaylist.getSongs().contains(song)) {
      specifiedPlaylist.getSongs().remove(song);
      message = PlayerEnums.AddRemoveInPlaylistMessagesEnum.SUCCESS_REMOVE.getName();
    } else {
      specifiedPlaylist.getSongs().add(song);
      message = PlayerEnums.AddRemoveInPlaylistMessagesEnum.SUCCESS_ADD.getName();
    }
    return message;
  }
}
