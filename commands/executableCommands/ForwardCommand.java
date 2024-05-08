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

import static commands.constants.StatusEnums.PlayPauseEnum.PLAY;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.getPodcastRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class ForwardCommand implements Executable {
  /**
   * If a podcast is loaded, the method retrieves the current episode and calculates the remaining
   * time. time. If remaining time is less than 90 seconds, then forward command will update the
   * podcast to the start of the next episode in podcast. Otherwise, it will skip forward with 90 in
   * current episode. In these 2 cases, the message is SUCCESS_FORWARD. If loaded audio file si not
   * a podcast, message is set to FILE_ERROR. If the player's loading status is not IS_LOADED, it
   * sets the message to LOAD_ERROR_FORWARD.
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

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, user.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
      message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    if (user.isOnline()) {
      if (player.getPlayPauseStatus() == PLAY.ordinal() && !player.isFinished()) {
        if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
          if (player.getLoadedPodcast() == null) {
            message = PlayerEnums.ForwardMessagesEnum.FILE_ERROR.getName();
            return new Output(command, message);
          } else {
            EpisodeInput currentEpisode =
                player.getLoadedPodcast().getCurrentPlayingEpisode(player.getTotalTimestamp());
            int episodeFinishedTimestamp =
                player.getTotalTimestamp() + getPodcastRemainingTime(player);
            Integer totalDurations = player.getLoadedPodcast().getSummedDurationsPodcast();
            assert currentEpisode != null;

            if (player.getRepeatStatus()
                == StatusEnums.RepeatAudioCollectionEnum.NO_REPEAT.ordinal()) {

              noRepeatForward(player, episodeFinishedTimestamp, totalDurations);
            } else {
              if (getPodcastRemainingTime(player) > Constants.FORWARD_BACKWARD_SECONDS) {
                player.setTotalTimestamp(
                    player.getTotalTimestamp() + Constants.FORWARD_BACKWARD_SECONDS);
              } else {
                goToNextSong(player);
              }
            }
            message = PlayerEnums.ForwardMessagesEnum.SUCCESS_FORWARD.getName();
          }
        } else {
          message = PlayerEnums.ForwardMessagesEnum.LOAD_ERROR_FORWARD.getName();
        }

        return new Output(command, message);
      }  else {
        message = PlayerEnums.ForwardMessagesEnum.LOAD_ERROR_FORWARD.getName();
        return new Output(command, message);
      }
    }
    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * The method updates the total timestamp of the player by adding the remaining time of the
   * podcast and 1. It then retrieves the next episode and sets the track name to the name of the
   * next episode.
   *
   * @param player The Player object.
   */
  private static void goToNextSong(final Player player) {
    EpisodeInput nextEpisode;
    player.setTotalTimestamp(player.getTotalTimestamp() + getPodcastRemainingTime(player) + 1);
    nextEpisode = player.getLoadedPodcast().getCurrentPlayingEpisode(player.getTotalTimestamp());
    assert nextEpisode != null;
  }

  /**
   * The method checks if the remaining time of the podcast is greater than 90 seconds. If it is, it
   * updates the total timestamp of the player by adding the 90 seconds and sets the track name to
   * the name of the current episode. If it is not, it checks if the episode finished timestamp is
   * equal to the total durations. If it is, it updates the total timestamp of the player by adding
   * the total durations. If it is not, it updates the total timestamp of the player by adding the
   * remaining time of the podcast and 1, retrieves the next episode, and sets the track name to the
   * name of the next episode.
   *
   * @param player The Player object.
   * @param episodeFinishedTimestamp The timestamp when the episode finished.
   * @param totalDurations The total durations of the episodes.
   */
  private static void noRepeatForward(
      final Player player, final int episodeFinishedTimestamp, final Integer totalDurations) {

    EpisodeInput nextEpisode;

    if (getPodcastRemainingTime(player) > Constants.FORWARD_BACKWARD_SECONDS) {
      player.setTotalTimestamp(player.getTotalTimestamp() + Constants.FORWARD_BACKWARD_SECONDS);
    } else {
      if (episodeFinishedTimestamp == totalDurations) {
        player.setTotalTimestamp(player.getTotalTimestamp() + totalDurations);
      } else {
        player.setTotalTimestamp(player.getTotalTimestamp() + getPodcastRemainingTime(player) + 1);
        nextEpisode =
            player.getLoadedPodcast().getCurrentPlayingEpisode(player.getTotalTimestamp());
        assert nextEpisode != null;
      }
    }
  }
}
