package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.AnnouncementInput;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.HostCommandsEnums.AddAPodcastMessagesEnum.NOT_HOST;
import static commands.constants.HostCommandsEnums.RemoveAnnouncementMessagesEnum.NO_ANNOUNCEMENT;
import static commands.constants.HostCommandsEnums.RemoveAnnouncementMessagesEnum.SUCCESS_DELETE_ANNOUNCEMENT;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class RemoveAnnouncementCommand implements Executable {

  /**
   * The method first checks if the user, artist, or host with the given username exists. If not, it
   * returns an error message. If the user is online, it updates the audio trackbar. If the user is
   * offline, it sets the last command timestamp and returns a message that the user is offline. If
   * the user is not a host, it returns an error message. It then checks if an announcement with the
   * given name exists for the host. If not, it returns an error message. If the announcement
   * exists, it removes the announcement from the host's announcements and returns a success
   * message.
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

    AnnouncementInput announcement =
        AnnouncementInput.getAnnouncementByName(host, command.getName());

    if (announcement == null) {
      message = username + NO_ANNOUNCEMENT.getName();
      return new Output(command, message);
    }

    host.getAnnouncements().remove(announcement);
    message = username + SUCCESS_DELETE_ANNOUNCEMENT.getName();
    return new Output(command, message);
  }
}
