package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.MerchInput;
import fileio.input.UserInput;

import java.util.List;

import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.NOT_ARTIST;
import static commands.constants.ArtistCommandsEnums.AddMerchMessagesEnum.ALREADY_EXISTS_MERCH;
import static commands.constants.ArtistCommandsEnums.AddMerchMessagesEnum.INVALID_PRICE;
import static commands.constants.ArtistCommandsEnums.AddMerchMessagesEnum.SUCCESS_ADD_MERCH;
import static commands.constants.Constants.DOT;
import static commands.constants.NotificationsConstants.NOTIFICATION_MERCH;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class AddMerchCommand implements Executable {

  /**
   * Executes a command and returns the result.
   *
   * <p>The method first checks if the user, artist, or host with the given username exists. If not,
   * it returns an error message. If the user is not an artist, it returns an error message. It then
   * checks if a merchandise with the given name already exists for the artist. If so, it returns an
   * error message. It checks if the price of the merchandise is valid (greater than or equal to 0).
   * If not, it returns an error message. If all checks pass, it creates a new merchandise and adds
   * it to the artist's merchandise list.
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

    if (artist == null) {
      message = username + NOT_ARTIST.getName();
      return new Output(command, message);
    }

    List<MerchInput> merch = artist.getMerch();
    for (MerchInput singleMerch : merch) {
      if (singleMerch.getName().equals(command.getName())) {
        message = username + ALREADY_EXISTS_MERCH.getName();
        return new Output(command, message);
      }
    }

    if (command.getPrice() < 0) {
      message = INVALID_PRICE.getName();
      return new Output(command, message);
    }

    MerchInput commandMerch = new MerchInput(command);

    merch.add(commandMerch);
    message = username + SUCCESS_ADD_MERCH.getName();

    artist.notifyObservers(NOTIFICATION_MERCH + artist.getUsername() + DOT);

    return new Output(command, message);
  }
}
