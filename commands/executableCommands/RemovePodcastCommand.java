package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;

import java.util.List;

import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.NOT_HOST;
import static commands.constants.HostCommandsEnums.RemovePodcastMessagesEnum.NO_DELETE;
import static commands.constants.HostCommandsEnums.RemovePodcastMessagesEnum.NO_PODCAST;
import static commands.constants.HostCommandsEnums.RemovePodcastMessagesEnum.SUCCESS_DELETE_PODCAST;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class RemovePodcastCommand implements Executable {

  /**
   * The method deletes a podcast from the host's list of podcasts. The command should contain the
   * username of the host who is deleting the podcast and the name of the podcast. The method first
   * checks if the user exists and is a host and the podcast exists. If not, it also checks if any
   * user is currently playing the podcast.
   * If all checks pass, it removes the podcast from the host's list and the library,
   * and returns a success message.
   *
   * @param command The command to execute. This should be an instance of Command with
   *                the username of the host and the name of the podcast.
   * @return A BaseOutput object containing the result of the command. If the command
   *         was successful, this will be a success message. If the command failed,
   *         this will be an error message.
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

    if (user != null) {
      Player player = user.getPlayer();
      if (user.isOnline()) {
        updateAudioTrackbar(command, player, command.getUsername());
      } else {
        player.setLastCommandTimestamp(command.getTimestamp());
        message = user.getUsername() + IS_OFFLINE.getName();
        return new Output(command, message);
      }
    }

    if (host == null) {
      message = username + NOT_HOST.getName();
      return new Output(command, message);
    }

    PodcastInput podcast = PodcastInput.getPodcastByName(command.getName());
    List<PodcastInput> podcasts = host.getHostPodcasts();

    if (podcast == null) {
      message = username + NO_PODCAST.getName();
      return new Output(command, message);
    }

    for (UserInput currentUser : LibraryInput.getInstance().getUsers()) {
      if (currentUser != null && currentUser.getPlayer().getLoadedPodcast() != null) {
        message = username + NO_DELETE.getName();
        return new Output(command, message);
      }
    }

    podcasts.remove(podcast);
    LibraryInput.getInstance().getPodcasts().remove(podcast);
    message = username + SUCCESS_DELETE_PODCAST.getName();
    return new Output(command, message);
  }
}
