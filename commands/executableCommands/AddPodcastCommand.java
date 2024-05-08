package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;;

import java.util.List;

import static commands.constants.Constants.DOT;
import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.ALREADY_EXISTS;
import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.NOT_HOST;
import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.SUCCESS;
import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.TWICE_EPISODE;
import static commands.constants.NotificationsConstants.NOTIFICATION_PODACST;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class AddPodcastCommand implements Executable {

  /**
   * The method first checks if the user, artist, or host with the given username exists. If not, it
   * returns an error message. If the user is not a host, it returns an error message. It then
   * checks if a podcast with the given name already exists for the host. If so, it returns an error
   * message. It checks if the podcast has unique episode names. If not, it returns an error
   * message. If all checks pass, it creates a new podcast and adds it to the host's podcast list
   * and the library's podcast list.
   *
   * @param command The command to execute. This should be an instance of Command.
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

    if (host == null) {
      message = username + NOT_HOST.getName();
      return new Output(command, message);
    }

    List<PodcastInput> podcasts = host.getHostPodcasts();
    for (PodcastInput podcast : podcasts) {
      if (podcast.getName().equals(command.getName())) {
        message = username + ALREADY_EXISTS.getName();
        return new Output(command, message);
      }
    }

    PodcastInput podcast = new PodcastInput(command);
    List<String> episodes = PodcastInput.getEpisodesNames(podcast);

    if (!LibraryInput.hasUniqueElements(episodes)) {
      message = TWICE_EPISODE.getName();
      return new Output(command, message);
    }

    podcasts.add(podcast);
    LibraryInput.getInstance().getPodcasts().add(podcast);
    message = username + SUCCESS.getName();

    host.notifyObservers(NOTIFICATION_PODACST + host.getUsername() + DOT);

    return new Output(command, message);
  }
}
