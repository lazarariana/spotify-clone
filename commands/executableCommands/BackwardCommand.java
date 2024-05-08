package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.constants.Constants;
import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.getPodcastRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class BackwardCommand implements Executable {

  /**
   * If a podcast is loaded, the method retrieves the current episode and calculates the elapsed
   * time. If the elapsed time is greater than 90 seconds, it updates the total timestamp of the
   * player by adding/subtracting 90 seconds. If the elapsed time is not greater than 90 seconds,
   * the current episode will be started again and the message is set to SUCCESS_REWIND. If the
   * player is not loaded, it sets the message to LOAD_ERROR_BACKWARD. If loaded audio file si not a
   * podcast, message is set to FILE_ERROR.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return A BaseOutput object representing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
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
      if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
        if (player.getLoadedPodcast() == null) {
          message = PlayerEnums.BackwardMessagesEnum.FILE_ERROR.getName();
          return new Output(command, message);
        } else {
          EpisodeInput currentEpisode =
              player.getLoadedPodcast().getCurrentPlayingEpisode(player.getTotalTimestamp());
          assert currentEpisode != null;
          int elapsedTime = currentEpisode.getDuration() - getPodcastRemainingTime(player);

          if (elapsedTime > Constants.FORWARD_BACKWARD_SECONDS) {
            player.setTotalTimestamp(
                player.getTotalTimestamp() - Constants.FORWARD_BACKWARD_SECONDS);
          } else {
            player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);
          }

          message = PlayerEnums.BackwardMessagesEnum.SUCCESS_REWIND.getName();
        }
      } else {
        message = PlayerEnums.BackwardMessagesEnum.LOAD_ERROR_BACKWARD.getName();
      }

      return new Output(command, message);
    }
    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
