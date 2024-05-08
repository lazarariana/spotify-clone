package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.AnnouncementInput;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.List;

import static commands.constants.Constants.DOT;
import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.NOT_HOST;
import static commands.constants.HostCommandsEnums.AddAnnouncementMessagesEnum.ALREADY_EXISTS_ANNOUNCEMENT;
import static commands.constants.HostCommandsEnums.AddAnnouncementMessagesEnum.SUCCESS_ADD_ANNOUNCEMENT;
import static commands.constants.NotificationsConstants.NOTIFICATION_ANNOUNCEMENT;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class AddAnnouncementCommand implements Executable {

  /**
   * If the user exists and is a host, the method checks if the host already has an announcement
   * with the same name. If not, it adds an announcement to the host's list of announcements If all
   * checks pass, it adds the announcement to the host's list and returns a success message.
   *
   * @param command The command to execute. This should be an instance of Command with the username
   *     of the host and the name of the announcement.
   * @return A BaseOutput object containing the result of the command with a relevant message.
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

    List<AnnouncementInput> announcements = host.getAnnouncements();
    for (AnnouncementInput announcement : announcements) {
      if (announcement.getName().equals(command.getName())) {
        message = username + ALREADY_EXISTS_ANNOUNCEMENT.getName();
        return new Output(command, message);
      }
    }

    AnnouncementInput announcement = new AnnouncementInput(command);
    message = username + SUCCESS_ADD_ANNOUNCEMENT.getName();
    announcements.add(announcement);

    host.notifyObservers(NOTIFICATION_ANNOUNCEMENT + host.getUsername() + DOT);

    return new Output(command, message);
  }
}
