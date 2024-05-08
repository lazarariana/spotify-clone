package commands.executableCommands;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.PlayerHelperFunctions.buildNoShuffleIndexArray;
import static commands.player.PlayerHelperFunctions.recalculateTotalTimestamp;
import static commands.player.PlayerHelperFunctions.shuffleIndexes;
import static commands.player.TimestampTrack.getShufflePlaylistRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;

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

public final class ShuffleCommand implements Executable {

  /**
   * If the remaining time of the loaded shuffled playlist is 0, it sets the player's loading status
   * to NOT_LOADED. If a playlist is loaded and the player's loading status is IS_LOADED, the method
   * checks the player's shuffle status. If the shuffle status is IS_SHUFFLED, it deactivates
   * shuffle, recalculates the total timestamp, and sets the message to DEACTIVATE. If the shuffle
   * status is NOT_SHUFFLED, it activates shuffle, recalculates the total timestamp, and sets the
   * message to ACTIVATE. If no playlist is loaded, it sets the message to FILE_ERROR. If the
   * player's loading status is not IS_LOADED, it sets the message to LOAD_ERROR_SHUFFLE. Finally,
   * it returns an Output object containing the command and the message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    assert user != null;

    if (!user.isOnline()) {
      message = command.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    Player player = user.getPlayer();

    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
    }
    if (user.isOnline()) {
      if (player.getLoadedPlaylist() != null && getShufflePlaylistRemainingTime(player) <= 0) {
        player.setLoadingStatus(StatusEnums.LoadEnum.NOT_LOADED.ordinal());
      }

      if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
        PlaylistInput playlistLoaded = player.getLoadedPlaylist();

        if (playlistLoaded != null) {
          if (player.getShuffleStatus() == StatusEnums.ShuffleEnum.IS_SHUFFLED.ordinal()) {
            message = getCurrentSongTimestampDeactivatedShuffle(playlistLoaded, player);
          } else {
            message = getCurrentSongTimestampActivatedShuffle(command, playlistLoaded, player);
          }
        } else {
          message = PlayerEnums.ShuffleMessagesEnum.FILE_ERROR.getName();
        }
      } else {
        message = PlayerEnums.ShuffleMessagesEnum.LOAD_ERROR_SHUFFLE.getName();
      }

      return new Output(command, message);
    }
      message = command.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

  /**
   * The method retrieves the currently playing song and the total duration of played songs in the
   * shuffled playlist. It then calculates the time in the current song and updates the player's
   * shuffle array and shuffle status. Finally, it recalculates the total timestamp and sets the
   * message to ACTIVATE.
   *
   * @param command The Command object.
   * @param playlistLoaded The PlaylistInput object representing the loaded playlist.
   * @param player The Player object.
   * @return A String representing the result of the operation.
   */
  private static String getCurrentSongTimestampActivatedShuffle(
      final Command command, final PlaylistInput playlistLoaded, final Player player) {
    String message;
    SongInput song =
        playlistLoaded.getShuffleCurrentPlayingSong(
            player.getTotalTimestamp(), player.getCurrentShuffleArray());

    Integer playedSongsSum =
        playlistLoaded.getShufflePlayedSongsTotalDuration(
            player.getTotalTimestamp(), player.getCurrentShuffleArray());

    Integer timeInSong = player.getTotalTimestamp() - playedSongsSum;
    player.setCurrentShuffleArray(shuffleIndexes(playlistLoaded, command));
    player.setShuffleStatus(StatusEnums.ShuffleEnum.IS_SHUFFLED.ordinal());

    player.setTotalTimestamp(
        recalculateTotalTimestamp(player.getLoadedPlaylist(), song, player.getCurrentShuffleArray())
            + timeInSong);

    message = PlayerEnums.ShuffleMessagesEnum.ACTIVATE.getName();
    return message;
  }

  /**
   * The method retrieves the currently playing song and the total duration of played songs in the
   * shuffled playlist. It then calculates the time in the current song and updates the player's
   * shuffle array and shuffle status. Finally, it recalculates the total timestamp and sets the
   * message to DEACTIVATE.
   *
   * @param playlistLoaded The PlaylistInput object representing the loaded playlist.
   * @param player The Player object.
   * @return A String representing the result of the operation.
   */
  private static String getCurrentSongTimestampDeactivatedShuffle(
      final PlaylistInput playlistLoaded, final Player player) {
    String message;
    message = PlayerEnums.ShuffleMessagesEnum.DEACTIVATE.getName();

    SongInput song =
        playlistLoaded.getShuffleCurrentPlayingSong(
            player.getTotalTimestamp(), player.getCurrentShuffleArray());

    Integer playedSongsSum =
        playlistLoaded.getShufflePlayedSongsTotalDuration(
            player.getTotalTimestamp(), player.getCurrentShuffleArray());

    Integer timeInSong = player.getTotalTimestamp() - playedSongsSum;

    player.setCurrentShuffleArray(buildNoShuffleIndexArray(playlistLoaded));
    player.setShuffleStatus(StatusEnums.ShuffleEnum.NOT_SHUFFLED.ordinal());

    player.setTotalTimestamp(
        recalculateTotalTimestamp(playlistLoaded, song, player.getCurrentShuffleArray())
            + timeInSong);

    return message;
  }
}
