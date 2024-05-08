package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.ArtistInput;
import fileio.input.EventInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.List;

import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.NOT_ARTIST;
import static commands.constants.ArtistCommandsEnums.RemoveEventMessagesEnum.NOT_EXISTS_EVENT;
import static commands.constants.ArtistCommandsEnums.RemoveEventMessagesEnum.SUCCESS_REMOVE_EVENT;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class RemoveEventCommand implements Executable {
  @Override
  public BaseOutput executeCommand(final Command command) {

    /**
     * The method first checks if the user, artist, or host with the given username exists. If not,
     * it returns an error message. If the user is online, it updates the audio trackbar. If the
     * user is offline, it sets the last command timestamp and returns a message that the user is
     * offline. If the user is not an artist, it returns an error message. It then checks if an
     * event with the given name exists for the artist. If not, it returns an error message. If the
     * event exists, it removes the event from the artist's events and returns a success message.
     *
     * @param command The command to execute. This should be an instance of Command.
     * @return A BaseOutput object containing the result of the command execution.
     */
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

    if (artist == null) {
      message = username + NOT_ARTIST.getName();
      return new Output(command, message);
    }

    List<EventInput> events = artist.getEvents();
    EventInput event = EventInput.getEventByName(artist, command.getName());

    if (event == null) {
      message = username + NOT_EXISTS_EVENT.getName();
      return new Output(command, message);
    }

    events.remove(event);
    message = username + SUCCESS_REMOVE_EVENT.getName();
    return new Output(command, message);
  }
}
