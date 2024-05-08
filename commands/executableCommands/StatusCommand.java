package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.player.Status;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.player.TimestampTrack.getShufflePlaylistRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;
import static commands.player.TimestampTrack.getPodcastRemainingTime;
import static commands.player.TimestampTrack.getSongRemainingTime;

public final class StatusCommand implements Executable {

  /**
   * The method first retrieves the user and player associated with the command. It then updates the
   * trackbar of the loaded playlist, podcast, or song in the player. If the remaining time of the
   * loaded shuffled playlist, podcast, or song is greater than 0, it updates the status. It returns
   * an Output object containing the command and the updated status.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the result of the command execution and the player's
   *     status.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    assert user != null;
    Player player = user.getPlayer();
    Status stats = new Status();

    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
    }

    if (player.getLoadedPlaylist() != null) {
      if (getShufflePlaylistRemainingTime(player) > 0) {
        stats = new Status(player);
      }

    } else if (player.getLoadedPodcast() != null) {
      if (getPodcastRemainingTime(player) > 0) {
        stats = new Status(player);
      }
    } else {
      if (getSongRemainingTime(player) > 0) {
        stats = new Status(player);
      }
    }

    return new Output(command, stats);
  }
}
