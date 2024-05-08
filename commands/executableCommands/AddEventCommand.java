package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.EventInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.ALREADY_EXISTS_ALBUM;
import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.NOT_ARTIST;
import static commands.constants.ArtistCommandsEnums.AddEventMessagesEnum.EVENT_FOR;
import static commands.constants.ArtistCommandsEnums.AddEventMessagesEnum.INVALID_DATE;
import static commands.constants.ArtistCommandsEnums.AddEventMessagesEnum.SUCCESS_ADD_EVENT;
import static commands.constants.ArtistCommandsEnums.DATE_FORMAT;
import static commands.constants.ArtistCommandsEnums.FEBRUARY;
import static commands.constants.ArtistCommandsEnums.FEBRUARY_DAYS;
import static commands.constants.ArtistCommandsEnums.MAX_DAYS_MONTH;
import static commands.constants.ArtistCommandsEnums.MAX_MONTHS_YEAR;
import static commands.constants.ArtistCommandsEnums.MAX_YEAR;
import static commands.constants.ArtistCommandsEnums.MIN_YEAR;
import static commands.constants.Constants.DOT;
import static commands.constants.NotificationsConstants.NOTIFICATION_EVENT;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class AddEventCommand implements Executable {

  /**
   * The method first checks if a user, artist, or host with the given username exists. If the user
   * is an artist it then checks if an event with the given name already exists for the artist. It
   * checks if the date of the event is in a valid format. If all checks pass, it creates a new
   * event and adds it to the artist's events.
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

    List<EventInput> events = artist.getEvents();
    for (EventInput event : events) {
      if (event.getName().equals(command.getName())) {
        message = command.getUsername() + ALREADY_EXISTS_ALBUM.getName();
        return new Output(command, message);
      }
    }

    if (!isValidDateFormat(command.getDate())) {
      message = EVENT_FOR.getName() + command.getUsername() + INVALID_DATE.getName();
      return new Output(command, message);
    }

    EventInput event = new EventInput(command);
    message = command.getUsername() + SUCCESS_ADD_EVENT.getName();
    artist.notifyObservers(NOTIFICATION_EVENT + artist.getUsername() + DOT);
    artist.getEvents().add(event);

    return new Output(command, message);
  }

  /**
   * The method checks if a given date string is in a valid format and within acceptable date
   * ranges.
   *
   * <p>The method parses the date string using a SimpleDateFormat. It then checks if the year,
   * month, and day are within acceptable ranges. If the date string is not in the correct format or
   * the date is not within the acceptable ranges, it returns false.
   *
   * @param date The date string to check. This should be a String in the format specified by
   *     DATE_FORMAT.
   * @return true if the date string is in the correct format and the date is within acceptable
   *     ranges, false otherwise.
   */
  public boolean isValidDateFormat(final String date) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    sdf.setLenient(false);
    try {
      Date parsedDate = sdf.parse(date);
      Calendar cal = Calendar.getInstance();
      cal.setTime(parsedDate);
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);
      if (year < MIN_YEAR || year > MAX_YEAR) {
        return false;
      }
      if (month > MAX_MONTHS_YEAR) {
        return false;
      }
      if (month == FEBRUARY && day > FEBRUARY_DAYS) {
        return false;
      }
      if (day > MAX_DAYS_MONTH) {
        return false;
      }
    } catch (ParseException e) {
      return false;
    }
    return true;
  }
}
